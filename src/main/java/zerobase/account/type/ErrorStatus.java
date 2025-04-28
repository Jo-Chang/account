package zerobase.account.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorStatus {
    INTERNAL_SERVER_ERROR("서버 내부 오류가 발생했습니다."),
    USER_NOT_FOUND("해당 유저가 존재하지 않습니다."),
    TOO_MANY_ACCOUNT_PER_MEMBER("개설한 계좌가 최대치(10개) 입니다."),
    ACCOUNT_NOT_FOUND("해당 계좌가 존재하지 않습니다."),
    ACCOUNT_MEMBER_UN_MATCH("사용자가 계좌의 소유주가 아닙니다."),
    ACCOUNT_ALREADY_CANCELED("계좌가 이미 해지되었습니다."),
    BALANCE_NOT_EMPTY("해지할 계좌에 잔고가 존재합니다."),
    BALANCE_NOT_ENOUGH("계좌 잔고가 부족합니다."),
    TRANSACTION_NOT_FOUND("해당 거래가 존재하지 않습니다."),
    TRANSACTION_AMOUNT_UN_MATCH("취소 금액이 거래 금액과 다릅니다."),
    TRANSACTION_ACCOUNT_UN_MATCH("거래 계좌가 일치하지 않습니다."),
    ACCOUNT_TRANSACTION_LOCK("해당 계좌가 거래중입니다."),
    ;

    private final String description;
}
