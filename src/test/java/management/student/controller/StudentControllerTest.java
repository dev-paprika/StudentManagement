package management.student.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import management.student.converter.StudentConverter;
import management.student.data.ApplicationStatus;
import management.student.data.Student;
import management.student.data.StudentCourse;
import management.student.domain.StudentDetail;
import management.student.exception.StudentBizException;
import management.student.repository.StudentRepository;
import management.student.service.StudentService;
import management.student.validation.OnCreate;
import management.student.validation.OnUpdate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(StudentController.class)
//@AutoConfigureMockMvc
class StudentControllerTest {

  @Autowired
  MockMvc mockMvc;

  @MockBean
  StudentService service;

  @MockBean
  StudentRepository repository;

  @MockBean
  StudentConverter converter;  // StudentConverterをモック化

  @MockBean
  private OnCreate onCreate;  // 受講生登録用バリデーションのインターフェース

  @MockBean
  private OnUpdate onUpdate;  // 受講生更新用バリデーションのインターフェース

  private final ObjectMapper objectMapper = new ObjectMapper();

  private Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
  private String validStudentJson;
  private String validStatusJson;

  @BeforeEach
  void setUp() {
    validStudentJson = """
            {
                "id": 1,
                "name": "佐藤",
                "furigana": "さとう",
                "region": "大阪",
                "email": "sample@gmail.com",
                "gender": "male",
                "nickname": "さっちゃん",
                "phoneNumber": "09011111111",
                "remarks": ""
            }
        """;

    validStatusJson = """
        {
                "id": 1,
                "studentCourseId": 1,
                "status": "本申込"
        }""";
  }

  @Test
  void 受講生詳細の一覧情報が正常に取得できること() throws Exception {
    when(service.getStudentList()).thenReturn(List.of(new StudentDetail()));
    mockMvc.perform(get("/students"))
        .andExpect(status().isOk());
//        .andExpect(content().json("[]"));

    verify(service, times(1)).getStudentList();
  }

  @Test
  void 申込状況の一覧情報が正常に取得できること() throws Exception {
    when(service.getApplicationStatusList()).thenReturn(List.of(new ApplicationStatus()));
    mockMvc.perform(get("/applicationStatuses"))
        .andExpect(status().isOk());
//        .andExpect(content().json("[]"));

    verify(service, times(1)).getApplicationStatusList();
  }

  @Test
  void 受講生詳細の登録時に不正なデータが設定された場合にバリデーションエラーが発生すること() {
    Student student = new Student();
    student.setFurigana("satou");
    student.setRegion("大阪");
    student.setEmail("sample@gmail.com");
    student.setGender("male");
    student.setNickname("sttaha");
    student.setPhoneNumber("090あ11111111");
    student.setName("佐藤");
    student.setRemarks("");

    Set<ConstraintViolation<Student>> violations = validator.validate(student, OnCreate.class);
//    Assertions.assertEquals(0, violations.size());

    assertThat(violations.size()).isEqualTo(1);
    assertThat(violations).extracting("message")
        .containsOnly("電話番号は数字11桁またはハイフン含む13桁で入力してください");

  }

  @Test
  void 正常なIDで受講生情報が取得できること() throws Exception {
    StudentDetail studentDetail = new StudentDetail(new Student(),
        List.of(new StudentCourse()));
    studentDetail.getStudent().setId(1);
    when(service.getStudent(1)).thenReturn(studentDetail);

    mockMvc.perform(get("/students/1"))
        .andExpect(status().isOk());

    verify(service, times(1)).getStudent(1);
  }

  @Test
  void 不正なIDで受講生情報取得時にバリデーションエラーが発生すること() throws Exception {
    mockMvc.perform(get("/students/1000")) // 1000は @Max(999) を超えているため異常値
        .andExpect(status().isBadRequest());
  }

  @Test
  void 正常なIDで申込状況が取得できること() throws Exception {
    ApplicationStatus applicationStatus = new ApplicationStatus();
    applicationStatus.setId(1);
    when(service.getApplicationStatusById(1)).thenReturn(applicationStatus);

    mockMvc.perform(get("/applicationStatuses/1"))
        .andExpect(status().isOk());

    verify(service, times(1)).getApplicationStatusById(1);
  }

