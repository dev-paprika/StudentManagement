package management.student.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import management.student.data.ApplicationStatus;
import management.student.data.Student;
import management.student.data.StudentCourse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

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
  List<Student> searchStudentList(@Param("id") Integer studentId,
      @Param("name") String name,
      @Param("furigana") String kana,
      @Param("email") String email,
      @Param("phoneNumber") String phoneNumber,
      @Param("age") Integer age);

  /**
   * 受講生1件検索
   *
   * @return 受講生（1件）
   */
  Optional<Student> searchStudentByID(int id);


  /**
   * 受講生コース絞り込み検索
   *
   * @return List<StudentCourses> 受講生コース
   */
  List<StudentCourse> searchStudentCourseList();

  /**
   * 受講生コース1件検索
   *
   * @return 受講生
   */
  List<StudentCourse> searchStudentCourseByID(int studentId);

  /**
   * 申込状況全件検索
   *
   * @return List<StudentCourses> 申込状況
   */
  List<ApplicationStatus> searchApplicationStatusList();

  /**
   * 申込状況１件検索
   *
   * @return 申込状況
   */
  Optional<ApplicationStatus> searchApplicationStatusByID(int id);


  /**
   * 受講生コースと申込状況の検索
   *
   * @param studentId 　受講生ID
   * @return List<StudentCourse> 受講生コース
   */
  List<StudentCourse> searchStudentCourseWithStatus(@Param("studentId") Integer studentId,
      @Param("courseName") String courseName,
      @Param("startDate") LocalDateTime startDate,
      @Param("endDate") LocalDateTime endDate,
      @Param("status") String status);


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
   * 申込状況登録
   *
   * @param applicationStatus 　申込状況
   */
  void createApplicationStatus(ApplicationStatus applicationStatus);


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

  /**
   * 申込状況更新
   *
   * @param  　applicationStatus 申込状況
   */
  void updateApplicationStatus(ApplicationStatus applicationStatus);

  /**
   * 申込状況削除
   *
   * @param  　applicationStatusId 申込状況ID
   */
  void deleteApplicationStatus(int applicationStatusId);
}
