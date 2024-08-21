-- studentsテーブルが存在しない場合に作成
CREATE TABLE IF NOT EXISTS student (
    id INT NOT NULL AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    furigana VARCHAR(50) NOT NULL,
    age INT DEFAULT NULL,
    gender VARCHAR(20) NOT NULL,
    nickname VARCHAR(50) DEFAULT NULL,
    email VARCHAR(100) NOT NULL,
    region VARCHAR(100) DEFAULT NULL,
    phone_number VARCHAR(15) DEFAULT NULL,
    remarks VARCHAR(255) DEFAULT NULL,
    delete_flag INT DEFAULT 0,
    PRIMARY KEY (id),
    UNIQUE (email)
);

-- student_coursesテーブルが存在しない場合に作成
-- MySQLとh2では使用できる構文に少し違いがるので注意
CREATE TABLE IF NOT EXISTS student_courses (
    id INT NOT NULL AUTO_INCREMENT,
    student_id INT NOT NULL,
    course_name VARCHAR(50) NOT NULL,
    start_date TIMESTAMP NOT NULL,
    end_date TIMESTAMP NULL DEFAULT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (student_id) REFERENCES student(id) ON DELETE CASCADE
);