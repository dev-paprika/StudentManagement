package management.student.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.List;
import java.util.Set;
import management.student.converter.StudentConverter;
import management.student.data.Student;
import management.student.data.StudentCourse;
import management.student.domain.StudentDetail;
import management.student.repository.StudentRepository;
import management.student.service.StudentService;
import management.student.validation.OnCreate;
import management.student.validation.OnUpdate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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
  private OnCreate onCreate;

  @MockBean
  private OnUpdate onUpdate;

  private Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
  private String validStudentJson;

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
  }

  @Test
  void 受講生の一覧情報が検索できて返ってくること() throws Exception {
    when(service.getStudentList()).thenReturn(List.of(new StudentDetail()));
    mockMvc.perform(get("/students"))
        .andExpect(status().isOk());
//        .andExpect(content().json("[]"));

    verify(service, times(1)).getStudentList();
  }

  @Test
  void 受講生詳細の受講生で正常な値を設定したときにエラーにならないこと() {
    Student student = new Student();
    student.setFurigana("satou");
    student.setRegion("大阪");
    student.setEmail("sample@gmail.com");
    student.setGender("male");
    student.setNickname("sttaha");
    student.setPhoneNumber("09011111111");
    student.setName("佐藤");
    student.setRemarks("");

    Set<ConstraintViolation<Student>> violations = validator.validate(student, OnCreate.class);
//    Assertions.assertEquals(0, violations.size());
    assertThat(violations.size()).isEqualTo(0);

  }

  @Test
  void 受講生詳細の受講生で電話番号が不正なときにチェックにひっかかること() {
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
    StudentDetail studentDetail = new StudentDetail(new Student(), List.of(new StudentCourse()));
    studentDetail.getStudent().setId(1);
    when(service.getStudent(1)).thenReturn(studentDetail);

    mockMvc.perform(get("/students/1"))
        .andExpect(status().isOk());

    verify(service, times(1)).getStudent(1);
  }

  @Test
  void 異常なIDでバリデーションエラーが発生すること() throws Exception {
    mockMvc.perform(get("/students/1000")) // 1000は @Max(999) を超えているため異常値
        .andExpect(status().isBadRequest());
  }

  @Test
  void 正常なデータをPOSTしたときに成功すること() throws Exception {
    Student student = createValidStudent();
    StudentDetail studentDetail = new StudentDetail(student, List.of(new StudentCourse()));
//    studentDetail.getStudent().setId(1);
    when(service.register(studentDetail)).thenReturn(studentDetail);
    mockMvc.perform(post("/students")
            .contentType(MediaType.APPLICATION_JSON)
            .content(validStudentJson))
        .andExpect(status().isOk());

    verify(service, times(1)).register(any(StudentDetail.class));
  }


  @Test
  void 正常なデータをPUTしたときに成功すること() throws Exception {

    mockMvc.perform(put("/students/update")
            .contentType(MediaType.APPLICATION_JSON)
            .content(validStudentJson))
        .andExpect(status().isOk());

    verify(service, times(1)).update(any(StudentDetail.class));
  }


  //以下バリデーションチェック
  @Test
  void 名前が入力されていないときにエラーとなること() {
    Student student = createValidStudent();
    student.setName(""); // 名前を空にする

    Set<ConstraintViolation<Student>> violations = validator.validate(student, OnCreate.class);

    assertThat(violations.size()).isEqualTo(1);
    assertThat(violations).extracting("message")
        .containsOnly("名前は必須です");
  }

  @Test
  void 不正なメールアドレスでエラーとなること() {
    Student student = createValidStudent();
    student.setEmail("invalid-email"); // 無効なメールアドレスを設定

    Set<ConstraintViolation<Student>> violations = validator.validate(student, OnCreate.class);

    assertThat(violations.size()).isEqualTo(1);
    assertThat(violations).extracting("message")
        .containsOnly("メールアドレスが有効ではありません。");
  }

  @Test
  void 不正な電話番号でエラーになること() {
    Student student = createValidStudent();
    student.setPhoneNumber("1234"); // 無効な電話番号を設定

    Set<ConstraintViolation<Student>> violations = validator.validate(student, OnCreate.class);

    assertThat(violations.size()).isEqualTo(1);
    assertThat(violations).extracting("message")
        .containsOnly("電話番号は数字11桁またはハイフン含む13桁で入力してください");
  }

  @Test
  void 不正なふりがなでエラーになること() {
    Student student = createValidStudent();
    student.setFurigana(null); // ふりがなをnullにする

    Set<ConstraintViolation<Student>> violations = validator.validate(student, OnCreate.class);

    assertThat(violations.size()).isEqualTo(1);
    assertThat(violations).extracting("message")
        .containsOnly("null は許可されていません");
  }

  @Test
  void 不正な地域が入力されたときにエラーとなること() {
    Student student = createValidStudent();
    student.setRegion(null); // 地域をnullにする

    Set<ConstraintViolation<Student>> violations = validator.validate(student, OnCreate.class);

    assertThat(violations.size()).isEqualTo(1);
    assertThat(violations).extracting("message")
        .containsOnly("null は許可されていません");
  }

  @Test
  void 不正なニックネームが入力されたときにエラーとなること() {
    Student student = createValidStudent();
    student.setNickname(null); // ニックネームをnullにする

    Set<ConstraintViolation<Student>> violations = validator.validate(student, OnCreate.class);

    assertThat(violations.size()).isEqualTo(1);
    assertThat(violations).extracting("message")
        .containsOnly("null は許可されていません");
  }

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