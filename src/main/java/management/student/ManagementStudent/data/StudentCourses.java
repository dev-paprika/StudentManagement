package management.student.ManagementStudent.data;

import java.sql.Timestamp;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudentCourses {

  private int id;                    //id
  private int studentId;      //受講生id
  private String courseName; // コース名
  private Timestamp startDate;    //受講開始日
  private Timestamp endDate;     //受講完了日

}
