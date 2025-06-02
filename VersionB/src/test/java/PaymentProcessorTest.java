import org.example.PaymentProcessor;
import org.junit.Before;
import org.junit.Test;

import static org.example.PaymentProcessor.PaymentMethod.*;
import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

/**
 * Test Design Approach:
 *    @CombinatorialTesting - Cover all payment method × first-order combinations
 *    @BoundaryValueAnalysis - Delivery fee thresholds (0, 49.99, 50, 100)
 *    @SpecialValues - Zero/negative amounts, minimum positive amount
 *    @ErrorCases - Validate exception handling
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
        assertEquals(85.0, result, DELTA);
    }

    @Test
    public void testFirstOrderPayPal() {
        final var result = processor.processPayment(100.0, true, PAYPAL);
        assertEquals(88.0, result, DELTA);
    }

    @Test
    public void testFirstOrderCash() {
        final var result = processor.processPayment(100.0, true, CASH);
        assertEquals(90.0, result, DELTA);
    }

    @Test
    public void testNonFirstOrderCreditCard() {
        final var result = processor.processPayment(100.0, false, CREDIT_CARD);
        assertEquals(95.0, result, DELTA);
    }

    @Test
    public void testNonFirstOrderPayPal() {
        final var result = processor.processPayment(100.0, false, PAYPAL);
        assertEquals(98.0, result, DELTA);
    }

    @Test
    public void testNonFirstOrderCash() {
        final var result = processor.processPayment(100.0, false, CASH);
        assertEquals(100.0, result, DELTA);
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
        assertEquals(8500.0, result, DELTA);
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
        assertEquals(28.33, result, DELTA); // 33.33 * 0.85 = 28.3305 → 28.33
    }

    /**
    *  Integrated workflow tests
    */
    @Test
    public void testFullOrderBelowDeliveryThreshold() {
        final var processed = processor.processPayment(45.0, false, CASH);
        final var fee = processor.calculateDeliveryFee(processed);
        assertEquals(5.0, fee, DELTA);
    }

    @Test
    public void testFullOrderAboveDeliveryThreshold() {
        final var processed = processor.processPayment(50.0, false, CASH);
        final var fee = processor.calculateDeliveryFee(processed);
        assertEquals(0.0, fee, DELTA);
    }
}