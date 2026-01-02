package pt.ulusofona.cd.reservation.events;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import pt.ulusofona.cd.reservation.dto.MessageEnvelope;
import pt.ulusofona.cd.reservation.dto.ReservationCancelledEvent;

import java.time.Instant;
import java.util.UUID;

@Component
public class ReservationCancelledProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${reservation.events.reservation-cancelled}")
    private String topic;

    public ReservationCancelledProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void send(ReservationCancelledEvent event) {
        MessageEnvelope<ReservationCancelledEvent> envelope = new MessageEnvelope<>(
                UUID.randomUUID(),
                "ReservationCancelledEvent",
                Instant.now(),
                event.getReservationId().toString(),
                "reservation-service:cancel",
                event
        );

        kafkaTemplate.send(topic, event.getReservationId().toString(), envelope)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        System.out.println("Published ReservationCancelled event for: " + event.getReservationId());
                    } else {
                        System.err.println("Failed to publish ReservationCancelled event: " + ex.getMessage());
                    }
                });
    }
}