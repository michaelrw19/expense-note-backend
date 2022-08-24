package demo.expensetracker;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import demo.expensetracker.model.Expense;
import demo.expensetracker.service.ExpenseService;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

@RestController
@RequestMapping("/expense")
public class ExpenseResource {

  private final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

  private static final String UPDATE_EXPENSE = "update";
  private static final String ADD_EXPENSE = "add";
  private static final String DELETE_EXPENSE = "delete";
  private final ExpenseService expenseService;
  private final String[] months = {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"};

  private HashMap<String, Double[]> costs = new HashMap<>();
  private HashMap<String, Double> totalCost = new HashMap<>();

  public ExpenseResource(ExpenseService expenseService) {
    this.expenseService = expenseService;
  }

  // Cost Functions //
  @GetMapping("/totalCost")
  public ResponseEntity<String> getTotalExpense(String date) {
    String totalCost = "";
    String year = date.substring(0, 4);
    if(!this.costs.containsKey(year) && !this.totalCost.containsKey(year)) {
      this.addYear(year);
    }
    if (this.totalCost.get(year) == 0.0) {
      this.calculateTotalExpenses(year);
    }

    if(date.length() == 4) {
      totalCost = String.valueOf(this.totalCost.get(year));
    }
    else if (date.length() > 4) {
      String month = date.substring(5, 7);
      int monthIndex = Integer.valueOf(month)-1;
      totalCost = String.valueOf(this.costs.get(year)[monthIndex]);
    }
    return new ResponseEntity<>(totalCost, HttpStatus.OK);
  }

  @GetMapping("/totalCostPerMonth")
  public ResponseEntity<Double[]> getTotalCostPerMonth(String year) {
    Double[] totalCosts = this.costs.get(year);
    return new ResponseEntity<>(totalCosts, HttpStatus.OK);
  }

  // Cost Functions //

  // Expense Functions //
  @GetMapping("/getExpensesByMonth")
  public ResponseEntity<List<Expense>> getExpensesByMonth(String date) {
    List<Expense> expenses = this.getExpensesByMonth_Private(date);
    return new ResponseEntity<>(expenses, HttpStatus.OK);
  }
  // Expense Functions //

  // Filter Functions //
  @GetMapping("/applySearchFilter")
  public ResponseEntity<List<Expense>> applySearchFilter(String keyword, String date) {
    List<Expense> expenses = this.getExpensesByMonth_Private(date);
    List<Expense> searchedExpenses = new ArrayList<>();

    ListIterator<Expense> expenseIterator = expenses.listIterator();
    String searchKeyword = keyword.toLowerCase();
    while (expenseIterator.hasNext()) {
      Expense expense = expenseIterator.next();
      String expenseDescription = expense.getDescription().toLowerCase();
      if (expenseDescription.substring(0, searchKeyword.length()).equals(searchKeyword)) {
        searchedExpenses.add(expense);
      }
    }
    return new ResponseEntity<>(searchedExpenses, HttpStatus.OK);
  }

  @GetMapping("/applyCostFilter")
  public ResponseEntity<List<Expense>> applyCostFilter(Double range1, Double range2, String code, String date) {
    List<Expense> expenses = this.getExpensesByMonth_Private(date);
    List<Expense> searchedExpenses = new ArrayList<>();

    ListIterator<Expense> expenseIterator = expenses.listIterator();
    if (code.equals("GT")){ //Greater Than
      while (expenseIterator.hasNext()) {
        Expense expense = expenseIterator.next();
        Double expenseCost = expense.getCost();
        if (expenseCost > range1) { //Greater Than
          searchedExpenses.add(expense);
        }
      }
    }
    else if (code.equals("GTE")) { //Greater Than or Equal
      while (expenseIterator.hasNext()) {
        Expense expense = expenseIterator.next();
        Double expenseCost = expense.getCost();
        if (expenseCost >= range1) {
          searchedExpenses.add(expense);
        }
      }
    }
    else if (code.equals("LT")) { //Less Than
      while (expenseIterator.hasNext()) {
        Expense expense = expenseIterator.next();
        Double expenseCost = expense.getCost();
        if (expenseCost < range1) {
          searchedExpenses.add(expense);
        }
      }
    }
    else if (code.equals("LTE")) { //Less Than or Equal
      while (expenseIterator.hasNext()) {
        Expense expense = expenseIterator.next();
        Double expenseCost = expense.getCost();
        if (expenseCost <= range1) {
          searchedExpenses.add(expense);
        }
      }
    }
    else if (code.equals("B")) { //Between
      while (expenseIterator.hasNext()) {
        Expense expense = expenseIterator.next();
        Double expenseCost = expense.getCost();
        if (expenseCost >= range1 && expenseCost <= range2) {
          searchedExpenses.add(expense);
        }
      }
    }
    return new ResponseEntity<>(searchedExpenses, HttpStatus.OK);
  }
  // Filter Functions //


  @GetMapping("/all")
  public ResponseEntity<List<Expense>> getAllExpenses () {
    List<Expense> expenses = expenseService.findAllExpenses();
    return new ResponseEntity<>(expenses, HttpStatus.OK);
  }

  @GetMapping("/find/{id}")
  public ResponseEntity<Expense> getExpenseById (@PathVariable("id") Long id) {
    Expense expense = expenseService.findExpenseById(id);
    return new ResponseEntity<>(expense, HttpStatus.OK);
  }

  @PostMapping("/add")
  public ResponseEntity<Expense> addExpense(@RequestBody Expense expense) {
    this.updateCostInfo(expense, this.ADD_EXPENSE);
    Expense newExpense = expenseService.addExpense(expense);
    return new ResponseEntity<>(newExpense, HttpStatus.CREATED);
  }

  @PutMapping("/update")
  public ResponseEntity<Expense> updateExpense(@RequestBody Expense expense) {
    this.updateCostInfo(expense, this.UPDATE_EXPENSE);
    Expense updateExpense = expenseService.updateExpense(expense);
    return new ResponseEntity<>(updateExpense, HttpStatus.OK);
  }

  @DeleteMapping("/delete/{id}")
  @Transactional
  public ResponseEntity<?> deleteExpense(@PathVariable("id") Long id) {
    this.updateCostInfo(expenseService.findExpenseById(id), this.DELETE_EXPENSE);
    expenseService.deleteExpense(id);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  private void addYear (String year) {
    this.costs.put(year, new Double[12]);
    this.totalCost.put(year, 0.0);
  }
  private List<Expense> getExpensesByMonth_Private (String date) {
    List<Expense> newExpenses = new ArrayList<>();
    ListIterator<Expense> expenseIterator = expenseService.findAllExpenses().listIterator();
    while (expenseIterator.hasNext()) {
      Expense expense = expenseIterator.next();
      String expenseDate = expense.getDate();
      if (expenseDate.contains(date)) {
        newExpenses.add(expense);
      }
    }
    return newExpenses;
  }

  private void calculateTotalExpenses(String year) {
    Double[] totalCosts = new Double[12];
    List<Expense> expenses = expenseService.findAllExpenses();
    ListIterator<Expense> expenseIterator = expenses.listIterator();
    String date = "";
    Double yearlyCost = 0.0;
    for(int i = 0; i < months.length; i++) {
      date = year + "-" + months[i];
      double monthlyCost = 0;
      while(expenseIterator.hasNext()) {
        Expense expense = expenseIterator.next();
        Double expenseCost = expense.getCost();
        if(expense.getDate().contains(date)) {
          monthlyCost += expenseCost;
        }
        if(expense.getDate().contains(year) && i == 0) {
          yearlyCost += expenseCost;
        }
      }
      totalCosts[i] = monthlyCost;
      expenseIterator = expenses.listIterator();
    }
    this.costs.put(year, totalCosts);
    this.totalCost.put(year, yearlyCost);
  }
  private void updateCostInfo (Expense expense, String code) {
    double cost = expense.getCost();
    String year = expense.getYear();
    int monthIndex = Integer.valueOf(expense.getMonth())-1;

    if(code.equals("add")) {
      Double newCost = this.totalCost.get(year) + cost;
      this.totalCost.replace(year, newCost);
      this.costs.get(year)[monthIndex] += cost;
    }
    else if (code.equals("delete")) {
      Double newCost = this.totalCost.get(year) - cost;
      this.totalCost.replace(year, newCost);
      this.costs.get(year)[monthIndex] -= cost;
    }
    else if (code.equals("update")) {
      long id = expense.getId();
      Expense oldExpense = expenseService.findExpenseById(id);

      Double newYearlyCost = this.totalCost.get(year) - oldExpense.getCost() + expense.getCost();
      this.totalCost.replace(year, newYearlyCost);

      Double newMonthlyCost = this.costs.get(year)[monthIndex] - oldExpense.getCost() + expense.getCost();
      this.costs.get(year)[monthIndex] = newMonthlyCost;
    }
  }
}
