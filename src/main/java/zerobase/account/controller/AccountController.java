package zerobase.account.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import zerobase.account.dto.*;
import zerobase.account.service.AccountService;

import java.util.List;

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

    @GetMapping("/account")
    public List<AccountInfo> getAccountsByMemberId(
            @RequestParam("member_id") Long memberId
    ) {
        return accountService.getAccount(memberId);
    }

}
