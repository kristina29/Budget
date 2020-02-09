package dataAccess;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import model.Expense;
import model.Month;
import model.Expense.Categories;

public class MonthManager {
	private final static MonthManager mm = new MonthManager();
	
	private MonthManager() {
	}
	
	public static MonthManager getInstance() {
		return mm;
	}
	
	/**
	 * Neuen Monat speichern.
	 */
	public void insertMonth(Month m) throws SQLException {				
		String sql = "INSERT INTO months VALUES(?, ?);";
		
		DBController.initDBConnection();
		PreparedStatement pstmt = DBController.getConnection().prepareStatement(sql);
		
		pstmt.setInt(1, m.getMonth());
		pstmt.setInt(2, m.getYear());
		pstmt.addBatch();
		pstmt.executeBatch();
	}
	
	/**
	 * Gibt die Monate innerhalb eines Jahres zurück.
	 */
	public List<Month> selectMonthsInYear(int year) throws SQLException,NoSuchElementException {
		DBController.initDBConnection();
		List<Month> result = new ArrayList<Month>();
		ResultSet rs = null;
		ResultSet expenses = null;
		PreparedStatement pstmt = DBController.getConnection().prepareStatement("SELECT DISTINCT * FROM expenses " +
				"WHERE month = ? AND year = ? ORDER BY day;");
		PreparedStatement pstmt2 = DBController.getConnection().prepareStatement("SELECT * FROM months WHERE year = ?;");
		pstmt2.setInt(1, year);
		rs = pstmt2.executeQuery();
		
		while (rs.next()) {
			Month m = new Month(rs.getInt(1), rs.getInt(2));
			result.add(m);
			pstmt.setInt(1, m.getMonth());
			pstmt.setInt(2, m.getYear());
			pstmt.addBatch();
			
			expenses = pstmt.executeQuery();				
			while (expenses.next()) {
				new Expense(m, expenses.getInt(3), Categories.valueOf(expenses.getString(4)), expenses.getDouble(5), 
						expenses.getString(6));
			}
			pstmt.clearBatch();
		}
		return result;
	}

	/**
	 * Gibt alle Monate zurück.
	 */
	public List<Month> selectAllMonths() throws SQLException {
		DBController.initDBConnection();
		List<Month> result = new ArrayList<Month>();
		ResultSet rs = null;
		ResultSet expenses = null;
		PreparedStatement pstmt = DBController.getConnection().prepareStatement("SELECT DISTINCT * FROM expenses " +
				"WHERE month = ? AND year = ? ORDER BY day;");
		Statement stmt = DBController.getConnection().createStatement();
		rs = stmt.executeQuery("SELECT DISTINCT * FROM months ORDER BY year, month DESC;");
		
		while (rs.next()) {
			Month m = new Month(rs.getInt(1), rs.getInt(2));
			result.add(m);
			pstmt.setInt(1, m.getMonth());
			pstmt.setInt(2, m.getYear());
			pstmt.addBatch();
			
			expenses = pstmt.executeQuery();				
			while (expenses.next()) {
				new Expense(m, expenses.getInt(3), Categories.valueOf(expenses.getString(4)), expenses.getDouble(5), 
						expenses.getString(6));
			}
			pstmt.clearBatch();
		}
		return result;
	}
	
	/**
	 * Löscht einen Monat.
	 */
	public void deleteMonth(int month, int year) throws SQLException {
		String sql = "DELETE FROM months "
				+ "WHERE month = ? AND year = ?;";
		DBController.initDBConnection();
		PreparedStatement pstmt = DBController.getConnection().prepareStatement(sql);
		pstmt.setInt(1, month);
		pstmt.setInt(2, year);
		pstmt.addBatch();
		
		pstmt.executeBatch();
	}
}

