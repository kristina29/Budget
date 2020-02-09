package controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.time.YearMonth;
import java.util.NoSuchElementException;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableValue;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.Expense;
import model.Expense.Categories;
import model.ModelFacade;
import model.Month;

/**
 * Controller für das MainFrame.
 */
public class MainFrameController {
	
	private static final String ALIGNMENT_CENTER ="-fx-alignment: CENTER;";
	private static final String ALIGNMENT_CENTER_LEFT ="-fx-alignment: CENTER;";
	private static final String CATEGORY_PROPERTY = "category";
	private static final String INFO_PROPERTY = "info";
		
	private ModelFacade modelFacade = new ModelFacade();
    private ObservableList<Month> monthOptions = FXCollections.observableArrayList();
    private ObservableList<Expense> expenses = FXCollections.observableArrayList();
    private ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
    private DoubleProperty sum = new SimpleDoubleProperty(0.0);
    
    private ObservableList<Integer> yearOptions = FXCollections.observableArrayList();
    	
	@FXML private ComboBox<Month> monthComboBox;
	@FXML private Button newExpense;
	@FXML private TableView<Expense> expenseTable;
	@FXML private TableColumn<Expense, String> days;
	@FXML private TableColumn<Expense, Categories> categories;
	@FXML private TableColumn<Expense, String> amounts;
	@FXML private TableColumn<Expense, String> descriptions;
	@FXML private Label sumLabel;
	@FXML private PieChart expensePieChart;
	
	@FXML private ComboBox<Integer> yearComboBox;
	@FXML private BarChart<String, Number> expensesChart;
	
	/**
	 * Initialisierung.
	 */
	public void initialize() {
		this.newExpense.setVisible(false);
		this.expenseTable.setEditable(false);
		this.expenseTable.setItems(this.expenses);
		this.monthComboBox.setItems(this.monthOptions);
		this.yearComboBox.setItems(this.yearOptions);
		this.expensePieChart.setData(this.pieChartData);
		
		Platform.runLater(() -> {
			this.initListener();
			this.initMonth();
			this.initTable();
			this.initYear();
		});
	}
	
	/**
	 * Gibt die ModelFacade zurück.
	 */
	ModelFacade getModelFacade() {
		return this.modelFacade;
	}
	
	/**
	 * Wählt den übergebenen Monat in der ComboBox aus.
	 */
	void selectMonth(Month m) {
		this.monthComboBox.getSelectionModel().select(m);
	}
	
