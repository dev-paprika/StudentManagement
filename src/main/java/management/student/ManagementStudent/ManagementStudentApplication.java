package management.student.ManagementStudent;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class ManagementStudentApplication {

  private String name = "paprika"; //名前
  private String age = "37";  //年齢
  private final Map<String, String> studentMap = new HashMap<>(); //受講生の情報

  public static void main(String[] args) {
    SpringApplication.run(ManagementStudentApplication.class, args);
  }

  /**
   * 受講生の情報（メンバ変数）を取得
   *
   * @return
   */
  @GetMapping("/studentInfo")
  public String getStudentInfo() {
    return this.name + " " + this.age + "歳";
  }

  /**
   * 受講生の情報（メンバ変数）を設定
   *
   * @param name 　名前
   * @param age  　年齢
   */
  @PostMapping("/studentInfo")
  public void setStudentInfo(String name, String age) {
    this.name = name;
    this.age = age;
  }

  /**
   * 受講生の名前（メンバ変数）を設定
   *
   * @param name 　名前
   */
  @PostMapping("/studentName")
  public void updateStudentName(String name) {
    this.name = name;
  }

  /**
   * 複数の受講生の情報をMapに設定
   *
   * @param name 　名前
   * @param age  　年齢
   */
  @PostMapping("/addStudent")
  public void addStudent(String name, String age) {
    //Mapに受講生を追加する
    this.studentMap.put(name, age);
  }

  /**
   * 受講生の情報をメンバ変数のMapに追加する
   *
   * @return 表示する内容
   */
  @GetMapping("/studentMap")
  public String getAllStudent() {
    //Mapに格納している受講生を表示させる
    StringBuilder result = new StringBuilder();
    for (Entry<String, String> e : this.studentMap.entrySet()) {
      result.append(e.getKey()).append(" ").append(e.getValue()).append("<br>");
    }
    return result.toString();
  }

  /**
   * 課題17
   *
   * @return 表示する内容
   */
  @GetMapping("/test")
  public String test() {
    String result = "";
    //文字列が空かどうか..
    result += "isEmptyの確認============<br>";
    result += "空値:" + StringUtils.isEmpty("") + "<br>";
    result += "null:" + StringUtils.isEmpty(null) + "<br>";
    result += "あ:" + StringUtils.isEmpty("あ") + "<br><br>";

    //大文字→小文字、小文字→大文字に変換
    result += "swapCaseの確認============<br>";
    result += "変換前：AAbbCCdd\n";
    result += "変換後：" + StringUtils.swapCase("AAbbCCdd") + "<br><br>";

    //文字列置換
    result += "replaceCharsの確認============<br>";
    result += "変換前：abcdefgh<br>";
    result += "変換後(ab→t)：" + StringUtils.replaceChars("abcdefgh", "ab", "t") + "<br><br>";

    //文字列を複数置換
    String[] searchs = {"ab", "g"};
    String[] afters = {"t", "u"};
    result += "replaceEachの確認============<br>";
    result +=
        "変換後(ab→t,t->u)：" + StringUtils.replaceEach("abcdefgh", searchs, afters) + "<br><br>";
    return result;
  }

}
