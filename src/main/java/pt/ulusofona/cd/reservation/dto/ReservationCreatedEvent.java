package pt.ulusofona.cd.reservation.events; // Ou .dto

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationCreatedEvent {
    private UUID reservationId;
    private UUID restaurantId;
    private String customerEmail;
}