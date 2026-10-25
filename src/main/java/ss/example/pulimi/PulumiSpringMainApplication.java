package ss.example.pulimi;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("ss.example.pulimi")
public class PulumiSpringMainApplication {

    public static void main(String[] args) {
        SpringApplication.run(PulumiSpringMainApplication.class, args);
    }
}
