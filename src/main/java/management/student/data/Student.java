package management.student.data;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Student {

  private int id; //id
  @NotEmpty(message = "名前は必須です")
  private String name; //名前
  private int age; //年齢
  private String furigana;  //かな
  private String gender;  //性別
  private String nickname;  //ニックネーム
  @Email(message = "メールアドレスが有効ではありません。")
  private String email;   //e-mail
  private String region;    //地域
  @Pattern(regexp = "^\\d{11}$|^\\d{3}-\\d{4}-\\d{4}$",
      message = "電話番号は数字11桁またはハイフン含む13桁で入力してください")
  private String phoneNumber;  //電話番号
  private String remarks; //備考
  private boolean deleteFlag; //削除フラグ


}
