package management.student.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import management.student.converter.StudentConverter;
import management.student.data.ApplicationStatus;
import management.student.domain.StudentDetail;
import management.student.service.StudentService;
import management.student.validation.OnCreate;
import management.student.validation.OnUpdate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
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
   * 渡された引数によって、絞り込み検索を行う
   * 引数がない場合は全件検索
   *
   * @param studentId       　受講生ID
   * @param name            　　受講生名前
   * @param kana            　　受講生フリガナ
   * @param email           　　受講生メールアドレス
   * @param phoneNumber     　受講生電話番号
   * @param age             　　受講生年齢
   * @param courseName      　受講コース名
   * @param courseStartDate 　受講コース開始日
   * @param courseEndDate   　　受講コース終了日
   * @param status          　　受講コース申込状況
   * @return ResponseEntity<List < StudentDetail>> 受講生詳細のリスト
   */
  @Operation(
      summary = "受講生情報の取得",
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
  public ResponseEntity<List<StudentDetail>> getStudentList(
      @RequestParam(required = false) Integer studentId,
      @RequestParam(required = false) String name,
      @RequestParam(required = false) String kana,
      @RequestParam(required = false) String email,
      @RequestParam(required = false) String phoneNumber,
      @RequestParam(required = false) Integer age,
      @RequestParam(required = false) String courseName,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDateTime courseStartDate,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDateTime courseEndDate,
      @RequestParam(required = false) String status
  ) {
    // サービスメソッドを呼び出し、検索処理を実行
    List<StudentDetail> studentDetails = service.getStudentList(
        studentId, name, kana, email, phoneNumber, age, courseName, courseStartDate, courseEndDate,
        status);

    if (studentDetails.isEmpty()) {
      return ResponseEntity.noContent().build();
    }
    return ResponseEntity.ok(studentDetails);

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
   * 申込状況の情報を取得
   * 全件検索のため条件の指定はなし
   *
   * @return String 受講生状況
   */
  @GetMapping("/applicationStatuses")
  @Operation(
      summary = "申込状況の取得",
      description = "受講生の申込状況を取得します。",
      responses = {
          @ApiResponse(
              responseCode = "200",
              description = "成功 - 受講生状況を全件取得します",
              content = @Content(mediaType = "application/json",
                  schema = @Schema(implementation = ApplicationStatus.class))
          ),
          @ApiResponse(
              responseCode = "404",
              description = "申込状況が見つかりません"
          )
      }
  )
  public List<ApplicationStatus> getApplicationStatuseList() {
    return service.getApplicationStatusList();
  }

  /**
   * 申込状況の情報（1件）を取得
   * IDに基づく任意の申込状況の情報を返します
   *
   * @return String 申込状況（１件）
   */
  @Operation(
      summary = "IDに基づく申込状況情報の取得",
      description = "指定されたIDに基づいて申込状況の情報を取得します。",
      responses = {
          @ApiResponse(
              responseCode = "200",
              description = "成功 - 指定された申込状況の情報を返します",
              content = @Content(mediaType = "application/json",
                  schema = @Schema(implementation = ApplicationStatus.class))
          ),
          @ApiResponse(
              responseCode = "404",
              description = "申込状況が見つかりません"
          )
      }
  )
  @GetMapping("/applicationStatuses/{id}")
  public ApplicationStatus getApplicationStatus(@PathVariable @Valid @Min(1) @Max(999) String id) {
    //受講生と受講生コース情報取得
    return service.getApplicationStatusById(Integer.parseInt(id));
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
   * 申込状況の情報を登録
   *
   * @return String 受講生情報
   */
  @Operation(
      summary = "新しい申込状況の登録",
      description = "新しい申込状況の情報をデータベースに登録します。",
      responses = {
          @ApiResponse(
              responseCode = "201",
              description = "申込状況が正常に登録されました。",
              content = @Content(mediaType = "application/json",
                  schema = @Schema(implementation = StudentDetail.class))
          ),
          @ApiResponse(
              responseCode = "500",
              description = "重複登録です。"
          )
      }
  )
  @PostMapping("/applicationStatuses")
  public ResponseEntity<ApplicationStatus> resisterApplicationStatus(
      @Validated @RequestBody ApplicationStatus status) {
    //申込状況登録のサービスのメソッド呼びだし
    ApplicationStatus responseStatus = this.service.register(status);
    return ResponseEntity.ok(responseStatus);
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
  public ResponseEntity<Map<String, String>> updateStudent(
      @Validated(OnUpdate.class) @RequestBody StudentDetail studentDetail) {
    //受講生更新のサービスのメソッド呼びだし
    this.service.update(studentDetail);
    // ResponseEntityで何を返すか設定する
    // レスポンス用のメッセージをMapに格納
    Map<String, String> response = new HashMap<>();
    response.put("message", "更新処理が成功しました");
    return ResponseEntity.ok(response);

  }

  /**
   * 申込状況の情報（1件）を更新
   *
   * @return String 申込状況
   */
  @Operation(
      summary = "申込状況情報の更新",
      description = "指定された申込状況情報を更新します。",
      responses = {
          @ApiResponse(
              responseCode = "200",
              description = "申込状況情報が正常に更新されました。",
              content = @Content(mediaType = "application/json")
          ),
          @ApiResponse(
              responseCode = "400",
              description = "リクエストデータが不正です。"
          ),
          @ApiResponse(
              responseCode = "500",
              description = "サーバーエラーです。"
          )
      }
  )
  @PutMapping("/applicationStatuses/update")
  public ResponseEntity<Map<String, String>> updateApplicationStatus(
      @Validated @RequestBody ApplicationStatus status) {
    //申込状況更新のサービスのメソッド呼びだし
    this.service.update(status);
    // ResponseEntityで何を返すか設定する
    // レスポンス用のメッセージをMapに格納する
    Map<String, String> response = new HashMap<>();
    response.put("message", "更新処理が成功しました");
    return ResponseEntity.ok(response);

  }

  /**
   * 申込状況の情報（1件）を削除
   *
   * @param id 申込状況のID
   * @return ResponseEntity 申込状況削除の結果
   */
  @Operation(
      summary = "申込状況情報の削除",
      description = "指定された申込状況情報を削除します。",
      responses = {
          @ApiResponse(
              responseCode = "200",
              description = "申込状況情報が正常に削除されました。",
              content = @Content(mediaType = "application/json")
          ),
          @ApiResponse(
              responseCode = "404",
              description = "削除対象の申込状況が見つかりません"
          )
      }
  )
  @DeleteMapping("/applicationStatuses/{id}")
  public ResponseEntity<Map<String, String>> deleteApplicationStatus(
      @PathVariable @Valid @Min(1) @Max(999) String id) {
    // サービスメソッドを呼び出し、申込状況を削除
    service.deleteApplicationStatus(Integer.parseInt(id));
    // レスポンス用のメッセージをMapに格納する
    Map<String, String> response = new HashMap<>();
    response.put("message", "削除処理が成功しました");
    return ResponseEntity.ok(response);
  }

}
