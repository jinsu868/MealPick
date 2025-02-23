-- Member 데이터 (member_id=12 포함)
INSERT INTO member (
    member_id, social_id, nickname, profile_image, name,
    dark_mode_check, deleted, notice_check, description, email,
    latitude, longitude, last_notification_time, fcm_token
) VALUES
      (12, 'kakao_12345', 'hong12', 'profile12.jpg', '홍길동',
       b'0', b'0', b'1', '맛집 탐험가입니다', 'hong12@test.com',
       37.498095, 127.027610, NOW(), 'fcm_token_12'),
      (13, 'kakao_13', 'kim13', 'profile13.jpg', '김철수',
       b'0', b'0', b'1', '맛집 좋아합니다', 'kim13@test.com',
       37.504898, 127.048850, NOW(), 'fcm_token_13'),
      (14, 'kakao_14', 'lee14', 'profile14.jpg', '이영희',
       b'0', b'0', b'1', '음식 리뷰어', 'lee14@test.com',
       37.557527, 126.924191, NOW(), 'fcm_token_14');

-- Post 데이터 (member_id=12가 작성한 게시물 포함)
INSERT INTO post (
    author_id, title, content, meal_time, food_tag,
    latitude, longitude, created_at, modified_at
) VALUES
      (12, '강남 맛집 추천', '여기 정말 맛있어요', 'LUNCH', 'KOREAN',
       37.498095, 127.027610, NOW(), NOW()),
      (12, '저녁 양식 맛집', '스테이크가 맛있습니다', 'DINNER', 'WESTERN',
       37.504898, 127.048850, NOW(), NOW()),
      (13, '분식집 발견', '떡볶이 맛집이에요', 'LUNCH', 'BUNSIK',
       37.557527, 126.924191, NOW(), NOW());

-- Comment 데이터 (member_id=12의 댓글 포함)
INSERT INTO comment (
    comment_id, post_id, member_id, parent_id,
    content, deleted, created_at, modified_at
) VALUES
      (1, 1, 12, NULL,
       '맛있어 보이네요', b'0', NOW(), NOW()),
      (2, 1, 13, 1,
       '저도 가봐야겠어요', b'0', NOW(), NOW()),
      (3, 2, 12, NULL,
       '여기 추천합니다', b'0', NOW(), NOW());