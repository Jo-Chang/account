package zerobase.account.service;

import jakarta.transaction.Transactional;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import zerobase.account.domain.Account;
import zerobase.account.domain.Member;
import zerobase.account.dto.AccountDto;
import zerobase.account.exception.AccountException;
import zerobase.account.repository.AccountRepository;
import zerobase.account.repository.MemberRepository;
import zerobase.account.type.AccountStatus;
import zerobase.account.type.ErrorStatus;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

import static zerobase.account.type.ErrorStatus.*;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final MemberRepository memberRepository;
    private final AccountRepository accountRepository;

    @Transactional
    public AccountDto createAccount(Long memberId, Long initialBalance) {
        Member member = getMember(memberId);

        String accountNumber = createAccountNumber();

        invalidateCreateAccount(member);

        return AccountDto.fromEntity(accountRepository.save(
                Account.builder()
                        .member(member)
                        .accountNumber(accountNumber)
                        .balance(initialBalance)
                        .accountStatus(AccountStatus.IN_USE)
                        .registeredAt(LocalDateTime.now())
                        .build()
        ));
    }

    private void invalidateCreateAccount(Member member) {
        if (accountRepository.countByMember(member) == 10) {
            throw new AccountException(TOO_MANY_ACCOUNT_PER_MEMBER);
        }
    }

    private String createAccountNumber() {
        Account account = accountRepository.findFirstByOrderByIdDesc().orElse(null);

        if (account != null) {
            int accountNumberInt = Integer.parseInt(account.getAccountNumber());
            return Integer.toString(accountNumberInt + 1);
        } else {
            return "1000000000";
        }
    }

    private Member getMember(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new AccountException(USER_NOT_FOUND));
    }

    @Transactional
    public AccountDto cancelAccount(Long memberId, String accountNumber) {
        Member member = getMember(memberId);

        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountException(ACCOUNT_NOT_FOUND));

        invalidateCancelAccount(member, account);

        account.setAccountStatus(AccountStatus.IN_CANCEL);
        account.setUnRegisteredAt(LocalDateTime.now());

        Account saved = accountRepository.save(account);

        return AccountDto.fromEntity(saved);
    }

    private void invalidateCancelAccount(Member member, Account account) {
        if (!Objects.equals(member.getId(), account.getMember().getId())) {
            throw new AccountException(ACCOUNT_MEMBER_UN_MATCH);
        }

        if (account.getAccountStatus() == AccountStatus.IN_CANCEL) {
            throw new AccountException(ACCOUNT_ALREADY_CANCELED);
        }

        if (account.getBalance() > 0) {
            throw new AccountException(BALANCE_NOT_EMPTY);
        }
    }

}
