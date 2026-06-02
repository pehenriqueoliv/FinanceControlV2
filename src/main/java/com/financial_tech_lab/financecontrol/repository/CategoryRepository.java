package com.financial_tech_lab.financecontrol.repository;

import com.financial_tech_lab.financecontrol.entity.Category;
import com.financial_tech_lab.financecontrol.entity.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByType(TransactionType type);
    boolean existsByName(String name);
}
