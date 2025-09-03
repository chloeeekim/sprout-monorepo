-- pgvector 확장 활성화
CREATE EXTENSION IF NOT EXISTS vector;

-- note_embeddings 테이블 생성
-- note_id를 PK 및 FK로 사용
CREATE TABLE IF NOT EXISTS note_embeddings (
    note_id UUID PRIMARY KEY,
    embedding VECTOR(1536),
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,
    CONSTRAINT fk_note_embeddings_on_note FOREIGN KEY (note_id) REFERENCES notes (id) ON DELETE CASCADE
);

-- embedding 컬럼에 대한 인덱스를 생성하여 벡터 유사도 검색 성능 향상
-- 코사인 유사도로 지정 (vector_cosine_ops)
CREATE INDEX ON note_embeddings USING ivfflat (embedding vector_cosine_ops) WITH (lists = 100);