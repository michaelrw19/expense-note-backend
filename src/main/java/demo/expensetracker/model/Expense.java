package demo.expensetracker.model;

import javax.persistence.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Entity
public class Expense implements Comparable<Expense> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private Long id;
    private Double cost;
    private String date;
    private String description;

    public Expense(){}

    public Expense(Double cost, String date, String description){
        this.cost = cost;
        this.date = date;
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
      Expense expense = (Expense) o;
      return this.id == expense.getId();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
      //Date format: YYYY-MM-DD
      return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getYear() {
      return this.date.substring(0, 4);
    }

    public String getMonth() {
      return this.date.substring(5, 7);
    }

    public String getDay() {
      return this.date.substring(8, 10);
    }

  @Override
  public int compareTo(Expense o) {
    int thisDate = Integer.parseInt(this.getMonth() + this.getDay());
    int otherDate = Integer.parseInt(o.getMonth() + o.getDay());

    if (thisDate > otherDate) return 1;
    else if (thisDate == otherDate) return 0;
    else return -1;
  }
}
