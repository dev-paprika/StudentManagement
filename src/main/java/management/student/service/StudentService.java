package management.student.service;

import static java.util.function.Predicate.not;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import management.student.converter.StudentConverter;
import management.student.data.ApplicationStatus;
import management.student.data.Student;
import management.student.data.StudentCourse;
import management.student.domain.StudentDetail;
import management.student.exception.StudentBizException;
import management.student.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
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
   * 受講生の情報を引数に渡されたパラメータによって絞り込み検索を行う
   * パラメータが渡されていない場合は全件検索となる
   *
   * @param studentId       　受講生ID
   * @param name            　　受講生名前
   * @param kana            　　受講生フリガナ
   * @param email           　　受講生メールアドレス
   * @param phoneNumber     　受講生電話番号
   * @param age             　　受講生年齢
   * @param courseName      　受講コース名
   * @param courseStartDate 　受講コース開始日
   * @param courseEndDate   　　受講コース終了日
   * @param status          　　受講コース申込状況
   * @return List<StudentDetail>　 受講生詳細リスト
   */
  public List<StudentDetail> getStudentList(Integer studentId, String name, String kana,
      String email, String phoneNumber, Integer age, String courseName,
      LocalDateTime courseStartDate, LocalDateTime courseEndDate, String status) {
    // 受講生を引数によって検索する
    List<Student> studentList = this.repository.searchStudentList(studentId, name, kana, email,
        phoneNumber, age);

    // 受講生コースを引数によって検索する
    List<StudentCourse> studentCoursesList = this.repository.searchStudentCourseWithStatus(
        studentId,
        courseName, courseStartDate, courseEndDate, status);

    List<Integer> searchIds = null;
    if (hasCourseFilter(courseName, courseStartDate, courseEndDate, status)) {
      //受講生コースに絞り込み条件がある場合はそれに紐づく受講生IDを抽出する
      searchIds = studentCoursesList.stream()
          .map(StudentCourse::getStudentId)
          .distinct()
          .toList();
    }

    return this.converter.convertStudentDetails(studentList, studentCoursesList, searchIds);
  }

  /**
   * 受講生コースの検索条件が指定されているか判定する
   *
   * @param courseName      　受講生コース名
   * @param courseStartDate 　受講開始日
   * @param courseEndDate   　　受講終了日
   * @param status          　申込状況
   * @return true/false
   */
  boolean hasCourseFilter(String courseName, LocalDateTime courseStartDate,
      LocalDateTime courseEndDate, String status) {

    return courseName != null || courseStartDate != null || courseEndDate != null
        || status != null;
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
    List<StudentCourse> courses = this.repository.searchStudentCourseWithStatus(student.getId(),
        null, null, null, null);
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
   * 申込状況全件検索
   *
   * @return List<ApplicationStatus> 申込状況
   */
  public List<ApplicationStatus> getApplicationStatusList() {
    return this.repository.searchApplicationStatusList();
  }

  /**
   * 申込状況１件検索
   *
   * @return ApplicationStatus 申込状況
   */
  public ApplicationStatus getApplicationStatusById(int id) {
    ApplicationStatus applicationStatus = this.repository.searchApplicationStatusByID(id)
        .orElseThrow(() -> new StudentBizException(
            "ApplicationStatus with Course ID " + id + " not found",
            HttpStatus.NOT_FOUND));
    return applicationStatus;
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
    courses.forEach(courseWithStatus -> {
      initStudentCourses(courseWithStatus, student);
      //受講生コース登録
      resister(courseWithStatus);
      // 申込状況登録
      ApplicationStatus status = courseWithStatus.getApplicationStatus();
      status.setStudentCourseId(courseWithStatus.getId());
      register(status);

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
    if (!studentDetail.getStudent().isDeleteFlag()) {
      studentDetail.getStudentCourseList().forEach(courseWithStatus -> {
        // コースを更新
        update(courseWithStatus);
        // 申込状況更新
        ApplicationStatus status = courseWithStatus.getApplicationStatus();
        update(status);
      });
    }
  }

  /**
   * 受講生コースに紐づく受講生ID,コース開始日、コース終了日を設定する
   *
   * @param course  　受講生コース
   * @param student 　受講生
   */
  void initStudentCourses(StudentCourse course, Student student) {
    course.setStudentId(student.getId());
    // startDateは現在の日付、endDateは１年後を設定
    LocalDateTime today = LocalDateTime.now();
    course.setStartDate(today);
    course.setEndDate(today.plusYears(1)); // 1年後の日付を設定
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
   * 申込状況登録
   *
   * @param status 申込状況
   */
  public ApplicationStatus register(ApplicationStatus status) {
    try {
      this.repository.createApplicationStatus(status);
    } catch (DataAccessException e) {
      throw new StudentBizException("DataBaseAccess Error",
          HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return status;
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
   * 申込状況更新
   *
   * @param status 申込状況
   */
  public void update(ApplicationStatus status) {
    try {
      // 更新対象が存在する場合に更新を実行
      if (this.repository.searchApplicationStatusByID(status.getId()).isPresent()) {
        this.repository.updateApplicationStatus(status);
      } else {
        // 更新対象が存在しない場合
        throw new StudentBizException(
            "ApplicationStatus with ID " + status.getId() + " not found",
            HttpStatus.NOT_FOUND);
      }
    } catch (DataAccessException e) {
      // データベースアクセスエラーが発生した場合の処理
      throw new StudentBizException("DataBaseAccess Error",
          HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }


  /**
   * 申込状況削除
   *
   * @param id 申込状況ID
   */
  public void deleteApplicationStatus(int id) {
    // 削除対象が存在した場合に実行
    if (this.repository.searchApplicationStatusByID(id).isPresent()) {
      this.repository.deleteApplicationStatus(id);
    } else {
      // 削除対象が存在しなかった場合
      throw new StudentBizException("ApplicationStatus with ID " + id + " Not Found",
          HttpStatus.NOT_FOUND);
    }

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
    mergedStudent.setId(updates.getId());
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
    updatesStudentDetail.setStudent(mergedStudent);
  }
}
