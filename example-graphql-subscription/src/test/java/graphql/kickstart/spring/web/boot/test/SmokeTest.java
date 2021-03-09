package graphql.kickstart.spring.web.boot.test;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisplayName("Basic smoke test")
public class SmokeTest {

    @Test
    @DisplayName("Ensure that Spring context loads successfully.")
    void contextLoads() {

    }
}
