```mermaid

classDiagram
    class Project {
        +int id
        +string name
        +string description
        +date startDate
        +date endDate
        +ProjectStatus status
        +int createdById
        +date createdAt
        +calculateProgress() decimal
        +isOverdue() bool
    }
    
    class Iteration {
        +int id
        +int projectId
        +string name
        +string goal
        +date startDate
        +date endDate
        +IterationStatus status
        +calculateProgress() decimal
        +getTotalTasks() int
        +getCompletedTasks() int
    }
    
    class Activity {
        +int id
        +int iterationId
        +string title
        +string description
        +ActivityStatus status
        +Priority priority
        +decimal estimatedHours
        +decimal actualHours
        +int assignedToId
        +date createdDate
        +date dueDate
        +updateStatus(status) void
        +calculateVariance() decimal
    }
    
    class TeamMember {
        +int id
        +string name
        +string email
        +string password
        +MemberRole role
        +string skills
        +bool isActive
        +getAssignedTasks() List~Activity~
        +getWorkload() decimal
    }
    
    class ProjectTeam {
        +int id
        +int projectId
        +int teamMemberId
        +string roleInProject
        +date joinedDate
    }
    
    class ActivityComment {
        +int id
        +int activityId
        +int memberId
        +string comment
        +datetime timestamp
    }
    
    class ProjectStatus {
        <<enumeration>>
        ACTIVE
        COMPLETED
        ON_HOLD
        CANCELLED
    }
    
    class IterationStatus {
        <<enumeration>>
        PLANNED
        ACTIVE
        COMPLETED
    }
    
    class ActivityStatus {
        <<enumeration>>
        TODO
        IN_PROGRESS
        IN_REVIEW
        DONE
        BLOCKED
    }
    
    class Priority {
        <<enumeration>>
        LOW
        MEDIUM
        HIGH
        CRITICAL
    }
    
    class MemberRole {
        <<enumeration>>
        ANALYST
        DEVELOPER
        TESTER
        PROJECT_MANAGER
        ADMIN
    }
    
    Project "1" --> "*" Iteration : contains
    Iteration "1" --> "*" Activity : contains
    Activity "*" --> "1" TeamMember : assignedTo
    Project "*" --> "*" TeamMember : team
    Project "1" --> "*" ProjectTeam : members
    ProjectTeam "*" --> "1" TeamMember : member
    Activity "1" --> "*" ActivityComment : comments
    ActivityComment "*" --> "1" TeamMember : author
    Project --> ProjectStatus
    Iteration --> IterationStatus
    Activity --> ActivityStatus
    Activity --> Priority
    TeamMember --> MemberRole
