```mermaid
flowchart TD
    Start([Task Created]) --> TODO[Status: TODO]
    
    TODO --> CheckAssign{Is task<br/>assigned?}
    CheckAssign -->|No| WaitAssign[Wait for assignment]
    WaitAssign --> Assigned[PM assigns to member]
    Assigned --> NotifyMember[Notify team member]
    CheckAssign -->|Yes| NotifyMember
    
    NotifyMember --> MemberView[Member views task]
    MemberView --> StartWork{Member ready<br/>to start?}
    
    StartWork -->|Yes| InProgress[Status: IN_PROGRESS]
    StartWork -->|No| TODO
    
    InProgress --> LogTime[Member logs time worked]
    LogTime --> WorkDone{Work<br/>completed?}
    
    WorkDone -->|No| CheckBlocked{Any<br/>blockers?}
    CheckBlocked -->|Yes| Blocked[Status: BLOCKED]
    Blocked --> AddComment[Member adds comment<br/>explaining blocker]
    AddComment --> NotifyPM[Notify PM]
    NotifyPM --> PMResolve[PM resolves blocker]
    PMResolve --> InProgress
    
    CheckBlocked -->|No| InProgress
    
    WorkDone -->|Yes| InReview[Status: IN_REVIEW]
    InReview --> NotifyReviewer[Notify reviewer<br/>Analyst/PM]
    NotifyReviewer --> Review{Review<br/>passed?}
    
    Review -->|No| Rework[Request changes]
    Rework --> InProgress
    
    Review -->|Yes| Done[Status: DONE]
    Done --> UpdateIterProgress[Update iteration progress]
    UpdateIterProgress --> CheckIterDone{All iteration<br/>tasks done?}
    
    CheckIterDone -->|Yes| CompleteIter[Mark iteration as COMPLETED]
    CompleteIter --> UpdateProjectProgress[Update project progress]
    CheckIterDone -->|No| UpdateProjectProgress
    
    UpdateProjectProgress --> End([Task Complete])
    
    style TODO fill:#ffeb3b
    style InProgress fill:#2196f3
    style Blocked fill:#f44336
    style InReview fill:#ff9800
    style Done fill:#4caf50
    style Start fill:#9c27b0
    style End fill:#9c27b0
