package management.student.converter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import management.student.data.Student;
import management.student.data.StudentCourses;
import management.student.domain.StudentDetail;
import org.springframework.stereotype.Component;

@Component
public class StudentConverter {

  /**
   * 受講生テーブルと受講生コーステーブルを使用して、受講生詳細を作成する
   *
   * @param students 　受講生
   * @param courses  　受講生コース
   * @return 受講生詳細情報
   */
  public List<StudentDetail> convertStudentDetails(List<Student> students,
      List<StudentCourses> courses) {

    List<StudentDetail> studentDetails = new ArrayList<>();
    //受講生と受講生テーブルで紐づくものを受講生詳細に変換
    students.forEach(student -> {
      StudentDetail studentDetail = new StudentDetail();
      studentDetail.setStudent(student);
      List<StudentCourses> convertCourses = courses.stream()
          .filter(course -> student.getId() == course.getId()).collect(Collectors.toList());
      studentDetail.setStudentCourses(convertCourses);
      studentDetails.add(studentDetail);
    });
    return studentDetails;
  }
}
