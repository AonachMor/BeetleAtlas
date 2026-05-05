/*
Servlet that takes a beetle "stage" as parameter, 
makes a query to a MySQL database and returns list of appropriate tissues as text/xml
DPL 23.06.2018
Revised to handle BeetleAtlas2 20.09.2024
Revised to avoid repetition of tissues with experimental conditions  03.01.2025
 */	
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.*;

public class BeetleMenu extends HttpServlet
{	        	
	StringBuffer tissueBuffer;	// Buffer for accumulating output
	int vers = 0;
	
	public void doGet(HttpServletRequest req, HttpServletResponse res)
		throws ServletException, IOException 
	{		
		String output = "";
		String stage =  (String) req.getParameter("stg");
		String version = (String) req.getParameter("vers");	// one or two for BeetleAtlas1 or BeetleAtlas2
			
		if(version == null)
		{
			vers = 1;	// default for backward compaat with BeetleAtlas1
		}
		else if (version.equals("one"))
		{
			vers = 1;
			//System.out.println("vers: " + vers);
		}		
		else if (version.equals("two"))
		{
			vers = 2;	
			//System.out.println("vers: " + vers);
		}
		else
		{
			vers = 1;	
			//System.out.println("else vers: " + vers);
		}

		if(stage != null)
		{
			// make query
			output = getTissueList(stage);
			res.setContentType("text/xml");
			res.setHeader("Cache-Control", "no-cache");
			// write out the response string
			res.getWriter().write(output);
		}
		else 
		{
			// Write error message - useful only for development
			res.setContentType("text/xml");
			res.setHeader("Cache-Control", "no-cache");
			res.getWriter().write("Error: " + stage);
		}
	}

	// makes connection and requests tissues for a given stage	
	public String getTissueList(String stg)
	{
		Connect cnt = new Connect(vers);
		Connection conn = cnt.getConnection();
		ParamQuery parQ = DBQuery.getParamQuery("STG_TISS");

		try 
		{
			parQ.setPrepStatement(conn);
		} 
		catch (SQLException e) 
		{System.out.println(e.toString());}

		tissueBuffer = new StringBuffer("");

		try 
		{
			PreparedStatement prepStat = parQ.getPrepStatement();
			prepStat.setString(1, stg);
			ResultSet resSet = prepStat.executeQuery();
			formatOutput(resSet);
		}
		catch (SQLException e) 
		{}

		return tissueBuffer.toString();
	}

	// lays out results in xml structure
	public void formatOutput(ResultSet resSet) throws SQLException 
	{
		if(resSet.first())			// only write if tissues - should be redundant
		{
			resSet.beforeFirst();	// hack to reset cursor as 'if' moves it on a row!
			
			// xml pragma etc
			tissueBuffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
			tissueBuffer.append("<tissues>");

			while (resSet.next())	// moves to next row while rows remain
			{
				String foundID = resSet.getString(1);
				String foundTiss = resSet.getString(2);
				
				tissueBuffer.append("<tiss>");
				tissueBuffer.append("<tissid>" + foundID + "</tissid>");
				tissueBuffer.append("<tissdescrip>" + foundTiss + "</tissdescrip>");
				tissueBuffer.append("</tiss>");
			}
			
			tissueBuffer.append("</tissues>");
		} 
		else 
		{
			tissueBuffer.append("");
		}
	}

}
