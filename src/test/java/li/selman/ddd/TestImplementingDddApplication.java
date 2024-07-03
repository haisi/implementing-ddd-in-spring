package li.selman.ddd;

import org.springframework.boot.SpringApplication;

public class TestImplementingDddApplication {

  public static void main(String[] args) {
    SpringApplication.from(ImplementingDddApplication::main)
        .with(TestcontainersConfiguration.class)
        .run(args);
  }
}
