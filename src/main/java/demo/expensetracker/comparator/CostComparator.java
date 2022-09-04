package demo.expensetracker.comparator;

import demo.expensetracker.model.Expense;

import java.util.Comparator;

public class CostComparator implements Comparator<Expense> {
  @Override
  //Low-High
  public int compare(Expense o1, Expense o2) {
    double thisCost = o1.getCost();
    double otherCost = o2.getCost();

    if (thisCost > otherCost) return 1;
    else if (thisCost == otherCost) return 0;
    else return -1;
  }
}
