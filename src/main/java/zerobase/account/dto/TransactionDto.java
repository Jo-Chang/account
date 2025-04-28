package zerobase.account.dto;

import lombok.*;
import zerobase.account.domain.Transaction;
import zerobase.account.type.TransactionResultType;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionDto {
    private String transactionId;
    private String accountNumber;
    private Long amount;
    private TransactionResultType transactionResultType;
    private LocalDateTime transactedAt;

    public static TransactionDto fromEntity(Transaction transaction) {
        return TransactionDto.builder()
                .transactionId(transaction.getTransactionId())
                .accountNumber(transaction.getAccount().getAccountNumber())
                .amount(transaction.getAmount())
                .transactionResultType(transaction.getTransactionResultType())
                .transactedAt(transaction.getTransactedAt())
                .build();
    }
}
