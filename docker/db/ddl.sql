USE techqbank;

CREATE TABLE users
(
    id         BIGINT       NOT NULL AUTO_INCREMENT,
    email      VARCHAR(255) NOT NULL,
    password   VARCHAR(255) NOT NULL,
    nickname   VARCHAR(100) NOT NULL,
    created_at DATETIME     NOT NULL,
    updated_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_users_email (email)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;


CREATE TABLE tags
(
    id   BIGINT       NOT NULL AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_tags_name (name)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;


CREATE TABLE questions
(
    id           BIGINT       NOT NULL AUTO_INCREMENT,
    author_id    BIGINT       NOT NULL,
    title        VARCHAR(500) NOT NULL,
    description  TEXT,
    career_level VARCHAR(20)  NOT NULL,
    difficulty   VARCHAR(10)  NOT NULL,
    visibility   VARCHAR(10)  NOT NULL,
    my_notes     TEXT,
    key_points   TEXT,
    memo         VARCHAR(500),
    created_at   DATETIME     NOT NULL,
    updated_at   DATETIME     NOT NULL,
    PRIMARY KEY (id),
    KEY idx_questions_author_id (author_id),
    KEY idx_questions_visibility (visibility),
    KEY idx_questions_career_level (career_level),
    KEY idx_questions_difficulty (difficulty)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;


CREATE TABLE question_tags
(
    id          BIGINT NOT NULL AUTO_INCREMENT,
    question_id BIGINT NOT NULL,
    tag_id      BIGINT NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_question_tags (question_id, tag_id)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;


CREATE TABLE user_question_answers
(
    id          BIGINT   NOT NULL AUTO_INCREMENT,
    user_id     BIGINT   NOT NULL,
    question_id BIGINT   NOT NULL,
    content     TEXT     NOT NULL,
    version     INT      NOT NULL DEFAULT 1,
    created_at  DATETIME NOT NULL,
    updated_at  DATETIME NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_user_question_answers (user_id, question_id)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;


CREATE TABLE answer_histories
(
    id         BIGINT   NOT NULL AUTO_INCREMENT,
    answer_id  BIGINT   NOT NULL,
    content    TEXT     NOT NULL,
    version    INT      NOT NULL,
    created_at DATETIME NOT NULL,
    PRIMARY KEY (id),
    KEY idx_answer_histories_answer_id (answer_id)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;


CREATE TABLE user_question_reviews
(
    id          BIGINT      NOT NULL AUTO_INCREMENT,
    user_id     BIGINT      NOT NULL,
    question_id BIGINT      NOT NULL,
    status      VARCHAR(20) NOT NULL,
    created_at  DATETIME    NOT NULL,
    updated_at  DATETIME    NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_user_question_reviews (user_id, question_id)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;


CREATE TABLE review_logs
(
    id         BIGINT      NOT NULL AUTO_INCREMENT,
    review_id  BIGINT      NOT NULL,
    status     VARCHAR(20) NOT NULL,
    created_at DATETIME    NOT NULL,
    PRIMARY KEY (id),
    KEY idx_review_logs_review_id (review_id)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;


CREATE TABLE bookmarks
(
    id          BIGINT   NOT NULL AUTO_INCREMENT,
    user_id     BIGINT   NOT NULL,
    question_id BIGINT   NOT NULL,
    created_at  DATETIME NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_bookmarks (user_id, question_id)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

