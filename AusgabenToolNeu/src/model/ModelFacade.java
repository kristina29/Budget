package model;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

import dataAccess.ExpensesManager;
import dataAccess.MonthManager;

/**
 * Fassade.
 */
public class ModelFacade {
    private MonthManager monthManager = MonthManager.getInstance();
    private ExpensesManager expensesManager = ExpensesManager.getInstance();
    
    /**
     * Gibt die Monate zurück.
     * @return List<Months> Unmodifiable
     */
    public final List<Month> getMonths() throws SQLException {
        return Collections.unmodifiableList(this.monthManager.selectAllMonths());
    }
    
    /**
     * Gibt die Monate eines Jahres zurück.
     */
    public final List<Month> getMonth(final int year) throws SQLException {
        return this.monthManager.selectMonthsInYear(year);
    }
    
    /**
     * Löscht den Monat.
     */
    public final void deleteMonth(Month m) throws SQLException {
        this.monthManager.deleteMonth(m.getMonth(), m.getYear());
    }
    
    /**
     * Fügt einen Monat hinzu.
     */
    public final void addMonth(Month m) throws SQLException {
        this.monthManager.insertMonth(m);
    }
    
    /**
     * Gibt die Ausgaben eines Monats zurück.
     * @return List<Expense> Unmodifiable
     */
    public final List<Expense> getExpenses(Month m) throws SQLException {
        return Collections.unmodifiableList(this.expensesManager.getExpenses(m));
    }

    /**
     * Fügt eine Ausgabe hinzu.
     */
    public final void addExpense(Expense e) throws SQLException {
        this.expensesManager.insertExpense(e);
    }

    /**
     * Löscht die Ausgabe.
     */
    public final void deleteExpense(Expense e) throws NoSuchElementException, SQLException {
        e.getMonth().getExpenses().remove(e);
        this.expensesManager.deleteExpense(e.getDay(), e.getMonth().getMonth(), e.getMonth().getYear(), e.getCategory(),
        		e.getAmount(), e.getInfo());
    }
}
