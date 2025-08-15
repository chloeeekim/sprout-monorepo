-- V3__add_folder_feature.sql

-- folders table
CREATE TABLE IF NOT EXISTS folders (
    id UUID PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    owner_id UUID NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,
    FOREIGN KEY (owner_id) REFERENCES users(id)
);

-- notes table에 folder_id 컬럼 추가
ALTER TABLE notes
ADD COLUMN IF NOT EXISTS folder_id UUID;

-- notes table의 folder_id에 외래 키 제약 조건 추가
ALTER TABLE notes
ADD FOREIGN KEY (folder_id) REFERENCES folders(id);