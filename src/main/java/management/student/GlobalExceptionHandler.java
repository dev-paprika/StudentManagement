package management.student;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import management.student.exception.StudentBizException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 例外を包括的に補足する
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

  /**
   * 入力チェック用のバリデーション例外用のメソッド
   *
   * @param ex 　MethodArgumentNotValidException　例外
   * @return レスポンス
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<List<String>> handleValidationExceptions(
      MethodArgumentNotValidException ex) {
    List<String> errors = ex.getBindingResult().getFieldErrors().stream()
        .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
        .collect(Collectors.toList());
    return ResponseEntity.badRequest().body(errors);
  }

  /**
   * 入力チェック用のバリデーション例外用のメソッド
   *
   * @param ex 　ConstraintViolationException 例外
   * @return レスポンス
   */
  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<Object> handleConstraintViolationException(
      ConstraintViolationException ex) {
    //以下のコードだとうまくいかない。
    //    Map<String, String> errors = ex.getConstraintViolations().stream()
//        .collect(Collectors.toMap(
//            violation -> violation.getPropertyPath().toString(),
//            ConstraintViolation::getMessage
//        ));
    Map<String, String> errors = new HashMap<>();
    for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
      errors.put(violation.getPropertyPath().toString(), violation.getMessage());
    }
    Map<String, Object> response = new HashMap<>();
    response.put("errors", errors);
    response.put("status", HttpStatus.BAD_REQUEST);
    return ResponseEntity.badRequest().body(response);
  }

  /**
   * 業務エラーが出た場合の例外メソッド
   * カスタム例外クラスのエラーハンドリングを行う
   *
   * @param ex 　StudentBizException　業務例外
   * @return レスポンス
   */
  @ExceptionHandler(StudentBizException.class)
  public ResponseEntity<Object> handleStudentBizException(StudentBizException ex) {
    Map<String, Object> errorDetails = new HashMap<>();
    errorDetails.put("message", ex.getMessage());
    return ResponseEntity.status(ex.getStatus()).body(errorDetails);
  }
}