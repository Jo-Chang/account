package zerobase.account.dto;

import lombok.*;
import zerobase.account.domain.Transaction;
import zerobase.account.type.TransactionResultType;
import zerobase.account.type.TransactionType;

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
    private TransactionType transactionType;
    private LocalDateTime transactedAt;

    public static TransactionDto fromEntity(Transaction transaction) {
        return TransactionDto.builder()
                .transactionId(transaction.getTransactionId())
                .accountNumber(transaction.getAccount().getAccountNumber())
                .amount(transaction.getAmount())
                .transactionResultType(transaction.getTransactionResultType())
                .transactionType(transaction.getTransactionType())
                .transactedAt(transaction.getTransactedAt())
                .build();
    }
}
