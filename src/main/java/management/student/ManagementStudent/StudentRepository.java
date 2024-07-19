package management.student.ManagementStudent;

import java.util.List;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface StudentRepository {

  @Select("SELECT * FROM student WHERE name = #{name}")
    //引数のnameと#のnameが紐づいている
  Student searchByName(String name);

  @Select("SELECT * FROM student")
  List<Student> searchAllStudent();

  @Insert("INSERT student VALUES(#{name},#{age})")
  void registerStudent(String name, int age);

  @Update("UPDATE student SET age = #{age} where name = #{name}")
  void updateStudent(String name, int age);

  @Delete("DELETE FROM student WHERE name = #{name}")
  void deleteStudent(String name);


}
