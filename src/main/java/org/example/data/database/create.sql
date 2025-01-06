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




-- ### Документация и основные запросы

-- 1. Добавление новой задачи
INSERT INTO Tasks (category_id, creator_id, title, description, status, due_date, reward, msg_id)
VALUES (1, 1, 'Задача по разработке', 'Описание задачи', 'open', '2023-12-31', 500.00, 'msg123');

-- 2.  Заявка пользователя на задачу
INSERT INTO TaskApplications (task_id, applicant_id) VALUES (1, 2);

-- 3. Оставление отзыва на задачу
INSERT INTO TaskReviews (task_id, reviewer_id, reviewee_id, rating, comment)
VALUES (1, 2, 1, 4.5, 'Отличная задача, выполнено успешно.');

-- 4. Запрос списка задач с категорией и статусом
SELECT Tasks.title, Tasks.description, TaskCategory.category_name, Tasks.status
FROM Tasks
         JOIN TaskCategory ON Tasks.category_id = TaskCategory.category_id
WHERE Tasks.status = 'open';


-- 6. Запрос списка заявок на конкретную задачу
SELECT Users.username, TaskApplications.applied_at
FROM TaskApplications
         JOIN Users ON TaskApplications.applicant_id = Users.user_id
WHERE TaskApplications.task_id = 1;

-- 7. Запрос отзывов по задачам, где участвует пользователь
SELECT TaskReviews.comment, TaskReviews.rating, Tasks.title
FROM TaskReviews
         JOIN Tasks ON TaskReviews.task_id = Tasks.task_id
WHERE TaskReviews.reviewee_id = 1;
