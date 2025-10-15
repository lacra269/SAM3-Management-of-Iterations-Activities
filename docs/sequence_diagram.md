'''marmaid

sequenceDiagram
    actor PM as Project Manager
    participant UI as Web UI
    participant API as API Controller
    participant Service as Business Service
    participant Repo as Repository
    participant DB as Database
    participant NotifService as Notification Service
    actor Dev as Developer
    
    PM->>UI: Opens "Create Task" form
    UI->>PM: Display form with iterations & members
    
    PM->>UI: Fills task details (title, description, etc.)
    PM->>UI: Selects iteration
    PM->>UI: Assigns to Developer
    PM->>UI: Clicks "Create Task"
    
    UI->>API: POST /api/activities
    Note over API: Validate request data
    
    API->>Service: createActivity(activityDto)
    
    Service->>Service: Validate business rules
    Note over Service: Check: member in project team<br/>Check: iteration exists<br/>Check: dates valid
    
    Service->>Repo: save(activity)
    Repo->>DB: INSERT INTO activities
    DB-->>Repo: activity saved (with ID)
    Repo-->>Service: return activity
    
    Service->>NotifService: notifyTaskAssignment(activity, developer)
    NotifService->>Dev: Send email/push notification
    
    Service-->>API: return ActivityResponse
    API-->>UI: 201 Created + activity data
    UI-->>PM: Show success message
    UI->>UI: Redirect to iteration board
    
    Dev->>UI: Logs in and checks tasks
    UI->>API: GET /api/activities/assigned-to-me
    API->>Service: getActivitiesByMember(developerId)
    Service->>Repo: findByAssignedTo(developerId)
    Repo->>DB: SELECT * FROM activities WHERE...
    DB-->>Repo: return activities list
    Repo-->>Service: return activities
    Service-->>API: return activities
    API-->>UI: 200 OK + activities
    UI-->>Dev: Display task list with new task