  @Test
  void 不正なIDで申込状況取得時にバリデーションエラーが発生すること() throws Exception {
    mockMvc.perform(get("/applicationStatuses/1000"))
        .andExpect(status().isBadRequest());
  }

  @Test
  void 存在しないIDで申込状況取得時に404エラーが発生すること() throws Exception {
    int testId = 999;

    // サービスクラスのメソッドが例外をスローすることをモックで設定
    when(service.getApplicationStatusById(testId))
        .thenThrow(
            new StudentBizException("ApplicationStatus with Course ID " + testId + " not found",
                HttpStatus.NOT_FOUND));

    // コントローラにGETリクエストを送信し、404エラーが返されることを確認
    mockMvc.perform(get("/applicationStatuses/" + testId))
        .andExpect(status().isNotFound());
  }


  @Test
  void 受講生詳細の登録時に正常な値でPOSTリクエストが成功すること() throws Exception {
    Student student = createValidStudent();
    StudentDetail studentDetail = new StudentDetail(student,
        List.of(new StudentCourse()));
//    studentDetail.getStudent().setId(1);
    when(service.register(studentDetail)).thenReturn(studentDetail);
    mockMvc.perform(post("/students")
            .contentType(MediaType.APPLICATION_JSON)
            .content(validStudentJson))
        .andExpect(status().isOk());

    verify(service, times(1)).register(any(StudentDetail.class));
  }


  @Test
  void 受講生詳細の更新時に正常な値でPUTリクエストが成功すること() throws Exception {

    mockMvc.perform(put("/students/update")
            .contentType(MediaType.APPLICATION_JSON)
            .content(validStudentJson))
        .andExpect(status().isOk());

    verify(service, times(1)).update(any(StudentDetail.class));
  }

  @Test
  void 申込状況の登録時に正常な値でPOSTリクエストで成功すること() throws Exception {
    ApplicationStatus newStatus = new ApplicationStatus();
    newStatus.setId(1);
    newStatus.setStatus("本申込");
    newStatus.setStudentCourseId(1);

    when(service.register(newStatus)).thenReturn(newStatus);

    mockMvc.perform(post("/applicationStatuses")
            .contentType(MediaType.APPLICATION_JSON)
            .content(validStatusJson))
        .andExpect(status().isOk());

    verify(service, times(1)).register(any(ApplicationStatus.class));
  }

  @Test
  void 申込状況の登録時にデータが不正な場合にPOSTリクエストがエラーになること() throws Exception {
    ApplicationStatus invalidStatus = new ApplicationStatus();
    invalidStatus.setId(1);
    // studentCourseId と status が不正
    String invalidJson = """
        {
          "id": 1,
          "studentCourseId": -2,
          "status": ""
        }
        """;
    mockMvc.perform(post("/applicationStatuses")
            .contentType(MediaType.APPLICATION_JSON)
            .content(invalidJson))
        .andExpect(status().isBadRequest());

    verify(service, times(0)).register(invalidStatus);
  }

  @Test
  void 申込状況の登録時にデータベースアクセスエラーが発生した場合に500エラーが発生すること()
      throws Exception {
    ApplicationStatus newStatus = new ApplicationStatus();
    newStatus.setId(1);
    newStatus.setStatus("本申込");
    newStatus.setStudentCourseId(1);

    // サービスメソッドがDataAccessExceptionをスローするようにモック設定
    doThrow(new StudentBizException("DB DataBaseAccess Error", HttpStatus.INTERNAL_SERVER_ERROR) {
    }).when(service).register(any(ApplicationStatus.class));

    mockMvc.perform(post("/applicationStatuses")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(newStatus)))
        .andExpect(status().isInternalServerError());

