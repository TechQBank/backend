DROP TABLE IF EXISTS users;
CREATE TABLE users
(
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    email       VARCHAR(255),
    password    VARCHAR(255),
    nickname    VARCHAR(100) NOT NULL,
    avatar_id   INT          NOT NULL DEFAULT 0,
    provider    VARCHAR(50)  NOT NULL DEFAULT 'OAUTH',
    provider_id VARCHAR(255),
    created_at  DATETIME     NOT NULL,
    updated_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_users_email (email),
    UNIQUE KEY uk_users_provider (provider, provider_id)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;


DROP TABLE IF EXISTS tags;
CREATE TABLE tags
(
    id   BIGINT       NOT NULL AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_tags_name (name)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;


DROP TABLE IF EXISTS questions;
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
    KEY idx_questions_difficulty (difficulty),
    KEY idx_questions_created_at (created_at),
    FULLTEXT KEY idx_questions_fulltext (title, description) WITH PARSER ngram
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;


DROP TABLE IF EXISTS question_tags;
CREATE TABLE question_tags
(
    id          BIGINT NOT NULL AUTO_INCREMENT,
    question_id BIGINT NOT NULL,
    tag_id      BIGINT NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_question_tags (question_id, tag_id),
    KEY idx_question_tags_tag_id (tag_id)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;


DROP TABLE IF EXISTS user_question_answers;
CREATE TABLE user_question_answers
(
    id          BIGINT   NOT NULL AUTO_INCREMENT,
    user_id     BIGINT   NOT NULL,
    question_id BIGINT   NOT NULL,
    content     TEXT     NOT NULL,
    version     INT      NOT NULL DEFAULT 1,
    is_public   BOOLEAN  NOT NULL DEFAULT false,
    created_at  DATETIME NOT NULL,
    updated_at  DATETIME NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_user_question_answers (user_id, question_id),
    KEY idx_user_question_answers_question_id (question_id)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;


DROP TABLE IF EXISTS answer_histories;
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


DROP TABLE IF EXISTS user_question_reviews;
CREATE TABLE user_question_reviews
(
    id          BIGINT      NOT NULL AUTO_INCREMENT,
    user_id     BIGINT      NOT NULL,
    question_id BIGINT      NOT NULL,
    status      VARCHAR(20) NOT NULL,
    created_at  DATETIME    NOT NULL,
    updated_at  DATETIME    NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_user_question_reviews (user_id, question_id),
    KEY idx_user_question_reviews_question_id (question_id)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;


DROP TABLE IF EXISTS review_logs;
CREATE TABLE review_logs
(
    id         BIGINT      NOT NULL AUTO_INCREMENT,
    review_id  BIGINT      NOT NULL,
    status     VARCHAR(20) NOT NULL,
    created_at DATETIME    NOT NULL,
    PRIMARY KEY (id),
    KEY idx_review_logs_review_id (review_id)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;


DROP TABLE IF EXISTS bookmarks;
CREATE TABLE bookmarks
(
    id          BIGINT   NOT NULL AUTO_INCREMENT,
    user_id     BIGINT   NOT NULL,
    question_id BIGINT   NOT NULL,
    created_at  DATETIME NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_bookmarks (user_id, question_id),
    KEY idx_bookmarks_question_id (question_id)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;


DROP TABLE IF EXISTS follows;
CREATE TABLE follows
(
    id          BIGINT   NOT NULL AUTO_INCREMENT,
    follower_id BIGINT   NOT NULL,
    followee_id BIGINT   NOT NULL,
    created_at  DATETIME NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_follows (follower_id, followee_id),
    KEY idx_follows_follower_id (follower_id),
    KEY idx_follows_followee_id (followee_id)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;


DROP TABLE IF EXISTS answer_likes;
CREATE TABLE answer_likes
(
    id         BIGINT   NOT NULL AUTO_INCREMENT,
    answer_id  BIGINT   NOT NULL,
    user_id    BIGINT   NOT NULL,
    created_at DATETIME NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_answer_likes (answer_id, user_id),
    KEY idx_answer_likes_answer_id (answer_id)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;


DROP TABLE IF EXISTS question_groups;
CREATE TABLE question_groups
(
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    user_id     BIGINT       NOT NULL,
    name        VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    is_public   BOOLEAN      NOT NULL DEFAULT false,
    created_at  DATETIME     NOT NULL,
    PRIMARY KEY (id),
    KEY idx_question_groups_user_id (user_id)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;


DROP TABLE IF EXISTS question_group_items;
CREATE TABLE question_group_items
(
    id          BIGINT   NOT NULL AUTO_INCREMENT,
    group_id    BIGINT   NOT NULL,
    question_id BIGINT   NOT NULL,
    added_at    DATETIME NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_question_group_items (group_id, question_id),
    KEY idx_question_group_items_group_id (group_id)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;