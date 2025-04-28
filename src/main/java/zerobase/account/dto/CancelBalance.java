package zerobase.account.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import zerobase.account.type.TransactionResultType;

import java.time.LocalDateTime;

public class CancelBalance {
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Request {
        @NotBlank
        private String transactionId;

        @NotBlank
        @Size(min = 10, max = 10)
        private String accountNumber;

        @NotNull
        @Min(100)
        @Max(30_000_000)
        private Long amount;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private String transactionId;
        private String accountNumber;
        private TransactionResultType transactionResult;
        private Long amount;
        private LocalDateTime transactedAt;

        public static CancelBalance.Response from(TransactionDto transactionDto) {
            return CancelBalance.Response.builder()
                    .transactionId(transactionDto.getTransactionId())
                    .accountNumber(transactionDto.getAccountNumber())
                    .transactionResult(transactionDto.getTransactionResultType())
                    .amount(transactionDto.getAmount())
                    .transactedAt(transactionDto.getTransactedAt())
                    .build();
        }
    }
}
