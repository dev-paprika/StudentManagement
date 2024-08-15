package management.student.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
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

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

  @Mock
  private StudentRepository repository;

  @Mock
  private StudentConverter converter;

  private StudentService sut;

  @BeforeEach
  void before() {
    sut = new StudentService(repository, converter);
  }


  @Test
  void 受講生詳細の一覧検索_リポジトリの呼び出しができていてコンバータ呼び出しができていること() {
    //事前準備
    // Mockitoというのを使う
    List<Student> studentList = new ArrayList<>();
    List<StudentCourse> studentCourseList = new ArrayList<>();

    when(repository.searchStudentList()).thenReturn(studentList);
    when(repository.searchStudentCourseList()).thenReturn(studentCourseList);
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
    Student mockStudent = new Student();
    mockStudent.setId(testId);
    List<StudentCourse> mockCourses = new ArrayList<>();

    when(repository.searchStudentByID(testId)).thenReturn(Optional.of(mockStudent));
    when(repository.searchStudentCourseByID(testId)).thenReturn(mockCourses);

    //実行
    StudentDetail result = sut.getStudent(testId);
    //検証
    assertNotNull(result);
    assertEquals(mockStudent, result.getStudent());
    assertEquals(mockCourses, result.getStudentCourseList());
    // 必ず１回呼び出されていることを確認する
    verify(repository, times(1)).searchStudentByID(testId);
    verify(repository, times(1)).searchStudentCourseByID(testId);
  }

  @Test
  void 受講生詳細の登録_正常に登録が行われること() {
    Student mockStudent = new Student();
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
    Student originalStudent = new Student();
    originalStudent.setId(1);
    originalStudent.setName("Original Name");

    Student updateStudent = new Student();
    updateStudent.setId(1);
    updateStudent.setName("Updated Name");

    StudentDetail originalDetail = new StudentDetail(originalStudent, new ArrayList<>());
    StudentDetail updateDetail = new StudentDetail(updateStudent, new ArrayList<>());
    //呼び出したときの戻り値が何を返ってくるか指定する
    //こうすることで、実際にDBアクセスをすることなく検証が行える
    // Optionalはnull対応するときに便利
    when(repository.searchStudentByID(1)).thenReturn(Optional.of(originalStudent));

    sut.update(updateDetail);

    verify(repository, times(1)).updateStudent(any(Student.class));
    verify(repository, never()).updateStudentCourse(
        any(StudentCourse.class)); // Assuming no courses to update
  }

  @Test
  void initStudentCourses_受講生コースに正しいデータが設定されること() {
    Student mockStudent = new Student();
    mockStudent.setId(1); // 任意のテスト用ID
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


}