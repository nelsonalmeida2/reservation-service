package pt.ulusofona.cd.reservation.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReservationRequest {

    @NotNull(message = "Restaurant ID is required")
    private UUID restaurantId;

    @NotBlank(message = "Customer name is required")
    @Size(min = 1, max = 255, message = "Customer name must be between 1 and 255 characters")
    private String customerName;

    @Email(message = "Invalid email format")
    @Size(max = 255, message = "Email must be at most 255 characters long")
    private String customerEmail;

    @Min(value = 1, message = "Party size must be greater than zero")
    private int partySize;

    @Future(message = "Reservation date must be in the future")
    @NotNull
    private LocalDateTime scheduledAt;
}