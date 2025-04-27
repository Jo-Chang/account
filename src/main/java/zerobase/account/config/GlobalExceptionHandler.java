package zerobase.account.config;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.bridge.Message;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import zerobase.account.dto.ErrorResponse;
import zerobase.account.exception.AccountException;
import zerobase.account.type.ErrorStatus;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AccountException.class)
    public ErrorResponse handleAccountException(AccountException e) {
        log.error("{} - {} error is occurred.", e, e.getErrorStatus());

        return new ErrorResponse(e.getErrorStatus());
    }

    @ExceptionHandler(Exception.class)
    public ErrorResponse handleException(Exception e) {
        log.error("{} - error occurred", e);

        return new ErrorResponse(ErrorStatus.INTERNAL_SERVER_ERROR);
    }

}
