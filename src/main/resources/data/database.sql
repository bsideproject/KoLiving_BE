CREATE TABLE IF NOT EXISTS LANGUAGES (
    ID               INTEGER        AUTO_INCREMENT PRIMARY KEY,
    LOCALE           VARCHAR(5)     NOT NULL,
    MESSAGE_KEY      VARCHAR(255)   NOT NULL,
    MESSAGE_CONTENT  VARCHAR(255)
);

CREATE INDEX IF NOT EXISTS IDX_LANGUAGES ON LANGUAGES (LOCALE, MESSAGE_KEY);
