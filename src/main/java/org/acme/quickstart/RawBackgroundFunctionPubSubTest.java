package org.acme.quickstart;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;
import com.google.cloud.firestore.WriteResult;
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
import java.util.concurrent.ExecutionException;

@Named("rawPubSubTest")
@ApplicationScoped
public class RawBackgroundFunctionPubSubTest implements RawBackgroundFunction {
    @Inject
    GreetingService greetingService;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static final Firestore firestore = FirestoreOptions.getDefaultInstance().getService();

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

        // Sauvegarder dans Firestore
        saveOrderToFirestore(decodedData);

        System.out.println("Be polite, say " + greetingService.hello());
    }

    private void saveOrderToFirestore(Order order) {
        try {
            WriteResult result = firestore.collection("orders")
                    .document(order.name + "_" + System.currentTimeMillis()) // Nom unique du document
                    .set(order)
                    .get(); // Récupérer le résultat

            System.out.println("Order saved at: " + result.getUpdateTime());
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Error saving order to Firestore: " + e.getMessage());
        }
    }
}
