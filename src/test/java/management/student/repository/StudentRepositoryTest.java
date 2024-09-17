package management.student.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import management.student.data.ApplicationStatus;
import management.student.data.Student;
import management.student.data.StudentCourse;
import org.h2.jdbc.JdbcSQLIntegrityConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

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

  @Test
  void Null値を持つ受講生を登録しようとするとエラーが発生すること() {
    Student student = new Student();
    student.setGender(null); // NULL 値を設定
    student.setNickname("ニック");
    student.setFurigana("ヨシダ　タカハシ");
    student.setName("Yoshida Takashi");
    student.setEmail("unique@example.com");
    student.setPhoneNumber("09012345678");
    student.setRegion("大阪");
    student.setRemarks("");
    student.setAge(45);
    student.setDeleteFlag(false);

    // Exceptionが発生することを期待
    assertThatThrownBy(() -> sut.createStudent(student))
        .isInstanceOf(DataIntegrityViolationException.class)
        .hasCauseInstanceOf(JdbcSQLIntegrityConstraintViolationException.class);

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

  @Test
  void 申込状況の全件検索が行えること() {
    List<ApplicationStatus> actual = sut.searchApplicationStatusList();
    assertThat(actual.size()).isEqualTo(10);
  }

  @Test
  void 申込状況の1件検索が行えること() {
    Optional<ApplicationStatus> actual = sut.searchApplicationStatusByID(1);
    assertThat(actual).isNotNull();
  }

  @Test
  void 対象の申込状況が存在しない場合は空の値が返ってくること() {
    Optional<ApplicationStatus> actual = sut.searchApplicationStatusByID(999);
    assertThat(actual).isEmpty();
  }

  @Test
  void 申込状況と紐づく受講生コースが受講生ＩＤを指定せず検索できること() {
    List<StudentCourse> actual = sut.searchStudentCourseWithStatus(null);
    assertThat(actual.size()).isEqualTo(10);
    assertThat(actual.getFirst().getApplicationStatus()).isNotNull();
  }

  @Test
  void 申込状況と紐づく受講生コースが受講生ＩＤを指定して検索できること() {
    List<StudentCourse> actual = sut.searchStudentCourseWithStatus(1);
    assertThat(actual.size()).isEqualTo(2);
    assertThat(actual.getFirst().getApplicationStatus()).isNotNull();
    assertThat(actual.getLast().getApplicationStatus()).isNotNull();

  }

  @Test
  void 申込状況が登録できること() {
    // 対応するコースを登録する
    StudentCourse course = new StudentCourse();
    course.setStudentId(1);
    course.setCourseName("Web開発基礎");
    course.setStartDate(LocalDateTime.now());
    course.setEndDate(LocalDateTime.now().plusYears(1));
    sut.createStudentCourse(course);

    // 申込状況準備
    ApplicationStatus applicationStatus = new ApplicationStatus();
    applicationStatus.setStudentCourseId(course.getId());
    applicationStatus.setStatus("受講中");
    //　実行
    sut.createApplicationStatus(applicationStatus);
    // 申込状況が正しく登録できているか検証
    Optional<ApplicationStatus> actual = sut.searchApplicationStatusByID(course.getId());
    assertThat(actual).isNotNull();
    assertThat(actual.get().getStatus()).isEqualTo("受講中");
  }

  @Test
  void 異常な申込状況が登録できないこと() {

    // 申込状況準備
    ApplicationStatus applicationStatus = new ApplicationStatus();
    applicationStatus.setStudentCourseId(999); //存在しないID
    applicationStatus.setStatus("受講中");
    // 申込状況が例外が出ることを確認
    assertThatThrownBy(() -> sut.createApplicationStatus(applicationStatus))
        .isInstanceOf(DataIntegrityViolationException.class)
        .hasCauseInstanceOf(JdbcSQLIntegrityConstraintViolationException.class);
  }


  @Test
  void 申込状況が更新できること() {
    // 申込状況準備

    ApplicationStatus applicationStatus = new ApplicationStatus();
    applicationStatus.setId(1);
    applicationStatus.setStudentCourseId(1);
    applicationStatus.setStatus("本申込");

    //　実行
    sut.updateApplicationStatus(applicationStatus);
    // 申込状況が正しく登録できているか検証
    Optional<ApplicationStatus> actualOptional = sut.searchApplicationStatusByID(
        applicationStatus.getStudentCourseId());
    assertThat(actualOptional).isNotNull();
    ApplicationStatus actual = actualOptional.get();
    assertThat(actual)
        .usingRecursiveComparison()
        .isEqualTo(applicationStatus);
  }

  @Test
  void 申込状況が削除できること() {
    // 削除前のリスト取得
    List<ApplicationStatus> applicationStatusList = sut.searchApplicationStatusList();

    //　削除実行
    sut.deleteApplicationStatus(10);
    // 削除されているか検証
    List<ApplicationStatus> afterList = sut.searchApplicationStatusList();
    assertThat(afterList.size()).isEqualTo(applicationStatusList.size() - 1);
  }


}