package management.student;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import management.student.repository.StudentRepository;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@OpenAPIDefinition(info = @Info(title = "受講生管理システム"))
@SpringBootApplication
@MapperScan
public class ManagementStudentApplication {

  @Autowired
  //Springが管理しているインスタンスをメンバ変数に入れてくれる（自動で紐づけしますよ。）
  private StudentRepository repository;

  public static void main(String[] args) {
    SpringApplication.run(ManagementStudentApplication.class, args);
  }


}
