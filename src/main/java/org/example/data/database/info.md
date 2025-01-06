### Структура базы данных и комментарии к запросам

```sql
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
    reward NUMERIC(10, 2) DEFAULT 0,                  -- Вознаграждение за выполнение задачи
    msg_id VARCHAR                                     -- ID сообщения в чате с опубликованными задачами
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


### Документация и комментарии к основным функциям и запросам

1. **Создание задачи заказчиком**

   Создает новую задачу, используя `creator_id` (идентификатор заказчика), название, описание, срок выполнения и вознаграждение.
   ```sql
   INSERT INTO Tasks (creator_id, title, description, due_date, reward)
   VALUES (?, ?, ?, ?, ?);
   ```

2. **Поиск задачи исполнителем**

   Находит все доступные задачи, статус которых — 'open', срок выполнения больше или равен текущей дате, а вознаграждение превышает указанный минимум.
   ```sql
   SELECT * FROM Tasks
   WHERE status = 'open' AND due_date >= NOW() AND reward >= ?;
   ```

3. **Создание заявки на выполнение задачи исполнителем**

   Вставляет новую заявку от исполнителя на выполнение задачи. В `task_id` указывается идентификатор задачи, `applicant_id` — идентификатор исполнителя.
   ```sql
   INSERT INTO TaskApplications (task_id, applicant_id)
   VALUES (?, ?);
   ```

4. **Просмотр заявок заказчиком**

   Извлекает информацию о заявках на выполнение задачи, включая идентификатор заявки, исполнителя, рейтинг и статус заявки, используя `task_id`.
   ```sql
   SELECT ta.application_id, ta.applicant_id, u.username, u.rating, ta.status
   FROM TaskApplications AS ta
   JOIN Users AS u ON ta.applicant_id = u.user_id
   WHERE ta.task_id = ?;
   ```

5. **Выбор исполнителя заказчиком и обновление статуса**

   Обновляет статус выбранной заявки на 'approved', все остальные заявки отклоняются. Статус задачи меняется на 'in_progress'.
   ```sql
   -- Обновление статуса заявки
   UPDATE TaskApplications
   SET status = 'approved'
   WHERE application_id = ?;

   -- Обновление статуса остальных заявок
   UPDATE TaskApplications
   SET status = 'rejected'
   WHERE task_id = ?
     AND application_id != ?;

   -- Обновление статуса задачи
   UPDATE Tasks
   SET status = 'in_progress'
   WHERE task_id = ?;
   ```

6. **Регистрация завершения задачи**

   Устанавливает статус задачи на 'completed' после завершения.
   ```sql
   UPDATE Tasks
   SET status = 'completed'
   WHERE task_id = ?;
   ```

7. **Оставление отзыва заказчиком**

   Вставляет отзыв на исполнителя, указывая идентификаторы задачи, заказчика, исполнителя, рейтинг и комментарий.
   ```sql
   INSERT INTO TaskReviews (task_id, reviewer_id, reviewee_id, rating, comment)
   VALUES (?, ?, ?, ?, ?);
   ```

8. **Обновление рейтинга пользователя на основе отзывов**

   Использует подзапрос для вычисления среднего рейтинга и обновления его в таблице `Users`.
   ```sql
   WITH AvgRating AS (
       SELECT reviewee_id, AVG(rating) AS avg_rating
       FROM TaskReviews
       GROUP BY reviewee_id
   )
   UPDATE Users
   SET rating = (SELECT avg_rating FROM AvgRating WHERE reviewee_id = user_id)
   WHERE user_id = ?;
   ```

9. **Запрос истории изменений статусов задачи**

   Извлекает историю изменений статусов для задачи, используя `task_id`, отсортированную по времени.
   ```sql
   SELECT old_status, new_status, changed_at
   FROM TaskHistory
   WHERE task_id = ?
   ORDER BY changed_at DESC;
   ```
### Триггеры для базы данных
- **Триггер 1** : Записывает в таблицу TaskHistory изменения статусов задач.
- **Триггер 2** : Автоматически обновляет средний рейтинг пользователя на основе всех отзывов, оставленных на него.
- **Триггер 3** : Обновляет статус задач на canceled при истечении срока выполнения.
- **Триггер 4** : Не позволяет одному исполнителю подавать более одной заявки на одну задачу.
- **Триггер 5** : Меняет статус задачи на in_progress, когда заявка исполнителя принята.
- **Триггер 6** : Предотвращает оставление повторных отзывов на одну задачу от одного и того же пользователя.
1. **Триггер для записи истории изменений статусов задач**

   Этот триггер автоматически добавляет запись в таблицу `TaskHistory` всякий раз, когда изменяется статус задачи в таблице `Tasks`.

   ```sql
   CREATE OR REPLACE FUNCTION record_task_status_change()
   RETURNS TRIGGER AS $$
   BEGIN
       IF OLD.status IS DISTINCT FROM NEW.status THEN
           INSERT INTO TaskHistory (task_id, changed_by, old_status, new_status)
           VALUES (NEW.task_id, NEW.creator_id, OLD.status, NEW.status);
       END IF;
       RETURN NEW;
   END;
   $$ LANGUAGE plpgsql;

   CREATE TRIGGER task_status_change
   AFTER UPDATE OF status ON Tasks
   FOR EACH ROW
   WHEN (OLD.status IS DISTINCT FROM NEW.status)
   EXECUTE FUNCTION record_task_status_change();
   ```

2. **Триггер для обновления среднего рейтинга пользователя**

   Этот триггер обновляет рейтинг пользователя в таблице `Users` каждый раз, когда добавляется новый отзыв в таблице `TaskReviews`.

   ```sql
   CREATE OR REPLACE FUNCTION update_user_rating()
   RETURNS TRIGGER AS $$
   BEGIN
       UPDATE Users
       SET rating = (SELECT AVG(rating) FROM TaskReviews WHERE reviewee_id = NEW.reviewee_id)
       WHERE user_id = NEW.reviewee_id;
       RETURN NEW;
   END;
   $$ LANGUAGE plpgsql;

   CREATE TRIGGER update_user_rating_trigger
   AFTER INSERT ON TaskReviews
   FOR EACH ROW
   EXECUTE FUNCTION update_user_rating();
   ```

3. **Триггер на автоматическое обновление статуса задачи при истечении срока выполнения**

   Этот триггер проверяет, истек ли срок выполнения задачи, и автоматически переводит её в статус `canceled`, если она не была выполнена вовремя. Такой триггер можно запустить по расписанию (через cron-работу или планировщик задач, например, в PostgreSQL — `pg_cron`), чтобы не перегружать базу.

   ```sql
   CREATE OR REPLACE FUNCTION cancel_expired_tasks()
   RETURNS VOID AS $$
   BEGIN
       UPDATE Tasks
       SET status = 'canceled'
       WHERE status = 'open' AND due_date < NOW();
   END;
   $$ LANGUAGE plpgsql;

   -- Пример создания задания, которое выполняет функцию каждые 24 часа:
   -- В PostgreSQL можно использовать расширение pg_cron.
   -- SELECT cron.schedule('0 0 * * *', 'SELECT cancel_expired_tasks()');
   ```

4. **Триггер для предотвращения дублирования заявок на одну задачу от одного исполнителя**

   Этот триггер отклоняет вставку новой заявки, если исполнитель уже подал заявку на выполнение этой задачи.

   ```sql
   CREATE OR REPLACE FUNCTION prevent_duplicate_applications()
   RETURNS TRIGGER AS $$
   BEGIN
       IF EXISTS (
           SELECT 1 FROM TaskApplications
           WHERE task_id = NEW.task_id AND applicant_id = NEW.applicant_id
       ) THEN
           RAISE EXCEPTION 'Duplicate application: this user has already applied for this task';
       END IF;
       RETURN NEW;
   END;
   $$ LANGUAGE plpgsql;

   CREATE TRIGGER prevent_duplicate_applications_trigger
   BEFORE INSERT ON TaskApplications
   FOR EACH ROW
   EXECUTE FUNCTION prevent_duplicate_applications();
   ```

5. **Триггер для установки даты и времени принятия заявки и начала задачи**

   Когда заявка на задачу принимается (меняется статус на `approved`), этот триггер автоматически устанавливает статус задачи в `in_progress` и записывает в `accepted_at` дату принятия заявки, что может пригодиться для учета сроков выполнения.

   ```sql
   CREATE OR REPLACE FUNCTION start_task_on_application_approval()
   RETURNS TRIGGER AS $$
   BEGIN
       IF NEW.status = 'approved' THEN
           UPDATE Tasks
           SET status = 'in_progress'
           WHERE task_id = NEW.task_id;

           -- Можно добавить поле accepted_at в TaskApplications, если оно необходимо
           NEW.applied_at = CURRENT_TIMESTAMP;
       END IF;
       RETURN NEW;
   END;
   $$ LANGUAGE plpgsql;

   CREATE TRIGGER start_task_on_application_approval_trigger
   AFTER UPDATE OF status ON TaskApplications
   FOR EACH ROW
   WHEN (NEW.status = 'approved')
   EXECUTE FUNCTION start_task_on_application_approval();
   ```

6. **Триггер для ограничения отзывов (один отзыв на одну задачу от одного пользователя)**

   Чтобы избежать дублирующих отзывов, можно добавить триггер, который проверяет, был ли уже оставлен отзыв на определенную задачу от данного пользователя.

   ```sql
   CREATE OR REPLACE FUNCTION prevent_duplicate_reviews()
   RETURNS TRIGGER AS $$
   BEGIN
       IF EXISTS (
           SELECT 1 FROM TaskReviews
           WHERE task_id = NEW.task_id AND reviewer_id = NEW.reviewer_id
       ) THEN
           RAISE EXCEPTION 'Duplicate review: this user has already reviewed this task';
       END IF;
       RETURN NEW;
   END;
   $$ LANGUAGE plpgsql;

   CREATE TRIGGER prevent_duplicate_reviews_trigger
   BEFORE INSERT ON TaskReviews
   FOR EACH ROW
   EXECUTE FUNCTION prevent_duplicate_reviews();
   ```

