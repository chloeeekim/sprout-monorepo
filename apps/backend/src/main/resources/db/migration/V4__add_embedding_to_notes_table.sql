-- pgvector 확장 활성화
CREATE EXTENSION IF NOT EXISTS vector;

-- notes 테이블에 embedding 컬럼 추가
-- OpenAI의 text-embedding-ada-002 모델이 사용하는 1536차원 벡터 저장
ALTER TABLE notes ADD COLUMN embedding vector(1536);

-- notes 테이블에 embedding_updated_at 컬럼 추가
-- 임베딩이 마지막으로 업데이트된 시간 저장
ALTER TABLE notes ADD COLUMN embedding_updated_at TIMESTAMPTZ;

-- embedding 컬럼에 대한 인덱스를 생성하여 벡터 유사도 검색 성능 향상
CREATE INDEX ON notes USING ivfflat (embedding vector_l2_ops) WITH (lists = 100);