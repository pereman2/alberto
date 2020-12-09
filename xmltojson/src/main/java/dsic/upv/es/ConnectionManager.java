package dsic.upv.es;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionManager {

	static final String CONDITIONS = "?user=root?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";

	public ConnectionManager() {}
	
	public Connection getConnection(String URL, String username, String password) throws SQLException {
		DriverManager.registerDriver(new com.mysql.cj.jdbc.Driver());
		Connection connection = DriverManager.getConnection(URL + CONDITIONS, username, password);
		connection.setAutoCommit(false);
		return connection;
	}
}
