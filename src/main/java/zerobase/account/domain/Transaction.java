package zerobase.account.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import zerobase.account.type.TransactionResultType;
import zerobase.account.type.TransactionType;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
public class Transaction extends BaseEntity {
    @ManyToOne
    private Account account;
    private Long amount;

    private String transactionId;
    @Enumerated(EnumType.STRING)
    private TransactionResultType transactionResultType;
    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    private LocalDateTime transactedAt;
}
