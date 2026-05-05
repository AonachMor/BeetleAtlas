/*
	BeetlePara2
	Utility Servlet to list paralogues or orthologues to a gene and allow BeetleAtlas2 or FlyAtlas2 Query
	DPL 10.12.2023
	Use 664529 to test for paras, 664581 gives 4 orthos
	Modified for BeetleAtlas2 22.09.2024
*/	

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BeetlePara2 extends HttpServlet
{		
	
	public void doGet(HttpServletRequest req, HttpServletResponse res)
                               throws ServletException, IOException 
	{	
		// get parameters	
		String id = req.getParameter("id");					// GeneID
		String type = req.getParameter("type");				// ortho or para

		if(type.equals("para"))
		{
			String paraList = getPara(id);
				// convert to array of ids
			String[] paras = paraList.split(",");
			
			String url = "/BeetleAtlas2/?search=bulk&geneList=" + id + "," + buildString(paras);
	
			// To prevent cross-site scripting, accept only letters or numbers, - and :
			id = id.replaceAll("[^-:a-zA-Z0-9]", "");
			paraList = paraList.replaceAll("[^,a-zA-Z0-9]", "");
			
			// Make HTTP response
			res.setContentType("text/html");
			res.setCharacterEncoding("UTF-8");
			PrintWriter out = res.getWriter();
			
			// head
			out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01//EN\" \"http://www.w3.org/TR/html4/strict.dtd\">");
			out.println("<html>\n<head>\n");
			out.println("<title>List of Beetle Gene Paralogues</title>\n<meta http-equiv=\"content-type\" content=\"text/html; charset=utf-8\">");
			out.println("<script type=\"text/javascript\" src=\"scripts/para.js\"></script>");
			out.println("<style type = \"text/css\">@import url(\"scripts/para.css\");</style>");
			out.println("<link rel=\"icon\" href=\"images/beetle.ico\" type=\"image/x-icon\">");	
			out.println("</head>\n");
			
			//start of body and first line with button
			out.println("<body>\n<div id=\"heading\">\n<h1>Paralogues of " + id + "</h1>\n</div>\n");
			out.println("<div id=\"main\">\n");
		
			// comments
			out.println("<p class=\"title\">INFORMATION ABOUT THE PARALOGUES</p>\n");
			out.println("<ul><li>The paralogues encode proteins with statistically significant similarity to that encoded by the query gene. This may encompass the length of both proteins or be restricted to a small part of them.</li>");
			out.println("<li>Pressing the ‘Go’ button, below, will open a new BeetleAtlas2 window showing the tissue distribution of transcripts of each paralogue.<br>");
			out.println("<li>We do not provide facilities in BeetleAtlas2 for comparing the sequences of paralogues. If you wish to do this we suggest that you copy the list below, obtain the protein sequences from NCBI, and compare them using publicly available alignment tools.</li></ul>\n");
			out.println("<p style=\"text-align:center;\"><button onclick=\"openLinkWindow('" + url + "');\">Go</button></p>");
			// list
			out.println("<p class=\"title\">QUERY &amp; PARALOGUES</p>\n");
					
			out.println("<div class=\"list\">\n");
			
			out.println(id + "<br><br>\n");
			for(int i=0; i< paras.length; i++)
			{
				out.println(paras[i] + "<br>\n");			
			}
			out.println("</div>\n");
			
			// end of body
			out.println("</div>\n</body>\n</html>");	
		}
		else if(type.equals("ortho"))
		{
			String orthoList = getOrtho(id);
			// convert to array of ids
			String[] orthos = orthoList.split(",");	// need this for display
			
			// construct string with eols for bulk FlyAtlas 2 query
			StringBuffer sb = new StringBuffer();
			for(int i=0;i<orthos.length;i++)
			{
				sb.append(orthos[i]);
				sb.append("%0D%0A");
			}			
			String orthosToFly = sb.toString();
		
			String url = "https://motif.mvls.gla.ac.uk/FlyAtlas2/?search=bulk&geneList=" + orthosToFly;
			// Make HTTP response
			res.setContentType("text/html");
			res.setCharacterEncoding("UTF-8");
			PrintWriter out = res.getWriter();
			
			// head
			out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01//EN\" \"http://www.w3.org/TR/html4/strict.dtd\">");
			out.println("<html>\n<head>\n");
			out.println("<title>List of Beetle Gene Fly Homologues</title>\n<meta http-equiv=\"content-type\" content=\"text/html; charset=utf-8\">");
			out.println("<script type=\"text/javascript\" src=\"scripts/para.js\"></script>");
			out.println("<style type = \"text/css\">@import url(\"scripts/para.css\");</style>");
			out.println("<link rel=\"icon\" href=\"images/beetle.ico\" type=\"image/x-icon\">");	
			out.println("</head>\n");
			
			//start of body and first line with button
			out.println("<body>\n<div id=\"heading\">\n<h1>Fly Homologues of " + id + "</h1>\n</div>\n");
			out.println("<div id=\"main\">\n");
		
			// comments
			out.println("<p class=\"title\">INFORMATION ABOUT THE HOMOLOGUES</p>\n");
			out.println("<ul><li>The <em>Drosophila</em> homologues are thought to be evolutionarily related to the <em>Tribolium</em> query gene. (The list should not be considered exhaustive — more distantly related genes may also exist.)</li>");
			out.println("<li>Pressing the ‘Go’ button, below, will run them on FlyAtlas 2 and open a new browser window showing the tissue distribution of their transcripts.<br>");
			out.println("<li>We do not provide facilities for comparing the fly and beetle sequences. If you wish to do this we suggest that you copy the list below, obtain the protein sequences from NCBI and FlyBase, and compare them using publicly available alignment tools.</li></ul>\n");
			out.println("<p style=\"text-align:center;\"><button onclick=\"openLinkWindow('" + url + "');\">Go</button></p>");

			// list
			out.println("<p class=\"title\">QUERY &amp; HOMOLOGUES</p>\n");
					
			out.println("<div class=\"list\">\n");
			
			out.println(id + "<br><br>\n");
			for(int i=0; i< orthos.length; i++)
			{
				out.println(orthos[i] + "<br>\n");			
			}
			out.println("</div>\n");
			
			// end of body
			out.println("</div>\n</body>\n</html>");	
		}
		
	}	
	
	// Make query for Beetle Paralogues and construct String
	private String getPara(String geneID)
	{
		String [] paraList;				// array to hold FBgns retrieved from query
		paraList = new String [100];	// OK as Max No. Paralogues is 80
		int paraListSize = 0;
		
		// Make connection and query
		Connect cnt = new Connect();
		Connection conn = cnt.getConnection();
		ParamQuery parQ = DBQuery.getParamQuery("PARAS_FROM_NCBI_ID");
		try 
		{
			parQ.setPrepStatement(conn);
			PreparedStatement prepStat = parQ.getPrepStatement();
			prepStat.setString(1, geneID);
			
			ResultSet resSet = prepStat.executeQuery();
			resSet.beforeFirst();		// hack to reset cursor as 'if' moves it on a row!
			while (resSet.next())		// moves to next row while rows remain
			{	
				String id = resSet.getString("ParaID");
				paraList[paraListSize] = id;
				paraListSize++;
			}
		}
		catch (SQLException e)
		{
			System.out.println("SQL Exception: " + e.toString());
		}
		
		StringBuilder sb  = new StringBuilder();
		if(paraListSize>0)
		{
			for(int i=0;i<paraListSize;i++)
			{
				sb.append(paraList[i]);
				sb.append(",");
			}
		}
		return sb.toString();
	}	
	
	// Make query for Fly Orthologues
	private String getOrtho(String geneID)
	{
		String [] orthoList;			// array to hold FBgns retrieved from query
		orthoList = new String [100];	// OK as max No. ortholgues is 61
		int orthoListSize = 0;
		
		// Make connection and query
		Connect cnt = new Connect();
		Connection conn = cnt.getConnection();
		ParamQuery parQ = DBQuery.getParamQuery("FLY_ORTHOLOGUES_FROM_NCBI_ID");
		try 
		{
			parQ.setPrepStatement(conn);
			PreparedStatement prepStat = parQ.getPrepStatement();
			prepStat.setString(1, geneID);
			
			ResultSet resSet = prepStat.executeQuery();
			resSet.beforeFirst();		// hack to reset cursor as 'if' moves it on a row!
			while (resSet.next())		// moves to next row while rows remain
			{	
				String fbgn = resSet.getString("FBgn");
				orthoList[orthoListSize] = fbgn;
				orthoListSize++;
			}
		}
		catch (SQLException e)
		{
			System.out.println("SQL Exception: " + e.toString());
		}
		
		StringBuilder sb  = new StringBuilder();
		if(orthoListSize>0)
		{
			for(int i=0;i<orthoListSize;i++)
			{
				sb.append(orthoList[i]);
				sb.append(",");
			}
		}
		return sb.toString();
	}
	
	public String buildString(String[] inputArray)
	{
		StringBuilder sb = new StringBuilder("");
		for(int i=0; i<inputArray.length; i++)
		{
			sb.append(inputArray[i] + ",");
		}
		return sb.toString();
	}

}

