package pt.ulusofona.cd.reservation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pt.ulusofona.cd.reservation.model.Reservation;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, UUID> {

    List<Reservation> findByCustomerEmail(String customerEmail);

    List<Reservation> findByCustomerEmailAndScheduledAt(String customerEmail, LocalDateTime scheduledAt);

    List<Reservation> findByRestaurantIdAndIsConfirmedTrueAndScheduledAtBetween(
            UUID restaurantId, LocalDateTime start, LocalDateTime end
    );


    List<Reservation> findByIsPending();
    List<Reservation> findByIsConfirmed();
    List<Reservation> findByIsCancelled();
}