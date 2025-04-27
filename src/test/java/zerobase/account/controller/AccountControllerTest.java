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
import zerobase.account.dto.CreateAccount;
import zerobase.account.service.AccountService;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AccountController.class)
class AccountControllerTest {
    private final String ACCOUNT_NUMBER = "1000000002";

    @MockitoBean
    private AccountService accountService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("[계좌 생성 컨트롤러] 성공")
    void successCreateAccount() throws Exception {
        // given
        given(accountService.createAccount(anyLong(), anyLong()))
                .willReturn(AccountDto.builder()
                        .memberId(1L)
                        .accountNumber(ACCOUNT_NUMBER)
                        .registeredAt(LocalDateTime.now())
                        .unRegisteredAt(LocalDateTime.now())
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

}