package lojaki.lojavirtual;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan(basePackages = "lojaki.lojavirtual.model")
public class LojakiApplication {

	public static void main(String[] args) {
		SpringApplication.run(LojakiApplication.class, args);
	}

}
