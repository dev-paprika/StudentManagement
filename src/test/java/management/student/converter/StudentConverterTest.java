package management.student.converter;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import management.student.data.ApplicationStatus;
import management.student.data.Student;
import management.student.data.StudentCourse;
import management.student.domain.StudentDetail;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StudentConverterTest {

  private StudentConverter sut;

  @BeforeEach
  void setUp() {
    sut = new StudentConverter();
  }

  @Test
  void 受講生に紐づく受講生コースが設定されて受講生詳細が作成されること() {

    //事前準備
    List<Student> studentList = new ArrayList<>();
    List<StudentCourse> studentCourseList = new ArrayList<>();
    List<ApplicationStatus> statuses = new ArrayList<>();
    // 受講生と受講生コースの作成
    Student student = createStudent();
    StudentCourse studentCourse = new StudentCourse();
    ApplicationStatus status = new ApplicationStatus();

    studentCourse.setId(1);
    studentCourse.setStudentId(1); //受講生IDと受講生オブジェクトに設定したIDが同じになるようにする
    studentCourse.setCourseName("Javaコース");
    studentCourse.setStartDate(LocalDateTime.now());
    studentCourse.setEndDate(LocalDateTime.now().plusYears(1));
    status.setStudentCourseId(studentCourse.getId());
    status.setId(1);
    studentCourse.setApplicationStatus(status);
    studentList.add(student);
    studentCourseList.add(studentCourse);

    List<Integer> searchIds = List.of(1);
    // 想定されるリストの設定
    List<StudentDetail> expectedList = List.of(new StudentDetail(student, studentCourseList));
    // 実行
    List<StudentDetail> actualList = sut.convertStudentDetails(studentList, studentCourseList,
        searchIds);
    // 検証
    assertThat(expectedList).isEqualTo(actualList);

  }

  @Test
  void 受講生に紐づかない受講生コースは設定されないこと() {

    //事前準備
    List<Student> studentList = new ArrayList<>();
    List<StudentCourse> studentCourseList = new ArrayList<>();
    List<ApplicationStatus> statuses = new ArrayList<>();
    // 受講生と受講生コースの作成
    Student student = createStudent();
    StudentCourse studentCourse = new StudentCourse();
    studentCourse.setId(1);
    studentCourse.setStudentId(2); //受講生IDと受講生オブジェクトに設定したIDが異なるように設定する
    studentCourse.setCourseName("Javaコース");
    studentCourse.setStartDate(LocalDateTime.now());
    studentCourse.setEndDate(LocalDateTime.now().plusYears(1));
    //リストに格納
    studentList.add(student);
    studentCourseList.add(studentCourse);
    // 実行
    List<StudentDetail> actualList = sut.convertStudentDetails(studentList, studentCourseList,
        null);
    // 検証
    assertThat(actualList.get(0).getStudent()).isEqualTo(student);
    assertThat(actualList.get(0).getStudentCourseList()).isEmpty();

  }

  @Test
  void 受講生ＩＤの引数がある場合にそのＩＤに紐づく受講生と受講生コースから受講生詳細ができること() {

    //事前準備
    List<Student> studentList = new ArrayList<>();
    List<StudentCourse> studentCourseList = new ArrayList<>();
    List<ApplicationStatus> statuses = new ArrayList<>();
    // 受講生と受講生コースの作成

    Student student = createStudent();
    Student testStudent = createStudent();
    testStudent.setId(2);

    StudentCourse studentCourse = new StudentCourse();
    studentCourse.setId(1);
    studentCourse.setStudentId(1); //受講生IDと受講生オブジェクトに設定したIDが同じになるように設定する
    studentCourse.setCourseName("Javaコース");
    studentCourse.setStartDate(LocalDateTime.now());
    studentCourse.setEndDate(LocalDateTime.now().plusYears(1));

    StudentCourse testStudentCourse = new StudentCourse();
    testStudentCourse.setId(2);
    testStudentCourse.setStudentId(2); //テスト用受講生IDと受講生オブジェクトに設定したIDが同じになるように設定する
    testStudentCourse.setCourseName("Javaコース");
    testStudentCourse.setStartDate(LocalDateTime.now());
    testStudentCourse.setEndDate(LocalDateTime.now().plusYears(1));
    //リストに格納
    studentList.add(student);
    studentList.add(testStudent);

    studentCourseList.add(studentCourse);

    List<Integer> ids = List.of(1);
    // 実行
    List<StudentDetail> actualList = sut.convertStudentDetails(studentList, studentCourseList,
        ids);
    // 検証
    assertThat(actualList.size()).isEqualTo(1);
    assertThat(actualList.get(0).getStudent()).isEqualTo(student);
    // idsに格納されている受講生が取得されている
    assertThat(actualList.get(0).getStudent().getId()).isEqualTo(1);
  }

  /**
   * テスト用の受講生オブジェyクトの生成と値の設定を行う
   *
   * @return 受講生
   */
  private Student createStudent() {
    Student student = new Student();
    student.setId(1);
    student.setGender("male");
    student.setFurigana("ヨシダ　タカハシ");
    student.setNickname("ニック");
    student.setName("Yoshida Takashi");
    student.setEmail("test@sample.com");
    student.setPhoneNumber("09012345678");
    student.setRegion("大阪");
    student.setRemarks("");
    student.setAge(45);
    return student;
  }

}