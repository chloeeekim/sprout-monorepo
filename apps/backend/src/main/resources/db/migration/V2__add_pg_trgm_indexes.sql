-- V2__add_pg_trgm_indexes.sql

-- pg_trgm 확장 활성화
-- 데이터베이스당 한 번만 실행
CREATE EXTENSION IF NOT EXISTS pg_trgm;

-- title 및 content 컬럼에 GIN 인덱스 생성
-- IF NOT EXISTS를 사용하여 이미 인덱스가 존재할 경우 오류 방지
CREATE INDEX IF NOT EXISTS notes_title_trgm_idx ON notes USING GIN (title gin_trgm_ops);
CREATE INDEX IF NOT EXISTS notes_content_trgm_idx ON notes USING GIN (content gin_trgm_ops);