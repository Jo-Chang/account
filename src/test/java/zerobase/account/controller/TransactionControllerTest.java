package zerobase.account.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import zerobase.account.dto.*;
import zerobase.account.service.AccountService;
import zerobase.account.service.TransactionService;
import zerobase.account.type.TransactionResultType;
import zerobase.account.type.TransactionType;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TransactionController.class)
class TransactionControllerTest {
    private final String TRANSACTION_ID = "transactionId";
    private final String ACCOUNT_NUMBER = "1000000002";

    @MockitoBean
    private TransactionService transactionService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("[거래] 성공")
    void successUseBalance() throws Exception {
        // given
        Long amount = 100L;

        given(transactionService.useBalance(anyLong(), anyString(), anyLong()))
                .willReturn(TransactionDto.builder()
                        .transactionId(TRANSACTION_ID)
                        .transactionResultType(TransactionResultType.SUCCESS)
                        .transactedAt(LocalDateTime.now())
                        .amount(amount)
                        .accountNumber(ACCOUNT_NUMBER)
                        .build());

        // when
        // then
        mockMvc.perform(post("/transaction/use")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new UseBalance.Request(1L, ACCOUNT_NUMBER, 100L)
                        )))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transactionId").value(TRANSACTION_ID))
                .andExpect(jsonPath("$.accountNumber").value(ACCOUNT_NUMBER))
                .andExpect(jsonPath("$.transactionResult").value("SUCCESS"))
                .andExpect(jsonPath("$.amount").value(amount))
                .andDo(print());
    }

    @Test
    @DisplayName("[거래 취소] 성공")
    void successCancelBalance() throws Exception {
        // given
        Long amount = 100L;

        given(transactionService.cancelBalance(anyString(), anyString(), anyLong()))
                .willReturn(TransactionDto.builder()
                        .transactionId(TRANSACTION_ID)
                        .transactionResultType(TransactionResultType.SUCCESS)
                        .transactedAt(LocalDateTime.now())
                        .amount(amount)
                        .accountNumber(ACCOUNT_NUMBER)
                        .build());

        // when
        // then
        mockMvc.perform(post("/transaction/cancel")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new CancelBalance.Request("1", ACCOUNT_NUMBER, 100L)
                        )))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transactionId").value(TRANSACTION_ID))
                .andExpect(jsonPath("$.accountNumber").value(ACCOUNT_NUMBER))
                .andExpect(jsonPath("$.transactionResult").value("SUCCESS"))
                .andExpect(jsonPath("$.amount").value(amount))
                .andDo(print());
    }

    @Test
    @DisplayName("[거래 확인] 성공")
    void successQueryTransaction() throws Exception {
        // given
        Long amount = 100L;

        given(transactionService.queryTransaction(anyString()))
                .willReturn(TransactionDto.builder()
                        .transactionId(TRANSACTION_ID)
                        .transactionResultType(TransactionResultType.SUCCESS)
                        .transactionType(TransactionType.USE)
                        .transactedAt(LocalDateTime.now())
                        .amount(amount)
                        .accountNumber(ACCOUNT_NUMBER)
                        .build());

        // when
        // then
        mockMvc.perform(get("/transaction/id"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transactionId").value(TRANSACTION_ID))
                .andExpect(jsonPath("$.accountNumber").value(ACCOUNT_NUMBER))
                .andExpect(jsonPath("$.transactionResultType").value("SUCCESS"))
                .andExpect(jsonPath("$.transactionType").value("USE"))
                .andExpect(jsonPath("$.amount").value(amount))
                .andDo(print());
    }
    
}