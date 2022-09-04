package demo.expensetracker.comparator;

import demo.expensetracker.model.Expense;

import java.util.Comparator;

public class DateComparator implements Comparator<Expense> {
  @Override
  //Recent-Latest
  public int compare(Expense o1, Expense o2) {
    int thisDate = Integer.parseInt(o1.getMonth() + o1.getDay());
    int otherDate = Integer.parseInt(o2.getMonth() + o2.getDay());

    if (thisDate > otherDate) return 1;
    else if (thisDate == otherDate) return 0;
    else return -1;
  }
}
