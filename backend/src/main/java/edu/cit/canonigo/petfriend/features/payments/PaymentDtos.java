package edu.cit.canonigo.petfriend.features.payments;

import java.util.UUID;

public class PaymentDtos {

    public static class CheckoutRequest {
        private UUID bookingId;

        public UUID getBookingId() {
            return bookingId;
        }

        public void setBookingId(UUID bookingId) {
            this.bookingId = bookingId;
        }
    }

    public static class CheckoutResponse {
        private String checkoutUrl;
        private String paymentId;

        public CheckoutResponse(String checkoutUrl, String paymentId) {
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
}
