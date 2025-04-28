package zerobase.account.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import zerobase.account.domain.Account;
import zerobase.account.domain.Member;
import zerobase.account.domain.Transaction;
import zerobase.account.dto.AccountDto;
import zerobase.account.dto.TransactionDto;
import zerobase.account.exception.AccountException;
import zerobase.account.repository.AccountRepository;
import zerobase.account.repository.MemberRepository;
import zerobase.account.repository.TransactionRepository;
import zerobase.account.type.AccountStatus;
import zerobase.account.type.ErrorStatus;
import zerobase.account.type.TransactionResultType;
import zerobase.account.type.TransactionType;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    private final Long MEMBER_ID = 111L;
    private final String ACCOUNT_NUMBER = "1234567890";
    private final String TRANSACTION_ID = "transactionId";

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TransactionService transactionService;

    @Test
    @DisplayName("[거래] 성공")
    void useBalance_Success() {
        // given
        final Long AMOUNT = 111L;
        Member testUser = Member.builder()
                .id(MEMBER_ID)
                .username("test-user")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        Account account = Account.builder()
                .accountNumber(ACCOUNT_NUMBER)
                .member(testUser)
                .balance(10000L)
                .accountStatus(AccountStatus.IN_USE)
                .build();

        given(memberRepository.findById(anyLong()))
                .willReturn(Optional.of(testUser));
        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(account));
        given(transactionRepository.save(any()))
                .willReturn(Transaction.builder()
                        .transactionId(TRANSACTION_ID)
                        .transactedAt(LocalDateTime.now())
                        .amount(AMOUNT)
                        .account(account)
                        .transactionResultType(TransactionResultType.SUCCESS)
                        .transactionType(TransactionType.USE)
                        .build());

        ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass(Transaction.class);

        // when
        TransactionDto transactionDto = transactionService.useBalance(1L, ACCOUNT_NUMBER, 100L);

        // then
        verify(transactionRepository, times(1))
                .save(captor.capture());
        assertEquals(TRANSACTION_ID, transactionDto.getTransactionId());
        assertEquals(ACCOUNT_NUMBER, transactionDto.getAccountNumber());
        assertEquals(AMOUNT, transactionDto.getAmount());
        assertEquals(TransactionResultType.SUCCESS,
                transactionDto.getTransactionResultType());
    }

    @Test
    @DisplayName("[거래] 실패 - 해당 유저가 없습니다.")
    void useBalance_Fail_UserNotFound() {
        // given
        given(memberRepository.findById(anyLong()))
                .willReturn(Optional.empty());

        // when
        AccountException exception = assertThrows(AccountException.class,
                () -> transactionService.useBalance(
                        1L, ACCOUNT_NUMBER, 100L
                ));

        // then
        assertEquals(ErrorStatus.USER_NOT_FOUND, exception.getErrorStatus());
    }

    @Test
    @DisplayName("[거래] 실패 - 해당 계좌가 없습니다.")
    void useBalance_Fail_AccountNotFound() {
        // given
        Member testUser = Member.builder()
                .id(MEMBER_ID)
                .username("test-user")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        given(memberRepository.findById(anyLong()))
                .willReturn(Optional.of(testUser));
        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.empty());

        // when
        AccountException exception = assertThrows(AccountException.class,
                () -> transactionService.useBalance(
                        1L, ACCOUNT_NUMBER, 100L
                ));

        // then
        assertEquals(ErrorStatus.ACCOUNT_NOT_FOUND, exception.getErrorStatus());
    }

    @Test
    @DisplayName("[거래] 실패 - 계좌 소유가 일치하지 않습니다.")
    void useBalance_Fail_AccountMemberUnMatch() {
        // given
        Member testUser = Member.builder()
                .id(MEMBER_ID)
                .username("test-user")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        Member testUser2 = Member.builder()
                .id(MEMBER_ID + 2)
                .username("test-user2")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        Account account = Account.builder()
                .accountNumber(ACCOUNT_NUMBER)
                .member(testUser2)
                .balance(10000L)
                .accountStatus(AccountStatus.IN_USE)
                .build();

        given(memberRepository.findById(anyLong()))
                .willReturn(Optional.of(testUser));
        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(account));

        // when
        AccountException exception = assertThrows(AccountException.class,
                () -> transactionService.useBalance(
                        1L, ACCOUNT_NUMBER, 100L
                ));

        // then
        assertEquals(ErrorStatus.ACCOUNT_MEMBER_UN_MATCH, exception.getErrorStatus());
    }

    @Test
    @DisplayName("[거래] 실패 - 이미 해지된 계좌입니다.")
    void useBalance_Fail_AccountAlreadyCanceled() {
        // given
        Member testUser = Member.builder()
                .id(MEMBER_ID)
                .username("test-user")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        Account account = Account.builder()
                .accountNumber(ACCOUNT_NUMBER)
                .member(testUser)
                .balance(10000L)
                .accountStatus(AccountStatus.IN_CANCEL)
                .build();

        given(memberRepository.findById(anyLong()))
                .willReturn(Optional.of(testUser));
        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(account));

        // when
        AccountException exception = assertThrows(AccountException.class,
                () -> transactionService.useBalance(
                        1L, ACCOUNT_NUMBER, 100L
                ));

        // then
        assertEquals(ErrorStatus.ACCOUNT_ALREADY_CANCELED, exception.getErrorStatus());
    }

    @Test
    @DisplayName("[거래] 실패 - 계좌 잔고가 부족합니다.")
    void useBalance_Fail_BalanceNotEmpty() {
        // given
        Member testUser = Member.builder()
                .id(MEMBER_ID)
                .username("test-user")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        Account account = Account.builder()
                .accountNumber(ACCOUNT_NUMBER)
                .member(testUser)
                .balance(10000L)
                .accountStatus(AccountStatus.IN_USE)
                .build();

        given(memberRepository.findById(anyLong()))
                .willReturn(Optional.of(testUser));
        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(account));

        // when
        AccountException exception = assertThrows(AccountException.class,
                () -> transactionService.useBalance(
                        1L, ACCOUNT_NUMBER, 10001L
                ));

        // then
        assertEquals(ErrorStatus.BALANCE_NOT_ENOUGH, exception.getErrorStatus());
    }

}