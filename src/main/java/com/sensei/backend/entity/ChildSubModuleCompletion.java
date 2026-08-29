package com.sensei.backend.entity;

import lombok.*;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "child_submodule_completion")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChildSubModuleCompletion {

    @Id
    @GeneratedValue
    @Column( updatable = false)
    @org.hibernate.annotations.JdbcTypeCode(java.sql.Types.VARCHAR)
    private UUID id;

    @Column(name = "child_id", nullable = false)
    @org.hibernate.annotations.JdbcTypeCode(java.sql.Types.VARCHAR)
    private UUID childId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sub_module_id", nullable = false)
    private SubModule subModule;

    private LocalDateTime completedAt;
    @Column(name = "score")
private BigDecimal score;
    private String remarks;
}
