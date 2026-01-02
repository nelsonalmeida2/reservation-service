package pt.ulusofona.cd.reservation.mapper;

import pt.ulusofona.cd.reservation.dto.ReservationRequest;
import pt.ulusofona.cd.reservation.dto.ReservationResponse;
import pt.ulusofona.cd.reservation.model.Reservation;

public class ReservationMapper {

    public static Reservation toEntity(ReservationRequest request) {
        Reservation reservation = new Reservation();
        reservation.setRestaurantId(request.getRestaurantId());
        reservation.setCustomerName(request.getCustomerName());
        reservation.setCustomerEmail(request.getCustomerEmail());
        reservation.setScheduledAt(request.getScheduledAt());
        reservation.setPartySize(request.getPartySize());
        reservation.setPending(true);
        return reservation;
    }

    public static ReservationResponse toResponse(Reservation reservation) {
        ReservationResponse response = new ReservationResponse();
        response.setId(reservation.getId());
        response.setRestaurantId(reservation.getRestaurantId());
        response.setCustomerName(reservation.getCustomerName());
        response.setCustomerEmail(reservation.getCustomerEmail());
        response.setPartySize(reservation.getPartySize());
        response.setScheduledAt(reservation.getScheduledAt());
        response.setConfirmed(reservation.isConfirmed());
        response.setCancelled(reservation.isCancelled());
        response.setPending(reservation.isPending());
        response.setCreatedAt(reservation.getCreatedAt());
        return response;
    }
}