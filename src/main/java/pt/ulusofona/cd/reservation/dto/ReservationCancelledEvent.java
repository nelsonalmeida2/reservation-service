package pt.ulusofona.cd.reservation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationCancelledEvent {
    private UUID reservationId;
    private UUID restaurantId;
    private LocalDateTime scheduledAt;
    private Integer numberOfPeople;
    private String customerEmail;
    private String reason;
    private LocalDateTime cancelDate;
}