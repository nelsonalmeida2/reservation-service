package pt.nelsonalmeida.reservation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pt.nelsonalmeida.reservation.model.Reservation;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, UUID> {

    // 1. Para verificar duplicados na criação
    List<Reservation> findByCustomerEmailAndScheduledAt(String customerEmail, LocalDateTime scheduledAt);

    // 2. Para listar reservas do cliente
    List<Reservation> findByCustomerEmail(String customerEmail);

    // 3. O MÉTODO QUE DAVA ERRO (CORRIGIDO)
    // Antes estava: findByIsPending(); -> O Spring pedia um argumento boolean.
    // Agora fica: findByIsPendingTrue(); -> O Spring sabe que é para procurar "isPending = true"
    List<Reservation> findByIsPendingTrue();

    // 4. Para o Restaurante ver a agenda (apenas confirmadas)
    List<Reservation> findByRestaurantIdAndIsConfirmedTrueAndScheduledAtBetween(
            UUID restaurantId,
            LocalDateTime start,
            LocalDateTime end
    );
}