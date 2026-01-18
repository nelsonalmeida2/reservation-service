package pt.nelsonalmeida.reservation.events;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import pt.nelsonalmeida.reservation.dto.MessageEnvelope;
import pt.nelsonalmeida.reservation.dto.ReservationCreatedEvent;

import java.time.Instant;
import java.util.UUID;

@Component
public class ReservationCreatedProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${reservation.events.reservation-created}")
    private String topic;

    public ReservationCreatedProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void send(ReservationCreatedEvent event) {
        MessageEnvelope<ReservationCreatedEvent> envelope = new MessageEnvelope<>(
                UUID.randomUUID(),
                "ReservationCreatedEvent",
                Instant.now(),
                event.getReservationId().toString(),
                "reservation-service:create",
                event
        );

        kafkaTemplate.send(topic, event.getReservationId().toString(), envelope)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        System.out.println("Published ReservationCreated event for: " + event.getReservationId());
                    } else {
                        System.err.println("Failed to publish ReservationCreated event: " + ex.getMessage());
                    }
                });
    }
}