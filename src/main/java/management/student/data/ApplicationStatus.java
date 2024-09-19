package management.student.data;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "申込状況")
@Getter
@Setter
public class ApplicationStatus {

  private int id; //ID
  @Min(value = 1, message = "受講生コースIDは1以上の値である必要があります")
  private int studentCourseId; //受講生コースID
  @NotEmpty(message = "ステータスは必須です")
  private String status; //ステータス

}
