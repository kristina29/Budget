package model;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import java.time.YearMonth;

import model.Expense.Categories;

/**
 * Repräsentiert einen Monat.
 *
 */
public class Month {
	
	private YearMonth date;
	private Set<Expense> expenses;
	
	/**
	 * Konstruktur.
	 * @param month
	 * @param year
	 */
	public Month (int month, int year) {
		this.date = YearMonth.of(year, month);
		this.expenses = new HashSet<Expense>();
	}
	
	/**
	 * Gibt den Monat zurück.
	 */
	public int getMonth() {
		return this.date.getMonthValue();
	}
	
	/**
	 * Gibt das Jahr zurück.
	 */
	public int getYear() {
		return this.date.getYear();
	}
	
	/**
	 * Gibt das Datum zurück.
	 */
	public YearMonth getDate() {
		return this.date;
	}
	
	/**
	 * Gibt die List der Ausgaben in diesem Monat zurück.
	 */
	public Set<Expense> getExpenses() {
		return this.expenses;
	}
	
	/**
	 * Berechnet die Gesamtsumme der Ausgaben.
	 */
	public double getTotalSum() {
		double totalSum = 0.0;
		for (Expense e: this.expenses) {
			totalSum = totalSum + e.getAmount();
		}
		return totalSum;
	}
	
	/**
	 * Berechnet die Summe der Ausgaben einer bestimmten Kategorie.
	 */
	public double getCategorySum(Categories category) {
		double sum = 0.0;
		for (Expense e: this.expenses) {
			if (e.getCategory() == category) {
				sum = sum + e.getAmount();
			}
		}
		return sum;
	}
	
	/**
	 * Fügt eine Ausgbae hinzu.
	 */
	public void addExpense(Expense expense) {
		boolean enthalten = false;
		for (Expense containedE: this.expenses) {
			if (containedE.equals(expense)) {
				enthalten = true;
			}
		}
		if (!enthalten) {
			this.expenses.add(expense);
		}
	}
	
	/**
	 * Fügt eine Liste von Ausgaben hinzu.
	 */
	public void addExpenses(List<Expense> expenses) {
		this.expenses.addAll(expenses);
	}
	
	/**
	 * Entfernt eine Ausgbae.
	 */
	public void removeExpense(Expense expense) {
		this.expenses.remove(expense);
	}
	
	@Override
	public String toString() {
		return this.date.getMonthValue() + "/" + this.date.getYear();
	}
}

