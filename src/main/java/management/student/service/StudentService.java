package management.student.service;

import static java.util.function.Predicate.not;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import management.student.converter.StudentConverter;
import management.student.data.Student;
import management.student.data.StudentCourse;
import management.student.domain.StudentDetail;
import management.student.exception.StudentBizException;
import management.student.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 受講生情報を取り扱うサービス
 * 受講生情報の検索、更新、登録処理を行う
 */
@Service
public class StudentService {

  private StudentRepository repository;
  private StudentConverter converter;

  @Autowired
  //コンストラクタにAutowiredする。フィールドにAutowirdeしない
  public StudentService(StudentRepository repository, StudentConverter converter) {
    this.repository = repository;
    this.converter = converter;
  }

  /**
   * 受講生の情報を取得
   * 全件検索のため条件の指定はなし
   *
   * @return String 受講生情報
   */
  public List<StudentDetail> getStudentList() {
    //受講生全件取得
    List<Student> studentList = this.repository.searchStudentList();
    // 受講生コース全件取得
    List<StudentCourse> studentCoursesList = this.repository.searchStudentCourseList();
    //コンバータークラスで欲しい情報に変換
    return this.converter.convertStudentDetails(studentList, studentCoursesList);
  }

  /**
   * 受講生詳細の情報（1件）を取得
   * 　IDに基づく任意の受講生情報を取得したあと、その受講生に紐づく受講生コースを取得し、
   * 　受講生詳細に設定します。
   *
   * @param id 受講生ID
   * @return String 受講生情報
   */
  public StudentDetail getStudent(int id) {
    //該当の受講生が存在しない場合はエラーとする。
    Student student = this.repository.searchStudentByID(id)
        .orElseThrow(() -> new StudentBizException("Student with ID " + id + " not found",
            HttpStatus.NOT_FOUND));
    List<StudentCourse> courses = this.repository.searchStudentCourseByID(student.getId());
    return new StudentDetail(student, courses);
  }


  /**
   * 受講生コースの情報を取得
   *
   * @return String 受講生情報
   */
  public List<StudentCourse> getStudentCourseList() {
    return this.repository.searchStudentCourseList();
  }

  /**
   * 受講生コースの情報を取得
   *
   * @return String 受講生コース
   */
  public List<StudentCourse> getStudentCourses(int studentId) {
    return this.repository.searchStudentCourseByID(studentId);
  }

  /**
   * 受講生詳細の登録
   * 受講生と受講生コースをそれぞれ登録する
   * 受講生コースには受講生登録の後に紐づく受講生ID、コース開始日、コース終了日を設定して登録する
   *
   * @param studentDetail 　受講生詳細
   */
  @Transactional
  public StudentDetail register(StudentDetail studentDetail) {
    //受講生を登録
    Student student = studentDetail.getStudent();
    //@Optionを指定しているので、登録時にstudentにIDが設定される
    resister(student);
    List<StudentCourse> courses = studentDetail.getStudentCourseList();
    //受講生コースのループを回して受講生コースに初期値を設定
    courses.forEach(studentCourses -> {
      initStudentCourses(studentCourses, student);
      //受講生コース登録
      resister(studentCourses);
    });
    return studentDetail;
  }


  /**
   * 受講生と受講生コース更新
   *
   * @param studentDetail 　受講生詳細
   */
  @Transactional
  public void update(StudentDetail studentDetail) {
    //受講生を更新
    StudentDetail beforeStudentDetail = this.getStudent(studentDetail.getStudent().getId());
    //更新時に入力が入っている値だけを更新し、他の値は元の受講生の値を利用する。
    mergedStudent(beforeStudentDetail, studentDetail);
    update(studentDetail.getStudent());
    //受講生コースを受講生が削除されていないときのみ更新
    studentDetail.getStudentCourseList().stream()
        .filter(courses -> !studentDetail.getStudent().isDeleteFlag()).forEach(this::update);
  }

  /**
   * 受講生コースに紐づく受講生ID,コース開始日、コース終了日を設定する
   *
   * @param courses 　受講生コース
   * @param student 　受講生
   */
  private static void initStudentCourses(StudentCourse courses, Student student) {
    courses.setStudentId(student.getId());
    // startDateは現在の日付、endDateは１年後を設定
    LocalDateTime today = LocalDateTime.now();
    courses.setStartDate(today);
    courses.setEndDate(today.plusYears(1)); // 1年後の日付を設定
  }

  /**
   * 受講生登録
   * 　IDは自動採番
   *
   * @param student 　受講生
   */
  private void resister(Student student) {
    this.repository.createStudent(student);
  }

  /**
   * 受講生コース登録
   *
   * @param courses 受講生コース
   */
  private void resister(StudentCourse courses) {
    this.repository.createStudentCourse(courses);
  }

  /**
   * 受講生更新
   *
   * @param student 　受講生
   */
  private void update(Student student) {
    this.repository.updateStudent(student);
  }

  /**
   * 受講生コース更新
   *
   * @param courses 　受講生コース
   */
  private void update(StudentCourse courses) {
    this.repository.updateStudentCourse(courses);
  }

  /**
   * 　更新用の受講生のフィールドに値が入っているかどうか判定し、設定されいなければ、元の受講生の値を設定する
   *
   * @param existingStudentDetail 受講生詳細（更新用）
   * @param updatesStudentDetail  　受講生詳細（更新前）
   */
  private static void mergedStudent(StudentDetail existingStudentDetail,
      StudentDetail updatesStudentDetail) {
    Student existing = existingStudentDetail.getStudent();
    Student updates = updatesStudentDetail.getStudent();
    Student mergedStudent = new Student();

    mergedStudent.setName(Optional.ofNullable(updates.getName()).filter(not(String::isBlank))
        .orElse(existing.getName()));
    mergedStudent.setAge(updates.getAge() > 0 ? updates.getAge() : existing.getAge());
    mergedStudent.setGender(Optional.ofNullable(updates.getGender()).orElse(existing.getGender()));
    mergedStudent.setFurigana(
        Optional.ofNullable(updates.getFurigana()).filter(not(String::isBlank))
            .orElse(existing.getFurigana()));
    mergedStudent.setNickname(
        Optional.ofNullable(updates.getNickname()).orElse(existing.getNickname()));
    mergedStudent.setPhoneNumber(
        Optional.ofNullable(updates.getPhoneNumber()).filter(not(String::isBlank))
            .orElse(existing.getPhoneNumber()));
    mergedStudent.setEmail(Optional.ofNullable(updates.getEmail()).orElse(existing.getEmail()));
    mergedStudent.setRegion(Optional.ofNullable(updates.getRegion()).orElse(existing.getRegion()));
    mergedStudent.setRemarks(
        Optional.ofNullable(updates.getRemarks()).orElse(existing.getRemarks()));
    existingStudentDetail.setStudent(mergedStudent);
  }
}
