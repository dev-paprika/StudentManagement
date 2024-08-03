package management.student.service;

import java.util.List;
import management.student.converter.StudentConverter;
import management.student.data.Student;
import management.student.data.StudentCourses;
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
    List<Student> studentList = this.repository.searchStudents();
    // 受講生コース全件取得
    List<StudentCourses> studentCoursesList = this.repository.searchCourses();
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
    List<StudentCourses> courses = this.repository.searchStudentCourseByID(student.getId());
    return new StudentDetail(student, courses);
  }


  /**
   * 受講生コースの情報を取得
   *
   * @return String 受講生情報
   */
  public List<StudentCourses> getStudentCourseList() {
    return this.repository.searchCourses();
  }

  /**
   * 受講生コースの情報を取得
   *
   * @return String 受講生コース
   */
  public List<StudentCourses> getStudentCourses(int studentId) {
    return this.repository.searchStudentCourseByID(studentId);
  }

  /**
   * 受講生と受講生コース登録
   * 受講生コースには受講生登録の後に受講生IDを設定して登録する
   *
   * @param studentDetail 　受講生詳細
   */
  @Transactional
  public StudentDetail register(StudentDetail studentDetail) {
    //受講生を登録
    Student student = studentDetail.getStudent();
    StudentCourses courses = studentDetail.getStudentCourses().getFirst();
    //@Optionを指定しているので、登録時にstudentにIDが設定される
    resister(student);
    //受講生IDを取得してコース情報に設定してから受講生コース登録
    courses.setStudentId(student.getId());
    resister(courses);
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
    for (StudentCourses courses : studentDetail.getStudentCourses()) {
      //受講生が削除されていないときのみ更新
      if (!studentDetail.getStudent().isDeleteFlag()) {
        update(courses);
      }
    }
  }

  /**
   * 受講生登録
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
  private void resister(StudentCourses courses) {
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
  private void update(StudentCourses courses) {
    this.repository.updateStudentCourses(courses);
  }
}
