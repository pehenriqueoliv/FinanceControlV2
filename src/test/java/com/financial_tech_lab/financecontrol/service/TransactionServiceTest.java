package com.financial_tech_lab.financecontrol.service;

import com.financial_tech_lab.financecontrol.dto.request.TransactionRequest;
import com.financial_tech_lab.financecontrol.dto.response.BalanceSummaryResponse;
import com.financial_tech_lab.financecontrol.dto.response.CategoryResponse;
import com.financial_tech_lab.financecontrol.dto.response.TransactionResponse;
import com.financial_tech_lab.financecontrol.entity.Category;
import com.financial_tech_lab.financecontrol.entity.Transaction;
import com.financial_tech_lab.financecontrol.entity.TransactionType;
import com.financial_tech_lab.financecontrol.entity.User;
import com.financial_tech_lab.financecontrol.exception.business.CategoryTypeMismatchException;
import com.financial_tech_lab.financecontrol.exception.notfound.CategoryNotFoundException;
import com.financial_tech_lab.financecontrol.exception.notfound.TransactionNotFoundException;
import com.financial_tech_lab.financecontrol.exception.notfound.UserNotFoundException;
import com.financial_tech_lab.financecontrol.mapper.TransactionMapper;
import com.financial_tech_lab.financecontrol.messaging.producer.NotificationProducer;
import com.financial_tech_lab.financecontrol.repository.TransactionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private UserService userService;

    @Mock
    private CategoryService categoryService;

    @Mock
    private TransactionMapper transactionMapper;

    @Mock
    private NotificationProducer notificationProducer;

    @InjectMocks
    private TransactionService transactionService;

    // helpers

    private User buildUser() {
        return User.builder().id(1L).name("Pedro").email("pedro@email.com").build();
    }

    private Category buildCategory(TransactionType type) {
        return Category.builder().id(1L).name("Salary").type(type).build();
    }

    private TransactionRequest buildRequest(TransactionType type) {
        return new TransactionRequest(
                "Test transaction",
                new BigDecimal("1000.00"),
                type,
                LocalDate.now(),
                1L,
                1L
        );
    }

    private TransactionResponse buildResponse(Transaction transaction) {
        return new TransactionResponse(
                transaction.getId(),
                transaction.getDescription(),
                transaction.getAmount(),
                transaction.getType(),
                transaction.getDate(),
                new CategoryResponse(1L, "Salary", transaction.getType()),
                1L,
                LocalDateTime.now()
        );
    }

    @Test
    @DisplayName("Should create INCOME transaction successfully")
    void shouldCreateIncomeTransactionSuccessfully() {
        TransactionRequest request = buildRequest(TransactionType.INCOME);
        User user = buildUser();
        Category category = buildCategory(TransactionType.INCOME);

        Transaction entity = Transaction.builder()
                .id(1L)
                .description(request.description())
                .amount(request.amount())
                .type(TransactionType.INCOME)
                .date(request.date())
                .user(user)
                .category(category)
                .build();

        TransactionResponse response = buildResponse(entity);

        when(userService.findEntityById(1L)).thenReturn(user);
        when(categoryService.findEntityById(1L)).thenReturn(category);
        when(transactionMapper.toEntity(request, user, category)).thenReturn(entity);
        when(transactionRepository.save(entity)).thenReturn(entity);
        when(transactionMapper.toResponse(entity)).thenReturn(response);

        TransactionResponse result = transactionService.create(request);

        assertThat(result.type()).isEqualTo(TransactionType.INCOME);
        assertThat(result.amount()).isEqualByComparingTo("1000.00");
        verify(transactionRepository).save(entity);
        verify(notificationProducer).publishTransactionCreated(any());
    }

    @Test
    @DisplayName("Should throw CategoryTypeMismatchException when category type mismatches transaction type")
    void shouldThrowWhenCategoryTypeMismatch() {
        TransactionRequest request = buildRequest(TransactionType.INCOME);
        User user = buildUser();
        Category expenseCategory = buildCategory(TransactionType.EXPENSE);

        when(userService.findEntityById(1L)).thenReturn(user);
        when(categoryService.findEntityById(1L)).thenReturn(expenseCategory);

        assertThrows(CategoryTypeMismatchException.class, () -> transactionService.create(request));

        verify(transactionRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when user does not exist")
    void shouldThrowWhenUserNotFound() {
        TransactionRequest request = buildRequest(TransactionType.INCOME);

        when(userService.findEntityById(1L)).thenThrow(new UserNotFoundException(1L));

        assertThrows(UserNotFoundException.class, () -> transactionService.create(request));

        verify(transactionRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw CategoryNotFoundException when category does not exist")
    void shouldThrowWhenCategoryNotFound() {
        TransactionRequest request = buildRequest(TransactionType.INCOME);
        User user = buildUser();

        when(userService.findEntityById(1L)).thenReturn(user);
        when(categoryService.findEntityById(1L)).thenThrow(new CategoryNotFoundException(1L));

        assertThrows(CategoryNotFoundException.class, () -> transactionService.create(request));

        verify(transactionRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should return transaction by id")
    void shouldReturnTransactionById() {
        User user = buildUser();
        Category category = buildCategory(TransactionType.EXPENSE);

        Transaction entity = Transaction.builder()
                .id(1L)
                .description("Market")
                .amount(new BigDecimal("250.00"))
                .type(TransactionType.EXPENSE)
                .date(LocalDate.now())
                .user(user)
                .category(category)
                .build();

        TransactionResponse response = buildResponse(entity);

        when(transactionRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(transactionMapper.toResponse(entity)).thenReturn(response);

        TransactionResponse result = transactionService.findById(1L);

        assertThat(result.id()).isEqualTo(1L);
        verify(transactionRepository).findById(1L);
    }

    @Test
    @DisplayName("Should throw when transaction not found")
    void shouldThrowWhenTransactionNotFound() {
        when(transactionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(TransactionNotFoundException.class, () -> transactionService.findById(99L));
    }

    @Test
    @DisplayName("Should delete transaction successfully")
    void shouldDeleteTransaction() {
        Transaction entity = Transaction.builder().id(1L).build();

        when(transactionRepository.findById(1L)).thenReturn(Optional.of(entity));

        transactionService.delete(1L);

        verify(transactionRepository).delete(entity);
    }

    @Test
    @DisplayName("Should throw when deleting non-existent transaction")
    void shouldThrowWhenDeletingNonExistentTransaction() {
        when(transactionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(TransactionNotFoundException.class, () -> transactionService.delete(99L));

        verify(transactionRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Should update transaction successfully")
    void shouldUpdateTransactionSuccessfully() {
        User user = buildUser();
        Category category = buildCategory(TransactionType.EXPENSE);

        Transaction existing = Transaction.builder()
                .id(1L)
                .description("Old description")
                .amount(new BigDecimal("100.00"))
                .type(TransactionType.EXPENSE)
                .date(LocalDate.now())
                .user(user)
                .category(category)
                .build();

        TransactionRequest updateRequest = new TransactionRequest(
                "New description",
                new BigDecimal("200.00"),
                TransactionType.EXPENSE,
                LocalDate.now(),
                1L,
                1L
        );

        Transaction updated = Transaction.builder()
                .id(1L)
                .description("New description")
                .amount(new BigDecimal("200.00"))
                .type(TransactionType.EXPENSE)
                .date(LocalDate.now())
                .user(user)
                .category(category)
                .build();

        TransactionResponse response = buildResponse(updated);

        when(transactionRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(userService.findEntityById(1L)).thenReturn(user);
        when(categoryService.findEntityById(1L)).thenReturn(category);
        when(transactionRepository.save(existing)).thenReturn(updated);
        when(transactionMapper.toResponse(updated)).thenReturn(response);

        TransactionResponse result = transactionService.update(1L, updateRequest);

        assertThat(result.description()).isEqualTo("New description");
        assertThat(result.amount()).isEqualByComparingTo("200.00");
        verify(transactionRepository).save(existing);
    }

    @Test
    @DisplayName("Should throw when updating non-existent transaction")
    void shouldThrowWhenUpdatingNonExistentTransaction() {
        TransactionRequest request = buildRequest(TransactionType.EXPENSE);

        when(transactionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(TransactionNotFoundException.class, () -> transactionService.update(99L, request));

        verify(transactionRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should calculate balance summary correctly")
    void shouldCalculateBalanceSummary() {
        when(userService.findEntityById(1L)).thenReturn(buildUser());
        when(transactionRepository.sumByUserIdAndType(1L, TransactionType.INCOME))
                .thenReturn(new BigDecimal("3000.00"));
        when(transactionRepository.sumByUserIdAndType(1L, TransactionType.EXPENSE))
                .thenReturn(new BigDecimal("1200.00"));

        BalanceSummaryResponse result = transactionService.getBalanceSummary(1L);

        assertThat(result.getTotalIncome()).isEqualByComparingTo("3000.00");
        assertThat(result.getTotalExpense()).isEqualByComparingTo("1200.00");
        assertThat(result.getBalance()).isEqualByComparingTo("1800.00");
    }

    @Test
    @DisplayName("Should return negative balance when expenses exceed income")
    void shouldReturnNegativeBalance() {
        when(userService.findEntityById(1L)).thenReturn(buildUser());
        when(transactionRepository.sumByUserIdAndType(1L, TransactionType.INCOME))
                .thenReturn(new BigDecimal("500.00"));
        when(transactionRepository.sumByUserIdAndType(1L, TransactionType.EXPENSE))
                .thenReturn(new BigDecimal("1500.00"));

        BalanceSummaryResponse result = transactionService.getBalanceSummary(1L);

        assertThat(result.getBalance()).isEqualByComparingTo("-1000.00");
    }
}
