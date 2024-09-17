package management.student.converter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import management.student.data.Student;
import management.student.data.StudentCourse;
import management.student.domain.StudentDetail;
import org.springframework.stereotype.Component;

/**
 * Serviceから取得したオブジェクト受講生情報と受講生コースの情報を受講生詳細、
 * もしくはその逆に変換するConverterクラスです
 */
@Component
public class StudentConverter {

  /**
   * 受講生テーブルと受講生コーステーブルを使用して、受講生詳細を作成する
   * 受講生コースは受講生に対して複数存在するのでループを回して受講生IDで紐づく受講生コースとコースの申込状況を設定する
   *
   * @param studentList 　受講生
   * @param courseList  　受講生コース
   * @return 受講生詳細情報
   * @Param applicationStatusList 申込状況リスト
   */
  public List<StudentDetail> convertStudentDetails(List<Student> studentList,
      List<StudentCourse> courseList) {

    List<StudentDetail> studentDetailList = new ArrayList<>();
    //受講生と受講生テーブルで紐づくものを受講生詳細に変換
    studentList.forEach(student -> {
      StudentDetail studentDetail = new StudentDetail();
      studentDetail.setStudent(student);
      List<StudentCourse> convertCourseList = courseList.stream()
          .filter(course -> student.getId() == course.getStudentId()).collect(Collectors.toList());
      studentDetail.setStudentCourseList(convertCourseList);
      studentDetailList.add(studentDetail);
    });
    return studentDetailList;
  }
}
