package management.student.repository;

import java.util.List;
import management.student.data.Student;
import management.student.data.StudentCourses;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface StudentRepository {

  /**
   * 受講生全件検索
   *
   * @return 受講生
   */
  @Select("SELECT * FROM student where delete_flag = 0")
  List<Student> searchStudents();

  /**
   * 受講生1件検索
   *
   * @return 受講生
   */
  @Select("SELECT * FROM student where id = #{id} AND delete_flag = 0")
  Student searchStudentByID(int id);


  /**
   * 受講生コース全件検索
   *
   * @return List<StudentCourses> 受講生コース
   */
  @Select("SELECT * FROM student_courses ")
  List<StudentCourses> searchCourses();

  /**
   * 受講生コース1件検索
   *
   * @return 受講生
   */
  @Select("SELECT * FROM student_courses where student_id = #{studentId}")
  List<StudentCourses> searchStudentCourseByID(int studentId);


  /**
   * 受講生登録
   *
   * @param student 　受講生
   */
  void createStudent(Student student);

  /**
   * 受講生コース登録
   *
   * @param studentCourses 　受講生コース
   */
  void createStudentCourse(StudentCourses studentCourses);

  /**
   * 受講生更新
   *
   * @param student 　受講生
   */
  void updateStudent(Student student);

}
