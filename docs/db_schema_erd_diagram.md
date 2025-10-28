```mermaid
erDiagram
    PROJECTS ||--o{ ITERATIONS : contains
    ITERATIONS ||--o{ ACTIVITIES : contains
    TEAM_MEMBERS ||--o{ ACTIVITIES : assigned_to
    PROJECTS ||--o{ PROJECT_TEAMS : has
    TEAM_MEMBERS ||--o{ PROJECT_TEAMS : member_of
    ACTIVITIES ||--o{ ACTIVITY_COMMENTS : has
    TEAM_MEMBERS ||--o{ ACTIVITY_COMMENTS : writes

    PROJECTS {
        int id PK
        varchar name
        text description
        date start_date
        date end_date
        enum status
        int created_by_id FK
        timestamp created_at
        timestamp updated_at
    }

    ITERATIONS {
        int id PK
        int project_id FK
        varchar name
        text goal
        date start_date
        date end_date
        enum status
        timestamp created_at
        timestamp updated_at
    }

    ACTIVITIES {
        int id PK
        int iteration_id FK
        varchar title
        text description
        enum status
        enum priority
        decimal estimated_hours
        decimal actual_hours
        int assigned_to_id FK
        date created_date
        date due_date
        timestamp created_at
        timestamp updated_at
    }

    TEAM_MEMBERS {
        int id PK
        varchar name
        varchar email UK
        varchar password_hash
        enum role
        text skills
        bool is_active
        timestamp created_at
        timestamp updated_at
    }

    PROJECT_TEAMS {
        int id PK
        int project_id FK
        int team_member_id FK
        varchar role_in_project
        date joined_date
        timestamp created_at
    }

    ACTIVITY_COMMENTS {
        int id PK
        int activity_id FK
        int member_id FK
        text comment
        timestamp timestamp
    }
