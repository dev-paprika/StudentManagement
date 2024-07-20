package management.student.controller;

import java.util.List;
import management.student.converter.StudentConverter;
import management.student.data.Student;
import management.student.data.StudentCourses;
import management.student.domain.StudentDetail;
import management.student.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StudentController {

  private StudentService service;
  private StudentConverter converter;

  @Autowired
  public StudentController(StudentService service, StudentConverter converter) {
    this.service = service;
    this.converter = converter;
  }

  /**
   * 受講生の情報を取得
   *
   * @return String 受講生情報
   */
  @GetMapping("/student")
  public List<StudentDetail> getStudentList() {
    List<Student> students = this.service.getStudentList();
    List<StudentCourses> courses = this.service.getStudentCourseList();
    // コンバートしたものを取得する
    return converter.convertStudentDetails(students, courses);
  }


  /**
   * 受講生コースの情報を取得
   *
   * @return String 受講生情報
   */
  @GetMapping("/student-course")
  public List<StudentCourses> getStudentCourseList() {
    return this.service.getStudentCourseList();
  }
}
