package management.student.repository;

import java.util.List;
import management.student.data.Student;
import management.student.data.StudentCourse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * 受講生テーブルと受講生詳細テーブルを操作するRepositoryです。
 */
@Mapper
public interface StudentRepository {

  /**
   * 受講生全件検索
   *
   * @return 受講生一覧
   */
  @Select("SELECT * FROM student where delete_flag = 0")
  List<Student> searchStudentList();

  /**
   * 受講生1件検索
   *
   * @return 受講生（1件）
   */
  @Select("SELECT * FROM student where id = #{id} AND delete_flag = 0")
  Student searchStudentByID(int id);


  /**
   * 受講生コース全件検索
   *
   * @return List<StudentCourses> 受講生コース
   */
  @Select("SELECT * FROM student_courses ")
  List<StudentCourse> searchStudentCourseList();

  /**
   * 受講生コース1件検索
   *
   * @return 受講生
   */
  @Select("SELECT * FROM student_courses where student_id = #{studentId}")
  List<StudentCourse> searchStudentCourseByID(int studentId);


  /**
   * 受講生登録
   *
   * @param student 　受講生
   */
  void createStudent(Student student);

  /**
   * 受講生コース登録
   *
   * @param studentCourse 　受講生コース
   */
  void createStudentCourse(StudentCourse studentCourse);

  /**
   * 受講生更新
   *
   * @param student 　受講生
   */
  void updateStudent(Student student);

  /**
   * 受講生コース更新
   *
   * @param courses 　受講生コース
   */
  void updateStudentCourse(StudentCourse courses);
}
