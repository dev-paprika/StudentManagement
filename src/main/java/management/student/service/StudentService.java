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
    //課題：30代の人のみを抽出する
    List<Student> students = this.repository.searchStudents();
    students = students.stream()
        .filter(v -> v.getAge() >= 30 && v.getAge() < 40)
        .toList();
    return students;
  }

  /**
   * 受講生コースの情報を取得
   *
   * @return String 受講生情報
   */
  public List<StudentCourses> getStudentCourseList() {
    //課題：Javaのコースのみ抽出
    List<StudentCourses> courses = this.repository.searchCourses();
    courses = courses.stream()
        .filter(v -> v.getCourseName().matches(".*Java.*"))
        .toList();
    return courses;
  }

}
