Server Interface for Real-time Information
(C) Copyright CEN SIRI 2007,2008

Changes to SIRI schema v1.3b   since v1.0 

Note that this is a work in progress version that may be updated further. Any Changes will be backwards compatible
ie, if not used will not break existing function unless marked specifcally

============================ 
2009-03-31  Corrections 
    (a) siri.xsd Correct cardinality on SIRI-SX request & SIRI-FM request to be many
    (b) siriSg.xsd Remove uneccessary groups for decoupled version
2009-03-31  Corrections 
    (a) stopMonitoring  
    change the element type of MaximumNumberOfCalls.Previous, MaximumNumberOfCalls.Onwards  from xsd:positiveInteger to xsd:nonNegativeInteger
    
   and clarify handling of 
    
    MaximumNumberOfCalls :  If calls are to be returned, maximum number of calls to include in response. If absent, exclude all calls.  
    
    Previous :  Maximum number of previous calls to include. Zero for none. Only applies if MaximumNumberOfCalls  soecified. Zero for none. If MaximumNumber of Calls specified but  MaximumNumberOfCalls.previous absent, include all previous calls.
    
    Onwards : Maximum number of onwards calls to include. Zero for none. Only applies if MaximumNumberOfCalls  soecified. Zero for none. If MaximumNumber of Calls specified but  MaximumNumberOfCalls.Onwards absent, include all onwards  calls.
    
___________________________________________________________________________
    
    (b) siriSg.xsd Remove uneccessary groups for decoupled version
2009-03-03  Corrections 
    (a) siri.xsd Correct cardinality on SIRI-SX request & SIRI-FM request to be many
    (b) siriSg.xsd Correct cardinality on servcierequest & subscription request to be many
2009-09-18  Siricommon - allow empty Terminate subscription response  
    (a) Relax mandatory on  in siri_common 
2008-11-18  Revise FM servics 
    (a) Revise daytypes in siri_time-v1.2
2008-11-17  Revise to support substitution groups
    (a) Make all requests subtypes of abstract request.  Add Subst elements
    (b) Introduce  AbstractFunctionalServiceRequest, AbstarctCapoabilityServiceRequest, AbstarctDiscoveryRequest
    as common supertypes. revised versiosn of siri_requests-v1.2.xsd,  siri_journey-v1.2.xsd and siri_permissions-v1.1.xsd, siri-All and siribase
    (c) add SiriSg and Siri_base-v1.3.xsd packages
2008-11-12  Corrections to the Caridnailities on the siri_discovery services  
    (a) Change min maxes on >LineRef, StopPoints, Features etc etc
    Add SubscriberRef to TerminateSubscriptionRequest  
2008-10-08  Corrections to the SIRI  service  
    (a) Add SubscriberRef to TerminateSubscriptionRequest  
2008-10-06  Corrections to the SIRI  service  
    (a) Correct StopTimetable SubscriptionRequest to use group
    (b) Correct cardinality on AnnotateStopPointRef in StopPointDeliviery
2008-10-01  Corrections to the SIRI-SX service  
    (a) Add StatusFilterGroup  to SIRI-SX request with Verification,   Progress and Reality 
    (b) Make Predictability
2008-09-30  Corrections to the SIRI-SX service      
    (a) Make Undefined and Unknown reason codes strinsg so they do not require content
    (b) Extensions change defaults to lax,  type =#any   to simplify binding
    
2008-07-07  Corrections to the SIRI-SX service      
    (a) Allow link projection and start / end offset.
    (b) Introduce a separate AffectedSection to handle this (refines SectionRef) 
        used on AffectedRoute  and 
    (c) Allow a link projection & Offset  on connection Link via AFfectedPathLin
    (d) Add an AFfectedRoad to hold basic road details 
    (e) Correct SX request to be predctability not nature
    (f) Add Scope to SX request
     
2008-07-05  Corrections to the SIRI-SX service               
    (a) SIRI_SX Rename AccessibilityDisruption to AccessibilityAssessment & reuse ifopt defs
2008-07-05  Corercytions toi the SIRI-SX service               
    (a) SIRI_SX Add missing SituationRecord to RoadSituation
    (b) SIRI_SX correct type to be participant ref  not participant pair ref-->
    (c) Allow zero PT or Road Situation elements in a delivery
    (d) Affects Line group corrected to ref a lineref and not a route ref 
    (e) Add missing scopeType to Situation classifier group
    (f) Add other subreasons
    (g) add secondary reasons

2008 05 08  StopMonitoring service 
    (a) Correct type on  FeatureRef in Stop monitoring       
    (b) Add StopMonitoringMultipleRequest 
    (c) Add optional MonitoringRef to StopMonitoringDelivery so that can return id even if no data  
 
2008 03 27
    Fix up ifopt & ACSB version numbers to match ifopt 0.4 
    
    
2008-03-26 EstimatedTimetable Production Timetable Service Service 
     Add wrapper tag for Line + Direction to help binding to Axis
     Wraps multiple instances
     ** Note this will break strict Comaptibility with 1.0    
     
2008 03  12    
       Add comments for elements and arrtributes that lacked them
       Correct wdsl errors 
       strip out degree character
       BeyondHorizon type corrected  
       
2008 02  12    
      Add SIRI-SX revisions & Datex2 harmonisation features

2008 02 12 V1.3 draft
=====================================
2007 10  17 
      Add Situation Exchange & Facility Exchange services.
      Added a  siri_all-v1.2.xsd, ifopt_allStopPlace-v0.3.xsd, acsp_all.xsd packages to force explicit declaration of all elements in an imported namespace on the first reference. This overcomes a limitation of some XML tools that only pick up thos elements on the first import and ignotre all subsequent imports.  


2007 04 17 
    Name Space improvements
        revise to use explicit namespaces
        Change name space :  http://siri.org.uk/ ==? siri.org.uk/siri
        
    harmonise Facility Monitoring 
         Revise SpecialNeeds to use  acsb package
         Use Ifopt facility etc        
         Factor out Extensions to utilty file
         
2007 04 V1.2 
=======================================