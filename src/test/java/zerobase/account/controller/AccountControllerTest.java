package zerobase.account.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import zerobase.account.dto.AccountDto;
import zerobase.account.dto.AccountInfo;
import zerobase.account.dto.CancelAccount;
import zerobase.account.dto.CreateAccount;
import zerobase.account.service.AccountService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AccountController.class)
class AccountControllerTest {
    private final Long MEMBER_ID = 111L;
    private final String ACCOUNT_NUMBER = "1000000002";

    @MockitoBean
    private AccountService accountService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("[계좌 생성] 성공")
    void successCreateAccount() throws Exception {
        // given
        given(accountService.createAccount(anyLong(), anyLong()))
                .willReturn(AccountDto.builder()
                        .memberId(1L)
                        .accountNumber(ACCOUNT_NUMBER)
                        .registeredAt(LocalDateTime.now())
                        .unRegisteredAt(null)
                        .build());

        // when
        // then
        mockMvc.perform(post("/account")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new CreateAccount.Request(3L, 1L)
                        )))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.memberId").value(1L))
                .andExpect(jsonPath("$.accountNumber").value(ACCOUNT_NUMBER))
                .andDo(print());
    }

    @Test
    @DisplayName("[계좌 해지] 성공")
    void successCancelAccount() throws Exception {
        // given
        given(accountService.cancelAccount(anyLong(), anyString()))
                .willReturn(AccountDto.builder()
                        .memberId(MEMBER_ID)
                        .accountNumber(ACCOUNT_NUMBER)
                        .registeredAt(LocalDateTime.now())
                        .unRegisteredAt(LocalDateTime.now())
                        .build());

        // when
        // then
        mockMvc.perform(delete("/account")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new CancelAccount.Request(3L, ACCOUNT_NUMBER)
                        )))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.memberId").value(MEMBER_ID))
                .andExpect(jsonPath("$.accountNumber").value(ACCOUNT_NUMBER))
                .andDo(print());
    }

    @Test
    @DisplayName("[계좌 확인] 성공")
    void successGetlAccount() throws Exception {
        // given
        AccountInfo info = AccountInfo.builder()
                .accountNumber(ACCOUNT_NUMBER)
                .balance(1L)
                .build();
        AccountInfo info2 = AccountInfo.builder()
                .accountNumber("accountNum")
                .balance(22222L)
                .build();
        given(accountService.getAccount(anyLong()))
                .willReturn(List.of(info, info2));

        // when
        // then
        mockMvc.perform(get("/account?member_id=1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].accountNumber").value(ACCOUNT_NUMBER))
                .andExpect(jsonPath("$[0].balance").value(1L))
                .andExpect(jsonPath("$[1].accountNumber").value(info2.getAccountNumber()))
                .andExpect(jsonPath("$[1].balance").value(info2.getBalance()));
    }

}