-- note_links 테이블 생성
CREATE TABLE IF NOT EXISTS note_links (
    id UUID PRIMARY KEY,
    owner_id UUID NOT NULL,
    source_note_id UUID NOT NULL,
    target_note_id UUID NOT NULL,
    label VARCHAR(50),
    direction VARCHAR(20) NOT NULL DEFAULT 'BIDIRECTIONAL',
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,
    FOREIGN KEY (owner_id) REFERENCES users(id),
    FOREIGN KEY (source_note_id) REFERENCES notes(id),
    FOREIGN KEY (target_note_id) REFERENCES notes(id)
)