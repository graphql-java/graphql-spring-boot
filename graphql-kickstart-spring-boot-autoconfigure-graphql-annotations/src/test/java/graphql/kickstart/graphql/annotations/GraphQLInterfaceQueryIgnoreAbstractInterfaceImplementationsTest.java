package graphql.kickstart.graphql.annotations;

import static org.assertj.core.api.Assertions.assertThat;

import com.graphql.spring.boot.test.GraphQLResponse;
import com.graphql.spring.boot.test.GraphQLTestTemplate;
import graphql.kickstart.graphql.annotations.test.interfaces.Car;
import graphql.kickstart.graphql.annotations.test.interfaces.Truck;
import graphql.schema.GraphQLNamedType;
import graphql.schema.GraphQLScalarType;
import graphql.schema.GraphQLSchema;
import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@DisplayName("Testing interface handling (ignore abstract implementations).")
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = "graphql.annotations.ignore-abstract-interface-implementations=true")
@ActiveProfiles({"test", "interface-test"})
class GraphQLInterfaceQueryIgnoreAbstractInterfaceImplementationsTest {

  @Autowired private GraphQLTestTemplate graphQLTestTemplate;

  @Autowired private GraphQLSchema graphQLSchema;

  @Test
  @DisplayName("Assert that GraphQL interfaces and their implementations are registered correctly.")
  void testInterfaceQuery() throws IOException {
    // WHEN
    final GraphQLResponse actual =
        graphQLTestTemplate.postForResource("queries/test-interface-query.graphql");
    // THEN
    assertThat(actual.get("$.data.vehicles[0]", Car.class))
        .usingRecursiveComparison()
        .ignoringAllOverriddenEquals()
        .isEqualTo(Car.builder().numberOfSeats(4).registrationNumber("ABC-123").build());
    assertThat(actual.get("$.data.vehicles[1]", Truck.class))
        .usingRecursiveComparison()
        .ignoringAllOverriddenEquals()
        .isEqualTo(Truck.builder().cargoWeightCapacity(12).registrationNumber("CBA-321").build());
  }

  @Test
  @DisplayName(
      "Assert that abstract GraphQL interface implementations are excluded from the schema.")
  void testInterfaceImplementationDetection() {
    // THEN
    Set<String> vehicleDomainTypes =
        graphQLSchema.getAllTypesAsList().stream()
            .filter(type -> !(type instanceof GraphQLScalarType))
            .map(GraphQLNamedType::getName)
            .filter(name -> !name.startsWith("__"))
            .filter(name -> !"PageInfo".equals(name))
            .collect(Collectors.toSet());
    // Must not contain "AbstractVehicle"
    assertThat(vehicleDomainTypes)
        .containsExactlyInAnyOrder("InterfaceQuery", "Vehicle", "Car", "Truck");
  }
}
