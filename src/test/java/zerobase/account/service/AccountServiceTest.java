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
import zerobase.account.dto.AccountDto;
import zerobase.account.dto.AccountInfo;
import zerobase.account.exception.AccountException;
import zerobase.account.repository.AccountRepository;
import zerobase.account.repository.MemberRepository;
import zerobase.account.type.AccountStatus;
import zerobase.account.type.ErrorStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {
    private final Long MEMBER_ID = 111L;
    private final String ACCOUNT_NUMBER = "1000000002";

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private AccountService accountService;

    @Test
    @DisplayName("[계좌 생성] 성공")
    void createAccount_Success() {
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
                .build();

        given(memberRepository.findById(anyLong()))
                .willReturn(Optional.of(testUser));
        given(accountRepository.findFirstByOrderByIdDesc())
                .willReturn(Optional.of(account));
        given(accountRepository.save(any()))
                .willReturn(account);

        ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);

        // when
        AccountDto accountDto = accountService.createAccount(1L, 0L);

        // then
        verify(accountRepository, times(1)).save(captor.capture());
        assertEquals(ACCOUNT_NUMBER, accountDto.getAccountNumber());
        assertEquals(MEMBER_ID, accountDto.getMemberId());
        assertEquals(AccountStatus.IN_USE, captor.getValue().getAccountStatus());

        // 랜덤 계좌 생성 방식으로 변경 시 삭제할 코드
        assertEquals(
                Integer.toString(Integer.parseInt(ACCOUNT_NUMBER) + 1),
                captor.getValue().getAccountNumber()
        );
    }

    @Test
    @DisplayName("[계좌 생성] 실패 - 해당 유저가 존재하지 않습니다.")
    void createAccount_UserNotFound() {
        // given
        Member testUser = Member.builder()
                .id(MEMBER_ID)
                .username("test-user")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        given(memberRepository.findById(anyLong()))
                .willReturn(Optional.empty());

        // when
        AccountException exception = assertThrows(
                AccountException.class,
                () -> accountService.createAccount(1L, 0L)
        );

        // then
        assertEquals(ErrorStatus.USER_NOT_FOUND, exception.getErrorStatus());
    }

    @Test
    @DisplayName("[계좌 생성] 실패 - 한 유저당 생성 가능한 계좌 수는 10개입니다.")
    void createAccount_TooManyAccountPerMember() {
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
                .build();

        given(memberRepository.findById(anyLong()))
                .willReturn(Optional.of(testUser));
        given(accountRepository.countByMember(any()))
                .willReturn(10);

        // when
        AccountException exception = assertThrows(
                AccountException.class,
                () -> accountService.createAccount(1L, 0L)
        );

        // then
        assertEquals(ErrorStatus.TOO_MANY_ACCOUNT_PER_MEMBER, exception.getErrorStatus());
    }

    @Test
    @DisplayName("[계좌 해지] 성공")
    void cancelAccount_Success() {
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
                .balance(0L)
                .accountStatus(AccountStatus.IN_USE)
                .build();

        given(memberRepository.findById(anyLong()))
                .willReturn(Optional.of(testUser));
        given(accountRepository.findByAccountNumber(any()))
                .willReturn(Optional.of(account));
        given(accountRepository.save(any()))
                .willReturn(account);

        ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);

        // when
        AccountDto accountDto = accountService.cancelAccount(
                1L, ACCOUNT_NUMBER);

        // then
        verify(accountRepository, times(1)).save(captor.capture());
        assertEquals(ACCOUNT_NUMBER, accountDto.getAccountNumber());
        assertEquals(MEMBER_ID, accountDto.getMemberId());
        assertEquals(AccountStatus.IN_CANCEL, captor.getValue().getAccountStatus());
    }

    @Test
    @DisplayName("[계좌 해지] 실패 - 유저가 존재하지 않습니다.")
    void cancelAccount_UserNotFound() {
        // given
        given(memberRepository.findById(anyLong()))
                .willReturn(Optional.empty());

        // when
        AccountException exception = assertThrows(
                AccountException.class,
                () -> accountService.cancelAccount(1L, "")
        );

        // then
        assertEquals(ErrorStatus.USER_NOT_FOUND, exception.getErrorStatus());
    }

    @Test
    @DisplayName("[계좌 해지] 실패 - 해당 계좌가 존재하지 않습니다.")
    void cancelAccount_AccountNotFound() {
        // given
        Member testUser = Member.builder()
                .id(MEMBER_ID)
                .username("test-user")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        given(memberRepository.findById(anyLong()))
                .willReturn(Optional.of(testUser));
        given(accountRepository.findByAccountNumber(any()))
                .willReturn(Optional.empty());

        // when
        AccountException exception = assertThrows(
                AccountException.class,
                () -> accountService.cancelAccount(1L, "")
        );

        // then
        assertEquals(ErrorStatus.ACCOUNT_NOT_FOUND, exception.getErrorStatus());
    }

    @Test
    @DisplayName("[계좌 해지] 실패 - 사용자와 계좌 소유자가 일치하지 않습니다.")
    void cancelAccount_AccountMemberUnMatch() {
        // given
        Member testUser = Member.builder()
                .id(MEMBER_ID)
                .username("test-user")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        Member testUser2 = Member.builder()
                .id(MEMBER_ID + 1)
                .username("test-user2")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        Account account = Account.builder()
                .accountNumber(ACCOUNT_NUMBER)
                .member(testUser2)
                .balance(0L)
                .accountStatus(AccountStatus.IN_USE)
                .build();

        given(memberRepository.findById(anyLong()))
                .willReturn(Optional.of(testUser));
        given(accountRepository.findByAccountNumber(any()))
                .willReturn(Optional.of(account));

        // when
        AccountException exception = assertThrows(
                AccountException.class,
                () -> accountService.cancelAccount(1L, "")
        );

        // then
        assertEquals(ErrorStatus.ACCOUNT_MEMBER_UN_MATCH, exception.getErrorStatus());
    }

    @Test
    @DisplayName("[계좌 해지] 실패 - 계좌가 이미 해지되었습니다.")
    void cancelAccount_AccountAlreadyCanceled() {
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
                .balance(0L)
                .accountStatus(AccountStatus.IN_CANCEL)
                .build();

        given(memberRepository.findById(anyLong()))
                .willReturn(Optional.of(testUser));
        given(accountRepository.findByAccountNumber(any()))
                .willReturn(Optional.of(account));

        // when
        AccountException exception = assertThrows(
                AccountException.class,
                () -> accountService.cancelAccount(1L, "")
        );

        // then
        assertEquals(ErrorStatus.ACCOUNT_ALREADY_CANCELED, exception.getErrorStatus());
    }

    @Test
    @DisplayName("[계좌 해지] 실패 - 해지할 계좌의 잔고가 존재합니다.")
    void cancelAccount_BalanceNotEmpty() {
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
                .balance(1L)
                .accountStatus(AccountStatus.IN_USE)
                .build();

        given(memberRepository.findById(anyLong()))
                .willReturn(Optional.of(testUser));
        given(accountRepository.findByAccountNumber(any()))
                .willReturn(Optional.of(account));

        // when
        AccountException exception = assertThrows(
                AccountException.class,
                () -> accountService.cancelAccount(1L, "")
        );

        // then
        assertEquals(ErrorStatus.BALANCE_NOT_EMPTY, exception.getErrorStatus());
    }

    @Test
    @DisplayName("[계좌 확인] 성공")
    void getAccount_Success() {
        // given
        Member testUser = Member.builder()
                .id(MEMBER_ID)
                .username("test-user")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        Account account1 = Account.builder()
                .accountNumber("accountNumber1")
                .member(testUser)
                .balance(1L)
                .accountStatus(AccountStatus.IN_USE)
                .build();
        Account account2 = Account.builder()
                .accountNumber("accountNumber2")
                .member(testUser)
                .balance(1L)
                .accountStatus(AccountStatus.IN_USE)
                .build();
        Account account3 = Account.builder()
                .accountNumber("accountNumber3")
                .member(testUser)
                .balance(1L)
                .accountStatus(AccountStatus.IN_USE)
                .build();

        given(memberRepository.findById(anyLong()))
                .willReturn(Optional.of(testUser));
        given(accountRepository.findAllByMember(any()))
                .willReturn(List.of(
                        account1,
                        account2,
                        account3
                ));

        // when
        List<AccountInfo> accountInfoList = accountService.getAccount(1L);

        // then
        assertEquals(3, accountInfoList.size());
        assertEquals("accountNumber1", accountInfoList.get(0).getAccountNumber());
        assertEquals(1L, accountInfoList.get(0).getBalance());
    }

    @Test
    @DisplayName("[계좌 확인] 실패 - 유저가 존재하지 않습니다.")
    void getAccount_UserNotFound() {
        // given
        given(memberRepository.findById(anyLong()))
                .willReturn(Optional.empty());

        // when
        AccountException exception = assertThrows(
                AccountException.class,
                () -> accountService.cancelAccount(1L, "")
        );

        // then
        assertEquals(ErrorStatus.USER_NOT_FOUND, exception.getErrorStatus());
    }

}