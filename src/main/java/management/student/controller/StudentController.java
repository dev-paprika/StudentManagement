package management.student.controller;

import java.util.Arrays;
import java.util.List;
import management.student.converter.StudentConverter;
import management.student.data.StudentCourses;
import management.student.domain.StudentDetail;
import management.student.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 受講生の情報を操作（検索・登録・更新）するREST　APIが実行されるControllerクラス
 */
@RestController
public class StudentController {

  private StudentService service;
  private StudentConverter converter;

  /**
   * 引数２つコンストラクタ
   *
   * @param service   受講生の操作に関わるサービス
   * @param converter 　受講生コースと受講生を受講生詳細にコンバートするためのクラス
   */
  @Autowired
  public StudentController(StudentService service, StudentConverter converter) {
    this.service = service;
    this.converter = converter;
  }

  /**
   * 受講生の情報を取得
   * 全件検索のため条件の指定はなし
   *
   * @return String 受講生一覧（全件）
   */
  @GetMapping("/students")
  public List<StudentDetail> getStudentList() {
    return this.service.getStudentList();
  }


  /**
   * 受講生詳細の情報（1件）を取得
   * 　IDに基づく任意の受講生情報を返します。
   *
   * @return String 受講生情報（１件）
   */
  @GetMapping("/students/{id}")
  public StudentDetail getStudent(@PathVariable String id, Model model) {
    //受講生と受講生コース情報取得
    return service.getStudent(Integer.parseInt(id));
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
  public ResponseEntity<StudentDetail> resisterStudent(@RequestBody StudentDetail studentDetail) {
    //受講生登録のサービスのメソッド呼びだし
    StudentDetail responseStudentDetail = this.service.register(studentDetail);
    return ResponseEntity.ok(responseStudentDetail);
  }

  /**
   * 受講生詳細の情報（1件）を更新
   *
   * @return String 受講生情報
   */
  @PostMapping("/students/update")
  public ResponseEntity<String> updateStudent(@RequestBody StudentDetail studentDetail) {
    //受講生更新のサービスのメソッド呼びだし
    this.service.update(studentDetail);
    // ResponseEntityで何を返すか設定する
    // form-dataだとjsonで送られない
    return ResponseEntity.ok("更新処理が成功しました");

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
