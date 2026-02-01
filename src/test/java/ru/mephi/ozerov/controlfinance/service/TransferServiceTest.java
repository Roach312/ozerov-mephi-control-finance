package ru.mephi.ozerov.controlfinance.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.mephi.ozerov.controlfinance.dto.transfer.TransferRequest;
import ru.mephi.ozerov.controlfinance.dto.transfer.TransferResponse;
import ru.mephi.ozerov.controlfinance.entity.Transfer;
import ru.mephi.ozerov.controlfinance.entity.User;
import ru.mephi.ozerov.controlfinance.entity.Wallet;
import ru.mephi.ozerov.controlfinance.exception.ValidationException;
import ru.mephi.ozerov.controlfinance.repository.TransactionRepository;
import ru.mephi.ozerov.controlfinance.repository.TransferRepository;
import ru.mephi.ozerov.controlfinance.repository.WalletRepository;
import ru.mephi.ozerov.controlfinance.service.impl.TransferServiceImpl;

@ExtendWith(MockitoExtension.class)
class TransferServiceTest {

    @Mock private TransferRepository transferRepository;

    @Mock private TransactionRepository transactionRepository;

    @Mock private WalletRepository walletRepository;

    @Mock private WalletService walletService;

    @Mock private UserService userService;

    @InjectMocks private TransferServiceImpl transferService;

    private User fromUser;
    private User toUser;
    private Wallet fromWallet;
    private Wallet toWallet;
    private Transfer transfer;

    @BeforeEach
    void setUp() {
        fromUser = User.builder().id(1L).login("sender").build();
        toUser = User.builder().id(2L).login("receiver").build();

        fromWallet =
                Wallet.builder().id(1L).user(fromUser).balance(new BigDecimal("1000.00")).build();

        toWallet = Wallet.builder().id(2L).user(toUser).balance(new BigDecimal("500.00")).build();

        transfer =
                Transfer.builder()
                        .id(1L)
                        .fromWallet(fromWallet)
                        .toWallet(toWallet)
                        .amount(new BigDecimal("200.00"))
                        .createdAt(LocalDateTime.now())
                        .fromUserLogin("sender")
                        .toUserLogin("receiver")
                        .build();
    }

    @Test
    void createTransfer_ShouldCreateTransfer() {
        TransferRequest request =
                TransferRequest.builder()
                        .toUserLogin("receiver")
                        .amount(new BigDecimal("200.00"))
                        .build();

        when(walletService.getCurrentUserWalletEntity()).thenReturn(fromWallet);
        when(userService.getCurrentUser()).thenReturn(fromUser);
        when(walletService.getWalletByUserLogin("receiver")).thenReturn(toWallet);
        when(transferRepository.save(any(Transfer.class))).thenReturn(transfer);
        when(transactionRepository.save(any())).thenReturn(null);
        when(walletRepository.save(any())).thenReturn(null);

        TransferResponse response = transferService.createTransfer(request);

        assertNotNull(response);
        assertEquals("sender", response.getFromUserLogin());
        assertEquals("receiver", response.getToUserLogin());
        assertEquals(new BigDecimal("200.00"), response.getAmount());

        assertEquals(new BigDecimal("800.00"), fromWallet.getBalance());
        assertEquals(new BigDecimal("700.00"), toWallet.getBalance());

        verify(transferRepository).save(any(Transfer.class));
        verify(transactionRepository, times(2)).save(any());
        verify(walletRepository, times(2)).save(any());
    }

    @Test
    void createTransfer_ShouldThrowExceptionWhenTransferToSelf() {
        TransferRequest request =
                TransferRequest.builder()
                        .toUserLogin("sender")
                        .amount(new BigDecimal("200.00"))
                        .build();

        when(walletService.getCurrentUserWalletEntity()).thenReturn(fromWallet);
        when(userService.getCurrentUser()).thenReturn(fromUser);

        assertThrows(ValidationException.class, () -> transferService.createTransfer(request));
        verify(transferRepository, never()).save(any());
    }

    @Test
    void createTransfer_ShouldThrowExceptionWhenInsufficientBalance() {
        TransferRequest request =
                TransferRequest.builder()
                        .toUserLogin("receiver")
                        .amount(new BigDecimal("2000.00"))
                        .build();

        when(walletService.getCurrentUserWalletEntity()).thenReturn(fromWallet);
        when(userService.getCurrentUser()).thenReturn(fromUser);
        when(walletService.getWalletByUserLogin("receiver")).thenReturn(toWallet);

        assertThrows(ValidationException.class, () -> transferService.createTransfer(request));
        verify(transferRepository, never()).save(any());
    }

    @Test
    void getAllTransfers_ShouldReturnAllTransfers() {
        when(walletService.getCurrentUserWalletEntity()).thenReturn(fromWallet);
        when(transferRepository.findByFromWalletOrToWalletOrderByCreatedAtDesc(
                        fromWallet, fromWallet))
                .thenReturn(List.of(transfer));

        List<TransferResponse> responses = transferService.getAllTransfers();

        assertNotNull(responses);
        assertEquals(1, responses.size());
    }

    @Test
    void getSentTransfers_ShouldReturnSentTransfers() {
        when(walletService.getCurrentUserWalletEntity()).thenReturn(fromWallet);
        when(transferRepository.findByFromWalletOrderByCreatedAtDesc(fromWallet))
                .thenReturn(List.of(transfer));

        List<TransferResponse> responses = transferService.getSentTransfers();

        assertNotNull(responses);
        assertEquals(1, responses.size());
    }

    @Test
    void getReceivedTransfers_ShouldReturnReceivedTransfers() {
        when(walletService.getCurrentUserWalletEntity()).thenReturn(toWallet);
        when(transferRepository.findByToWalletOrderByCreatedAtDesc(toWallet))
                .thenReturn(List.of(transfer));

        List<TransferResponse> responses = transferService.getReceivedTransfers();

        assertNotNull(responses);
        assertEquals(1, responses.size());
    }
}
