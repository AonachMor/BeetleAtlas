/*
Class to handle JDBC connections to TriboliumDB on localhost
Methods to make simple or prepared queries and return ResultSet
Based on examples in JDBC chapters in various O'Reilly books
Updated to allow connection to alternative DB 20.09.2024
*/

import java.sql.*;

public class Connect
{
	Connection conn = null;	
	String username = "charles";
	String password = "atlas";
	String TriboliumDB = "jdbc:mysql://localhost/TriboliumDB";
	String Beetle2DB = "jdbc:mysql://localhost/Beetle2DB";
	final static int ONE = 1;
	final static int TWO = 2;
	
	ResultSet resSet;
	
	public Connect()
	{	
		connect(TriboliumDB);
	}
	
	public Connect(int version)
	{
		if (version == ONE)
		{
			connect(TriboliumDB);
		}
		else if (version == TWO)
		{
			connect(Beetle2DB);
		}
		else
		{
			connect(TriboliumDB);
		}
	}	

	private void connect(String url)
	{
		try
		{
			Class.forName("org.gjt.mm.mysql.Driver");	
			conn = DriverManager.getConnection(url, username, password); 
		}
		catch(ClassNotFoundException ex)
		{
			System.out.println(ex.toString());
		}
		catch(SQLException e)
		{
			System.out.println("Trying to connect to MySQL " + e);
		}		
	}
	
	// Allows user to construct prepared query
	public Connection getConnection()
	{
		return conn;	
	}
	
	// Takes simple query string and runs simple query - not used here
	public ResultSet runSimpleQuery(String query)
	{	
		System.out.println("Query: " + query);
		try 
    	{
    		Statement stmt = conn.createStatement();
    		resSet = stmt.executeQuery(query);					
		}
		catch(SQLException e)
		{
			System.out.println("Trying to make query" + e.toString());
		}
		return resSet;
	}
	
	// Takes PreparedStatement to run query
	public ResultSet runPreparedQuery(PreparedStatement stmt)
	{
		try 
    	{
    		resSet = stmt.executeQuery();					
		}
		catch(SQLException e)
		{
			System.out.println("Trying to make query" + e.toString());
		}
		return resSet;		
	}

}