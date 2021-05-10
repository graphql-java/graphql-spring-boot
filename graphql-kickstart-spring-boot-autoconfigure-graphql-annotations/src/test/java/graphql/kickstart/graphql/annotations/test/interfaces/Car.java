package graphql.kickstart.graphql.annotations.test.interfaces;

import graphql.annotations.annotationTypes.GraphQLField;
import graphql.annotations.annotationTypes.GraphQLNonNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
// “implements Vehicle” has to be repeated here although already inherited from AbstractVehicle
// because otherwise GraphQL-Java Annotations would not find this class.
public class Car extends AbstractVehicle implements Vehicle {

  @GraphQLField @GraphQLNonNull private int numberOfSeats;

  public Car(String registrationNumber, int numberOfSeats) {
    super(registrationNumber);
    this.numberOfSeats = numberOfSeats;
  }
}
