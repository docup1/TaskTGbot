
CREATE TABLE Users (
                       user_id SERIAL PRIMARY KEY,
                       username VARCHAR(255) UNIQUE NOT NULL,
                       created_at TIMESTAMPTZ DEFAULT NOW(),
                       bio TEXT,
                       rating NUMERIC(3, 2) DEFAULT 0.0
);

CREATE TABLE Tasks (
                       task_id SERIAL PRIMARY KEY,
                       creator_id INTEGER REFERENCES Users(user_id) ON DELETE CASCADE,
                       title VARCHAR(255) NOT NULL,
                       description TEXT,
                       status VARCHAR(50) DEFAULT 'open', -- варианты: open, in_progress, completed, canceled
                       created_at TIMESTAMPTZ DEFAULT NOW(),
                       due_date TIMESTAMPTZ,
                       reward NUMERIC(10, 2)
);

CREATE TABLE UsersTaskMatcher (
                                  matcher_id SERIAL PRIMARY KEY,
                                  user_id INTEGER REFERENCES Users(user_id) ON DELETE CASCADE,
                                  task_id INTEGER REFERENCES Tasks(task_id) ON DELETE CASCADE,
                                  executor BOOLEAN default false,
                                  UNIQUE(user_id, task_id)
);

CREATE TABLE TaskApplications (
                                  application_id SERIAL PRIMARY KEY,
                                  task_id INTEGER REFERENCES Tasks(task_id) ON DELETE CASCADE,
                                  applicant_id INTEGER REFERENCES Users(user_id) ON DELETE CASCADE,
                                  status VARCHAR(50) DEFAULT 'pending', -- варианты: pending, approved, rejected
                                  applied_at TIMESTAMPTZ DEFAULT NOW(),
                                  UNIQUE(task_id, applicant_id)
);

CREATE TABLE TaskReviews (
                             review_id SERIAL PRIMARY KEY,
                             task_id INTEGER REFERENCES Tasks(task_id) ON DELETE CASCADE,
                             reviewer_id INTEGER REFERENCES Users(user_id) ON DELETE CASCADE,
                             reviewee_id INTEGER REFERENCES Users(user_id) ON DELETE CASCADE,
                             rating NUMERIC(2, 1) CHECK (rating >= 1 AND rating <= 5),
                             comment TEXT,
                             created_at TIMESTAMPTZ DEFAULT NOW()
);INSERT INTO Users (username, bio) VALUES ('username_example', 'This is a bio text');
