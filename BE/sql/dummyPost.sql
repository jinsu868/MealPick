INSERT INTO post (
    latitude,
    longitude,
    author_id,
    created_at,
    modified_at,
    content,
    title,
    food_tag,
    meal_time
) VALUES (
             37.5665, -- 예시 위도
             126.9780, -- 예시 경도
             1, -- author_id
             NOW(), -- created_at
             NOW(), -- modified_at
             '맛있는 식당 방문', -- content
             '오늘의 점심', -- title
             'KOREAN', -- food_tag
             'LUNCH' -- meal_time
         );
INSERT INTO tag (
    created_at,
    modified_at,
    tag_name
) VALUES (
             NOW(), -- created_at
             NOW(), -- modified_at
             '맛집' -- tag_name
         );

-- 4. 게시글-태그 연결
INSERT INTO post_tag (tag_id,created_at, modified_at)
VALUES
(1, NOW(), NOW()),
(2, NOW(), NOW()),
(1, NOW(), NOW());

-- 5. 게시글 이미지
INSERT INTO post_image (file_name)
VALUES
( 'image1.jpg'),
( 'image2.jpg'),
( 'image3.jpg');

-- 6. 스크랩 데이터
INSERT INTO scrab (post_id)
VALUES ( 2);

-- 7. 좋아요 데이터
INSERT INTO post_like (member_id, post_id)
VALUES (1, 1);

-- 8. 댓글 데이터
INSERT INTO comment (post_id, member_id, content, created_at)
VALUES
(1, 1, '댓글1', NOW()),
(1, 1, '댓글2', NOW());