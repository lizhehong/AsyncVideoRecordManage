package cn.hy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling

public class AsyncVideoRecorderManageApplication {

	public static void main(String[] args) {
		SpringApplication.run(AsyncVideoRecorderManageApplication.class, args);
	}
}
