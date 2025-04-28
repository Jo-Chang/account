package zerobase.account.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import zerobase.account.type.TransactionResultType;

import java.time.LocalDateTime;

public class UseBalance {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Request implements AccountLockIdInterface {
        @NotNull
        @Min(1)
        private Long memberId;

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

        public static Response from(TransactionDto transactionDto) {
            return Response.builder()
                    .transactionId(transactionDto.getTransactionId())
                    .accountNumber(transactionDto.getAccountNumber())
                    .transactionResult(transactionDto.getTransactionResultType())
                    .amount(transactionDto.getAmount())
                    .transactedAt(transactionDto.getTransactedAt())
                    .build();
        }
    }

}
