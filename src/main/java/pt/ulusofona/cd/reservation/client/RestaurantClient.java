package pt.ulusofona.cd.reservation.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.UUID;

@FeignClient(name = "restaurant-service", url = "restaurant-service:8081")
public interface RestaurantClient {


    @GetMapping("/api/v1/restaurants/{restaurantId}/availability")
    boolean checkAvailability(
            @PathVariable("restaurantId") UUID restaurantId,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime date,
            @RequestParam("partySize") int partySize 
    );
}
