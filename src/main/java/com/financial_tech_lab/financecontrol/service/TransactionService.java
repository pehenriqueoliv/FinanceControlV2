package com.financial_tech_lab.financecontrol.service;

import com.financial_tech_lab.financecontrol.dto.request.TransactionRequest;
import com.financial_tech_lab.financecontrol.dto.response.BalanceSummaryResponse;
import com.financial_tech_lab.financecontrol.dto.response.TransactionResponse;
import com.financial_tech_lab.financecontrol.entity.Category;
import com.financial_tech_lab.financecontrol.entity.Transaction;
import com.financial_tech_lab.financecontrol.entity.User;
import com.financial_tech_lab.financecontrol.entity.TransactionType;
import com.financial_tech_lab.financecontrol.exception.business.CategoryTypeMismatchException;
import com.financial_tech_lab.financecontrol.exception.notfound.TransactionNotFoundException;
import com.financial_tech_lab.financecontrol.mapper.TransactionMapper;
import com.financial_tech_lab.financecontrol.messaging.dto.TransactionNotificationMessage;
import com.financial_tech_lab.financecontrol.messaging.producer.NotificationProducer;
import com.financial_tech_lab.financecontrol.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserService userService;
    private final CategoryService categoryService;
    private final TransactionMapper transactionMapper;
    private final NotificationProducer notificationProducer;

    @CacheEvict(value = "balance-summary", key = "#request.userId()")
    @Transactional
    public TransactionResponse create(TransactionRequest request) {
        User user = userService.findEntityById(request.userId());
        Category category = categoryService.findEntityById(request.categoryId());

        validateCategoryType(request.type(), category);

        Transaction transaction = transactionMapper.toEntity(request, user, category);
        Transaction saved = transactionRepository.save(transaction);

        TransactionNotificationMessage message = new TransactionNotificationMessage(
                saved.getId(),
                user.getId(),
                user.getEmail(),
                saved.getDescription(),
                saved.getAmount(),
                saved.getType().name()
        );
        notificationProducer.publishTransactionCreated(message);

        return transactionMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public Page<TransactionResponse> findAll(
            Long userId,
            TransactionType type,
            Long categoryId,
            LocalDate startDate,
            LocalDate endDate,
            Pageable pageable
    ) {
        userService.findEntityById(userId);

        return transactionRepository
                .findWithFilters(userId, type, categoryId, startDate, endDate, pageable)
                .map(transactionMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public TransactionResponse findById(Long id) {
        return transactionRepository.findById(id)
                .map(transactionMapper::toResponse)
                .orElseThrow(() -> new TransactionNotFoundException(id));
    }

    @CacheEvict(value = "balance-summary", allEntries = true)
    @Transactional
    public TransactionResponse update(Long id, TransactionRequest request) {
        Transaction existing = transactionRepository.findById(id)
                .orElseThrow(() -> new TransactionNotFoundException(id));

        User user = userService.findEntityById(request.userId());
        Category category = categoryService.findEntityById(request.categoryId());

        validateCategoryType(request.type(), category);

        existing.setDescription(request.description());
        existing.setAmount(request.amount());
        existing.setType(request.type());
        existing.setDate(request.date());
        existing.setUser(user);
        existing.setCategory(category);

        return transactionMapper.toResponse(transactionRepository.save(existing));
    }

    @CacheEvict(value = "balance-summary", allEntries = true)
    @Transactional
    public void delete(Long id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new TransactionNotFoundException(id));
        transactionRepository.delete(transaction);
    }

    @Cacheable(value = "balance-summary", key = "#userId")
    @Transactional(readOnly = true)
    public BalanceSummaryResponse getBalanceSummary(Long userId) {
        userService.findEntityById(userId);

        BigDecimal totalIncome = transactionRepository
                .sumByUserIdAndType(userId, TransactionType.INCOME);

        BigDecimal totalExpense = transactionRepository
                .sumByUserIdAndType(userId, TransactionType.EXPENSE);

        BigDecimal balance = totalIncome.subtract(totalExpense);

        return new BalanceSummaryResponse(totalIncome, totalExpense, balance);
    }

    private void validateCategoryType(TransactionType transactionType, Category category) {
        if (!category.getType().equals(transactionType)) {
            throw new CategoryTypeMismatchException(category.getType(), transactionType);
        }
    }
}
