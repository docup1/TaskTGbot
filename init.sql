-- ### Шаг 1: Определение типов ENUM для статусов задач и сообщений
CREATE TYPE task_status AS ENUM ('open', 'completed', 'draft');

-- ### Шаг 2: Таблица ролей пользователей UserRoles
CREATE TABLE UserRoles (
                           role_id SERIAL PRIMARY KEY,                     -- Уникальный идентификатор роли
                           role_name VARCHAR(50) NOT NULL,                 -- Название роли
                           role_comment VARCHAR(255),                      -- Комментарий к роли
                           CONSTRAINT unique_role_name UNIQUE (role_name)  -- Уникальность имени роли
);

-- ### Шаг 3: Таблица категорий задач TaskCategory
CREATE TABLE TaskCategory (
                              category_id SERIAL PRIMARY KEY,                 -- Уникальный идентификатор категории
                              category_name VARCHAR(50) NOT NULL,             -- Название категории
                              category_comment VARCHAR(255),                  -- Комментарий к категории
                              CONSTRAINT unique_category_name UNIQUE (category_name)  -- Уникальность названия категории
);

-- ### Шаг 4: Таблица пользователей Users
CREATE TABLE Users (
                       user_id SERIAL PRIMARY KEY,                     -- Уникальный идентификатор пользователя
                       username VARCHAR(50) UNIQUE NOT NULL,           -- Уникальное имя пользователя
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- Дата и время создания аккаунта
                       bio TEXT,                                       -- Биография пользователя
                       role_id INT REFERENCES UserRoles(role_id) ON DELETE SET NULL   -- Внешний ключ на таблицу UserRoles
);

-- ### Шаг 5: Таблица задач Tasks
CREATE TABLE Tasks (
                       task_id SERIAL PRIMARY KEY,                     -- Уникальный идентификатор задачи
                       category_id INT REFERENCES TaskCategory(category_id) ON DELETE SET NULL, -- Внешний ключ на категорию
                       creator_id INT REFERENCES Users(user_id) ON DELETE CASCADE, -- Идентификатор создателя
                       title VARCHAR(100) NOT NULL,                    -- Название задачи
                       description TEXT,                               -- Описание задачи
                       status task_status NOT NULL DEFAULT 'draft',     -- Статус задачи
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- Дата создания задачи
                       due_date TIMESTAMP,                             -- Срок выполнения задачи
                       reward NUMERIC(10, 2) DEFAULT 0,                -- Вознаграждение за выполнение задачи
                       msg_id VARCHAR                                  -- ID сообщения в чате с опубликованными задачами
);
