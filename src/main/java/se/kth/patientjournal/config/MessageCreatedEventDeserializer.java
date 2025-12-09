package se.kth.patientjournal.config;

import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer;
import se.kth.patientjournal.dto.MessageCreatedEvent;

public class MessageCreatedEventDeserializer extends ObjectMapperDeserializer<MessageCreatedEvent> {
    public MessageCreatedEventDeserializer() {
        super(MessageCreatedEvent.class);
    }
}