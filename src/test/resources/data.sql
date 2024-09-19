-- 初期データの挿入
INSERT INTO student (name, furigana, age, gender, nickname, email, region, phone_number, remarks, delete_flag)
VALUES
('佐藤 太郎', 'さとう たろう', 20, 'male', 'たろちゃん', 'taro.sato@example.com', '東京', '09011112222', '優秀な学生', 0),
('鈴木 次郎', 'すずき じろう', 22, 'male', 'じろちゃん', 'jiro.suzuki@example.com', '大阪', '08033334444', 'リーダーシップがある', 0),
('田中 花子', 'たなか はなこ', 19, 'female', 'はなちゃん', 'hanako.tanaka@example.com', '福岡', '07055556666', 'とても真面目', 0),
('山田 三郎', 'やまだ さぶろう', 21, 'male', 'さぶちゃん', 'saburo.yamada@example.com', '北海道', '09077778888', '運動が得意', 0),
('高橋 四郎', 'たかはし しろう', 23, 'male', 'しろちゃん', 'shiro.takahashi@example.com', '沖縄', '08099990000', 'リーダーシップがある', 0);

INSERT INTO student_courses (student_id, course_name, start_date, end_date)
VALUES
(1, 'Javaプログラミング基礎', '2024-08-01 09:00:00', '2024-08-01 10:30:00'),
(1, 'バックエンド開発', '2024-08-02 09:00:00', '2024-08-02 10:30:00'),
(2, 'バックエンド開発', '2024-08-01 11:00:00', '2024-08-01 12:30:00'),
(2, 'データサイエンス入門  ', '2024-08-02 11:00:00', '2024-08-02 12:30:00'),
(3, 'Web開発基礎 ', '2024-08-03 09:00:00', '2024-08-03 10:30:00'),
(3, 'Pythonプログラミング', '2024-08-04 09:00:00', '2024-08-04 10:30:00'),
(4, 'フロントエンド開発 ', '2024-08-05 09:00:00', '2024-08-05 10:30:00'),
(4, 'バックエンド開発', '2024-08-06 09:00:00', '2024-08-06 10:30:00'),
(5, ' Javaプログラミング（フル）', '2024-08-07 09:00:00', '2024-08-07 10:30:00'),
(5, 'Pythonプログラミング ', '2024-08-08 09:00:00', '2024-08-08 10:30:00');



-- application_statuses テーブルへのデータ挿入
INSERT INTO application_status (student_course_id, status) VALUES
(1, '仮申し込み'),
(2, '本申込'),
(3, '仮申し込み'),
(4, '受講中'),
(5, '本申込'),
(6, '仮申し込み'),
(7, '受講中'),
(8, '受講終了'),
(9, '本申込'),
(10, '仮申し込み');
