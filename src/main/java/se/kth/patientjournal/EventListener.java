package se.kth.patientjournal;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import com.fasterxml.jackson.databind.ObjectMapper;
import se.kth.patientjournal.dto.MessageCreatedEvent;

@ApplicationScoped
public class EventListener {

    @Inject
    NotificationWebSocket webSocket;

    @Inject
    ObjectMapper objectMapper;

    @Incoming("message-events-in")
    public void consume(MessageCreatedEvent event) {

        System.out.println("Received event for: " + event.receiverId);

        try {
            String jsonOutput = objectMapper.writeValueAsString(event);

            webSocket.sendToUser(event.receiverId.toString(), jsonOutput);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
