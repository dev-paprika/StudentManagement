package management.student.ManagementStudent.controller;

import java.util.List;
import management.student.ManagementStudent.data.Student;
import management.student.ManagementStudent.data.StudentCourses;
import management.student.ManagementStudent.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StudentController {

  private StudentService service;

  @Autowired
  public StudentController(StudentService service) {
    this.service = service;
  }

  /**
   * 受講生の情報を取得
   *
   * @return String 受講生情報
   */
  @GetMapping("/student")
  public List<Student> getStudentList() {

    return this.service.getStudentList();
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
