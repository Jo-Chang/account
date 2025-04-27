package zerobase.account.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

public class CancelAccount {

    /**
     * 파라미터 : 사용자 아이디, 계좌 번호
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Request {
        @NotNull
        @Min(1)
        private Long memberId;

        @NotBlank
        @Size(min = 10, max = 10)
        private String accountNumber;
    }

    /**
     * 응답 : 사용자 아이디, 계좌번호, 해지일시
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private Long memberId;
        private String accountNumber;
        private LocalDateTime unRegisteredAt;

        public static CancelAccount.Response from(AccountDto accountDto) {
            return Response.builder()
                    .memberId(accountDto.getMemberId())
                    .accountNumber(accountDto.getAccountNumber())
                    .unRegisteredAt(accountDto.getUnRegisteredAt())
                    .build();
        }
    }
}
