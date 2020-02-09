package dataAccess;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

class DBController {
	private static final DBController dbcontroller = new DBController();
	private static final String DRIVER_MANAGER = "org.sqlite.JDBC";
	private static final String DB_PATH = System.getProperty("user.home") + "/" + "⁨expensestool.db";
	private static Connection connection;
	
	static {
		try {
			Class.forName(DRIVER_MANAGER);
		} catch (ClassNotFoundException e) {
			System.err.println("Fehler beim Laden des JDBC-Treibers");
			e.printStackTrace();
		}
	}
	
	private DBController(){
	}
	
	public static DBController getInstance() {
		return dbcontroller;
	}
	
	public static Connection getConnection() {
		return connection; 
	}
	
	public static void initDBConnection() throws SQLException {

		if (connection != null)
			return;
		System.out.println("Creating Connection to Database...");
		connection = DriverManager.getConnection("jdbc:sqlite:" + DB_PATH);
		if (!connection.isClosed())
			System.out.println("...Connection established");
		
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				try {
					if (!connection.isClosed() && connection != null) {
						connection.close();
						if (connection.isClosed())
							System.out.println("Connection to Database closed");
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	private void deleteAll() throws SQLException {
		Statement stmt = connection.createStatement();
		stmt.executeUpdate("DROP TABLE IF EXISTS expenses;");
		stmt.executeUpdate("DROP TABLE IF EXISTS months;");
	}
	
	private void initDB() throws SQLException {
		this.deleteAll();
		
		Statement stmt = connection.createStatement();
		
		stmt.executeUpdate("CREATE TABLE expenses ("
				+ "year integer"
				+ ", month integer"
				+ ", day integer"
				+ ", category varchar"
				+ ", amount numeric"
				+ ", info varchar"
				+ ", constraint pk_Expenses primary key (year, month, day, category, amount, info)"
				+ ", constraint fk_Categories foreign key (category)"
					+ " references categories (number) on delete set null"
				+ ", constraint fk_Months foreign key (month, year)"
					+ " references months (month, year) on delete set null"
				+ ");");
		 
		stmt.executeUpdate("CREATE TABLE months ("
				+ "month integer"
				+ ", year integer"
				+ ", constraint pk_Months primary key (month, year)"
				+ ");");
		
		connection.close();
	}
	
	public static void main(String[] args) throws SQLException {
		DBController dbc = DBController.getInstance();
		initDBConnection();
		dbc.initDB();
	}
	
}
