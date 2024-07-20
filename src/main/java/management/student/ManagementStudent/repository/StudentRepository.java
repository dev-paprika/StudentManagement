package management.student.ManagementStudent.repository;

import java.util.List;
import management.student.ManagementStudent.data.Student;
import management.student.ManagementStudent.data.StudentCourses;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface StudentRepository {

  @Select("SELECT * FROM student ")
  List<Student> searchStudents();

  @Select("SELECT * FROM student_courses ")
  List<StudentCourses> searchCourses();

}
