package zerobase.account.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import zerobase.account.dto.UseBalance;
import zerobase.account.exception.AccountException;
import zerobase.account.service.TransactionService;

@RestController
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    /**
     * @Param 사용자 아이디, 계좌 번호, 거래 금액
     * @return 계좌번호, transaction_result, transaction_id, 거래금액, 거래일시
     */
    @PostMapping("/transaction/use")
    public UseBalance.Response useBalance(
        @RequestBody @Valid UseBalance.Request request
    ) {
        try {
            return UseBalance.Response.from(
                    transactionService.useBalance(
                            request.getMemberId(),
                            request.getAccountNumber(),
                            request.getAmount()
                            )
            );
        } catch (AccountException e) {
            transactionService.failedUseTransaction(
                    request.getAccountNumber(),
                    request.getAmount()
            );

            throw e;
        }
    }
}
