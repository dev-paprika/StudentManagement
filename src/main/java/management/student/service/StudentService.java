package management.student.service;

import java.util.List;
import management.student.data.Student;
import management.student.data.StudentCourses;
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
   * 受講生の情報を取得
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

}
