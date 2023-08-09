package Controller;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EntityScan(basePackages = "Model")
@EnableJpaRepositories(basePackages = "Repository")
@ComponentScan(basePackages = {"Controller", "Service", "Security"})
@EnableTransactionManagement
public class TaskManApplication {

	public static void main(String[] args) {
		SpringApplication.run(TaskManApplication.class, args);
	}

}
