package com.sensei.backend.controller;

import com.sensei.backend.entity.PaymentTransaction;
import com.sensei.backend.service.RazorpayOrderService;
import com.sensei.backend.service.RazorpayVerificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class PaymentControllerTest {

    @Mock
    private RazorpayOrderService orderService;

    @Mock
    private RazorpayVerificationService verificationService;

    @InjectMocks
    private PaymentController paymentController;

    @Test
    public void testCreateOrder() throws Exception {
        PaymentTransaction mockTransaction = new PaymentTransaction();
        mockTransaction.setGatewayOrderId("order_12345");
        
        Mockito.when(orderService.createOrder(
                anyInt(),
                any(UUID.class),
                any(UUID.class),
                any(UUID.class),
                Mockito.isNull(),
                anyInt()
        )).thenReturn(mockTransaction);

        PaymentTransaction response = paymentController.createOrder(
                100,
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID()
        );
        
        assertNotNull(response);
        assertEquals("order_12345", response.getGatewayOrderId());
    }

    @Test
    public void testVerifyPayment() throws Exception {
        Mockito.doNothing().when(verificationService).verifyAndActivate(
                anyString(),
                anyString(),
                anyString(),
                any()
        );

        assertDoesNotThrow(() -> {
            paymentController.verifyPayment("order_12345", "pay_12345", "signature_abc");
        });
    }
}
