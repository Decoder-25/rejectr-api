package dev.decoder.rejectr;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@OpenAPIDefinition(
        info = @Info(
                title = "Rejectr API",
                version = "v1",
                description = "Anonymous job-rejection tracker. A shred stores only the category "
                        + "(stage, outcome, reason) - the story text is never persisted."
        )
)
@SpringBootApplication
public class RejectrApplication {

	public static void main(String[] args) {
		SpringApplication.run(RejectrApplication.class, args);
	}

}
