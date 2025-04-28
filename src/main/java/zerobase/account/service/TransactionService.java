package zerobase.account.service;

import jakarta.validation.constraints.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zerobase.account.domain.Account;
import zerobase.account.domain.Member;
import zerobase.account.domain.Transaction;
import zerobase.account.dto.TransactionDto;
import zerobase.account.exception.AccountException;
import zerobase.account.repository.AccountRepository;
import zerobase.account.repository.MemberRepository;
import zerobase.account.repository.TransactionRepository;
import zerobase.account.type.AccountStatus;
import zerobase.account.type.TransactionResultType;
import zerobase.account.type.TransactionType;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

import static zerobase.account.type.ErrorStatus.*;
import static zerobase.account.type.TransactionResultType.FAIL;
import static zerobase.account.type.TransactionResultType.SUCCESS;
import static zerobase.account.type.TransactionType.USE;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public TransactionDto useBalance(Long memberId, String accountNumber, Long amount) {
        Member member = getMember(memberId);

        Account account = getAccountByAccountNumber(accountNumber);

        invalidUseBalance(member, account, amount);

        account.useBalance(amount);

        return TransactionDto.fromEntity(executeTransaction(
                SUCCESS, USE, account, amount
                ));
    }

    private Account getAccountByAccountNumber(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountException(ACCOUNT_NOT_FOUND));
        return account;
    }

    private Transaction executeTransaction(
            TransactionResultType transactionResultType,
            TransactionType transactionType,
            Account account,
            Long amount) {
        return transactionRepository.save(
                Transaction.builder()
                        .account(account)
                        .transactedAt(LocalDateTime.now())
                        .amount(amount)
                        .transactionResultType(transactionResultType)
                        .transactionType(transactionType)
                        .transactionId(getRandomTransactionId())
                        .build()
        );
    }

    private static String getRandomTransactionId() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    private void invalidUseBalance(Member member, Account account, Long amount) {
        if (!Objects.equals(member.getId(), account.getMember().getId())) {
            throw new AccountException(ACCOUNT_MEMBER_UN_MATCH);
        }

        if (account.getAccountStatus() == AccountStatus.IN_CANCEL) {
            throw new AccountException(ACCOUNT_ALREADY_CANCELED);
        }

        if (account.getBalance() < amount) {
            throw new AccountException(BALANCE_NOT_ENOUGH);
        }
    }

    private Member getMember(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new AccountException(USER_NOT_FOUND));
    }

    public void failedUseTransaction(String accountNumber, Long amount) {
        Account account = getAccountByAccountNumber(accountNumber);

        executeTransaction(FAIL, USE, account, amount);
    }

}
