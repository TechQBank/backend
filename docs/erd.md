```dbml
// ──────────────────────────────────────
// 사용자
// ──────────────────────────────────────
Table users {
  id         bigint      [pk, increment]
  email      varchar(255) [unique, not null]
  password   varchar(255) [not null]
  nickname   varchar(100) [not null]
  created_at datetime    [not null]
}

// ──────────────────────────────────────
// 질문 (공유 리소스)
// ──────────────────────────────────────
Table questions {
  id           bigint       [pk, increment]
  author_id    bigint       [not null, ref: > users.id]
  title        varchar(500) [not null]
  description  text
  career_level varchar(20)  [not null, note: 'JUNIOR | YEAR_1_3 | YEAR_3_5 | YEAR_5_PLUS']
  difficulty   varchar(10)  [not null, note: 'EASY | NORMAL | HARD']
  visibility   varchar(10)  [not null, note: 'PUBLIC | PRIVATE']
  created_at   datetime     [not null]
  updated_at   datetime     [not null]
}

// ──────────────────────────────────────
// 태그 (전역)
// ──────────────────────────────────────
Table tags {
  id   bigint      [pk, increment]
  name varchar(100) [unique, not null, note: '정규화 저장 — e.g. spring → Spring']
}

Table question_tags {
  id          bigint [pk, increment]
  question_id bigint [not null, ref: > questions.id]
  tag_id      bigint [not null, ref: > tags.id]

  indexes {
    (question_id, tag_id) [unique]
  }
}

// ──────────────────────────────────────
// 사용자별 학습 데이터
// ──────────────────────────────────────

// 현재 답변 (user × question 당 1개)
Table user_question_answers {
  id          bigint   [pk, increment]
  user_id     bigint   [not null, ref: > users.id]
  question_id bigint   [not null, ref: > questions.id]
  content     text     [not null]
  version     int      [not null, default: 1]
  created_at  datetime [not null]
  updated_at  datetime [not null]

  indexes {
    (user_id, question_id) [unique]
  }
}

// 답변 수정 이력 (version 1부터 누적)
Table answer_histories {
  id        bigint   [pk, increment]
  answer_id bigint   [not null, ref: > user_question_answers.id]
  content   text     [not null]
  version   int      [not null]
  created_at datetime [not null]
}

// 복습 상태 (user × question 당 1개)
Table user_question_reviews {
  id          bigint      [pk, increment]
  user_id     bigint      [not null, ref: > users.id]
  question_id bigint      [not null, ref: > questions.id]
  status      varchar(20) [not null, note: 'UNKNOWN | UNCERTAIN | KNOWN | MASTERED']
  created_at  datetime    [not null]
  updated_at  datetime    [not null]

  indexes {
    (user_id, question_id) [unique]
  }
}

// 복습 상태 변경 이력
Table review_logs {
  id        bigint      [pk, increment]
  review_id bigint      [not null, ref: > user_question_reviews.id]
  status    varchar(20) [not null, note: 'UNKNOWN | UNCERTAIN | KNOWN | MASTERED']
  created_at datetime   [not null]
}

// 북마크 (user × question 당 1개)
Table bookmarks {
  id          bigint   [pk, increment]
  user_id     bigint   [not null, ref: > users.id]
  question_id bigint   [not null, ref: > questions.id]
  created_at  datetime [not null]

  indexes {
    (user_id, question_id) [unique]
  }
}
```