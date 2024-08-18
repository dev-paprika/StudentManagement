package management.student.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.util.List;
import management.student.converter.StudentConverter;
import management.student.domain.StudentDetail;
import management.student.service.StudentService;
import management.student.validation.OnCreate;
import management.student.validation.OnUpdate;
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
  @Operation(
      summary = "全受講生情報の取得",
      description = "データベースに登録されている全受講生の詳細情報を取得します。",
      responses = {
          @ApiResponse(
              responseCode = "200",
              description = "成功 - 全受講生の情報を返します",
              content = @Content(mediaType = "application/json",
                  schema = @Schema(implementation = List.class))
          )
      }
  )
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
  @Operation(
      summary = "IDに基づく受講生情報の取得",
      description = "指定されたIDに基づいて受講生の詳細情報を取得します。",
      responses = {
          @ApiResponse(
              responseCode = "200",
              description = "成功 - 指定された受講生の情報を返します",
              content = @Content(mediaType = "application/json",
                  schema = @Schema(implementation = StudentDetail.class))
          ),
          @ApiResponse(
              responseCode = "404",
              description = "受講生が見つかりません"
          )
      }
  )
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
  @Operation(
      summary = "新しい受講生の登録",
      description = "新しい受講生情報をデータベースに登録します。",
      responses = {
          @ApiResponse(
              responseCode = "201",
              description = "受講生が正常に登録されました。",
              content = @Content(mediaType = "application/json",
                  schema = @Schema(implementation = StudentDetail.class))
          )
      }
  )
  @PostMapping("/students")
  public ResponseEntity<StudentDetail> resisterStudent(
      @Validated(OnCreate.class) @RequestBody StudentDetail studentDetail) {
    //受講生登録のサービスのメソッド呼びだし
    StudentDetail responseStudentDetail = this.service.register(studentDetail);
    return ResponseEntity.ok(responseStudentDetail);
  }

  /**
   * 受講生詳細の情報（1件）を更新
   *
   * @return String 受講生情報
   */
  @Operation(
      summary = "受講生情報の更新",
      description = "指定された受講生情報を更新します。",
      responses = {
          @ApiResponse(
              responseCode = "200",
              description = "受講生情報が正常に更新されました。",
              content = @Content(mediaType = "application/json")
          ),
          @ApiResponse(
              responseCode = "400",
              description = "リクエストデータが不正です。"
          )
      }
  )
  @PutMapping("/students/update")
  public ResponseEntity<String> updateStudent(
      @Validated(OnUpdate.class) @RequestBody StudentDetail studentDetail) {
    //受講生更新のサービスのメソッド呼びだし
    this.service.update(studentDetail);
    // ResponseEntityで何を返すか設定する
    // form-dataだとjsonで送られない
    return ResponseEntity.ok("更新処理が成功しました");

  }

}
