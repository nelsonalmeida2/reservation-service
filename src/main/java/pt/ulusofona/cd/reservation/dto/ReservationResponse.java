package pt.ulusofona.cd.reservation.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class ReservationResponse {
    private UUID id;
    private UUID restaurantId;
    private String customerName;
    private String customerEmail;
    private int partySize;
    private LocalDateTime scheduledAt;
    private boolean isConfirmed;
    private boolean isCancelled;
    private boolean isPending;

    private LocalDateTime createdAt;
}