    verify(service, times(1)).register(any(ApplicationStatus.class));
  }

  @Test
  void 申込状況の更新時に正常な値でPUTリクエストが成功すること() throws Exception {
    ApplicationStatus updatedStatus = new ApplicationStatus();
    updatedStatus.setId(1);
    updatedStatus.setStudentCourseId(1);
    updatedStatus.setStatus("更新済み");

    mockMvc.perform(put("/applicationStatuses/update")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updatedStatus)))
        .andExpect(status().isOk());

    verify(service, times(1)).update(any(ApplicationStatus.class));
  }

  @Test
  void 存在しない申込状況IDの更新時に404エラーが発生すること() throws Exception {
    int testId = 999;
    ApplicationStatus updateStatus = new ApplicationStatus();
    updateStatus.setId(testId);
    updateStatus.setStudentCourseId(testId);
    updateStatus.setStatus("受講中");

    // searchApplicationStatusByID メソッドが Optional.empty() を返すようにモック設定
    when(repository.searchApplicationStatusByID(testId)).thenReturn(Optional.empty());

    // void メソッドで例外を投げる場合は doThrow().when() を使う
    doThrow(new StudentBizException("ApplicationStatus with ID " + testId + " not found",
        HttpStatus.NOT_FOUND))
        .when(service).update(any(ApplicationStatus.class));

    mockMvc.perform(put("/applicationStatuses/update")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updateStatus)))
        .andExpect(status().isNotFound());

    verify(service, times(1)).update(any(ApplicationStatus.class));
  }

  @Test
  void 申込状況の更新時にデータベースアクセスエラーが発生した場合に500エラーが発生すること()
      throws Exception {
    int testId = 999;
    ApplicationStatus updateStatus = new ApplicationStatus();
    updateStatus.setId(testId);
    updateStatus.setStudentCourseId(testId);
    updateStatus.setStatus("受講中");

    // サービスメソッドがDataAccessExceptionをスローするようにモック設定
    doThrow(new StudentBizException("DB DataBaseAccess Error", HttpStatus.INTERNAL_SERVER_ERROR) {
    }).when(service).update(any(ApplicationStatus.class));

    // PUTリクエストを送信し、500エラーが返されることを確認
    mockMvc.perform(put("/applicationStatuses/update")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updateStatus)))
        .andExpect(status().isInternalServerError());

    verify(service, times(1)).update(any(ApplicationStatus.class));
  }

  @Test
  void 申込状況がDELETEリクエストで正常に削除されること() throws Exception {
    // 実行
    mockMvc.perform(delete("/applicationStatuses/1"))
        .andExpect(status().isOk());
    // 検証
    verify(service, times(1)).deleteApplicationStatus(1);
  }

  @Test
  void 存在しない申込状況IDの削除時に404エラーが発生すること() throws Exception {
    int testId = 999;
    // searchApplicationStatusByID メソッドが Optional.empty() を返すようにモック設定
    when(repository.searchApplicationStatusByID(testId)).thenReturn(Optional.empty());

    // 戻り値がvoidの場合はdoThrowを使う
    doThrow(new StudentBizException("Not Found", HttpStatus.NOT_FOUND))
        .when(service).deleteApplicationStatus(testId);

    mockMvc.perform(delete("/applicationStatuses/999"))
        .andExpect(status().isNotFound());

    verify(service, times(1)).deleteApplicationStatus(testId);
  }

  @Test
  void 申込状況削除時にデータベースアクセスエラーが発生した場合に500エラーが発生すること()
      throws Exception {
    int testId = 1;

    // サービスメソッドがDataAccessExceptionをスローするようにモック設定
    doThrow(new StudentBizException("DB DataBaseAccess Error", HttpStatus.INTERNAL_SERVER_ERROR) {
    }).when(service).deleteApplicationStatus(anyInt());

    // DELETEリクエストを送信し、500エラーが返されることを確認
    mockMvc.perform(delete("/applicationStatuses/" + testId))
        .andExpect(status().isInternalServerError());

    verify(service, times(1)).deleteApplicationStatus(anyInt());
  }

  //以下バリデーションチェック
  @Test
  void 受講生登録時に名前が未入力でエラーとなること() {
    Student student = createValidStudent();
    student.setName(""); // 名前を空にする

    Set<ConstraintViolation<Student>> violations = validator.validate(student, OnCreate.class);

    assertThat(violations.size()).isEqualTo(1);
    assertThat(violations).extracting("message")
        .containsOnly("名前は必須です");
  }

  @Test
  void 受講生登録時に不正なメールアドレスでエラーとなること() {
    Student student = createValidStudent();
    student.setEmail("invalid-email"); // 無効なメールアドレスを設定

    Set<ConstraintViolation<Student>> violations = validator.validate(student, OnCreate.class);

    assertThat(violations.size()).isEqualTo(1);
    assertThat(violations).extracting("message")
        .containsOnly("メールアドレスが有効ではありません。");
  }

  @Test
  void 受講生登録時に不正な電話番号でエラーになること() {
    Student student = createValidStudent();
    student.setPhoneNumber("1234"); // 無効な電話番号を設定

    Set<ConstraintViolation<Student>> violations = validator.validate(student, OnCreate.class);

    assertThat(violations.size()).isEqualTo(1);
    assertThat(violations).extracting("message")
        .containsOnly("電話番号は数字11桁またはハイフン含む13桁で入力してください");
  }

  @Test
  void 受講生登録時に不正なふりがなでエラーになること() {
    Student student = createValidStudent();
    student.setFurigana(null); // ふりがなをnullにする

    Set<ConstraintViolation<Student>> violations = validator.validate(student, OnCreate.class);

    assertThat(violations.size()).isEqualTo(1);
    assertThat(violations).extracting("message")
        .containsOnly("null は許可されていません");
  }

  @Test
  void 受講生登録時に不正な地域でエラーとなること() {
    Student student = createValidStudent();
    student.setRegion(null); // 地域をnullにする

    Set<ConstraintViolation<Student>> violations = validator.validate(student, OnCreate.class);

    assertThat(violations.size()).isEqualTo(1);
    assertThat(violations).extracting("message")
        .containsOnly("null は許可されていません");
  }

  @Test
  void 受講生登録時に不正なニックネームでエラーとなること() {
    Student student = createValidStudent();
    student.setNickname(null); // ニックネームをnullにする

    Set<ConstraintViolation<Student>> violations = validator.validate(student, OnCreate.class);

    assertThat(violations.size()).isEqualTo(1);
    assertThat(violations).extracting("message")
        .containsOnly("null は許可されていません");
  }

  //　申込状況のバリデーションチェック
  @Test
  void 申込状況の登録時に受講生IDが未設定でエラーになること() {
    ApplicationStatus status = new ApplicationStatus();
    status.setId(1);
    status.setStatus("本申込");

    Set<ConstraintViolation<ApplicationStatus>> violations = validator.validate(status);

    assertThat(violations.size()).isEqualTo(1);
    assertThat(violations).extracting("message")
        .containsOnly("受講生コースIDは1以上の値である必要があります");
  }

  @Test
  void 申込状況の登録時と更新時で受講生IDが不正な場合にエラーになること() {
    ApplicationStatus status = new ApplicationStatus();
    status.setId(1);
    status.setId(-2);
    status.setStatus("本申込");

    Set<ConstraintViolation<ApplicationStatus>> violations = validator.validate(status);

    assertThat(violations.size()).isEqualTo(1);
    assertThat(violations).extracting("message")
        .containsOnly("受講生コースIDは1以上の値である必要があります");
  }

  @Test
  void 申込状況の登録時と更新時で申込ステータスが不正な場合にエラーになること() {
    ApplicationStatus status = new ApplicationStatus();
    status.setId(1);
    status.setStudentCourseId(1);

    Set<ConstraintViolation<ApplicationStatus>> violations = validator.validate(status);

    assertThat(violations.size()).isEqualTo(1);
    assertThat(violations).extracting("message")
        .containsOnly("ステータスは必須です");
  }

  /**
   * バリデート用の受講生データを作成する
   *
   * @return Student 受講生
   */
  private Student createValidStudent() {
    Student student = new Student();
    student.setId(1);
    student.setName("佐藤");
    student.setFurigana("さとう");
    student.setRegion("大阪");
    student.setEmail("sample@gmail.com");
    student.setGender("male");
    student.setNickname("さっちゃん");
    student.setPhoneNumber("09011111111");
    student.setRemarks(""); // 備考がない場合は空文字を設定
    return student;
  }

}