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
public class TransactionInfo {
    private String transactionId;
    private String accountNumber;
    private Long amount;
    private TransactionResultType transactionResultType;
    private TransactionType transactionType;
    private LocalDateTime transactedAt;

    public static TransactionDto from(TransactionDto transactionDto) {
        return TransactionDto.builder()
                .transactionId(transactionDto.getTransactionId())
                .accountNumber(transactionDto.getAccountNumber())
                .amount(transactionDto.getAmount())
                .transactionResultType(transactionDto.getTransactionResultType())
                .transactionType(transactionDto.getTransactionType())
                .transactedAt(transactionDto.getTransactedAt())
                .build();
    }
}
