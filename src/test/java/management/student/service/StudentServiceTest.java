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
  void 受講生詳細の一覧検索_リポジトリの呼び出しができていてコンバータ呼び出しができていること() {
    //事前準備
    // Mockitoというのを使う
    List<Student> studentList = new ArrayList<>();
    List<StudentCourse> studentCourseList = new ArrayList<>();
    List<ApplicationStatus> statuses = new ArrayList<>();

    when(repository.searchStudentList()).thenReturn(studentList);
    when(repository.searchStudentCourseList()).thenReturn(studentCourseList);
    when(repository.searchApplicationStatusList()).thenReturn(statuses);
    //実行
    sut.getStudentList();
    //検証
    verify(repository, times(1)).searchStudentList();
    verify(repository, times(1)).searchStudentCourseList();
    verify(converter, times(1)).convertStudentDetails(studentList, studentCourseList);

  }

  @Test
  void 受講生詳細の情報取得_正しいIDで受講生が取得できること() {
    int testId = 1;
    StudentCourse mockCourse = new StudentCourse();
    mockCourse.setApplicationStatus(new ApplicationStatus());
    List<StudentCourse> mockCourses = List.of(mockCourse);

    when(repository.searchStudentByID(testId)).thenReturn(Optional.of(mockStudent));
    when(repository.searchStudentCourseWithStatus(testId)).thenReturn(mockCourses);

    //実行
    StudentDetail result = sut.getStudent(testId);
    //検証
    assertNotNull(result);
    // 同じ生徒であることを確認する
    assertEquals(mockStudent, result.getStudent());
    // 必ず１回呼び出されていることを確認する
    verify(repository, times(1)).searchStudentByID(testId);
    verify(repository, times(1)).searchStudentCourseWithStatus(testId);
  }

  @Test
  void 受講生詳細の登録_正常にリポジトリの呼び出しができていること() {
    List<StudentCourse> mockCourses = new ArrayList<>();
    StudentDetail studentDetail = new StudentDetail(mockStudent, mockCourses);
    //実行
    sut.register(studentDetail);
    //検証
    verify(repository, times(1)).createStudent(mockStudent);
    verify(repository, times(mockCourses.size())).createStudentCourse(any(StudentCourse.class));
  }

  @Test
  void 受講生情報の更新_更新処理が正しく行われること() {

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
  void initStudentCourses_受講生コースに正しいデータが設定されること() {
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
  void 受講生詳細の情報取得_存在しないIDで例外が投げられること() {
    int testId = 999;
    when(repository.searchStudentByID(testId)).thenReturn(Optional.empty());

    assertThrows(StudentBizException.class, () -> {
      sut.getStudent(testId);
    });
  }

  @Test
  void 申込状況の全件検索_リポジトリから検索の呼び出しができること() {
    // 準備
    List<ApplicationStatus> mockStatusList = new ArrayList<>();
    when(sut.getApplicationStatusList()).thenReturn(mockStatusList);
    //　実行
    List<ApplicationStatus> actual = sut.getApplicationStatusList();
    // 検証
    assertThat(actual).isEqualTo(mockStatusList);

  }

  @Test
  void 申込状況の１件検索_リポジトリから呼び出しができること() {
    // 準備
    int testId = 1;
    when(repository.searchApplicationStatusByID(testId)).thenReturn(Optional.of(mockStatus));
    //　実行
    ApplicationStatus actual = sut.getApplicationStatusById(testId);
    // 検証
    assertThat(actual).isEqualTo(mockStatus);

  }

  @Test
  void 申込状況の情報取得_存在しないIDで例外が投げられること() {
    int testId = 999;
    when(repository.searchApplicationStatusByID(testId)).thenReturn(Optional.empty());

    assertThrows(StudentBizException.class, () -> {
      sut.getApplicationStatusById(testId);
    });
  }


  @Test
  void 申込状況の登録_リポジトリから登録メソッドを呼び出せること() {
    //　実行
    sut.register(mockStatus);
    // 検証
    verify(repository, times(1)).createApplicationStatus(mockStatus);

  }

  @Test
  void 申込状況の登録_データベースアクセスエラーが発生した場合にStudentBizExceptionがスローされること() {
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
  void 申込状況の更新_リポジトリから更新メソッドを呼び出せること() {
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
  void 申込状況の更新_更新対象がない場合にStudentBizExceptionがスローされること() {
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
  void 申込状況の更新_データベースアクセスエラーが発生した場合にStudentBizExceptionがスローされること() {
    // モックでリポジトリのメソッドがDataAccessExceptionをスローするように設定
    mockStatus.setStudentCourseId(1);
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
  void 申込状況の削除_リポジトリから削除メソッドを呼び出ししていること() {
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
  void 申込状況の削除_削除対象がない場合にStudentBizExceptionがスローされること() {
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