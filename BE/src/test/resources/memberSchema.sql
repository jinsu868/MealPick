
CREATE TABLE IF NOT EXISTS member (
                                      member_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                      name VARCHAR(255),
    profile_image VARCHAR(255),
    nickname VARCHAR(255) NOT NULL,
    social_id VARCHAR(255),
    password VARCHAR(255),
    email VARCHAR(255),
    description TEXT,
    notice_check BOOLEAN,
    dark_mode_check BOOLEAN,
    deleted BOOLEAN DEFAULT FALSE,
    latitude DECIMAL(10,7),
    longitude DECIMAL(10,7),
    last_notification_time TIMESTAMP,
    CONSTRAINT uk_member_social_id UNIQUE (social_id)
    );