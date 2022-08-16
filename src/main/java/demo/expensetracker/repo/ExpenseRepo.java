package demo.expensetracker.repo;

import demo.expensetracker.model.Expense;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ExpenseRepo extends JpaRepository<Expense, Long> {

    void deleteExpenseById(Long id);

    Optional<Expense> findExpenseById(Long id);
}
