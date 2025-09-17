package xyz.whysoarbh.bugetbaba.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import xyz.whysoarbh.bugetbaba.dto.ExpenseDTO;
import xyz.whysoarbh.bugetbaba.entity.CategoryEntity;
import xyz.whysoarbh.bugetbaba.entity.ExpenseEntity;
import xyz.whysoarbh.bugetbaba.entity.ProfileEntity;
import xyz.whysoarbh.bugetbaba.repository.CategoryRepository;
import xyz.whysoarbh.bugetbaba.repository.ExpenseRepository;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExpenseService {
    private final CategoryRepository categoryRepository;
    private final ExpenseRepository expenseRepository;
    private final ProfileService profileService;

    public ExpenseDTO addExpense(ExpenseDTO expenseDTO) {
        ProfileEntity profile = profileService.getCurrentProfile();
        CategoryEntity category = categoryRepository.findById(expenseDTO.getCatergoryId())
                .orElseThrow(() -> new RuntimeException("Category not Found"));
        ExpenseEntity newExpense = toEntity(expenseDTO, profile, category);
        newExpense = expenseRepository.save(newExpense);
        return toDTO(newExpense);
    }

    // Current month expenses
    public List<ExpenseDTO> getCurrentMonthExpensesForCurrentUser() {
        LocalDate now = LocalDate.now();
        return getExpensesForDateRange(
                now.withDayOfMonth(1),
                now.withDayOfMonth(now.lengthOfMonth())
        );
    }

    // Generic date range
    public List<ExpenseDTO> getExpensesForDateRange(LocalDate startDate, LocalDate endDate) {
        ProfileEntity profile = profileService.getCurrentProfile();
        List<ExpenseEntity> list = expenseRepository.findByProfileIdAndDateBetween(
                profile.getId(),
                startDate,
                endDate
        );
        return list.stream().map(this::toDTO).toList();
    }
    //delete expense
    public void deleteExpenses(Long expenseId) {
        ProfileEntity profile = profileService.getCurrentProfile();
        ExpenseEntity entity = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new RuntimeException("Expense not Found"));

        if (!entity.getProfile().getId().equals(profile.getId())) {
            throw new RuntimeException("Not authorized to delete expense");
        }

        expenseRepository.delete(entity);
    }


    //  Helpers
    private ExpenseEntity toEntity(ExpenseDTO dto, ProfileEntity profile, CategoryEntity category) {
        return ExpenseEntity.builder()
                .name(dto.getName())
                .icon(dto.getIcon())
                .amount(dto.getAmount())
                .date(dto.getDate()) // LocalDate
                .profile(profile)
                .category(category)
                .build();
    }

    private ExpenseDTO toDTO(ExpenseEntity entity) {
        return ExpenseDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .icon(entity.getIcon())
                .catergoryId(entity.getCategory() != null ? entity.getCategory().getId() : null)
                .categoryName(entity.getCategory() != null ? entity.getCategory().getName() : "N/A")
                .amount(entity.getAmount())
                .date(entity.getDate()) // LocalDate
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
