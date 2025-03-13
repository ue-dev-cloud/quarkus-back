package org.acme.quickstart;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.functions.Context;
import com.google.cloud.functions.RawBackgroundFunction;
import org.acme.quickstart.models.Order;
import org.acme.quickstart.models.PubSubMessage;
import org.acme.quickstart.service.GreetingService;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Named("rawPubSubTest")
@ApplicationScoped
public class RawBackgroundFunctionPubSubTest implements RawBackgroundFunction {
    @Inject
    GreetingService greetingService;

    private static final ObjectMapper objectMapper = new ObjectMapper();


    @Override
    public void accept(String event, Context context) throws Exception {
        System.out.println("PubSub event: " + event);

        PubSubMessage message = objectMapper.readValue(event, PubSubMessage.class);
        System.out.println("PubSub message: " + message);

        // Décoder la donnée en Base64
        String decodedJson = new String(Base64.getDecoder().decode(message.data), StandardCharsets.UTF_8);
        System.out.println("Decoded PubSub JSON: " + decodedJson);

        // Désérialiser decodedJson en objet Java
        Order decodedData = objectMapper.readValue(decodedJson, Order.class);

        // Utiliser les attributs du JSON
        System.out.println("name: " + decodedData.name);
        System.out.println("address: " + decodedData.address);
        System.out.println("product: " + decodedData.product);

        System.out.println("Be polite, say " + greetingService.hello());
    }
}
