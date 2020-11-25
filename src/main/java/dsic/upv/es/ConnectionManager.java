package dsic.upv.es;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;



public class ConnectionManager {
	public static void main(String [] args) {
		Connection connection;
		try
		{
			DriverManager.registerDriver(new com.mysql.cj.jdbc.Driver());
		   connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/publications?user=root?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC", "root", "passwordroot");
		   connection.setAutoCommit(false);
		   System.out.println(connection.getCatalog());
		   Statement statement = connection.createStatement();
		   
		   statement.executeUpdate("INSERT INTO persona (nombre,apellidos) VALUES ('Manolo','el navajas')");
		   connection.commit();
		   connection.close();
		   
		   
		}catch(Exception e) {
			System.out.println(e.toString());
		}
		
	}
	
	private getBibtexJson() {
		
	}
}
