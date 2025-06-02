import org.example.PaymentProcessor;
import org.junit.Before;
import org.junit.Test;

import static org.example.PaymentProcessor.PaymentMethod.*;
import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

/**
 * Our bug fix fixes the functionality.
 */
public class PaymentProcessorTest {
    private PaymentProcessor processor;
    private static final double DELTA = 0.001;

    @Before
    public void setUp() {
        processor = new PaymentProcessor();
    }

    /**
     * Processing tests
     */
    @Test
    public void testFirstOrderCreditCard() {
        final var result = processor.processPayment(100.0, true, CREDIT_CARD);
        assertEquals(100 * 0.85 * 1.15, result, DELTA);
    }

    @Test
    public void testFirstOrderPayPal() {
        final var result = processor.processPayment(100.0, true, PAYPAL);
        assertEquals(100 * 0.88 * 1.15, result, DELTA);
    }

    @Test
    public void testFirstOrderCash() {
        final var result = processor.processPayment(100.0, true, CASH);
        assertEquals(100 * 0.90 * 1.15, result, DELTA);
    }

    @Test
    public void testNonFirstOrderCreditCard() {
        final var result = processor.processPayment(100.0, false, CREDIT_CARD);
        assertEquals(100 * 0.95 * 1.15, result, DELTA);
    }

    @Test
    public void testNonFirstOrderPayPal() {
        final var result = processor.processPayment(100.0, false, PAYPAL);
        // 100 * 1.15 * 0.98
        assertEquals(100 * 0.98 * 1.15, result, DELTA);
    }

    @Test
    public void testNonFirstOrderCash() {
        final var result = processor.processPayment(100.0, false, CASH);
        // 100 * 1.15 * 1
        assertEquals(100 * 1 * 1.15, result, DELTA);
    }

    /**
     * Boundary tests
     */
    @Test
    public void testMinimumValidAmount() {
        final var result = processor.processPayment(0.01, false, CASH);
        assertEquals(0.01, result, DELTA);
    }

    @Test
    public void testHighValueAmount() {
        final var result = processor.processPayment(10000.0, true, CREDIT_CARD);
        assertEquals(10000 * 0.85 * 1.15, result, DELTA);
    }

    /**
     * Exception tests
     */
    @Test(expected = IllegalArgumentException.class)
    public void testZeroAmount() {
        processor.processPayment(0.0, true, CREDIT_CARD);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNegativeAmount() {
        processor.processPayment(-50.0, false, PAYPAL);
    }

    /**
     * Delivery fee tests
     */
    @Test
    public void testDeliveryFeeFree() {
        final var fee = processor.calculateDeliveryFee(50.0);
        assertEquals(0.0, fee, DELTA);
    }

    @Test
    public void testDeliveryFeeFreeAboveThreshold() {
        final var fee = processor.calculateDeliveryFee(100.0);
        assertEquals(0.0, fee, DELTA);
    }

    @Test
    public void testDeliveryFeeCharged() {
        final var fee = processor.calculateDeliveryFee(49.99);
        assertEquals(5.0, fee, DELTA);
    }

    @Test
    public void testDeliveryFeeChargedAtZero() {
        final var fee = processor.calculateDeliveryFee(0.0);
        assertEquals(5.0, fee, DELTA);
    }

    /**
     * Rounding tests
     */
    @Test
    public void testRounding() {
        final var result = processor.processPayment(33.33, true, CREDIT_CARD);
        assertEquals(Math.round(33.33 * 1.15 * 0.85 * 100.) / 100. , result, DELTA);
    }

    /**
     *  Integrated workflow tests
     */
    @Test
    public void testFullOrderBelowDeliveryThresholdPostTax() {
        final var processed = processor.processPayment(50.1, true, CREDIT_CARD);
        System.out.println("Processed amount: " + processed);
        final var fee = processor.calculateDeliveryFee(processed);
        assertEquals(5.0, fee, DELTA);
    }
    @Test
    public void testFullOrderBelowDeliveryThreshold() {
        final var processed = processor.processPayment(40.0, false, CASH);
        final var fee = processor.calculateDeliveryFee(processed);
        assertEquals(5.0, fee, DELTA);
    }

    @Test
    public void testFullOrderAboveDeliveryThresholdPostTax() {
        final var processed = processor.processPayment(49.99, false, CASH);
        final var fee = processor.calculateDeliveryFee(processed);
        assertEquals(0.0, fee, DELTA);
    }

    @Test
    public void testFullOrderAboveDeliveryThreshold() {
        final var processed = processor.processPayment(50.0, false, CASH);
        final var fee = processor.calculateDeliveryFee(processed);
        assertEquals(0.0, fee, DELTA);
    }
}