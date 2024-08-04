package management.student.data;

import jakarta.validation.constraints.NotEmpty;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudentCourse {

  private int id;                    //ID
  private int studentId;   //受講生ID
  @NotEmpty(message = "コース名は必須です")
  private String courseName; // コース名
  private LocalDateTime startDate;    //受講開始日
  private LocalDateTime endDate;     //受講完了日

}
