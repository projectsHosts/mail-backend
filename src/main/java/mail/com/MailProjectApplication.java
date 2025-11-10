package mail.com;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MailProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(MailProjectApplication.class, args);
		System.out.println("Successful Run");
	}

}
