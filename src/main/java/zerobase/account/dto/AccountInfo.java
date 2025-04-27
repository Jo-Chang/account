package zerobase.account.dto;

import lombok.*;
import zerobase.account.domain.Account;

/**
 * (계좌번호, 잔액) 정보
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountInfo {
    private String accountNumber;
    private Long balance;

    public static AccountInfo fromEntity(Account account) {
        return AccountInfo.builder()
                .accountNumber(account.getAccountNumber())
                .balance(account.getBalance())
                .build();
    }
}
