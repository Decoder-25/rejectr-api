ALTER TABLE shreds
    DROP COLUMN device_id,
    ADD COLUMN device_hash VARCHAR(64);

CREATE INDEX idx_shreds_device_hash ON shreds (device_hash);
CREATE INDEX idx_shreds_created_at ON shreds (created_at);
