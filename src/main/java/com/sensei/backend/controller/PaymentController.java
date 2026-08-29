package com.sensei.backend.controller;

import com.sensei.backend.entity.PaymentTransaction;
import com.sensei.backend.service.RazorpayOrderService;
import com.sensei.backend.service.RazorpayVerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

    private final RazorpayOrderService orderService;
    private final RazorpayVerificationService verificationService;

    @Value("${razorpay.key.secret}")
    private String razorpaySecret;

    /**
     * Generic Razorpay order creation (NO coupon, NO wallet)
     * Amount is in RUPEES
     */
    @PostMapping("/create-order")
    public PaymentTransaction createOrder(
            @RequestParam Integer amount,      // rupees
            @RequestParam UUID childId,
            @RequestParam UUID parentId,
            @RequestParam UUID pricingPlanId
    ) throws Exception {
        log.info("Initiating payment order for childId: {}", childId);

        return orderService.createOrder(
                amount,            // rupees (conversion happens inside service)
                childId,
                parentId,
                pricingPlanId,
                null,               // couponCode
                0                   // couponDiscount
        );
    }

    /**
     * Razorpay verification callback
     */
    @PostMapping("/verify")
    public void verifyPayment(
            @RequestParam String orderId,
            @RequestParam String paymentId,
            @RequestParam String signature
    ) throws Exception {
        log.info("Verifying payment orderId: {}", orderId);

        verificationService.verifyAndActivate(
                orderId,
                paymentId,
                signature,
                razorpaySecret
        );
    }
}
