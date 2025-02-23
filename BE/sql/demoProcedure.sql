DROP PROCEDURE generate_follow_data;

DELIMITER //

CREATE PROCEDURE generate_follow_data(
    IN num_users INT,           -- 생성할 사용자 수
    IN max_follows_per_user INT -- 사용자당 최대 팔로우 수
)
BEGIN
    DECLARE i INT DEFAULT 1;
    DECLARE j INT;
    DECLARE following_user INT;
    DECLARE follower_user INT;
    DECLARE created_at TIMESTAMP;

    -- 각 사용자에 대해
    WHILE i <= num_users DO
            -- 랜덤하게 팔로우 수 결정
            SET j = 1;
            WHILE j <= FLOOR(1 + RAND() * max_follows_per_user) DO
                    -- 랜덤한 사용자 선택 (자기 자신은 제외)
                    SET following_user = FLOOR(1 + RAND() * num_users);
                    IF following_user != i THEN
                        -- 생성 시간을 최근 1년 내로 랜덤하게 설정
                        SET created_at = DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 365) DAY);

                        -- 중복 체크 후 데이터 삽입
                        INSERT IGNORE INTO follow
                        (following_id, follower_id, created_at, modified_at)
                        VALUES
                            (following_user, i, created_at, created_at);
END IF;
                    SET j = j + 1;
END WHILE;
            SET i = i + 1;
END WHILE;

    -- 생성된 데이터 수 출력
SELECT COUNT(*) as generated_follows FROM follow;
END //

DELIMITER ;

-- 프로시저 사용 예시:
CALL generate_follow_data(1000, 50);  -- 1000명의 사용자, 각각 최대 50명까지 팔로우
