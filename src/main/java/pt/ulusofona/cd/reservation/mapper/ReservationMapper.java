package pt.ulusofona.cd.reservation.mapper;

import pt.ulusofona.cd.reservation.dto.ReservationRequest;
import pt.ulusofona.cd.reservation.dto.ReservationResponse;
import pt.ulusofona.cd.reservation.model.Reservation;

public class ReservationMapper {

    public static Reservation toEntity(ReservationRequest dto) {
        Reservation reservation = new Reservation();

        reservation.setRestaurantId(dto.getRestaurantId());
        reservation.setCustomerName(dto.getCustomerName());
        reservation.setCustomerEmail(dto.getCustomerEmail());
        reservation.setPartySize(dto.getPartySize());
        reservation.setScheduledAt(dto.getScheduledAt());
        reservation.setPending(true);
        reservation.setConfirmed(false);
        reservation.setCancelled(false);

        return reservation;
    }

    public static ReservationResponse toResponse(Reservation entity) {
        ReservationResponse response = new ReservationResponse();

        response.setId(entity.getId());
        response.setRestaurantId(entity.getRestaurantId());
        response.setCustomerName(entity.getCustomerName());
        response.setCustomerEmail(entity.getCustomerEmail());
        response.setPartySize(entity.getPartySize());
        response.setScheduledAt(entity.getScheduledAt());

        response.setConfirmed(entity.isConfirmed());
        response.setCancelled(entity.isCancelled());
        response.setPending(entity.isPending());

        response.setCreatedAt(entity.getCreatedAt());

        return response;
    }
}