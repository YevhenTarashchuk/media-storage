INSERT INTO file_data(id, name, url, file_type, user_id, is_deleted)
VALUES (1, 'data/file/photo.jpeg', 'user/1/image/url', 'IMAGE', 1, false),
       (2, 'audio1.mp3', 'some_url', 'AUDIO', 2, false),
       (3, 'audio2.aac', 'some_url', 'AUDIO', 1, false),
       (4, 'video1.mp4', 'some_url', 'VIDEO', 1, false),
       (5, 'video2.mp4', 'some_url', 'VIDEO', 1, true),
       (6, 'book.pdf', 'some_url', 'OTHER', 1, false),
       (7, 'app.apk', 'some_url', 'OTHER', 1, false);
