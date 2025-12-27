package pt.ulusofona.cd.reservation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pt.ulusofona.cd.reservation.model.Reservation;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, UUID> {


    List<Reservation> findByCustomerEmail(String customerEmail);



    // Todas as pendentes (para processamento)
    List<Reservation> findByIsPendingTrue();

    // Todas as confirmadas (para estatísticas ou histórico válido)
    List<Reservation> findByIsConfirmedTrue();


    // Aqui assumimos que o restaurante só quer ver quem está confirmado para aparecer
    List<Reservation> findByRestaurantIdAndIsConfirmedTrueAndScheduledAtBetween(
            UUID restaurantId, LocalDateTime start, LocalDateTime end
    );

    // 4. Validação de Duplicados (Cliente não pode ter reservas confirmadas ou pendentes à mesma hora)
    @Query("SELECT COUNT(r) > 0 FROM Reservation r " +
            "WHERE r.customerEmail = :email " +
            "AND r.scheduledAt = :scheduledAt " +
            "AND (r.isConfirmed = true OR r.isPending = true)")
    boolean existsActiveReservation(@Param("email") String email,
                                    @Param("scheduledAt") LocalDateTime scheduledAt);


    // Soma apenas se estiver CONFIRMADA (ignoramos pendentes e canceladas aqui)
    @Query("SELECT COALESCE(SUM(r.partySize), 0) FROM Reservation r " +
            "WHERE r.restaurantId = :restaurantId " +
            "AND r.isConfirmed = true " +
            "AND r.scheduledAt BETWEEN :start AND :end")
    int countPeopleConfirmed(@Param("restaurantId") UUID restaurantId,
                             @Param("start") LocalDateTime start,
                             @Param("end") LocalDateTime end);
}