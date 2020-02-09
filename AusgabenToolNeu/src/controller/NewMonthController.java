package controller;

import java.sql.SQLException;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Month;

/**
 * Controller für den NewMonthDialogue.
 */
public class NewMonthController {
	
	@FXML private TextField monthField;
	@FXML private TextField yearField;
	
	private ObservableList<Month> monthOptions = FXCollections.observableArrayList();
	private MainFrameController parent;
	private Stage stage;
	
	/**
	 * Initialisierung.
	 */
	public void initialize() {
		Platform.runLater(() -> this.stage = (Stage) this.monthField.getScene().getWindow());
	}

	/**
	 * Attribute festlegen, weil kein Konstruktor aufgerufen wird.
	 */
	public void setAttributes(ObservableList<Month> monthOptions, MainFrameController parent) {
		this.monthOptions = monthOptions;
		this.parent = parent;
	}
	
	/**
	 * Eventhandling für den Confirm-Button.
	 */
	@FXML
	public final void confirm() {
		try {
			String m = this.monthField.getText();
			String y = this.yearField.getText();
			
			//Monat und Jahr muss angegeben sein
			if (m == null || y == null) {
				throw new InputInvalidException(Messages_DE.FILL_ALL);
			}
			
			//Neuen Monat erstellen, in der Datenbank speichern und im Mainframe auswählen
			Month newMonth = new Month(Integer.parseInt(this.monthField.getText()), Integer.parseInt(this.yearField.getText()));
			this.parent.getModelFacade().addMonth(newMonth);
	
			this.monthOptions.add(newMonth);
			this.monthOptions.sort((a, b) -> {
				if (a.getDate().isBefore(b.getDate())) {
					return 1;
				}
				return -1;
			});
			
			this.parent.selectMonth(newMonth);
			this.stage.close();
			
		} catch (NumberFormatException e) {
			ExceptionAlert.show(Messages_DE.MONTH_YEAR_NUMERIC);
		} catch (InputInvalidException e) {
			ExceptionAlert.show(e.getMessage());
		} catch (SQLException e) {
			ExceptionAlert.show(Messages_DE.DATABASE_EXCEPTION);
			e.printStackTrace();
		}
	}

}
