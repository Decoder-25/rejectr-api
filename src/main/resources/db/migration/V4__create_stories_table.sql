CREATE TABLE stories (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    stage       TEXT NOT NULL,
    outcome     TEXT NOT NULL,
    body        TEXT NOT NULL,
    status      TEXT NOT NULL DEFAULT 'PUBLISHED',
    created_at  TIMESTAMPTZ NOT NULL DEFAULT now(),
    device_hash VARCHAR(64),
    CONSTRAINT chk_stories_stage CHECK (stage IN (
        'APPLIED_NEVER_HEARD_BACK',
        'RECRUITER_CHAT',
        'SCREENING_CALL',
        'TAKE_HOME_OR_ASSESSMENT',
        'FIRST_INTERVIEW',
        'LATER_ROUNDS_OR_PANEL',
        'FINAL_ROUND',
        'AFTER_AN_OFFER'
    )),
    CONSTRAINT chk_stories_outcome CHECK (outcome IN (
        'GHOSTED',
        'AUTOMATED_EMAIL',
        'PERSONAL_EMAIL_OR_CALL',
        'POSITION_CLOSED',
        'ON_HOLD',
        'OFFER_WITHDRAWN'
    )),
    CONSTRAINT chk_stories_status CHECK (status IN (
        'PENDING',
        'PUBLISHED',
        'REJECTED',
        'HIDDEN'
    )),
    CONSTRAINT chk_stories_body CHECK (
        body ~ '[^[:space:]]' AND char_length(body) <= 2000
    )
);

CREATE INDEX idx_stories_wall ON stories (created_at DESC) WHERE status = 'PUBLISHED';
CREATE INDEX idx_stories_device_hash ON stories (device_hash);
