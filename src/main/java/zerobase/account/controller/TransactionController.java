package zerobase.account.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import zerobase.account.aop.AccountLock;
import zerobase.account.dto.CancelBalance;
import zerobase.account.dto.TransactionDto;
import zerobase.account.dto.TransactionInfo;
import zerobase.account.dto.UseBalance;
import zerobase.account.exception.AccountException;
import zerobase.account.service.TransactionService;

@Slf4j
@RestController
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    /**
     * @Param 사용자 아이디, 계좌 번호, 거래 금액
     * @Return 계좌번호, transaction_result, transaction_id, 거래금액, 거래일시
     * @Exception 사용자 없는 경우,
     * 사용자 아이디와 계좌 소유주가 다른 경우,
     * 계좌가 이미 해지 상태인 경우, 거래금액이 잔액보다 큰 경우,
     * 거래금액이 너무 작거나 큰 경우 실패 응답
     */
    @PostMapping("/transaction/use")
    @AccountLock
    public UseBalance.Response useBalance(
            @RequestBody @Valid UseBalance.Request request
    ) throws InterruptedException {
        try {
            return UseBalance.Response.from(
                    transactionService.useBalance(
                            request.getMemberId(),
                            request.getAccountNumber(),
                            request.getAmount()
                    )
            );
        } catch (AccountException e) {
            log.error("Failed to use balance.");

            transactionService.failedUseTransaction(
                    request.getAccountNumber(),
                    request.getAmount()
            );

            throw e;
        }
    }

    /**
     * @Param transaction_id, 계좌번호, 거래금액
     * @Return 계좌번호, transaction_result, transaction_id, 취소 거래금액, 거래일시
     * @Exception 원거래 금액과 취소 금액이 다른 경우,
     * 트랜잭션이 해당 계좌의 거래가 아닌경우 실패 응답
     */
    @PostMapping("/transaction/cancel")
    @AccountLock
    public CancelBalance.Response cancelBalance(
            @RequestBody @Valid CancelBalance.Request request
    ) {
        try {
            return CancelBalance.Response.from(transactionService.cancelBalance(
                    request.getTransactionId(),
                    request.getAccountNumber(),
                    request.getAmount()
            ));
        } catch (AccountException e) {
            log.error("Failed to use balance.");

            transactionService.failedCancelTransaction(
                    request.getAccountNumber(),
                    request.getAmount()
            );

            throw e;
        }
    }

    /**
     * @Param transaction_id
     * @Return 계좌번호, 거래종류(잔액 사용, 잔액 사용 취소), transaction_result,
     * transaction_id, 거래금액, 거래일시
     * @Exception 해당 transaction_id 없는 경우 실패 응답
     */
    @GetMapping("/transaction/{id}")
    public TransactionDto queryTransaction(
            @PathVariable String id
    ) {
        return TransactionInfo.from(
                transactionService.queryTransaction(id)
        );
    }
}
