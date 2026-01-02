package pt.ulusofona.cd.reservation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.ulusofona.cd.reservation.client.RestaurantClient;
import pt.ulusofona.cd.reservation.dto.ReservationCancelledEvent;
import pt.ulusofona.cd.reservation.dto.ReservationConfirmedEvent;
import pt.ulusofona.cd.reservation.dto.ReservationCreatedEvent;
import pt.ulusofona.cd.reservation.dto.ReservationRequest;
import pt.ulusofona.cd.reservation.events.ReservationCancelledProducer;
import pt.ulusofona.cd.reservation.events.ReservationConfirmedProducer;
import pt.ulusofona.cd.reservation.events.ReservationCreatedProducer;
import pt.ulusofona.cd.reservation.exception.ReservationNotFoundException;
import pt.ulusofona.cd.reservation.mapper.ReservationMapper;
import pt.ulusofona.cd.reservation.model.Reservation;
import pt.ulusofona.cd.reservation.repository.ReservationRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final RestaurantClient restaurantClient;

    // Injeção dos Producers separados
    private final ReservationCreatedProducer createdProducer;
    private final ReservationConfirmedProducer confirmedProducer;
    private final ReservationCancelledProducer cancelledProducer;

    @Transactional
    public Reservation createReservation(ReservationRequest request) {

        // 1. Verificar se o cliente já tem reserva para aquela hora (evitar duplicados)
        List<Reservation> existingReservations = reservationRepository
                .findByCustomerEmailAndScheduledAt(request.getCustomerEmail(), request.getScheduledAt());

        boolean hasActiveReservation = existingReservations.stream()
                .anyMatch(r -> !r.isCancelled());

        if (hasActiveReservation) {
            throw new IllegalArgumentException("O cliente já possui uma reserva ativa para esta hora.");
        }

        // 2. Verificar disponibilidade no Restaurant Service (Chamada Síncrona via Feign)
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
            // Se o serviço de restaurante estiver em baixo, não deixamos criar reserva
            throw new IllegalStateException("Serviço de restaurantes indisponível.");
        }

        // 3. Guardar na BD local (Estado Pendente)
        Reservation reservation = ReservationMapper.toEntity(request);
        Reservation savedReservation = reservationRepository.save(reservation);

        // 4. Emitir evento de CRIAÇÃO
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

        // Atualizar estado
        reservation.setConfirmed(true);
        reservation.setPending(false);
        reservation.setCancelled(false);
        reservationRepository.save(reservation);

        // 5. Emitir evento de CONFIRMAÇÃO
        // Nota: Enviamos 'scheduledAt' porque a BD não tem 'slotId'
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
            return; // Já está cancelada, não faz nada
        }

        // Atualizar estado
        reservation.setCancelled(true);
        reservation.setPending(false);
        reservation.setConfirmed(false);
        reservationRepository.save(reservation);

        // 6. Emitir evento de CANCELAMENTO
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