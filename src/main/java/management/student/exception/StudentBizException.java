package management.student.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

/**
 * 業務エラーが出た際に利用するカスタム例外クラス
 */
@Getter
@Setter
public class StudentBizException extends RuntimeException {

  private final HttpStatus status; //HTTPステータス

  /**
   * エラーメッセージとHttpStatusを引数にとるコンストラクタ
   *
   * @param message 　エラーメッセージ
   */
  public StudentBizException(String message, HttpStatus status) {
    super(message);
    this.status = status;
  }

}