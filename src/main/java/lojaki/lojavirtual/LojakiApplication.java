package lojaki.lojavirtual;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@SpringBootApplication
@EntityScan(basePackages = "lojaki.lojavirtual.model")
@ComponentScan(basePackages = {"lojaki.*"})
@EnableJpaRepositories(basePackages = {"lojaki.lojavirtual.repository"})
@EnableTransactionManagement

public class LojakiApplication {

	public static void main(String[] args) {
		SpringApplication.run(LojakiApplication.class, args);
		
		System.out.println(new BCryptPasswordEncoder().encode("123"));
		
	}

}
