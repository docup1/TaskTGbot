-- Определяем типы ENUM для статусов задач, статусов заявок и ролей пользователей.
CREATE TYPE task_status AS ENUM ('open', 'in_progress', 'completed', 'canceled');
CREATE TYPE application_status AS ENUM ('pending', 'approved', 'rejected');
CREATE TYPE user_role AS ENUM ('contractor', 'customer');

-- Таблица Users хранит информацию о пользователях.
CREATE TABLE Users (
                       user_id SERIAL PRIMARY KEY,                       -- Уникальный идентификатор пользователя
                       username VARCHAR(50) UNIQUE NOT NULL,             -- Уникальное имя пользователя
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,   -- Дата и время создания аккаунта
                       bio TEXT,                                         -- Биография пользователя
                       rating NUMERIC(2, 1) DEFAULT 0 CHECK (rating BETWEEN 0 AND 5)  -- Рейтинг пользователя (от 0 до 5)
);

-- Таблица Tasks хранит информацию о задачах, созданных пользователями.
CREATE TABLE Tasks (
                       task_id SERIAL PRIMARY KEY,                       -- Уникальный идентификатор задачи
                       creator_id INT REFERENCES Users(user_id) ON DELETE CASCADE,  -- Идентификатор заказчика, связанный с Users
                       title VARCHAR(100) NOT NULL,                      -- Название задачи
                       description TEXT,                                 -- Описание задачи
                       status task_status NOT NULL DEFAULT 'open',       -- Статус задачи
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,   -- Дата создания задачи
                       due_date TIMESTAMP,                               -- Срок выполнения задачи
                       reward NUMERIC(10, 2) DEFAULT 0                   -- Вознаграждение за выполнение задачи
);

-- Таблица TaskApplications хранит заявки на выполнение задач от исполнителей.
CREATE TABLE TaskApplications (
                                  application_id SERIAL PRIMARY KEY,                -- Уникальный идентификатор заявки
                                  task_id INT REFERENCES Tasks(task_id) ON DELETE CASCADE,      -- Идентификатор задачи, связанный с Tasks
                                  applicant_id INT REFERENCES Users(user_id) ON DELETE CASCADE, -- Идентификатор исполнителя, связанный с Users
                                  status application_status NOT NULL DEFAULT 'pending',         -- Статус заявки
                                  applied_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP     -- Дата подачи заявки
);

-- Таблица TaskReviews хранит отзывы от пользователей.
CREATE TABLE TaskReviews (
                             review_id SERIAL PRIMARY KEY,                     -- Уникальный идентификатор отзыва
                             task_id INT REFERENCES Tasks(task_id) ON DELETE CASCADE,      -- Идентификатор задачи, связанный с Tasks
                             reviewer_id INT REFERENCES Users(user_id) ON DELETE SET NULL, -- Идентификатор того, кто оставил отзыв
                             reviewee_id INT REFERENCES Users(user_id) ON DELETE SET NULL, -- Идентификатор получателя отзыва
                             rating NUMERIC(2, 1) CHECK (rating BETWEEN 1 AND 5),          -- Оценка (от 1 до 5)
                             comment TEXT,                                     -- Текст отзыва
                             created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP    -- Дата и время создания отзыва
);

-- Таблица TaskHistory хранит историю изменений статусов задач.
CREATE TABLE TaskHistory (
                             history_id SERIAL PRIMARY KEY,                    -- Уникальный идентификатор записи истории
                             task_id INT REFERENCES Tasks(task_id) ON DELETE CASCADE,      -- Идентификатор задачи, связанный с Tasks
                             changed_by INT REFERENCES Users(user_id) ON DELETE SET NULL,  -- Идентификатор пользователя, изменившего статус
                             old_status task_status,                           -- Старый статус задачи
                             new_status task_status,                           -- Новый статус задачи
                             changed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP    -- Дата и время изменения статуса
);

-- Триггер 1: Запись истории изменений статусов задач

-- Функция для записи изменений статуса задачи
CREATE OR REPLACE FUNCTION record_task_status_change()
    RETURNS TRIGGER AS $$
BEGIN
    -- Проверка, что статус задачи изменился
    IF OLD.status IS DISTINCT FROM NEW.status THEN
        -- Вставка записи об изменении статуса в таблицу TaskHistory
        INSERT INTO TaskHistory (task_id, changed_by, old_status, new_status)
        VALUES (NEW.task_id, NEW.creator_id, OLD.status, NEW.status);
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Создание триггера для отслеживания изменений статуса задачи
CREATE TRIGGER task_status_change
    AFTER UPDATE OF status ON Tasks
    FOR EACH ROW
    WHEN (OLD.status IS DISTINCT FROM NEW.status)
