package pt.ulusofona.cd.reservation.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.HashMap;
import java.util.Map;
import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ReservationNotFoundException.class)
    public ResponseEntity<Map<String,Object>> handleReservationNotFoundException(ReservationNotFoundException ex){
        Map<String, Object> error = new HashMap<>();
        error.put("error", "Reservation Not Found");
        error.put("message", ex.getMessage());
        error.put("timestamp", Instant.now());
        error.put("status", HttpStatus.NOT_FOUND.value());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(IllegalArgumentException ex) {
        Map<String, Object> error = new HashMap<>();
        error.put("error", "Bad Request");
        error.put("message", ex.getMessage());
        error.put("timestamp", Instant.now());
        error.put("status", HttpStatus.BAD_REQUEST.value());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
}
