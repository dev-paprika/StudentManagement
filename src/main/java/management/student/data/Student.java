package management.student.data;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import management.student.validation.OnCreate;

@Schema(description = "受講生")
@Setter
@Getter
public class Student {

  private int id; //id
  @NotEmpty(message = "名前は必須です", groups = OnCreate.class)
  private String name; //名前
  @NotEmpty(groups = OnCreate.class)
  private int age; //年齢
  @NotNull(groups = OnCreate.class)
  private String furigana;  //かな
  @NotNull(groups = OnCreate.class)
  private String gender;  //性別
  @NotNull(groups = OnCreate.class)
  private String nickname;  //ニックネーム
  @NotEmpty(groups = OnCreate.class)
  @Email(message = "メールアドレスが有効ではありません。")
  private String email;   //e-mail
  @NotNull(groups = OnCreate.class)
  private String region;    //地域
  @NotEmpty(groups = OnCreate.class)
  @Pattern(regexp = "^\\d{11}$|^\\d{3}-\\d{4}-\\d{4}$",
      message = "電話番号は数字11桁またはハイフン含む13桁で入力してください")
  private String phoneNumber;  //電話番号
  @NotNull(groups = OnCreate.class)
  private String remarks; //備考
  private boolean deleteFlag; //削除フラグ

}
