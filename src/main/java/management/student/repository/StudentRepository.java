package management.student.repository;

import java.util.List;
import management.student.data.Student;
import management.student.data.StudentCourses;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface StudentRepository {

  @Select("SELECT * FROM student ")
  List<Student> searchStudents();

  @Select("SELECT * FROM student_courses ")
  List<StudentCourses> searchCourses();

}
