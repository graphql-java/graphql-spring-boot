package graphql.kickstart.graphql.annotations.test.custom.relay;

import graphql.relay.Relay;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("custom-relay-test")
public class CustomRelay extends Relay {}
