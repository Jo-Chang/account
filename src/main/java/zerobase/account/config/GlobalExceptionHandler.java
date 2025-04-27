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
    public ResponseEntity handleAccountException(AccountException e) {
        log.error("{} Exception is occurred.", e);

        return ResponseEntity
                .badRequest()
                .body(new ErrorResponse(e.getErrorStatus()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity handleException(Exception e) {
        log.error("error occurred", e);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(e);
    }

}
