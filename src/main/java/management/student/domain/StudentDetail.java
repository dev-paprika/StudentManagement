package management.student.domain;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import management.student.data.Student;
import management.student.data.StudentCourses;

@Getter
@Setter
public class StudentDetail {

  private Student student; //受講生
  private List<StudentCourses> studentCourses;  //受講生コース
}