EXECUTE FUNCTION record_task_status_change();


-- Триггер 2: Обновление среднего рейтинга пользователя

-- Функция для обновления среднего рейтинга пользователя
CREATE OR REPLACE FUNCTION update_user_rating()
    RETURNS TRIGGER AS $$
BEGIN
    -- Обновление рейтинга пользователя, основываясь на среднем значении всех его отзывов
    UPDATE Users
    SET rating = (SELECT AVG(rating) FROM TaskReviews WHERE reviewee_id = NEW.reviewee_id)
    WHERE user_id = NEW.reviewee_id;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Создание триггера для обновления рейтинга после добавления нового отзыва
CREATE TRIGGER update_user_rating_trigger
    AFTER INSERT ON TaskReviews
    FOR EACH ROW
EXECUTE FUNCTION update_user_rating();


-- Триггер 3: Автоматическое обновление статуса задачи при истечении срока выполнения

-- Функция для отмены задач с истекшим сроком выполнения
CREATE OR REPLACE FUNCTION cancel_expired_tasks()
    RETURNS VOID AS $$
BEGIN
    -- Обновление статуса задач на "canceled", если срок истек и статус "open"
    UPDATE Tasks
    SET status = 'canceled'
    WHERE status = 'open' AND due_date < NOW();
END;
$$ LANGUAGE plpgsql;

-- Этот триггер рекомендуется запускать по расписанию (cron-задание).
-- Например, в PostgreSQL с расширением pg_cron:
-- SELECT cron.schedule('0 0 * * *', 'SELECT cancel_expired_tasks()');


-- Триггер 4: Предотвращение дублирования заявок на одну задачу от одного исполнителя

-- Функция для предотвращения дублирования заявок
CREATE OR REPLACE FUNCTION prevent_duplicate_applications()
    RETURNS TRIGGER AS $$
BEGIN
    -- Проверка, подал ли уже пользователь заявку на эту задачу
    IF EXISTS (
        SELECT 1 FROM TaskApplications
        WHERE task_id = NEW.task_id AND applicant_id = NEW.applicant_id
    ) THEN
        -- Сообщение об ошибке при попытке вставить дублирующую заявку
        RAISE EXCEPTION 'Duplicate application: this user has already applied for this task';
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Создание триггера для проверки дублирования заявок перед вставкой
CREATE TRIGGER prevent_duplicate_applications_trigger
    BEFORE INSERT ON TaskApplications
    FOR EACH ROW
EXECUTE FUNCTION prevent_duplicate_applications();


-- Триггер 5: Автоматическое начало выполнения задачи при принятии заявки

-- Функция для автоматического начала задачи при принятии заявки
CREATE OR REPLACE FUNCTION start_task_on_application_approval()
    RETURNS TRIGGER AS $$
BEGIN
    -- Если заявка принята, меняем статус задачи на "in_progress"
    IF NEW.status = 'approved' THEN
        UPDATE Tasks
        SET status = 'in_progress'
        WHERE task_id = NEW.task_id;

        -- Записываем дату принятия заявки, если это поле добавлено
        NEW.applied_at = CURRENT_TIMESTAMP;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Создание триггера для автоматического начала задачи при принятии заявки
CREATE TRIGGER start_task_on_application_approval_trigger
    AFTER UPDATE OF status ON TaskApplications
    FOR EACH ROW
    WHEN (NEW.status = 'approved')
EXECUTE FUNCTION start_task_on_application_approval();


-- Триггер 6: Ограничение отзывов (один отзыв на одну задачу от одного пользователя)

-- Функция для предотвращения дублирования отзывов на задачу
CREATE OR REPLACE FUNCTION prevent_duplicate_reviews()
    RETURNS TRIGGER AS $$
BEGIN
    -- Проверка, оставлял ли пользователь отзыв на эту задачу ранее
    IF EXISTS (
        SELECT 1 FROM TaskReviews
        WHERE task_id = NEW.task_id AND reviewer_id = NEW.reviewer_id
    ) THEN
        -- Ошибка при попытке вставить повторный отзыв на задачу
        RAISE EXCEPTION 'Duplicate review: this user has already reviewed this task';
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Создание триггера для предотвращения дублирования отзывов перед вставкой
CREATE TRIGGER prevent_duplicate_reviews_trigger
    BEFORE INSERT ON TaskReviews
    FOR EACH ROW
EXECUTE FUNCTION prevent_duplicate_reviews();
