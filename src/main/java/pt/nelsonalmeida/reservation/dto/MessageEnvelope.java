package pt.nelsonalmeida.reservation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageEnvelope<T> {
    private UUID messageId;
    private String type;
    private Instant timestamp;
    private String correlationId;
    private String causationId;
    private T payload;
}