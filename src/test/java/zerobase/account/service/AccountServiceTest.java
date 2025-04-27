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
import zerobase.account.exception.AccountException;
import zerobase.account.repository.AccountRepository;
import zerobase.account.repository.MemberRepository;
import zerobase.account.type.AccountStatus;
import zerobase.account.type.ErrorStatus;

import java.time.LocalDateTime;
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
                .id(111L)
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
        assertEquals(111L, accountDto.getMemberId());
        assertEquals(
                Integer.toString(Integer.parseInt(ACCOUNT_NUMBER) + 1),
                captor.getValue().getAccountNumber()
        );
        assertEquals(AccountStatus.IN_USE, captor.getValue().getAccountStatus());
    }

    @Test
    @DisplayName("[계좌 생성] 실패 - 해당 유저가 존재하지 않습니다.")
    void createAccount_UserNotFound() {
        // given
        Member testUser = Member.builder()
                .id(111L)
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
                .id(111L)
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

}