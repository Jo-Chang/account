package zerobase.account.service;

import jakarta.transaction.Transactional;
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

import static zerobase.account.type.ErrorStatus.TOO_MANY_ACCOUNT_PER_MEMBER;
import static zerobase.account.type.ErrorStatus.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final MemberRepository memberRepository;
    private final AccountRepository accountRepository;

    @Transactional
    public AccountDto createAccount(Long userId, Long initialBalance) {
        Member member = getMember(userId);

        String accountNumber = createAccountNumber();

        invalidateCreateAccount(member, userId, initialBalance);

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

    private void invalidateCreateAccount(Member member, Long userId, Long initialBalance) {
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

    private Member getMember(Long userId) {
        return memberRepository.findById(userId)
                .orElseThrow(() -> new AccountException(USER_NOT_FOUND));
    }

}
