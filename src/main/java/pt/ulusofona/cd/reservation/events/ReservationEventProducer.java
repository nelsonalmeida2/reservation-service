package pt.ulusofona.cd.reservation.events;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import pt.ulusofona.cd.reservation.dto.MessageEnvelope;
import pt.ulusofona.cd.reservation.dto.ReservationCancelledEvent;
import pt.ulusofona.cd.reservation.dto.ReservationConfirmedEvent;
import pt.ulusofona.cd.reservation.dto.ReservationCreatedEvent;

import java.time.Instant;
import java.util.UUID;

@Service
public class ReservationEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;


    @Value("${reservation.events.reservation-created}")
    private String reservationCreatedTopic;

    @Value("${reservation.events.reservation-confirmed}")
    private String reservationConfirmedTopic;

    @Value("${reservation.events.reservation-cancelled}")
    private String reservationCancelledTopic;

    public ReservationEventProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    // created
    public void sendReservationCreated(ReservationCreatedEvent event) {
        MessageEnvelope<ReservationCreatedEvent> envelope = new MessageEnvelope<>(
                UUID.randomUUID(),                  // messageId
                "ReservationCreatedEvent",          // type
                Instant.now(),                      // timestamp
                event.getReservationId().toString(),// correlationId
                "reservation-service:create",       // causationId
                event                               // payload
        );

        kafkaTemplate.send(reservationCreatedTopic, event.getReservationId().toString(), envelope)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        System.out.println("Published ReservationCreated event for: " + event.getReservationId());
                    } else {
                        System.err.println("Failed to publish ReservationCreated event: " + ex.getMessage());
                    }
                });
    }

    // confirmed
    public void sendReservationConfirmed(ReservationConfirmedEvent event) {
        MessageEnvelope<ReservationConfirmedEvent> envelope = new MessageEnvelope<>(
                UUID.randomUUID(),
                "ReservationConfirmedEvent",
                Instant.now(),
                event.getReservationId().toString(),
                "reservation-service:confirm",
                event
        );

        kafkaTemplate.send(reservationConfirmedTopic, event.getReservationId().toString(), envelope)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        System.out.println("Published ReservationConfirmed event for: " + event.getReservationId());
                    } else {
                        System.err.println("Failed to publish ReservationConfirmed event: " + ex.getMessage());
                    }
                });
    }

    // cancelled
    public void sendReservationCancelled(ReservationCancelledEvent event) {
        MessageEnvelope<ReservationCancelledEvent> envelope = new MessageEnvelope<>(
                UUID.randomUUID(),
                "ReservationCancelledEvent",
                Instant.now(),
                event.getReservationId().toString(),
                "reservation-service:cancel",
                event
        );

        kafkaTemplate.send(reservationCancelledTopic, event.getReservationId().toString(), envelope)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        System.out.println("Published ReservationCancelled event for: " + event.getReservationId());
                    } else {
                        System.err.println("Failed to publish ReservationCancelled event: " + ex.getMessage());
                    }
                });
    }
}