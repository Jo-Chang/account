package zerobase.account.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import zerobase.account.dto.CancelAccount;
import zerobase.account.dto.CreateAccount;
import zerobase.account.service.AccountService;

@RestController
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping("/account")
    public CreateAccount.Response createAccount(
            @RequestBody @Valid CreateAccount.Request request
    ) {
        return CreateAccount.Response.from(accountService.createAccount(
                request.getMemberId(),
                request.getInitialBalance()
        ));
    }

    @DeleteMapping("/account")
    public CancelAccount.Response cancelAccount(
            @RequestBody @Valid CancelAccount.Request request
    ) {
        return CancelAccount.Response.from(
                accountService.cancelAccount(
                        request.getMemberId(),
                        request.getAccountNumber()
                )
        );
    }

}
