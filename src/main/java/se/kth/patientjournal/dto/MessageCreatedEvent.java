package se.kth.patientjournal.dto;

import java.util.UUID;

public class MessageCreatedEvent {
    public UUID messageId;
    public UUID sessionId;
    public UUID receiverId;
    public UUID senderId;
    public String content;
    public long timestamp;

    public MessageCreatedEvent() {}
}