	private void initListener() {
		//Bei Neuberechnung der Summe die neue Summe anzeigen und das Tortendiagramm neu erstellen
		this.sum.addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
			this.sumLabel.setText(this.round(newValue.doubleValue()) + " €");
			this.createPieChart();
		});
		
		//Bei Hinzufügen oder Entfernen von Ausgaben die Summe neu berechnen
		this.expenses.addListener((javafx.collections.ListChangeListener.Change<? extends Expense> c) -> {
			while(c.next()) {
				if (c.wasAdded()) {
					for (Expense e : c.getAddedSubList()) {
						this.sum.set(this.sum.doubleValue() + e.getAmount());
					}
				}
				if (c.wasRemoved()) {
					for (Expense e : c.getRemoved()) {
						this.sum.set(this.sum.doubleValue() - e.getAmount());
					}
				}
			}
		});
	}
	
	/**
	 * Gibt, wenn vorhanden, den aktuellen Monat zurück.
	 */
	private Month getActual() {
		if (this.monthOptions.size() == 0) {
			return null;
		}
		YearMonth now = YearMonth.now();
		for (Month m : this.monthOptions) {
			if (m.getYear() == now.getYear() && m.getMonth() == now.getMonthValue()) {
				return m;
			}
		}
		return null;
	}
	
	/**
	 * Initialisiert die Auswahl der Monate.
	 */
	private void initMonth() {		
		try {
			//Alle Monate der Datenbank zu den Optionen hinzufügen und sortieren
			for (Month m: this.modelFacade.getMonths()) {
				this.monthOptions.add(m);
			}
			this.monthOptions.sort((a, b) -> {
				if (a.getDate().isBefore(b.getDate())) {
					return 1;
				}
				return -1;
			});
			
			//Den aktuellen Monat auswählen und enthaltende Ausgaben laden
			if (this.getActual() != null) {
				this.monthComboBox.getSelectionModel().select(this.getActual());
				this.handleSelectedMonth();
			}
		} catch (SQLException e) {
			ExceptionAlert.show(Messages_DE.DATABASE_EXCEPTION);
			e.printStackTrace();
		}
	}
	
	private void initYear() {
		try {
			//Alle Jahre zu den Optionen hinzufügen und absteigend sortieren
			for (Month m: this.modelFacade.getMonths()) {
				if (!this.yearOptions.contains(m.getYear())) {
					this.yearOptions.add(m.getYear());
				}
			}
			this.yearOptions.sort((a, b) -> Integer.compare(b, a));
			
			//Neustes Jahr auswählen und Inhalte laden
			this.yearComboBox.getSelectionModel().select(0);
			this.handleSelectedYear();
		} catch (SQLException e) {
			ExceptionAlert.show(Messages_DE.DATABASE_EXCEPTION);
			e.printStackTrace();
		}
	}
	
	/**
	 * Lädt die Inhalte der Spalten und formatiert diese.
	 */
	private void initTable() {
		this.days.setCellValueFactory(cdf -> {
		    return new SimpleStringProperty(cdf.getValue().getDay() + "." + cdf.getValue().getMonth().getMonth());
		});
		this.days.setStyle(ALIGNMENT_CENTER);
		this.categories.setCellValueFactory(new PropertyValueFactory<>(CATEGORY_PROPERTY));
		this.categories.setStyle(ALIGNMENT_CENTER_LEFT);
		this.amounts.setCellValueFactory(cdf -> {
		    return new SimpleStringProperty(cdf.getValue().getAmount() + " €");
		});
		this.amounts.setStyle(ALIGNMENT_CENTER);
		this.descriptions.setCellValueFactory(new PropertyValueFactory<>(INFO_PROPERTY));
		this.descriptions.setCellFactory(tc -> {
		    TableCell<Expense, String> cell = new TableCell<>();
		    Text text = new Text();
		    cell.setGraphic(text);
		    cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
		    text.wrappingWidthProperty().bind(this.descriptions.widthProperty());
		    text.textProperty().bind(cell.itemProperty());
		    return cell ;
		});
	}
	
	/**
	 * Rundet einen Betrag auf zwei Nachkommastellen.
	 */
	private double round(double value) {
	    BigDecimal bd = BigDecimal.valueOf(value);
	    bd = bd.setScale(2, RoundingMode.HALF_UP);
	    return bd.doubleValue();
	}
	
	/**
	 * Eventhandling bei der Auswahl eines Monats in der Combobox.
	 */
	@FXML
	public void handleSelectedMonth() throws SQLException {
		if (this.monthComboBox.getSelectionModel().getSelectedItem() != null) {
			Month selectedMonth = this.monthComboBox.getSelectionModel().getSelectedItem();
			try {
				if (selectedMonth != null) {
					//Ausgaben in die Tabelle laden
					this.newExpense.setVisible(true);
					this.expenses.clear();;
					this.expenses.addAll(selectedMonth.getExpenses());
				}
			}
			catch (NoSuchElementException e){
				ExceptionAlert.show(e.getMessage());
			}
			
		}
	}
	
	private void createPieChart() {
		this.pieChartData.clear();
		if (this.sum.get() != 0) {
			for (Categories c : Categories.values()) {
				//Neuen Datensatz erstellen
				PieChart.Data data = new PieChart.Data(c.toString(), 0);
				data.nameProperty().bind(Bindings.concat(data.getName(), " ", data.pieValueProperty(), " %"));
				
				double catSum = this.monthComboBox.getSelectionModel().getSelectedItem().getCategorySum(c);
				double percent = this.round(catSum / this.sum.get() * 100.0);
				
				//Datensatz nur hinzufügen, wenn Ausgaben dieser Kategorie vorhanden sind
				if (percent > 0) {
					data.setPieValue(percent);
					this.pieChartData.add(data);
				}
			}
		}
	}
	
	/**
	 * Eventhandling für das Erstellen eines neuen Monats.
	 */
	@FXML
	public void handleNewMonth() {
		try {
			//NewMonthDialgue laden
			FXMLLoader loader = new FXMLLoader(getClass().getResource(FXMLResources.NEW_MONTH_DIALOGUE_FXML));
			Parent root = loader.load();
	        
	        final Stage stage = new Stage();
	        stage.setResizable(false);
	        stage.centerOnScreen();
	        
	        NewMonthController controller = loader.getController();
	        controller.setAttributes(this.monthOptions, this);
		        	        
	        stage.setScene(new Scene(root));
	        stage.showAndWait();
		} catch (IOException e) {
			ExceptionAlert.show(Messages_DE.LOAD_EXCEPTION);
	    	e.printStackTrace();
		}
	}
	
	/**
	 * Eventhandling für das Erstellen einer neuen Ausgabe.
	 */
	@FXML
	public void handleNewExpense() {
		try {
			//NewExpenseDialogue laden
			FXMLLoader loader = new FXMLLoader(getClass().getResource(FXMLResources.NEW_EXPENSE_DIALOGUE_FXML));
			Parent root = loader.load();
	        
	        final Stage stage = new Stage();
	        stage.setTitle(Messages_DE.NEW_EXPENSE);
	        stage.setResizable(false);
	        stage.centerOnScreen();
	        
	        NewExpenseController controller = loader.getController();
	        controller.setAttributes(this.monthComboBox.getValue(), this.expenses, this);
		         	        
	        stage.setScene(new Scene(root));
	        stage.showAndWait();
		} catch (IOException e) {
			ExceptionAlert.show(Messages_DE.LOAD_EXCEPTION);
	    	e.printStackTrace();
		}
	}
	
	/**
	 * Eventhandling für die Auswahl eines Jahres in der Combobox (Statistik).
	 */
	@FXML
	public void handleSelectedYear() {
		//Balkendiagramm neu laden
		this.expensesChart.getData().clear();
		
		try {
			XYChart.Series<String, Number> series = new XYChart.Series<>();

			//Für jede Kategorie Summe berechnen und hinzufügen
	        for (Categories c : Categories.values()) {
	        	series.getData().add(new XYChart.Data<>(c.toString(), this.calculateSum(c)));
	        }
	        
	        this.expensesChart.getData().add(series);
		} catch (NoSuchElementException e) {
			ExceptionAlert.show(e.getMessage());
		} catch (SQLException e) {
			ExceptionAlert.show(Messages_DE.DATABASE_EXCEPTION);
			e.printStackTrace();
		}
	}
	
	private double calculateSum(Categories c) throws NoSuchElementException, SQLException {
		double result = 0.0;
		//Summe nur über die Monate des aktuell ausgewählten Jahres berechnen
		Integer selectedYear = this.yearComboBox.getSelectionModel().getSelectedItem();
		if (selectedYear != null) {
			for (Month m : this.modelFacade.getMonth(selectedYear.intValue())) {
				result = result + m.getCategorySum(c);
			}
		}
		return result;
	}

}