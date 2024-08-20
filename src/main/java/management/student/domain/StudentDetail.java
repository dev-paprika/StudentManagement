package management.student.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import management.student.data.Student;
import management.student.data.StudentCourse;

@Schema(description = "受講生詳細")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class StudentDetail {

  @Valid
  private Student student; //受講生
  private List<StudentCourse> studentCourseList;  //受講生コース

}
