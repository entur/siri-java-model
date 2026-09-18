/*
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 *   https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 */

package org.entur.siri.adapter;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

/**
 * Position-based parser for the ISO-8601 dateTime forms produced by SIRI feeds:
 * {@code yyyy-MM-ddTHH:mm:ss[.fffffffff][Z|±HH:MM]}.
 * <p>
 * Why this exists: profiling of idle SIRI-ET ingestion showed about 13 % of CPU inside
 * {@link ZonedDateTime#parse(CharSequence)}. The {@code DateTimeFormatter} machinery clones a
 * {@code HashMap} in {@code Parsed.copy()}, walks roughly 15 sub-parsers and allocates for every
 * optional group, all to parse strings that in practice have one fixed shape. This parser reads
 * the fields at fixed positions, uses no regular expressions and allocates nothing but the result,
 * which makes it about an order of magnitude faster on real data.
 * <p>
 * It is deliberately strict. Any other ISO form (missing seconds, ±HH:MM:SS offsets, zone region
 * ids, extended or negative years, lowercase designators, ...) and any out-of-range field makes it
 * return {@code null}, so that the caller can fall back to a full parser and report errors
 * consistently. Input without a zone designator is a local time and is resolved in the requested
 * zone exactly as {@link ZonedDateTime#ofLocal(LocalDateTime, ZoneId, ZoneOffset)} with a UTC
 * preferred offset does, mirroring {@link ZonedDateTimeAdapter}'s fallback.
 */
final class FastZonedDateTimeParser {

    /*
     * Layout of the supported text. The date and time part has fixed positions; the fraction and
     * the zone designator are optional and follow directly after the seconds.
     *
     *   yyyy-MM-ddTHH:mm:ss.fffffffff+HH:MM
     *   0    5  8  11 14 17 |         |
     *                       19        fraction end (19..29), zone designator starts here
     */
    private static final int YEAR = 0;
    private static final int MONTH = 5;
    private static final int DAY = 8;
    private static final int HOUR = 11;
    private static final int MINUTE = 14;
    private static final int SECOND = 17;
    /** Index just after {@code yyyy-MM-ddTHH:mm:ss}; where an optional fraction or zone begins. */
    private static final int DATE_TIME_END = 19;

    private static final int MAX_FRACTION_DIGITS = 9;
    /** Length of {@code ±HH:MM}. */
    private static final int OFFSET_LENGTH = 6;

    private static final int MIN_LENGTH = DATE_TIME_END;
    private static final int MAX_LENGTH = DATE_TIME_END + 1 + MAX_FRACTION_DIGITS + OFFSET_LENGTH;

    /** Multiplier turning a fraction of {@code n} digits into nanoseconds, indexed by {@code n}. */
    private static final int[] NANOS_PER_FRACTION_DIGIT = {
        0, 100_000_000, 10_000_000, 1_000_000, 100_000, 10_000, 1_000, 100, 10, 1,
    };

    /** Preferred offset when a local time falls in a DST overlap, matching the adapter's fallback. */
    private static final ZoneOffset PREFERRED_OFFSET = ZoneOffset.UTC;

    /** Returned by the {@code read*} helpers when the text does not have the expected shape. */
    private static final int INVALID = -1;

    private FastZonedDateTimeParser() {}

    /**
     * Parses {@code text} if it is in the supported form.
     *
     * @param text the text to parse, not null
     * @param zone the zone the result is expressed in: same instant as the parsed offset time, or
     *             the zone a local time without designator is interpreted in
     * @return the parsed value, or {@code null} if {@code text} is not in the supported form
     */
    static ZonedDateTime parse(String text, ZoneId zone) {
        int length = text.length();
        if (length < MIN_LENGTH || length > MAX_LENGTH || !hasDateTimeSeparators(text)) {
            return null;
        }

        int fractionEnd = readFractionEnd(text);
        if (fractionEnd == INVALID) {
            return null;
        }
        int nanos = readNanos(text, fractionEnd);

        LocalDateTime local = toLocalDateTime(text, nanos);
        if (local == null) {
            return null;
        }

        if (fractionEnd == length) {
            // No zone designator: a local time in the requested zone.
            return ZonedDateTime.ofLocal(local, zone, PREFERRED_OFFSET);
        }
        ZoneOffset offset = readOffset(text, fractionEnd);
        if (offset == null) {
            return null;
        }
        return ZonedDateTime.ofInstant(local, offset, zone);
    }

