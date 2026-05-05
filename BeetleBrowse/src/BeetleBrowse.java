/*
	BeetleBrowse
	Utility Servlet to take Tribolium chromosome locus and ID and prepare a link page to UCSC browser reads
	DPL 13.09.2024
	Updated to handle either Tcas 5.2 (original BeetleAtlas) or TriCast 1.1 (BeetleAtlas2)
	Update 07.05.2025 to make TriCast 1.1 default as Locus field in TriboliumDB Gene table is now for liftover to TriCast 1.1
	Update 08.06.2025
*/	
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

public class BeetleBrowse extends HttpServlet
{		
	
	public void doGet(HttpServletRequest req, HttpServletResponse res)
                               throws ServletException, IOException 
	{	
		String tracksFilename = "NCBITracks.txt";	// May 2025 default
		String version = "two";						// May 2025 default
		String refGeneome = "GCF_031307605.1";		// May 2025 default
		
		// get parameters	
		String locus = req.getParameter("locus");	// Gene Locus
		String id = req.getParameter("id");			// Gene identifier
		version = req.getParameter("version");		// Gene identifier

		// Handle version-dependent variables
		if(version == null)
		{
			version = "two";	
		}
		
		if(version.equals("one"))					// retain in case needed
		{
			tracksFilename = "OGS3Tracks.txt";
			refGeneome = "GCF_000002335.3";
		}
		else if (version.equals("two"))
		{
			tracksFilename = "NCBITracks.txt";
			refGeneome = "GCF_031307605.1";
		}
		else	// for development only
		{
			System.out.println("unsupported version number");
		}
		
		// Make HTTP response
		res.setContentType("text/html");
		res.setCharacterEncoding("UTF-8");
		PrintWriter out = res.getWriter();
		
		// head
		out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01//EN\" \"http://www.w3.org/TR/html4/strict.dtd\">");
		out.println("<html>\n<head>\n");
		out.println("<title>View Reads in UCSC Browser</title>\n<meta http-equiv=\"content-type\" content=\"text/html; charset=utf-8\">");
		out.println("<script type=\"text/javascript\" src=\"scripts/beetlebrowse.js\"></script>");
		out.println("<style type = \"text/css\">@import url(\"scripts/beetlebrowse.css\");</style>");
		out.println("<link rel=\"icon\" href=\"images/beetle.ico\" type=\"image/x-icon\">");	
		out.println("</head>\n");
		
		//start of body and first line with button
		out.println("<body>\n<div id=\"heading\">\n<h1>View icTriCast1.1 assembly on UCSC Browser</h1>\n</div>\n");
		out.println("<div id=\"main\">\n");
		
		if(!locus.equals(""))
		{
			out.println("<p class=\"buttonText\">\n");				
			out.println("Launch UCSC Browser showing RNAseq reads in region of " + id + ": <button onclick=\"openLinkWindow('http://genome.ucsc.edu/cgi-bin/hgTracks?db="
					+ refGeneome
					+ "&position="
					+ expandLocus(locus)
					+ "&hgct_customText=http://motif.mvls.gla.ac.uk/tribolium/"
					+ tracksFilename
					+ "&augustus=hide&cpgIslands=hide&gc5Base=hide&repeatMasker=hide&simpleRepeat=hide&refSeqComposite=hide&windowMasker=hide&xenoRefGene=hide"
					+ "');\">Go</button>");	
			out.println("</p>");
			
			// hints
			out.println("<p class=\"title\">SOME HINTS FOR VIEWING TRACKS</p>\n");
			out.println("<ul><li style=\"text-align:left;\">You generally need to zoom out 3X or 10X to inspect a gene in the context of its neighbours: <img width=\"162\" height=\"17\" src=\"images/zoom.png\" alt=\"\"</li>");
			out.println("<li>All tracks are the same height. To get an impression of quantitative differences between tissues you need to examine the scale at the left (a). Sometimes adjacent genes with high expression need to be scrolled or zoomed out of view.</li>");
			out.println("<li>Often one is interested in a few of the many tissues presented. One may bring these together by dragging their left bars up or down (b) or click on the bar and hide those that are not of immediate interest (c).");
			out.println("Alternatively use the customization panel at the bottom of the page, which also allows other reference tracks to be added.</li></ul>\n");
	
			// graphics
			out.println("<div class=\"graphics\">\n");
			out.println("<img src=\"images/ucsc1.png\" alt=\"\" width=\"193\" height=\"160\"><img src=\"images/ucsc2.png\" alt=\"\" width=\"193\" height=\"160\"><img src=\"images/ucsc3.png\" alt=\"\" width=\"193\" height=\"160\">\n");
		}
		else		// TC id that did not liftover
		{
			out.println("<p class=\"buttonText\">NO LOCUS ON THE icTriCast1.1 ASSEMBLY FOR THE OSG3-PREDICTED GENE " + id + "</p>\n");
			out.println("<p>Further information about this predicted gene may be available on the <a href=\"javascript:linkToiBeetle('" + id + "');\">iBeetle-Base website</a>.</p>");
		}
		out.println("</div>\n");
		
		// end of body
		out.println("</div>\n</body>\n</html>");	
	}
	
	// utility method for expanding the range so the gene lies in the middle 50%
	private String expandLocus(String locus)
	{
		String prefix = locus.substring(0, locus.indexOf(":") + 1);
		String start = locus.substring(locus.indexOf(":") + 1, locus.indexOf("-"));
		int oldStart = Integer.parseInt(start);
		String end = locus.substring(locus.indexOf("-") + 1, locus.length());
		int oldEnd = Integer.parseInt(end);
		
		int range = oldEnd - oldStart;
		int newStart = oldStart - range/2;
		int newEnd = oldEnd + range/2;
		
		locus = prefix + newStart + "-" + newEnd;
		return locus;
	}
	
}

