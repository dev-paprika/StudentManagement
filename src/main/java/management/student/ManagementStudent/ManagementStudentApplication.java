package management.student.ManagementStudent;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class ManagementStudentApplication {

  public static void main(String[] args) {
    SpringApplication.run(ManagementStudentApplication.class, args);
  }

  @GetMapping("/hello")
  public String hello() {
    return "Hello World";
  }

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
