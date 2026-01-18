package pt.nelsonalmeida.reservation.util; // <--- Mudado para 'reservation'

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pt.nelsonalmeida.reservation.dto.MessageEnvelope; // <--- Import correto

@Slf4j
@Component
@RequiredArgsConstructor
public class MessageEnvelopeConverter {

    private final ObjectMapper objectMapper;

    /**
     * Converte uma String JSON num MessageEnvelope tipado.
     */
    public <T> MessageEnvelope<T> convertFromJson(String json, Class<T> payloadType) {
        try {
            // 1. Deserializar para um Envelope genérico (Object)
            TypeReference<MessageEnvelope<Object>> typeRef = new TypeReference<>() {};
            MessageEnvelope<Object> rawEnvelope = objectMapper.readValue(json, typeRef);

            // 2. Converter o payload interno para o tipo específico T
            T typedPayload = objectMapper.convertValue(rawEnvelope.getPayload(), payloadType);

            // 3. Criar e devolver o envelope final tipado
            return new MessageEnvelope<>(
                    rawEnvelope.getMessageId(),
                    rawEnvelope.getType(),
                    rawEnvelope.getTimestamp(),
                    rawEnvelope.getCorrelationId(),
                    rawEnvelope.getCausationId(),
                    typedPayload
            );

        } catch (JsonProcessingException e) {
            log.error("Failed to parse JSON message: {}", json, e);
            throw new IllegalArgumentException("Invalid message format", e);
        } catch (Exception e) {
            log.error("Error converting message: {}", json, e);
            throw new RuntimeException("Error processing message", e);
        }
    }

    /**
     * Garante que o payload dentro do envelope está no tipo certo (T).
     * Útil quando o Jackson deserializa o payload como LinkedHashMap.
     */
    public <T> MessageEnvelope<T> convertPayload(MessageEnvelope<?> envelope, Class<T> payloadType) {
        if (envelope == null) {
            return null;
        }

        Object rawPayload = envelope.getPayload();
        T typedPayload = convertRawPayload(rawPayload, payloadType);

        return new MessageEnvelope<>(
                envelope.getMessageId(),
                envelope.getType(),
                envelope.getTimestamp(),
                envelope.getCorrelationId(),
                envelope.getCausationId(),
                typedPayload
        );
    }

    public <T> T extractPayload(MessageEnvelope<?> envelope, Class<T> payloadType) {
        if (envelope == null) {
            return null;
        }
        return convertRawPayload(envelope.getPayload(), payloadType);
    }

    private <T> T convertRawPayload(Object rawPayload, Class<T> targetType) {
        if (rawPayload == null) {
            return null;
        } else if (targetType.isInstance(rawPayload)) {
            return targetType.cast(rawPayload);
        } else {
            return objectMapper.convertValue(rawPayload, targetType);
        }
    }
}