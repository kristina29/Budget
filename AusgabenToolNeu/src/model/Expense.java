package model;

public class Expense {
	
	/**
	 * Kategorien einer Ausgabe.
	 */
	public static enum Categories {
		Urlaub, Simba, Lebensmittel, Haushalt, Freizeit, Sport, Diverse
	}
	
	private int day;
	private Categories category;
	private double amount;
	private String info;
	private Month month;
	
	/**
	 * Konstruktor.
	 */
	public Expense (Month month, int day, Categories category, double amount, String info) {
		if (!month.getExpenses().contains(this)) {
			month.addExpense(this);
		}
		this.month = month;
		this.day = day;
		this.category = category;
		this.amount = amount;
		this.info = info;
	}
	
	/**
	 * Gibt die Kategorie zurück.
	 */
	public Categories getCategory() {
		return this.category;
	}
	
	/**
	 * Gibt den Betrag der Ausgabe zurück.
	 */
	public double getAmount() {
		return this.amount;
	}
	
	/**
	 * Gibt die Info der Ausgabe zurück.
	 */
	public String getInfo() {
		return this.info;
	}
		
	/**
	 * Gibt den Tag der Ausgabe zurück.
	 */
	public int getDay() {
		return this.day;
	}
	
	/**
	 * Gibt den Monat der Ausgabe zurück.
	 */
	public Month getMonth() {
		return this.month;
	}
}
