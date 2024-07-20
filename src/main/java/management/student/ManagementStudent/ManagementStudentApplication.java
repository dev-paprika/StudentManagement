package management.student.ManagementStudent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class ManagementStudentApplication {

  @Autowired
  //Springが管理しているインスタンスをメンバ変数に入れてくれる（自動で紐づけしますよ。）
  private StudentRepository repository;
  private final Map<String, String> studentMap = new HashMap<>(); //受講生の情報

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
