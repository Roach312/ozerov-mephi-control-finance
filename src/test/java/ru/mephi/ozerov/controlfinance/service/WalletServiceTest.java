package ru.mephi.ozerov.controlfinance.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.mephi.ozerov.controlfinance.dto.wallet.WalletResponse;
import ru.mephi.ozerov.controlfinance.entity.User;
import ru.mephi.ozerov.controlfinance.entity.Wallet;
import ru.mephi.ozerov.controlfinance.exception.EntityNotFoundException;
import ru.mephi.ozerov.controlfinance.repository.WalletRepository;
import ru.mephi.ozerov.controlfinance.service.impl.WalletServiceImpl;

@ExtendWith(MockitoExtension.class)
class WalletServiceTest {

    @Mock private WalletRepository walletRepository;

    @Mock private UserService userService;

    @InjectMocks private WalletServiceImpl walletService;

    private User user;
    private Wallet wallet;

    @BeforeEach
    void setUp() {
        user = User.builder().id(1L).login("testuser").passwordHash("hash").build();

        wallet = Wallet.builder().id(1L).user(user).balance(new BigDecimal("1000.00")).build();
    }

    @Test
    void getCurrentUserWallet_ShouldReturnWalletResponse() {
        when(userService.getCurrentUser()).thenReturn(user);
        when(walletRepository.findByUser(user)).thenReturn(Optional.of(wallet));

        WalletResponse response = walletService.getCurrentUserWallet();

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("testuser", response.getUserLogin());
        assertEquals(new BigDecimal("1000.00"), response.getBalance());
    }

    @Test
    void getCurrentUserWallet_ShouldThrowExceptionWhenNotFound() {
        when(userService.getCurrentUser()).thenReturn(user);
        when(walletRepository.findByUser(user)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> walletService.getCurrentUserWallet());
    }

    @Test
    void getCurrentUserWalletEntity_ShouldReturnWallet() {
        when(userService.getCurrentUser()).thenReturn(user);
        when(walletRepository.findByUser(user)).thenReturn(Optional.of(wallet));

        Wallet result = walletService.getCurrentUserWalletEntity();

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void getWalletByUserLogin_ShouldReturnWallet() {
        when(userService.getUserByLogin("testuser")).thenReturn(user);
        when(walletRepository.findByUser(user)).thenReturn(Optional.of(wallet));

        Wallet result = walletService.getWalletByUserLogin("testuser");

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void getWalletByUserLogin_ShouldThrowExceptionWhenNotFound() {
        when(userService.getUserByLogin("testuser")).thenReturn(user);
        when(walletRepository.findByUser(user)).thenReturn(Optional.empty());

        assertThrows(
                EntityNotFoundException.class,
                () -> walletService.getWalletByUserLogin("testuser"));
    }
}
