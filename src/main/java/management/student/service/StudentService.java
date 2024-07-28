package management.student.service;

import java.util.List;
import management.student.data.Student;
import management.student.data.StudentCourses;
import management.student.domain.StudentDetail;
import management.student.repository.StudentRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StudentService {

  private StudentRepository repository;

  @Autowired
  //コンストラクタにAutowiredする。フィールドにAutowirdeしない
  public StudentService(StudentRepository repository) {
    this.repository = repository;
  }

  /**
   * 受講生の情報(全件)を取得
   *
   * @return String 受講生情報
   */
  public List<Student> getStudentList() {
    return this.repository.searchStudents();
  }

  /**
   * 受講生の情報(1件)を取得
   *
   * @return String 受講生情報
   */
  public Student getStudent(int id) {
    return this.repository.searchStudentByID(id);
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
   * 受講生とコース登録
   *
   * @param studentDetail 　受講生詳細
   */
  @Transactional
  public void register(StudentDetail studentDetail) {
    //受講生を登録
    Student student = studentDetail.getStudent();
    StudentCourses courses = studentDetail.getStudentCourses().getFirst();
    //@Optionを指定しているので、登録時にstudentにIDが設定される
    resister(student);
    //受講生IDを取得してコース情報に設定してから受講生コース登録
    courses.setStudentId(student.getId());
    resister(courses);
  }

  /**
   * 受講生更新とコースの追加登録
   *
   * @param studentDetail 　受講生詳細
   */
  @Transactional
  public void update(StudentDetail studentDetail, String courseName) {
    //受講生を更新
    Student student = studentDetail.getStudent();
    update(student);
    //コースがあればコースを追加登録する
    if (!StringUtils.isEmpty(courseName)) {
      StudentCourses courses = new StudentCourses();
      courses.setCourseName(courseName);
      //受講生IDを取得してコース情報に設定してから受講生コース登録
      courses.setStudentId(student.getId());
      resister(courses);
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


}
