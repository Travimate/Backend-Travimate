package id.synrgy.travimate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableAutoConfiguration
@SpringBootApplication
public class TravimateApplication {

	public static void main(String[] args) {
		SpringApplication.run(TravimateApplication.class, args);
	}

}
