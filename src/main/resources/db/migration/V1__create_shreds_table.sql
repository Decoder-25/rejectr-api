CREATE TABLE shreds (
    id      UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    stage   TEXT NOT NULL,
    outcome TEXT NOT NULL,
    reason  TEXT NOT NULL DEFAULT 'NO_REASON_GIVEN',
    CONSTRAINT chk_shreds_stage CHECK (stage IN (
        'APPLIED_NEVER_HEARD_BACK',
        'RECRUITER_CHAT',
        'SCREENING_CALL',
        'TAKE_HOME_OR_ASSESSMENT',
        'FIRST_INTERVIEW',
        'LATER_ROUNDS_OR_PANEL',
        'FINAL_ROUND',
        'AFTER_AN_OFFER'
    )),
    CONSTRAINT chk_shreds_outcome CHECK (outcome IN (
        'GHOSTED',
        'AUTOMATED_EMAIL',
        'PERSONAL_EMAIL_OR_CALL',
        'POSITION_CLOSED',
        'ON_HOLD',
        'OFFER_WITHDRAWN'
    )),
    CONSTRAINT chk_shreds_reason CHECK (reason IN (
        'NO_REASON_GIVEN',
        'VISA_OR_SPONSORSHIP',
        'ANOTHER_CANDIDATE_BETTER_FIT',
        'OVERQUALIFIED',
        'EXPERIENCE_GAP',
        'SALARY',
        'LOCATION_OR_RELOCATION',
        'HIRING_FREEZE_OR_ROLE_CHANGED',
        'HIRED_INTERNALLY',
        'VAGUE_NOT_A_FIT'
    ))
);
