package com.sensei.backend.entity;

import com.sensei.backend.enums.TransactionStatus;
import com.sensei.backend.enums.TransactionType;
import lombok.*;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "master_transaction")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MasterTransaction {

    @Id
    @GeneratedValue
    
    @org.hibernate.annotations.JdbcTypeCode(java.sql.Types.VARCHAR)
    private UUID id;

    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    @Enumerated(EnumType.STRING)
    private TransactionStatus transactionStatus;

    private Integer amount;
    private String currency;

    @org.hibernate.annotations.JdbcTypeCode(java.sql.Types.VARCHAR)
    private UUID childId;
    @org.hibernate.annotations.JdbcTypeCode(java.sql.Types.VARCHAR)
    private UUID parentId;

    @org.hibernate.annotations.JdbcTypeCode(java.sql.Types.VARCHAR)
    private UUID pricingPlanId;
    @org.hibernate.annotations.JdbcTypeCode(java.sql.Types.VARCHAR)
    private UUID paymentTransactionId;

    private String remarks;

    private LocalDateTime createdAt;
}
