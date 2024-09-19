package management.student.data;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "受講生コース")
@Getter
@Setter
public class StudentCourse {

  private int id;                    //ID
  private int studentId;   //受講生ID
  @NotEmpty(message = "コース名は必須です")
  private String courseName; // コース名
  private LocalDateTime startDate;    //受講開始日
  private LocalDateTime endDate;     //受講完了日
  private ApplicationStatus applicationStatus; //申込状況

}
