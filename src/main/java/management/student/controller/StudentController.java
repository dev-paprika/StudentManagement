package management.student.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.util.List;
import management.student.converter.StudentConverter;
import management.student.domain.StudentDetail;
import management.student.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 受講生の情報を操作（検索・登録・更新）するREST　APIが実行されるControllerクラス
 */
@RestController
@Validated
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
   * 受講生詳細の情報を取得
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
  public StudentDetail getStudent(@PathVariable @Valid @Min(1) @Max(999) String id) {
    //受講生と受講生コース情報取得
    return service.getStudent(Integer.parseInt(id));
  }


  /**
   * 受講生詳細の情報を登録
   *
   * @return String 受講生情報
   */
  @PostMapping("/students")
  public ResponseEntity<StudentDetail> resisterStudent(
      @Valid @RequestBody StudentDetail studentDetail) {
    //受講生登録のサービスのメソッド呼びだし
    StudentDetail responseStudentDetail = this.service.register(studentDetail);
    return ResponseEntity.ok(responseStudentDetail);
  }

  /**
   * 受講生詳細の情報（1件）を更新
   *
   * @return String 受講生情報
   */
  @PutMapping("/students/update")
  public ResponseEntity<String> updateStudent(@Valid @RequestBody StudentDetail studentDetail) {
    //受講生更新のサービスのメソッド呼びだし
    this.service.update(studentDetail);
    // ResponseEntityで何を返すか設定する
    // form-dataだとjsonで送られない
    return ResponseEntity.ok("更新処理が成功しました");

  }

}
