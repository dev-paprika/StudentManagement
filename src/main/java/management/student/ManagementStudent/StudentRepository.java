package management.student.ManagementStudent;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface StudentRepository {

  @Select("SELECT * FROM student ")
  List<Student> searchStudents();

  @Select("SELECT * FROM student_courses ")
  List<StudentCourses> searchCourses();

}
