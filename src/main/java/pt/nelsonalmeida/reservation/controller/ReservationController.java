package pt.nelsonalmeida.reservation.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pt.nelsonalmeida.reservation.dto.ReservationRequest;
import pt.nelsonalmeida.reservation.dto.ReservationResponse;
import pt.nelsonalmeida.reservation.mapper.ReservationMapper;
import pt.nelsonalmeida.reservation.model.Reservation;
import pt.nelsonalmeida.reservation.service.ReservationService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping
    public ResponseEntity<ReservationResponse> createReservation(@Valid @RequestBody ReservationRequest request) {
        Reservation created = reservationService.createReservation(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ReservationMapper.toResponse(created));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelReservation(@PathVariable UUID id) {
        reservationService.cancelReservation(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/confirm")
    public ResponseEntity<Void> confirmReservation(@PathVariable UUID id) {
        reservationService.confirmReservation(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservationResponse> getReservationById(@PathVariable UUID id) {
        Reservation reservation = reservationService.getReservationById(id);
        return ResponseEntity.ok(ReservationMapper.toResponse(reservation));
    }

    @GetMapping("/client")
    public ResponseEntity<List<ReservationResponse>> getReservationsByClient(@RequestParam String email) {
        List<Reservation> reservations = reservationService.getReservationsByClient(email);
        List<ReservationResponse> response = reservations.stream()
                .map(ReservationMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/restaurant/{id}/schedule")
    public ResponseEntity<List<ReservationResponse>> getRestaurantSchedule(
            @PathVariable UUID id,
            @RequestParam LocalDateTime date) {

        List<Reservation> schedule = reservationService.getRestaurantSchedule(id, date);
        List<ReservationResponse> response = schedule.stream()
                .map(ReservationMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }
}