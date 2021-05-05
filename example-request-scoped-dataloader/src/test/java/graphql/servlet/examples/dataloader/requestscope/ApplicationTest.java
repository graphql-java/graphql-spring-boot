package graphql.servlet.examples.dataloader.requestscope;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ApplicationTest {

  @Autowired private TestRestTemplate restTemplate;

  @Autowired private CustomerRepository repository;

  @Test
  void testSanity() {
    String schema = this.restTemplate.getForObject("/graphql/schema.json", String.class);
    assertNotNull(schema);
  }

  @Test
  void testRequestScope() {

    String requestGraphQL =
        "query {\n"
            + "  walmartCustomers(storeNumber:4177){\n"
            + "    customerId\n"
            + "    name\n"
            + "  }\n"
            + "}";
    HttpHeaders headers = new HttpHeaders();
    headers.add("content-type", "application/graphql");
    ResponseEntity<JsonNode> response =
        this.restTemplate.postForEntity(
            "/graphql", new HttpEntity<>(requestGraphQL, headers), JsonNode.class);

    assertThat(response.getBody())
        .hasToString(
            "{\"data\":{\"walmartCustomers\":[{\"customerId\":101,\"name\":\"Customer Name 1\"},{\"customerId\":102,\"name\":\"Customer Name 2\"},{\"customerId\":103,\"name\":\"Customer Name 3\"},{\"customerId\":104,\"name\":\"Customer Name 4\"}]}}");

    repository.updateUsernameForId(101, "New Name 1");

    response =
        this.restTemplate.postForEntity(
            "/graphql", new HttpEntity<>(requestGraphQL, headers), JsonNode.class);

    assertThat(response.getBody())
        .hasToString(
            "{\"data\":{\"walmartCustomers\":[{\"customerId\":101,\"name\":\"New Name 1\"},{\"customerId\":102,\"name\":\"Customer Name 2\"},{\"customerId\":103,\"name\":\"Customer Name 3\"},{\"customerId\":104,\"name\":\"Customer Name 4\"}]}}");
  }
}
