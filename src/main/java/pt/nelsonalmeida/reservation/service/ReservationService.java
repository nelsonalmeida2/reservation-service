package pt.nelsonalmeida.reservation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.nelsonalmeida.reservation.client.RestaurantClient;
import pt.nelsonalmeida.reservation.dto.ReservationCancelledEvent;
import pt.nelsonalmeida.reservation.dto.ReservationConfirmedEvent;
import pt.nelsonalmeida.reservation.dto.ReservationCreatedEvent;
import pt.nelsonalmeida.reservation.dto.ReservationRequest;
import pt.nelsonalmeida.reservation.events.ReservationCancelledProducer;
import pt.nelsonalmeida.reservation.events.ReservationConfirmedProducer;
import pt.nelsonalmeida.reservation.events.ReservationCreatedProducer;
import pt.nelsonalmeida.reservation.exception.ReservationNotFoundException;
import pt.nelsonalmeida.reservation.mapper.ReservationMapper;
import pt.nelsonalmeida.reservation.model.Reservation;
import pt.nelsonalmeida.reservation.repository.ReservationRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final RestaurantClient restaurantClient;
    private final ReservationCreatedProducer createdProducer;
    private final ReservationConfirmedProducer confirmedProducer;
    private final ReservationCancelledProducer cancelledProducer;

    @Transactional
    public Reservation createReservation(ReservationRequest request) {

        List<Reservation> existingReservations = reservationRepository
                .findByCustomerEmailAndScheduledAt(request.getCustomerEmail(), request.getScheduledAt());

        boolean hasActiveReservation = existingReservations.stream()
                .anyMatch(r -> !r.isCancelled());

        if (hasActiveReservation) {
            throw new IllegalArgumentException("O cliente já possui uma reserva ativa para esta hora.");
        }

        try {
            boolean isAvailable = restaurantClient.checkAvailability(
                    request.getRestaurantId(),
                    request.getScheduledAt(),
                    request.getPartySize()
            );
            if (!isAvailable) {
                throw new IllegalArgumentException("O restaurante não tem disponibilidade.");
            }
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalStateException("Serviço de restaurantes indisponível.");
        }

        Reservation reservation = ReservationMapper.toEntity(request);
        Reservation savedReservation = reservationRepository.save(reservation);

        createdProducer.send(new ReservationCreatedEvent(
                savedReservation.getId(),
                savedReservation.getRestaurantId(),
                savedReservation.getCustomerEmail()
        ));

        return savedReservation;
    }

    @Transactional
    public void confirmReservation(UUID reservationId) {
        Reservation reservation = getReservationById(reservationId);

        if (!reservation.isPending()) {
            throw new IllegalStateException("Apenas reservas pendentes podem ser confirmadas.");
        }

        reservation.setConfirmed(true);
        reservation.setPending(false);
        reservation.setCancelled(false);
        reservationRepository.save(reservation);

        confirmedProducer.send(new ReservationConfirmedEvent(
                reservation.getId(),
                reservation.getRestaurantId(),
                reservation.getScheduledAt(),
                reservation.getPartySize(),
                reservation.getCustomerEmail()
        ));
    }

    @Transactional
    public void cancelReservation(UUID reservationId) {
        Reservation reservation = getReservationById(reservationId);

        if (reservation.isCancelled()) {
            return;
        }

        reservation.setCancelled(true);
        reservation.setPending(false);
        reservation.setConfirmed(false);
        reservationRepository.save(reservation);


        try {
            restaurantClient.releaseSeats(
                    reservation.getRestaurantId(),
                    reservation.getScheduledAt(),
                    reservation.getPartySize()
            );
        } catch (Exception e) {
            System.err.println("Erro ao libertar lugares no restaurante: " + e.getMessage());
        }


        cancelledProducer.send(new ReservationCancelledEvent(
                reservation.getId(),
                reservation.getRestaurantId(),
                reservation.getScheduledAt(),
                reservation.getPartySize(),
                reservation.getCustomerEmail(),
                "Cancelado pelo cliente",
                LocalDateTime.now()
        ));
    }

    public Reservation getReservationById(UUID id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new ReservationNotFoundException("Reserva não encontrada: " + id));
    }

    public List<Reservation> getReservationsByClient(String email) {
        return reservationRepository.findByCustomerEmail(email);
    }

    public List<Reservation> getRestaurantSchedule(UUID restaurantId, LocalDateTime date) {
        LocalDateTime startOfDay = date.toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = date.toLocalDate().atTime(23, 59, 59);

        return reservationRepository.findByRestaurantIdAndIsConfirmedTrueAndScheduledAtBetween(
                restaurantId, startOfDay, endOfDay
        );
    }
}