    /** Checks the fixed separators of {@code yyyy-MM-ddTHH:mm:ss}. */
    private static boolean hasDateTimeSeparators(String text) {
        return (
            text.charAt(4) == '-' &&
            text.charAt(7) == '-' &&
            text.charAt(10) == 'T' &&
            text.charAt(13) == ':' &&
            text.charAt(16) == ':'
        );
    }

    /**
     * Locates the end of the optional fraction: {@link #DATE_TIME_END} when there is none, the
     * index after its last digit otherwise, or {@link #INVALID} for a bare dot or too many digits.
     */
    private static int readFractionEnd(String text) {
        int length = text.length();
        if (length == DATE_TIME_END || text.charAt(DATE_TIME_END) != '.') {
            return DATE_TIME_END;
        }
        int pos = DATE_TIME_END + 1;
        while (pos < length && isDigit(text.charAt(pos))) {
            pos++;
        }
        int fractionDigits = pos - (DATE_TIME_END + 1);
        if (fractionDigits < 1 || fractionDigits > MAX_FRACTION_DIGITS) {
            return INVALID;
        }
        return pos;
    }

    /**
     * Converts the fraction digits, if any, between the dot and {@code fractionEnd} to nanoseconds.
     * {@link #readFractionEnd} has already checked that these characters are digits.
     */
    private static int readNanos(String text, int fractionEnd) {
        if (fractionEnd == DATE_TIME_END) {
            return 0;
        }
        int fractionStart = DATE_TIME_END + 1;
        int value = 0;
        for (int i = fractionStart; i < fractionEnd; i++) {
            value = value * 10 + (text.charAt(i) - '0');
        }
        return value * NANOS_PER_FRACTION_DIGIT[fractionEnd - fractionStart];
    }

    /** Reads the six date-time fields; {@code null} if any is not numeric or out of range. */
    private static LocalDateTime toLocalDateTime(String text, int nanos) {
        int year = readDigits(text, YEAR, 4);
        int month = readDigits(text, MONTH, 2);
        int day = readDigits(text, DAY, 2);
        int hour = readDigits(text, HOUR, 2);
        int minute = readDigits(text, MINUTE, 2);
        int second = readDigits(text, SECOND, 2);
        if (
            year == INVALID ||
            month == INVALID ||
            day == INVALID ||
            hour == INVALID ||
            minute == INVALID ||
            second == INVALID
        ) {
            return null;
        }
        try {
            return LocalDateTime.of(year, month, day, hour, minute, second, nanos);
        } catch (DateTimeException e) {
            return null;
        }
    }

    /**
     * Reads the zone designator starting at {@code start} and running to the end of the text:
     * {@code Z} or {@code ±HH:MM}. Returns {@code null} for anything else, including offsets
     * outside the ±18:00 range supported by {@link ZoneOffset}.
     */
    private static ZoneOffset readOffset(String text, int start) {
        int length = text.length();
        char designator = text.charAt(start);
        if (designator == 'Z') {
            return start + 1 == length ? ZoneOffset.UTC : null;
        }
        if (designator != '+' && designator != '-') {
            return null;
        }
        if (start + OFFSET_LENGTH != length || text.charAt(start + 3) != ':') {
            return null;
        }
        int hours = readDigits(text, start + 1, 2);
        int minutes = readDigits(text, start + 4, 2);
        if (hours == INVALID || minutes == INVALID) {
            return null;
        }
        int sign = designator == '-' ? -1 : 1;
        try {
            return ZoneOffset.ofHoursMinutes(sign * hours, sign * minutes);
        } catch (DateTimeException e) {
            return null;
        }
    }

    /** Reads exactly {@code count} ASCII digits starting at {@code start}, or {@link #INVALID}. */
    private static int readDigits(String text, int start, int count) {
        int value = 0;
        for (int i = start; i < start + count; i++) {
            char c = text.charAt(i);
            if (!isDigit(c)) {
                return INVALID;
            }
            value = value * 10 + (c - '0');
        }
        return value;
    }

    private static boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }
}
