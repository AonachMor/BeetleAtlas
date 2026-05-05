/*
Servlet that takes a text string, makes a query to a MySQL database
and returns terms starting or containing this string as text/xml
Modified from original FlyAtlas version
DPL 24.05.2016
Last Update: 22.03.2026
 */	

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.*;

public class BeetleAuto extends HttpServlet
{
	// Number flags as variables to aid comprehension
	private final int GENE_SYMBOL = 1;
	private final int GENE_ID = 2;
	private final int GENE_NAME = 3;
	private final int FLY_FBGN = 4;
	private final int FLY_CGNUM = 5;
	private final int FLY_SYMBOL = 6;
	private final int NCBI_ID = 7;

	private int mode = GENE_SYMBOL;		// flag for query

	public void doGet(HttpServletRequest req, HttpServletResponse res)
					throws ServletException, IOException 
	{	
		String partWord = "";	// This is the user entry initialize to avoid poss npe
		int tooSmall = 10;	// partWord must be longer than this
		
		String textEntered = req.getParameter("gene");				// text typed into field for gene request
		String searchType = req.getParameter("searchType");		// radio search type
		
		if(textEntered != null && !textEntered.equals(""))
		{
			partWord = textEntered.trim();
			if(searchType.equals("symbol"))
			{
				mode = GENE_SYMBOL;
				tooSmall = 1;
			}
			else if(searchType.equals("id"))
			{
				mode = GENE_ID;
				tooSmall = 5;
			}
			else if(searchType.equals("name"))
			{
				mode = GENE_NAME;
				tooSmall = 2;
			}
			else if(searchType.equals("flyFBgn"))
			{
				mode = FLY_FBGN;
				tooSmall = 8;
			}
			else if(searchType.equals("flyCG"))
			{
				mode = FLY_CGNUM;
				tooSmall = 4;
			}
			else if(searchType.equals("flySymbol"))
			{
				mode = FLY_SYMBOL;
				tooSmall = 1;
			}
			else if(searchType.equals("ncbiID"))
			{
				mode = NCBI_ID;
				tooSmall = 4;
			}
			
		}
		
		if(partWord.length() > tooSmall)		// don't look at too short strings
		{	
		    res.setContentType("text/xml;charset=UTF-8");
			res.setHeader("Cache-Control", "no-cache");
			// Don't get PrintWriter until ContentType has been set
			PrintWriter writer = res.getWriter();
			writer.println("<response>");
			
			// String utf8Prefix = new String(partWord.getBytes("8859_1"), "UTF-8");	// Commented out for Tomcat 8 — Uncomment for Tomcat 6
			// int foundNum = doQuery(utf8Prefix, writer);
			
			int foundNum = doQuery(partWord, writer);
			
			if(foundNum ==0)
			{
				res.setStatus(HttpServletResponse.SC_NO_CONTENT); 
				return;
			}   
			         
			writer.println("</response>");
			writer.close();
		}
		else 
		{
			res.setStatus(HttpServletResponse.SC_NO_CONTENT);
		}
	}

	// makes connection and requests gene symbols starting with partWord
	public int doQuery(String partWord, PrintWriter writer)
	{
		int foundNum = 0;				// No of results returned
		Connect cnt = new Connect();
		Connection conn = cnt.getConnection();
		
		ParamQuery parQ = null;

		if(mode == GENE_SYMBOL)
		{
			parQ= DBQuery.getParamQuery("SYMBOL"); 	
		}
		else if(mode == GENE_ID)
		{
		 	parQ= DBQuery.getParamQuery("ID");		
		}
		else if(mode == GENE_NAME)
		{
		 	parQ= DBQuery.getParamQuery("PRODUCT");		
		}
		else if(mode == FLY_FBGN)
		{
		 	parQ= DBQuery.getParamQuery("FLY_FB");		
		}
		else if(mode == FLY_CGNUM)
		{
		 	parQ= DBQuery.getParamQuery("FLY_CG");		
		}
		else if(mode == FLY_SYMBOL)
		{
		 	parQ= DBQuery.getParamQuery("FLY_SYMBOL");		
		}
		else if(mode ==  NCBI_ID)
		{
		 	parQ= DBQuery.getParamQuery("NCBI_ID");		
		}

		try 
		{
			parQ.setPrepStatement(conn);
		} 
		catch (SQLException e) 
		{System.out.println(e.toString());}


		try 
		{
			PreparedStatement prepStat = parQ.getPrepStatement();
			if(mode == GENE_NAME)	// expand at start and end of partWord
			{
				prepStat.setString(1, '%' + partWord + '%');
			}
			else	// expand at end of partWord
			{
				//prepStat.setString(1, "" + partWord + '%');	// why was this in original?
				prepStat.setString(1, partWord + '%');
			}
			ResultSet resSet = prepStat.executeQuery();
			
			while (resSet.next())
			{
				String foundSymbol = resSet.getString(1);
				foundNum++;
				writer.println("<name>" + foundSymbol + "</name>");
			}				
		}
		catch (SQLException e) 
		{System.out.println(e.toString());}
		
		return foundNum;
	}
}
