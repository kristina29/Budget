package controller;

import java.sql.SQLException;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Expense;
import model.Expense.Categories;
import model.Month;

/**
 * Controller für den NewExpenseDialogue.
 */
public class NewExpenseController {
	
	@FXML private TextField dayField;
	@FXML private ComboBox<Categories> categoryBox;
	@FXML private TextField amountField;
	@FXML private TextArea infoText;
	@FXML private Label errorLabel;
	
	private Stage stage;
	private Month m;
	private ObservableList<Expense> expenses;
	private MainFrameController parent;
	
	/**
	 * Initialisierung.
	 */
	public void initialize() {
		Platform.runLater(() -> {
			this.stage = (Stage) this.dayField.getScene().getWindow();
			this.dayField.requestFocus();
		});
		
		//Alle Kategorien als Auswahlmöglichkeit hinzufügen
		for (Categories e : Categories.values()) {
			this.categoryBox.getItems().add(e);
		}
	}
	
	/**
	 * Attribute festlegen, weil kein Konstruktor aufgerufen wird.
	 */
	public void setAttributes(Month m, ObservableList<Expense> expenses, MainFrameController parent) {
		this.m = m;
		this.expenses = expenses;
		this.parent = parent;
	}
	
	/**
	 * Eventhandling für den Confirm-Button.
	 */
	@FXML
	public final void manageConfirmButton() {
		try {
			Categories c = this.categoryBox.getValue();
			String day = this.dayField.getText();
			String amount = this.amountField.getText();
			
			//Kategorie, Tag und Betrag muss angegeben werden
			if (c == null || day == null || amount == null) {
				throw new InputInvalidException(Messages_DE.FILL_ALL_BESIDES_INFO);
			}
			
			//Betrag nur aus Zahlen, Dezimalzeichen . oder ,
			double amountDouble = 0.0;
			try {
				amountDouble = Double.parseDouble(amount.replace(',', '.'));
			} catch (NumberFormatException e) {
				ExceptionAlert.show(Messages_DE.AMOUNT_NUMERIC);
			}
			
			//Neue Ausgabe erstellen, in der Datenbank speichern und in der Tabelle des Mainframes hinzufügen
			Expense expense = new Expense(this.m, Integer.parseInt(day), c, amountDouble, this.infoText.getText());
			this.parent.getModelFacade().addExpense(expense);
		
			this.expenses.add(expense);
			this.expenses.sort((a, b) -> Integer.compare(a.getDay(), b.getDay()));
			this.stage.close();
		} catch (NumberFormatException e) {
			ExceptionAlert.show(Messages_DE.DAY_NUMERIC);
		} catch (InputInvalidException e) {
			ExceptionAlert.show(e.getMessage());
		} catch (SQLException e) {
			ExceptionAlert.show(Messages_DE.DATABASE_EXCEPTION);
			e.printStackTrace();
		}
	}
	
	/**
	 * Eventhandling für den Cancel-Button.
	 */
	@FXML
	public final void manageCancelButton() {
		this.stage.close();
	}

}
