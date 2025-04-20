package telran.java57.forum.accounting.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import telran.java57.forum.accounting.dto.ChangePasswordDto;
import telran.java57.forum.accounting.dto.exceptions.InvalidPasswordException;
import telran.java57.forum.accounting.dto.exceptions.UserNotFoundException;
import telran.java57.forum.accounting.service.UserAccountService;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserAccountController.class)
public class UserAccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @SuppressWarnings("deprecation")
    @MockBean
    private UserAccountService userAccountService;

    @Test
    void changePassword_success() throws Exception {
        // Подготовка данных
        String requestBody = "{\"oldPassword\":\"oldPass\",\"newPassword\":\"newPass\"}";

        // Тестирование
        mockMvc.perform(put("/account/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .principal(() -> "testUser"))
                .andExpect(status().isNoContent());

        // Проверка
        verify(userAccountService).changePassword(eq("testUser"), eq("oldPass"), eq("newPass"));
    }

    @Test
    void changePassword_wrongOldPassword() throws Exception {
        // Подготовка данных
        String requestBody = "{\"oldPassword\":\"wrongPass\",\"newPassword\":\"newPass\"}";
        doThrow(new InvalidPasswordException()).when(userAccountService)
                .changePassword(eq("testUser"), eq("wrongPass"), eq("newPass"));

        // Тестирование
        mockMvc.perform(put("/account/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .principal(() -> "testUser"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void changePassword_userNotFound() throws Exception {
        // Подготовка данных
        String requestBody = "{\"oldPassword\":\"oldPass\",\"newPassword\":\"newPass\"}";
        doThrow(new UserNotFoundException()).when(userAccountService)
                .changePassword(eq("nonExistentUser"), eq("oldPass"), eq("newPass"));

        // Тестирование
        mockMvc.perform(put("/account/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .principal(() -> "nonExistentUser"))
                .andExpect(status().isNotFound());
    }
}