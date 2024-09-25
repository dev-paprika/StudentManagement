package management.student.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import management.student.converter.StudentConverter;
import management.student.data.ApplicationStatus;
import management.student.data.Student;
import management.student.data.StudentCourse;
import management.student.domain.StudentDetail;
import management.student.exception.StudentBizException;
import management.student.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

  @Mock
  private StudentRepository repository;

  @Mock
  private StudentConverter converter;

  private StudentService sut;
  private Student mockStudent;
  private StudentCourse mockCourse;
  private ApplicationStatus mockStatus;

  @BeforeEach
  void before() {
    sut = new StudentService(repository, converter);
    mockStudent = new Student();
    mockStudent.setId(1);
    mockCourse = new StudentCourse();
    mockStatus = new ApplicationStatus();

  }


  @Test
  void 受講生詳細取得時パラメータが全てnullの場合一覧が取得できリポジトリとコンバータが呼び出されていること() {
    //事前準備
    // Mockitoというのを使う
    List<Student> studentList = new ArrayList<>();
    List<StudentCourse> studentCourseList = new ArrayList<>();
    List<ApplicationStatus> statuses = new ArrayList<>();

    when(repository.searchStudentList(null, null, null,
        null, null, null)).thenReturn(studentList);
    when(repository.searchStudentCourseWithStatus(null, null,
        null, null, null)).thenReturn(studentCourseList);
    //実行
    sut.getStudentList(null, null, null, null, null, null, null, null, null, null);
    //検証
    verify(repository, times(1)).searchStudentList(null, null
        , null, null, null, null);
    verify(repository, times(1)).searchStudentCourseWithStatus(null,
        null, null, null, null);
    verify(converter, times(1)).convertStudentDetails(studentList, studentCourseList);

  }

  @Test
  void 受講生詳細取得時パラメータが全て設定されている場合にリポジトリとコンバータからメソッドが呼び出されていること() {
    // 各パラメータの設定
    Integer studentId = 1;
    String name = "佐藤 太郎";
    String kana = "さとう たろう";
    String email = "taro.sato@example.com";
    String phoneNumber = "09011112222";
    Integer age = 20;
    String courseName = "Javaプログラミング基礎";
    LocalDateTime courseStartDate = LocalDateTime.of(2024, 8, 1, 9, 0);
    LocalDateTime courseEndDate = LocalDateTime.of(2024, 8, 1, 10, 30);
    String status = "本申込";

    // モックの設定
    List<Student> studentList = new ArrayList<>();
    List<StudentCourse> studentCourseList = new ArrayList<>();
    when(repository.searchStudentList(studentId, name, kana, email, phoneNumber, age))
        .thenReturn(studentList);
    when(repository.searchStudentCourseWithStatus(studentId, courseName, courseStartDate,
        courseEndDate, status))
        .thenReturn(studentCourseList);

    List<StudentDetail> expectedDetails = new ArrayList<>();
    when(converter.convertStudentDetails(studentList, studentCourseList)).thenReturn(
        expectedDetails);

    // 実行
    List<StudentDetail> result = sut.getStudentList(studentId, name, kana, email, phoneNumber, age,
        courseName, courseStartDate, courseEndDate, status);

    // 検証
    verify(repository, times(1)).searchStudentList(studentId, name, kana, email, phoneNumber, age);
    verify(repository, times(1)).searchStudentCourseWithStatus(studentId, courseName,
        courseStartDate, courseEndDate, status);
    verify(converter, times(1)).convertStudentDetails(studentList, studentCourseList);
  }

  @Test
  void 正しいIDで受講生詳細が取得できること() {
    int testId = 1;
    StudentCourse mockCourse = new StudentCourse();
    mockCourse.setApplicationStatus(new ApplicationStatus());
    List<StudentCourse> mockCourses = List.of(mockCourse);

    when(repository.searchStudentByID(testId)).thenReturn(Optional.of(mockStudent));
    when(repository.searchStudentCourseWithStatus(testId, null, null
        , null, null)).thenReturn(mockCourses);

    //実行
    StudentDetail result = sut.getStudent(testId);
    //検証
    assertNotNull(result);
    // 同じ生徒であることを確認する
    assertEquals(mockStudent, result.getStudent());
    // 必ず１回呼び出されていることを確認する
    verify(repository, times(1)).searchStudentByID(testId);
    verify(repository, times(1)).searchStudentCourseWithStatus(testId, null, null, null, null);
  }

  @Test
  void 受講生の登録が正常に行われリポジトリが呼び出されること() {
    List<StudentCourse> mockCourses = new ArrayList<>();
    StudentDetail studentDetail = new StudentDetail(mockStudent, mockCourses);
    //実行
    sut.register(studentDetail);
    //検証
    verify(repository, times(1)).createStudent(mockStudent);
    verify(repository, times(mockCourses.size())).createStudentCourse(any(StudentCourse.class));
  }

  @Test
  void 受講生情報の更新時に受講生コースが設定されていないときにリポジトリから更新メソッドが呼び出されること() {

    mockStudent.setName("Original Name");

    Student updateStudent = new Student();
    updateStudent.setId(1);
    updateStudent.setName("Updated Name");

    StudentDetail originalDetail = new StudentDetail(mockStudent, new ArrayList<>());
    StudentDetail updateDetail = new StudentDetail(updateStudent, new ArrayList<>());
    //呼び出したときの戻り値が何を返ってくるか指定する
    //こうすることで、実際にDBアクセスをすることなく検証が行える
    // Optionalはnull対応するときに便利
    when(repository.searchStudentByID(1)).thenReturn(Optional.of(mockStudent));

    sut.update(updateDetail);

    verify(repository, times(1)).updateStudent(any(Student.class));
    verify(repository, never()).updateStudentCourse(
        any(StudentCourse.class)); // Assuming no courses to update
  }

  @Test
  void 受講生情報に受講生コースが設定されているときにリポジトリから更新メソッドが呼び出されること() {

    mockStudent.setName("Original Name");
    mockStudent.setId(1);

    Student updateStudent = new Student();
    updateStudent.setId(1);
    updateStudent.setName("Updated Name");
    mockCourse.setStudentId(1);
    mockCourse.setId(1);
    mockStatus.setStudentCourseId(1);
    mockStatus.setId(1);
    mockCourse.setApplicationStatus(mockStatus);
    List<StudentCourse> courseList = new ArrayList<>();
    courseList.add(mockCourse);

    StudentDetail originalDetail = new StudentDetail(mockStudent, courseList);
    StudentDetail updateDetail = new StudentDetail(updateStudent, courseList);
    //呼び出したときの戻り値が何を返ってくるか指定する
    //こうすることで、実際にDBアクセスをすることなく検証が行える
    // Optionalはnull対応するときに便利
    when(repository.searchStudentByID(1)).thenReturn(Optional.of(mockStudent));
    when(repository.searchApplicationStatusByID(1)).thenReturn(Optional.of(mockStatus));

    sut.update(updateDetail);

    verify(repository, times(1)).updateStudent(any(Student.class));
    verify(repository, times(1)).updateStudentCourse(
        any(StudentCourse.class));
    verify(repository, times(1)).updateApplicationStatus(
        any(ApplicationStatus.class));
  }

  @Test
  void 受講生コースに正しいデータが設定されること() {
    StudentCourse mockCourse = new StudentCourse();
    //実行
    sut.initStudentCourses(mockCourse, mockStudent);

    //設定された値が適切かどうかを判定
    assertEquals(mockStudent.getId(), mockCourse.getStudentId());
    assertDatesCloseEnough(LocalDateTime.now(), mockCourse.getStartDate());
    assertDatesCloseEnough(LocalDateTime.now().plusYears(1), mockCourse.getEndDate());
  }

  // LocalDateTimeの比較は、少しの時間差を許容するためのヘルパーメソッド
  private void assertDatesCloseEnough(LocalDateTime expected, LocalDateTime actual) {
    //localDateTimeの時間差を取得する
    long secondsDiff = ChronoUnit.SECONDS.between(expected, actual);
    assertTrue(Math.abs(secondsDiff) < 5,
        "The dates are not close enough: " + secondsDiff + " seconds apart");
  }

  @Test
  void 存在しないIDで受講生詳細の取得時にStudentBizExceptionが発生すること() {
    int testId = 999;
    when(repository.searchStudentByID(testId)).thenReturn(Optional.empty());

    StudentBizException thrown = assertThrows(StudentBizException.class, () -> {
      sut.getStudent(testId);
    });

    // エラーメッセージとステータスコードの確認
    assertEquals("Student with ID " + testId + " not found", thrown.getMessage());
    assertEquals(HttpStatus.NOT_FOUND, thrown.getStatus());
  }

  @Test
  void 申込状況の全件検索が正常に行われリポジトリが呼び出されること() {
    // 準備
    List<ApplicationStatus> mockStatusList = new ArrayList<>();
    when(sut.getApplicationStatusList()).thenReturn(mockStatusList);
    //　実行
    List<ApplicationStatus> actual = sut.getApplicationStatusList();
    // 検証
    assertThat(actual).isEqualTo(mockStatusList);

  }

  @Test
  void 申込状況の１件検索が正常に行われリポジトリが呼び出されること() {
    // 準備
    int testId = 1;
    when(repository.searchApplicationStatusByID(testId)).thenReturn(Optional.of(mockStatus));
    //　実行
    ApplicationStatus actual = sut.getApplicationStatusById(testId);
    // 検証
    assertThat(actual).isEqualTo(mockStatus);

  }

  @Test
  void 存在しないIDで申込状況取得時にStudentBizExceptionが発生すること() {
    int testId = 999;
    when(repository.searchApplicationStatusByID(testId)).thenReturn(Optional.empty());

    StudentBizException thrown = assertThrows(StudentBizException.class, () -> {
      sut.getApplicationStatusById(testId);
    });

    // エラーメッセージとステータスコードの確認
    assertEquals("ApplicationStatus with Course ID " + testId + " not found", thrown.getMessage());
    assertEquals(HttpStatus.NOT_FOUND, thrown.getStatus());

  }


  @Test
  void 申込状況の登録が正常に行われリポジトリが呼び出されること() {
    //　実行
    sut.register(mockStatus);
    // 検証
    verify(repository, times(1)).createApplicationStatus(mockStatus);

  }

  @Test
  void 申込状況の登録時にデータベースアクセスエラーでStudentBizExceptionが発生すること() {
    // モックでリポジトリのメソッドがDataAccessExceptionをスローするように設定
    doThrow(new DataAccessException("Test Exception") {
    }).when(repository).createApplicationStatus(mockStatus);

    // 実行と検証
    StudentBizException thrown = assertThrows(StudentBizException.class, () -> {
      sut.register(mockStatus);
    });

    // エラーメッセージとステータスコードの確認
    assertEquals("DataBaseAccess Error", thrown.getMessage());
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, thrown.getStatus());

    // リポジトリのメソッドが1回だけ呼び出されていることを確認
    verify(repository, times(1)).createApplicationStatus(mockStatus);
  }

  @Test
  void 申込状況の更新が正常に行われリポジトリが呼び出されること() {
    // 準備
    mockStatus.setStatus("本申込");
    mockStatus.setId(1);
    mockStatus.setStudentCourseId(1);
    when(repository.searchApplicationStatusByID(1)).thenReturn(Optional.of(mockStatus));
    //　実行
    sut.update(mockStatus);
    ApplicationStatus actual = sut.getApplicationStatusById(1);

    // 検証
    verify(repository, times(1)).updateApplicationStatus(mockStatus);
    assertThat(actual.getStatus()).isEqualTo("本申込");

  }

  @Test
  void 更新対象の申込状況が存在しない場合にStudentBizExceptionが発生すること() {
    when(repository.searchApplicationStatusByID(anyInt())).thenReturn(
        Optional.empty());
    //実行
    assertThrows(StudentBizException.class, () -> {
      sut.update(mockStatus);
    });
    //検証
    verify(repository, never()).updateApplicationStatus(mockStatus);
  }


  @Test
  void 申込状況の更新時にデータベースアクセスエラーでStudentBizExceptionが発生すること() {
    // モックでリポジトリのメソッドがDataAccessExceptionをスローするように設定
    mockStatus.setStudentCourseId(1);
    mockStatus.setId(1);
    doThrow(new DataAccessException("Test Exception") {
    }).when(repository).updateApplicationStatus(mockStatus);

    Optional<ApplicationStatus> optional = Optional.of(mockStatus);
    when(repository.searchApplicationStatusByID(mockStatus.getId())).thenReturn(
        optional);

    // 実行と検証
    StudentBizException thrown = assertThrows(StudentBizException.class, () -> {
      sut.update(mockStatus);
    });

    // エラーメッセージとステータスコードの確認
    assertEquals("DataBaseAccess Error", thrown.getMessage());
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, thrown.getStatus());

    // リポジトリのメソッドが1回だけ呼び出されていることを確認
    verify(repository, times(1)).updateApplicationStatus(mockStatus);

  }

  @Test
  void 申込状況の削除が正常に行われリポジトリが呼び出されること() {
    mockStatus.setId(1);
    mockStatus.setStudentCourseId(1);
    Optional<ApplicationStatus> optional = Optional.of(mockStatus);
    when(repository.searchApplicationStatusByID(mockStatus.getId())).thenReturn(
        optional);
    //実行
    sut.deleteApplicationStatus(1);
    //検証
    verify(repository, times(1)).deleteApplicationStatus(1);
  }

  @Test
  void 削除対象の申込状況が存在しない場合にStudentBizExceptionが発生すること() {
    when(repository.searchApplicationStatusByID(anyInt())).thenReturn(
        Optional.empty());
    //実行
    assertThrows(StudentBizException.class, () -> {
      sut.deleteApplicationStatus(1);
    });
    //検証
    verify(repository, never()).deleteApplicationStatus(anyInt());
  }

}