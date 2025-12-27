package pt.ulusofona.cd.reservation.dto;

import java.util.UUID;

public class MessageEnvelope<T> {
    private UUID messageId;
    private String type;
    private String correlationId;
    private String causationId;
    private T payload;
}
