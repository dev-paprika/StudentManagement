package management.student.controller;

import java.util.Arrays;
import java.util.List;
import management.student.converter.StudentConverter;
import management.student.data.Student;
import management.student.data.StudentCourses;
import management.student.domain.StudentDetail;
import management.student.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
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
  @GetMapping("/students")
  public String getStudentList(Model model) {
    List<Student> students = this.service.getStudentList();
    List<StudentCourses> courses = this.service.getStudentCourseList();
    // コンバートしてモデルに設定
    model.addAttribute("studentList", converter.convertStudentDetails(students, courses));
    return "studentList";
  }


  /**
   * 受講生詳細の情報（1件）を取得
   *
   * @return String 受講生情報
   */
  @GetMapping("/students/update")
  public String getStudentList(@RequestParam String id, Model model) {
    //受講生と受講生コース情報取得
    Student student = this.service.getStudent(Integer.parseInt(id));
    List<StudentCourses> courses = this.service.getStudentCourses(student.getId());
    StudentDetail studentDetail = new StudentDetail();
    studentDetail.setStudent(student);
    studentDetail.setStudentCourses(courses);

    // モデルに設定
    model.addAttribute("studentDetail", studentDetail);
    return "updateStudent";
  }


  /**
   * 受講生登録用のページの初期表示
   *
   * @return String 受講生情報
   */
  @GetMapping("/students/new")
  public String resisterStudent(Model model) {
    //オブジェクトは空のものを設定しておかないと画面でエラーになる
    StudentDetail studentDetail = new StudentDetail();
    // 受講生コースは複数なので、初期表示の場合はタグを表示させるために空の受講生コースオブジェクトを設定
    studentDetail.setStudentCourses(Arrays.asList(new StudentCourses()));
    model.addAttribute("studentDetail", studentDetail);
    return "resisterStudent";
  }


  /**
   * 受講生の情報を登録
   *
   * @return String 受講生情報
   */
  @PostMapping("/students")
  public String resisterStudent(@ModelAttribute StudentDetail studentDetail, BindingResult result) {
    if (result.hasErrors()) {
      return "resisterStudent";
    }
    //受講生登録のサービスのメソッド呼びだし
    this.service.register(studentDetail);
    return "redirect:/students";
  }

  /**
   * 受講生詳細の情報（1件）を更新
   *
   * @return String 受講生情報
   */
  @PostMapping("/students/update")
  public String updateStudent(@ModelAttribute StudentDetail studentDetail,
      @RequestParam(required = false) String newCourseName, BindingResult result) {
    //エラーがある場合は返却する
    if (result.hasErrors()) {
      return "updateStudent";
    }
    //受講生更新のサービスのメソッド呼びだし
    this.service.update(studentDetail, newCourseName);
    return "redirect:/students";

  }


  /**
   * 受講生コースの情報を取得
   *
   * @return String 受講生情報
   */
  @GetMapping("/students/course")
  public List<StudentCourses> getStudentCourseList() {
    return this.service.getStudentCourseList();
  }
}
