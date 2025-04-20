package telran.java57.forum.accounting.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mindrot.jbcrypt.BCrypt;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import telran.java57.forum.accounting.dao.UserAccountRepository;
import telran.java57.forum.accounting.dto.exceptions.InvalidPasswordException;
import telran.java57.forum.accounting.dto.exceptions.UserNotFoundException;
import telran.java57.forum.accounting.model.UserAccount;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserAccountServiceImplTest {

    @Mock
    private UserAccountRepository userAccountRepository;

    @InjectMocks
    private UserAccountServiceImpl userAccountService;

    private UserAccount testUser;
    private static final String LOGIN = "testUser";
    private static final String OLD_PASSWORD = "oldPassword";
    private static final String NEW_PASSWORD = "newPassword";
    private static final String HASHED_OLD_PASSWORD = "$2a$10$ABC123";

    @BeforeEach
    void setUp() {
        testUser = new UserAccount(LOGIN, HASHED_OLD_PASSWORD, "Test", "User");
    }

    @Test
    void changePassword_success() {
        when(userAccountRepository.findById(LOGIN)).thenReturn(Optional.of(testUser));
        when(BCrypt.checkpw(OLD_PASSWORD, HASHED_OLD_PASSWORD)).thenReturn(true);
        when(BCrypt.hashpw(eq(NEW_PASSWORD), any())).thenReturn("$2a$10$NEW456");

        userAccountService.changePassword(LOGIN, OLD_PASSWORD, NEW_PASSWORD);

        verify(userAccountRepository).save(testUser);
        assertNotEquals(HASHED_OLD_PASSWORD, testUser.getPassword());
    }

    @Test
    void changePassword_userNotFound() {
        when(userAccountRepository.findById(LOGIN)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () ->
                userAccountService.changePassword(LOGIN, OLD_PASSWORD, NEW_PASSWORD));

        verify(userAccountRepository, never()).save(any());
    }

    @Test
    void changePassword_invalidPassword() {
        when(userAccountRepository.findById(LOGIN)).thenReturn(Optional.of(testUser));
        when(BCrypt.checkpw(OLD_PASSWORD, HASHED_OLD_PASSWORD)).thenReturn(false);

        assertThrows(InvalidPasswordException.class, () ->
                userAccountService.changePassword(LOGIN, OLD_PASSWORD, NEW_PASSWORD));

        verify(userAccountRepository, never()).save(any());
    }
}