package pt.ulusofona.cd.reservation.events;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import pt.ulusofona.cd.reservation.dto.MessageEnvelope;
import pt.ulusofona.cd.reservation.dto.ReservationConfirmedEvent;

import java.time.Instant;
import java.util.UUID;

@Component
public class ReservationConfirmedProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${reservation.events.reservation-confirmed}")
    private String topic;

    public ReservationConfirmedProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void send(ReservationConfirmedEvent event) {
        MessageEnvelope<ReservationConfirmedEvent> envelope = new MessageEnvelope<>(
                UUID.randomUUID(),
                "ReservationConfirmedEvent",
                Instant.now(),
                event.getReservationId().toString(),
                "reservation-service:confirm",
                event
        );

        kafkaTemplate.send(topic, event.getReservationId().toString(), envelope)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        System.out.println("Published ReservationConfirmed event for: " + event.getReservationId());
                    } else {
                        System.err.println("Failed to publish ReservationConfirmed event: " + ex.getMessage());
                    }
                });
    }
}