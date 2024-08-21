package management.student.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import management.student.data.Student;
import management.student.data.StudentCourse;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;

@MybatisTest
class StudentRepositoryTest {

  @Autowired
  private StudentRepository sut;

  @Test
  void 受講生の全体検索が行えること() {
    List<Student> actual = sut.searchStudentList();
    assertThat(actual.size()).isEqualTo(5);
  }

  @Test
  void 受講生の１件検索が行えること() {
    Optional<Student> actual = sut.searchStudentByID(1);
    assertThat(actual).isPresent();
    assertThat(actual.get().getId()).isEqualTo(1);
  }

  @Test
  void 受講生の登録が行えること() {
    //受講生情報設定
    Student student = new Student();
    student.setGender("male");
    student.setNickname("ニック");
    student.setFurigana("ヨシダ　タカハシ");
    student.setName("Yoshida Takashi");
    student.setEmail("test@sample.com");
    student.setPhoneNumber("09012345678");
    student.setRegion("大阪");
    student.setRemarks("");
    student.setAge(45);
    student.setDeleteFlag(false);
    sut.createStudent(student);

    List<Student> actual = sut.searchStudentList();
    assertThat(actual.size()).isEqualTo(6);
  }


  @Test
  void 受講生の更新が行えること() {
    //受講生情報設定
    Student student = new Student();
    student.setId(1);
    student.setGender("male");
    student.setNickname("ニック");
    student.setFurigana("ヨシダ　タカハシ");
    student.setName("Yoshida Takashi");
    student.setEmail("test@sample.com");
    student.setPhoneNumber("09012345678");
    student.setRegion("大阪");
    student.setRemarks("");
    student.setAge(45);
    student.setDeleteFlag(false);
    sut.updateStudent(student);

    // 取得した受講生情報のフィールドが整合しているか確認
    Optional<Student> actual = sut.searchStudentByID(1);
    assertThat(actual).isPresent();
    assertThat(actual.get())
        .usingRecursiveComparison()
        .isEqualTo(student);

  }

  @Test
  void 受講生が取得出来ない場合は空のリストが返ってくること() {
    Optional<Student> actual = sut.searchStudentByID(999);
    assertThat(actual).isEmpty();

  }

  @Test
  void 受講生が論理削除されている場合は空のリストが返ってくること() {
    // 削除される前に取得できることを確認
    Optional<Student> before = sut.searchStudentByID(1);
    assertThat(before).isPresent();
    assertThat(before.get().getId()).isEqualTo(1);
    Student student = before.get();
    // delete_flagを設定して検索できないことを確認
    student.setDeleteFlag(true);
    // 実行
    sut.updateStudent(student);
    // 検証
    Optional<Student> after = sut.searchStudentByID(1);
    assertThat(after).isEmpty();

  }

  // 以降受講生コースに関するテスト
  @Test
  void 受講生コースの全体検索が行えること() {
    List<StudentCourse> actual = sut.searchStudentCourseList();
    assertThat(actual.size()).isEqualTo(10);
  }

  @Test
  void 受講生コースの登録が行えること() {
    StudentCourse course = new StudentCourse();
    course.setStudentId(1);
    course.setCourseName("Web開発基礎");
    course.setStartDate(LocalDateTime.now());
    course.setEndDate(LocalDateTime.now().plusYears(1));
    //　実行
    sut.createStudentCourse(course);

    // 登録されていることを確認
    List<StudentCourse> actual = sut.searchStudentCourseByID(1);
    assertThat(actual.size()).isEqualTo(3);
    assertThat(actual.getLast().getCourseName()).isEqualTo("Web開発基礎");
  }

  @Test
  void 受講生コースの更新が行えること() {
    List<StudentCourse> courseList = sut.searchStudentCourseByID(1);
    StudentCourse course = courseList.getFirst();
    course.setCourseName("Updated Course");
    // 実行
    sut.updateStudentCourse(course);
    //　値が更新されていることを確認
    List<StudentCourse> actual = sut.searchStudentCourseByID(1);
    assertThat(actual).isNotEmpty();
    assertThat(actual.getFirst().getCourseName()).isEqualTo("Updated Course");
  }

  @Test
  void 受講生コースが存在しない場合の検索結果が空であること() {
    List<StudentCourse> actual = sut.searchStudentCourseByID(999);
    assertThat(actual).isEmpty();
  }

}