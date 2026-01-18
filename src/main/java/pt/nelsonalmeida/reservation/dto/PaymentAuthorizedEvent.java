package pt.nelsonalmeida.reservation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentAuthorizedEvent {
    private UUID paymentId;
    private UUID reservationId;
    private Double amount;
    private LocalDateTime authorizedAt;
}