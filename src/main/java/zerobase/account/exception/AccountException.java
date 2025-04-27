package zerobase.account.exception;

import lombok.*;
import zerobase.account.type.ErrorStatus;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountException extends RuntimeException {
    private ErrorStatus errorStatus;
    private String errorMessage;

    public AccountException(ErrorStatus errorStatus) {
        this.errorStatus = errorStatus;
        this.errorMessage = errorStatus.getDescription();
    }
}
