package management.student.service;

import java.util.List;
import management.student.data.Student;
import management.student.data.StudentCourses;
import management.student.domain.StudentDetail;
import management.student.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    List<Student> students = this.repository.searchStudents();
    return students;
  }

  /**
   * 受講生コースの情報を取得
   *
   * @return String 受講生情報
   */
  public List<StudentCourses> getStudentCourseList() {
    List<StudentCourses> courses = this.repository.searchCourses();
    return courses;
  }

  /**
   * 受講生とコース登録
   *
   * @param studentDetail
   */
  public void registerStudent(StudentDetail studentDetail) {
    //受講生を登録
    Student student = studentDetail.getStudent();
    StudentCourses courses = studentDetail.getStudentCourses().getFirst();
    //@Optionを指定しているので、登録時にstudentにIDが設定される
    this.repository.createStudent(student);
    //受講生IDを取得してコース情報に設定してから受講生コース登録
    courses.setStudentId(student.getId());
    this.repository.createStudentCourse(courses);
  }

}
