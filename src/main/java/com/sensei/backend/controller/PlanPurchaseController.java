package com.sensei.backend.controller;
import jakarta.validation.Valid;


import com.sensei.backend.dto.planpurchase.PlanPurchaseRequestDTO;
import com.sensei.backend.service.PlanPurchaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/plan-purchases")
@RequiredArgsConstructor
public class PlanPurchaseController {

    private final PlanPurchaseService planPurchaseService;

    @PostMapping
    public ResponseEntity<String> purchasePlan(
            @Valid @RequestBody PlanPurchaseRequestDTO dto
    ) {
        planPurchaseService.purchasePlan(dto);
        return ResponseEntity.ok("PLAN_PURCHASE_INITIATED");
    }
}
