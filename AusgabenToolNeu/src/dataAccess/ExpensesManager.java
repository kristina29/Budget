package dataAccess;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.Expense;
import model.Expense.Categories;
import model.Month;

public class ExpensesManager {
private final static ExpensesManager EM = new ExpensesManager();
	
	private ExpensesManager() {
	}
	
	public static ExpensesManager getInstance() {
		return EM;
	}
	
	/**
	 * Neue Ausgabe speichern.
	 */
	public void insertExpense(Expense e) throws SQLException {
		String sql = "INSERT INTO expenses VALUES(?, ?, ?, ?, ?, ?);";
		
		DBController.initDBConnection();
		PreparedStatement pstmt = DBController.getConnection().prepareStatement(sql);
		
		pstmt.setInt(1, e.getMonth().getYear());
		pstmt.setInt(2, e.getMonth().getMonth());
		pstmt.setInt(3, e.getDay());
		pstmt.setString(4, e.getCategory().toString());
		pstmt.setDouble(5, e.getAmount());
		pstmt.setString(6, e.getInfo());
		pstmt.addBatch();
		
		pstmt.executeBatch();
	}
	
	/**
	 * Gibt die Ausgaben eines Monats zurück.
	 */
	public List<Expense> getExpenses(Month m) throws SQLException {
		String sql = "SELECT DISTINCT * FROM expenses "
				+ "WHERE year = ? AND month = ? ORDER BY day, month, year;";
		List<Expense> result = new ArrayList<Expense>();
		ResultSet rs = null;
		
		DBController.initDBConnection();
		PreparedStatement pstmt = DBController.getConnection().prepareStatement(sql);
		pstmt.setInt(1, m.getYear());
		pstmt.setInt(2, m.getMonth());
		pstmt.addBatch();
		
		rs = pstmt.executeQuery();
	
		while (rs.next()) {
			Expense e = new Expense(m, rs.getInt(3), Categories.valueOf(rs.getString(4)), rs.getDouble(5), rs.getString(6));
			result.add(e);
		}
		rs.close();
			
		return result;
	}
	
	/**
	 * Löscht eine Ausgabe.
	 */
	public void deleteExpense(final int day, final int month, final int year, final Categories category, 
    		final double amount, final String info) throws SQLException {
		String sql = "DELETE FROM expenses "
				+ "WHERE year = ? AND month = ? AND day = ? AND category = ? AND amount = ? AND info = ?;";
			
		DBController.initDBConnection();
		PreparedStatement pstmt = DBController.getConnection().prepareStatement(sql);
		pstmt.setInt(1, year);
		pstmt.setInt(2, month);
		pstmt.setInt(3, day);
		pstmt.setString(4, category.toString());
		pstmt.setDouble(5, amount);
		pstmt.setString(6, info);
		pstmt.addBatch();
		
		pstmt.executeBatch();
	}
}
