package demo.expensetracker.service;

import demo.expensetracker.exception.UserNotFoundException;
import demo.expensetracker.model.Expense;
import demo.expensetracker.repo.ExpenseRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExpenseService {
    private final ExpenseRepo expenseRepo;

    @Autowired
    public ExpenseService(ExpenseRepo expenseRepo) {
        this.expenseRepo = expenseRepo;
    }

    public Expense addExpense(Expense expense) {
        return expenseRepo.save(expense);
    }

    public List<Expense> findAllExpenses(){
        return expenseRepo.findAll();
    }

    public Expense updateExpense(Expense expense) {
        return expenseRepo.save(expense);
    }

    public void deleteExpense(Long id) {
        expenseRepo.deleteExpenseById(id);
    }

    public Expense findExpenseById(Long id) {
        return expenseRepo.findExpenseById(id)
                .orElseThrow(() -> new UserNotFoundException("User with Id " + id + " not found" ));
    }
}
