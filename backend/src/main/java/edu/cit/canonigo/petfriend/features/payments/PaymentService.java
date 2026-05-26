package edu.cit.canonigo.petfriend.features.payments;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.math.RoundingMode;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.cit.canonigo.petfriend.model.Booking;

@Service
public class PaymentService {

    private static final String PAYMONGO_CHECKOUT_URL = "https://api.paymongo.com/v1/checkout_sessions";

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${paymongo.secretKey:}")
    private String paymongoSecretKey;

    @Value("${paymongo.successUrl:}")
    private String successUrl;

    @Value("${paymongo.cancelUrl:}")
    private String cancelUrl;

    public PaymentService(ObjectMapper objectMapper) {
        this.restTemplate = new RestTemplate();
        this.objectMapper = objectMapper;
    }

    public CheckoutResult createCheckoutSession(Booking booking) throws PaymentException {
        if (paymongoSecretKey == null || paymongoSecretKey.isBlank()) {
            throw new PaymentException("PayMongo secret key is not configured");
        }

        int amount = toCentavos(booking.getTotalAmount());
        Map<String, Object> payload = new HashMap<>();
        Map<String, Object> data = new HashMap<>();
        Map<String, Object> attributes = new HashMap<>();

        Map<String, Object> lineItem = new HashMap<>();
        lineItem.put("name", "PetFriend booking");
        lineItem.put("amount", amount);
        lineItem.put("currency", booking.getCurrency() == null ? "PHP" : booking.getCurrency());
        lineItem.put("quantity", 1);

        attributes.put("line_items", List.of(lineItem));
        attributes.put("payment_method_types", List.of("card", "gcash", "grab_pay"));
        attributes.put("success_url", successUrl);
        attributes.put("cancel_url", cancelUrl);
        attributes.put("description", "PetFriend booking payment");

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("bookingId", booking.getBookingId() == null ? null : booking.getBookingId().toString());
        metadata.put("ownerId", booking.getOwner() == null ? null : booking.getOwner().getUserId().toString());
        metadata.put("sitterId", booking.getSitter() == null ? null : booking.getSitter().getUserId().toString());
        attributes.put("metadata", metadata);

        data.put("attributes", attributes);
        payload.put("data", data);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", buildBasicAuth(paymongoSecretKey));

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                PAYMONGO_CHECKOUT_URL,
                HttpMethod.POST,
                request,
                String.class
        );

        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new PaymentException("PayMongo checkout failed");
        }

        try {
            JsonNode root = objectMapper.readTree(response.getBody());
            String paymentId = root.path("data").path("id").asText("");
            String checkoutUrl = root.path("data").path("attributes").path("checkout_url").asText("");
            if (checkoutUrl.isBlank()) {
                throw new PaymentException("PayMongo did not return a checkout URL");
            }
            return new CheckoutResult(checkoutUrl, paymentId);
        } catch (Exception e) {
            throw new PaymentException("Unable to parse PayMongo response");
        }
    }

    public static class CheckoutResult {
        private final String checkoutUrl;
        private final String paymentId;

        public CheckoutResult(String checkoutUrl, String paymentId) {
            this.checkoutUrl = checkoutUrl;
            this.paymentId = paymentId;
        }

        public String getCheckoutUrl() {
            return checkoutUrl;
        }

        public String getPaymentId() {
            return paymentId;
        }
    }

    public static class PaymentException extends Exception {
        public PaymentException(String message) {
            super(message);
        }
    }

    private int toCentavos(BigDecimal amount) {
        if (amount == null) return 0;
        return amount.multiply(new BigDecimal("100")).setScale(0, RoundingMode.HALF_UP).intValue();
    }

    private String buildBasicAuth(String secretKey) {
        String token = secretKey + ":";
        String encoded = Base64.getEncoder().encodeToString(token.getBytes(StandardCharsets.UTF_8));
        return "Basic " + encoded;
    }
}
