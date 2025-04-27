package zerobase.account.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorStatus {
    INTERNAL_SERVER_ERROR("서버 내부 오류가 발생했습니다."),
    USER_NOT_FOUND("해당 유저가 존재하지 않습니다."),
    TOO_MANY_ACCOUNT_PER_MEMBER("개설한 계좌가 최대치(10개) 입니다.");

    private final String description;
}
