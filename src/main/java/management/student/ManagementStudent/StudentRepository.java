package management.student.ManagementStudent;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface StudentRepository {

  @Select("SELECT * FROM student WHERE name = #{name}")
    //引数のnameと#のnameが紐づいている
  Student searchByName(String name);

}
