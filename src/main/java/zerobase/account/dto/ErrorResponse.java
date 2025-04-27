package zerobase.account.dto;

import lombok.*;
import zerobase.account.type.ErrorStatus;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorResponse {
    private ErrorStatus errorStatus;
    private String errorMessage;

    public ErrorResponse(ErrorStatus errorStatus) {
        this.errorStatus = errorStatus;
        this.errorMessage = errorStatus.getDescription();
    }
}
