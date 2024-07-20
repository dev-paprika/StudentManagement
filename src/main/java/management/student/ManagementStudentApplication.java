package management.student;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ManagementStudentApplication {


  public static void main(String[] args) {
    SpringApplication.run(ManagementStudentApplication.class, args);
  }


  /**
   * 受講生の情報を取得
   *
   * @return String 受講生情報
   */
  @GetMapping("/student")
  public List<Student> getStudentList() {
    return this.repository.searchStudents();
  }

  /**
   * 受講生コースの情報を取得
   *
   * @return String 受講生情報
   */
  @GetMapping("/student-course")
  public List<StudentCourses> getStudentCourseList() {
    return this.repository.searchCourses();
  }


}
