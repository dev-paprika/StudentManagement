package management.student.data;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudentCourse {

  private int id;                    //id
  private int studentId;      //受講生id
  private String courseName; // コース名
  private LocalDateTime startDate;    //受講開始日
  private LocalDateTime endDate;     //受講完了日

}
