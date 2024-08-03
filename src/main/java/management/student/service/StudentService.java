package management.student.service;

import java.time.LocalDateTime;
import java.util.List;
import management.student.converter.StudentConverter;
import management.student.data.Student;
import management.student.data.StudentCourse;
import management.student.domain.StudentDetail;
import management.student.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 受講生情報を取り扱うサービス
 * 受講生情報の検索、更新、登録処理を行う
 */
@Service
public class StudentService {

  private StudentRepository repository;
  private StudentConverter converter;

  @Autowired
  //コンストラクタにAutowiredする。フィールドにAutowirdeしない
  public StudentService(StudentRepository repository, StudentConverter converter) {
    this.repository = repository;
    this.converter = converter;
  }

  /**
   * 受講生の情報を取得
   * 全件検索のため条件の指定はなし
   *
   * @return String 受講生情報
   */
  public List<StudentDetail> getStudentList() {
    //受講生全件取得
    List<Student> studentList = this.repository.searchStudentList();
    // 受講生コース全件取得
    List<StudentCourse> studentCoursesList = this.repository.searchStudentCourseList();
    //コンバータークラスで欲しい情報に変換
    return this.converter.convertStudentDetails(studentList, studentCoursesList);
  }

  /**
   * 受講生詳細の情報（1件）を取得
   * 　IDに基づく任意の受講生情報を取得したあと、その受講生に紐づく受講生コースを取得し、
   * 　受講生詳細に設定します。
   *
   * @param id 受講生ID
   * @return String 受講生情報
   */
  public StudentDetail getStudent(int id) {
    Student student = this.repository.searchStudentByID(id);
    List<StudentCourse> courses = this.repository.searchStudentCourseByID(student.getId());
    return new StudentDetail(student, courses);
  }


  /**
   * 受講生コースの情報を取得
   *
   * @return String 受講生情報
   */
  public List<StudentCourse> getStudentCourseList() {
    return this.repository.searchStudentCourseList();
  }

  /**
   * 受講生コースの情報を取得
   *
   * @return String 受講生コース
   */
  public List<StudentCourse> getStudentCourses(int studentId) {
    return this.repository.searchStudentCourseByID(studentId);
  }

  /**
   * 受講生詳細の登録
   * 受講生と受講生コースをそれぞれ登録する
   * 受講生コースには受講生登録の後に紐づく受講生ID、コース開始日、コース終了日を設定して登録する
   *
   * @param studentDetail 　受講生詳細
   */
  @Transactional
  public StudentDetail register(StudentDetail studentDetail) {
    //受講生を登録
    Student student = studentDetail.getStudent();
    //@Optionを指定しているので、登録時にstudentにIDが設定される
    resister(student);
    List<StudentCourse> courses = studentDetail.getStudentCourseList();
    //受講生コースのループを回して受講生コースに初期値を設定
    courses.forEach(studentCourses -> {
      initStudentCourses(studentCourses, student);
      //受講生コース登録
      resister(studentCourses);
    });
    return studentDetail;
  }


  /**
   * 受講生と受講生コース更新
   *
   * @param studentDetail 　受講生詳細
   */
  @Transactional
  public void update(StudentDetail studentDetail) {
    //受講生を更新
    update(studentDetail.getStudent());
    //コースを更新する
    //受講生が削除されていないときのみ更新
    studentDetail.getStudentCourseList().stream()
        .filter(courses -> !studentDetail.getStudent().isDeleteFlag()).forEach(this::update);
  }

  /**
   * 受講生コースに紐づく受講生ID,コース開始日、コース終了日を設定する
   *
   * @param courses 　受講生コース
   * @param student 　受講生
   */
  private static void initStudentCourses(StudentCourse courses, Student student) {
    courses.setStudentId(student.getId());
    // startDateは現在の日付、endDateは１年後を設定
    LocalDateTime today = LocalDateTime.now();
    courses.setStartDate(today);
    courses.setEndDate(today.plusYears(1)); // 1年後の日付を設定
  }

  /**
   * 受講生登録
   * 　IDは自動採番
   *
   * @param student 　受講生
   */
  private void resister(Student student) {
    this.repository.createStudent(student);
  }

  /**
   * 受講生コース登録
   *
   * @param courses 受講生コース
   */
  private void resister(StudentCourse courses) {
    this.repository.createStudentCourse(courses);
  }

  /**
   * 受講生更新
   *
   * @param student 　受講生
   */
  private void update(Student student) {
    this.repository.updateStudent(student);
  }

  /**
   * 受講生コース更新
   *
   * @param courses 　受講生コース
   */
  private void update(StudentCourse courses) {
    this.repository.updateStudentCourse(courses);
  }
}
