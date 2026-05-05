
/*
	BeetleAtlas2
	22.06.2018
	Updated: 08.11.2021 for Tomcat 8 / UTF8 parameter handling
	BeetleAtlas2 update: 30.05.2025
*/

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

public class BeetleAtlas2 extends HttpServlet 
{
	private TissueCatalogue  tCat;		// stores info about all beetle tissues and stages: passed to classes that need to display results
	
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException 
	{	
		// Set Content type
		res.setContentType("text/html;charset=UTF-8");	
		res.setCharacterEncoding("UTF-8");
		res.setHeader("Cache-Control", "no-cache");
		
		// Set security headers
		res.setHeader("X-Frame-Options", "deny");
		res.setHeader("X-Content-Type-Options", "nosniff");
		res.setHeader("X-XSS-Protection", "1; mode=block");
		
		// Do stuff and respond. NB Don't get PrintWriter until ContentType has been set
		PrintWriter writer = res.getWriter();
		
		// tCat = new TissueCatalogue();		// Uncomment for testing as it allows page refresh after code change

		/* CHECK & SET SEARCH PARAMETERS */
		boolean includeErrors = false;
		if (req.getParameter("errors")!=null)		// include SDs
		{
			includeErrors = true;		// JavaScript only creates form with hard-coded value if first error box is checked
		}
		
		boolean showWhole = false;
		if (req.getParameter("whole")!=null)		// include Whole Body data
		{
			showWhole = true;			// JavaScript only creates form with hard-coded value if first "show whole" box is checked
		}
		
		/* BUILD START PAGE ON LAUNCH */
		if (req.getParameter("page") == null  && req.getParameter("search") == null)	// Defines startup 
		{		
			HomePage home = new HomePage(includeErrors, showWhole);
			writer.println(home.getHome());	
			
			tCat = new TissueCatalogue();			// Set once at startup! — comment out for testing and set above
		}
		
		/* OR BUILD START PAGES ACCESSED BY LINK FROM OTHER PAGE */
		else if (req.getParameter("page") != null)	// Page request identification to distinguish from results pages
		{		
			if (req.getParameter("page").equals("gene"))			// Gene page
			{
				GenePage genePage = new GenePage(includeErrors, showWhole);
				writer.println(genePage.getHTML());
			}
			else if (req.getParameter("page").equals("go"))			// Category or GO page (note use of "go")
			{
				CategoryPage categoryPage = new CategoryPage(tCat, includeErrors, showWhole);
				writer.println(categoryPage.getHTML());
			}
			else if (req.getParameter("page").equals("top"))		// Tissue page (note use of "top") 
			{
				TopPage topPage = new TopPage(tCat, includeErrors, showWhole);
				writer.println(topPage.getHTML());
			}
			else if (req.getParameter("page").equals("devel"))		// Development page (note use of "devel")
			{
				DevelopmentPage develPage = new DevelopmentPage(tCat, includeErrors, showWhole);
				writer.println(develPage.getHTML());
			}
			else if (req.getParameter("page").equals("embryo"))		// Embryo page
			{
				EmbryoPage embryoPage = new EmbryoPage(tCat, includeErrors, showWhole);
				writer.println(embryoPage.getHTML());
			}
			else if (req.getParameter("page").equals("profile"))		// Profile page
			{
				ProfilePage profilePage = new ProfilePage(includeErrors, showWhole);
				writer.println(profilePage.getHTML());
			}
			else if (req.getParameter("page").equals("home"))		// Home page
			{
				HomePage home = new HomePage(includeErrors, showWhole);
				writer.println(home.getHome());
			}
			else if (req.getParameter("page").equals("contact"))		// Feedback page (note use of "contact")
			{
				FeedbackPage feedback = new FeedbackPage(includeErrors, showWhole);
				writer.println(feedback.getFeedback());
			}
			else if (req.getParameter("page").equals("help"))			// Documentation page (note use of "help")  NOT YET IMPLEMENTED
			{
				HelpPage help = new HelpPage(includeErrors, showWhole);
				writer.println(help.getHelp());
			}
		}	
		
		/* OR BUILD SEARCH PAGE OF APPROPRIATE TYPE */
		else if (req.getParameter("search").equals("gene"))	
		{		
			String searchTerm = req.getParameter("gene");					// Parameter to specify value of gene id		
			// searchTerm = new String(searchTerm.getBytes("8859_1"), "UTF-8");	// Commented out for Tomcat 8 — Uncomment for 6
			searchTerm = searchTerm.trim();									//trim whitespace		
			// find type of gene identifier
			String idType = req.getParameter("idtype");						// Parameter to specify whether ncbiID, ncbiSymbol etc)
			idType = idType.replaceAll("[^a-zA-Z0-9]", "");					// Prevent cross-scripting	
			// start search
			GeneSearch search = new GeneSearch(searchTerm, idType, tCat);	
			// declare a results page
			GenePage genePage = null;			

			// construct HTML page, return it, and close the print writer
			if(idType.equals("flyFBgn") || idType.equals("flyCG") || idType.equals("flySymbol"))
			{
				Gene[] geneList = search.getGeneList(); 				
				Expression[] expressList = search.getExpressList();
				int listSize = search.getListSize();
				genePage = new GenePage(geneList, expressList, searchTerm, idType, tCat, includeErrors, showWhole, listSize);	
			}
			else if(idType.equals("product") )	// Product — redundant
			{
				Gene[] geneList = search.getGeneList(); 				
				Expression[] expressList = search.getExpressList();
				int listSize = search.getListSize();
				genePage = new GenePage(geneList, expressList, searchTerm, idType, tCat, includeErrors, showWhole, listSize);					
			}
			else
			{
				Gene gene = search.getGene(); 				
				GeneExpression expn = search.getExpression();	// retrieve results
	    		genePage = new GenePage(gene, expn, searchTerm, idType, tCat, includeErrors, showWhole);			
			}
    		writer.println(genePage.getHTML());
			writer.close();
		}
		else if (req.getParameter("search").equals("bulk"))
		{
			String searchList = req.getParameter("geneList");
			// searchList = new String(searchList.getBytes("8859_1"), "UTF-8");	// Tomcat 8 — Uncomment for Tomcat 6 ?
			searchList = searchList.replaceAll("[^,a-zA-Z0-9\r\n]", "");		// Prevent cross-scripting		
			searchList = searchList.trim();									// trim still needed for e.g. first-line return	

			BulkSearch search = new BulkSearch(searchList, tCat);
			
			Expression[] expressList = search.getExpressList();
			Gene[] geneList = search.getGeneList();
			String idType = "ncbiID";
			String searchTerm = search.getQueryID();
			int numValidIDs = search.getNumValidIDs();
			
			GenePage genePage = new GenePage(geneList, expressList, searchTerm, idType, tCat, includeErrors, showWhole, numValidIDs);
			writer.println(genePage.getHTML());
			writer.close();			
		}
		else if(req.getParameter("search").equals("go"))
		{					
			String stage = req.getParameter("stage");			
			int tissueID = Integer.parseInt(req.getParameter("tissue"));			
			String order = req.getParameter("order");
			boolean byAbundance = true;	
			if (order.equals("enrichment"))
			{
				byAbundance=false;
			}
			String keyword = req.getParameter("keyword");
			int displayMax = Integer.parseInt(req.getParameter("maxdisplayed"));
			
			CategorySearch catSearch = new CategorySearch(stage, tissueID, order, byAbundance, keyword, displayMax, tCat);
			
			Expression[] expressList = catSearch.getExpressList();
			Gene[] geneList = catSearch.getGeneList();
			int actualDisplayed = catSearch.getActualDisplayed();
			int foundNum = catSearch.getIDListSize();
			
			CategoryPage catPage = new CategoryPage(expressList, geneList, stage, tissueID, keyword, byAbundance,
					foundNum, displayMax, actualDisplayed, includeErrors, showWhole, tCat);
    		writer.println(catPage.getHTML());
			writer.close();
		}		
		else if (req.getParameter("search").equals("top"))
		{
			String stage = req.getParameter("stage");			
			int tissueID = Integer.parseInt(req.getParameter("tissue"));			
			String order = req.getParameter("order");
			boolean byAbundance = true;	
			if (order.equals("enrichment"))
			{
				byAbundance=false;
			}
			int displayMax = Integer.parseInt(req.getParameter("maxdisplayed"));
			
			TopSearch search = new TopSearch(tissueID, byAbundance, displayMax, tCat);
			Expression[] expressList = search.getExpressList();
			Gene[] geneList = search.getGeneList();
			int actualDisplayed = search.getActualDisplayed();
			
			TopPage topPage = new TopPage (expressList, geneList, stage, tissueID, byAbundance, 
					displayMax, actualDisplayed, tCat, includeErrors, showWhole);
    		writer.println(topPage.getHTML());
			writer.close();
		}
		else if (req.getParameter("search").equals("devel"))
		{		
			String uniTissue = req.getParameter("uniTissue");
			String radioDev = req.getParameter("radioDev");
			boolean adultGreater = true;
			if (radioDev.equals("devLarval"))
			{
				adultGreater = false;
			}

			int displayMax = Integer.parseInt(req.getParameter("maxdisplayed"));

			DevelopmentSearch search = new DevelopmentSearch(uniTissue, adultGreater, displayMax, tCat); 
			Expression[] expressList = search.getExpressList();
			Gene[] geneList = search.getGeneList();
			int actualDisplayed = search.getActualDisplayed();		

			DevelopmentPage develPage = new DevelopmentPage (expressList, geneList, uniTissue, adultGreater, 
					displayMax, actualDisplayed, tCat, includeErrors, showWhole);
    		writer.println(develPage.getHTML());
			writer.close();
		}
		else if (req.getParameter("search").equals("embryo"))
		{			
			String stage = req.getParameter("radioEmbryo");
			int displayMax = Integer.parseInt(req.getParameter("maxdisplayed"));
			String embryoOnly = req.getParameter("embryoOnly");
			
			boolean exclusive = false;
			if(embryoOnly.equals("yes"))
			{
				exclusive = true;
			}

			EmbryoSearch search = new EmbryoSearch(stage, displayMax, exclusive, tCat);
			Expression[] expressList = search.getExpressList();
			Gene[] geneList = search.getGeneList();				
			int totalGenes = search.getGeneListSize();

			EmbryoPage embryoPage = new EmbryoPage (expressList, geneList, stage, exclusive, displayMax, totalGenes, tCat, includeErrors, showWhole);
    		writer.println(embryoPage.getHTML());
			writer.close();
		}
		else if (req.getParameter("search").equals("profile"))
		{
			// get parameters
			String geneQuery = req.getParameter("gene");
			// geneQuery = new String(geneQuery.getBytes("8859_1"), "UTF-8");	// Commented out for Tomcat 8 — Uncomment for Tomcat 6
			geneQuery = geneQuery.trim();		
			String idType = req.getParameter("idtype");
			
			boolean byPearson = true;
			if(req.getParameter("correlation").equals("spearman"))
			{
				byPearson = false;
			}
			String rString = req.getParameter("rcut");
			double rCut = Double.parseDouble(rString);
			int displayMax = Integer.parseInt(req.getParameter("maxdisplayed"));
			
			boolean aleProfile = false; 	// Include embryo data in profile search
			
			// get GeneExpression object and ncbiID corresponding to geneQuery
			GeneSearch geneSearch = new GeneSearch(geneQuery, idType, tCat);
			GeneExpression expression = geneSearch.getExpression();
			Gene searchGene = geneSearch.getGene();
			
			String ncbiID = new String();
			if(expression != null)
			{
				ncbiID = geneSearch.getGene().getNCBIid(); 
				if(geneSearch.getGene().isDiscontinued())	// abort
				{
					ProfilePage profilePage = new ProfilePage (geneQuery, idType, ncbiID, null, null, null, null, null, displayMax, 
							tCat, byPearson, aleProfile, rString, includeErrors, showWhole);
		    		writer.println(profilePage.getHTML());
					return;
				}
			}
			else		// abort
			{
				ProfilePage profilePage = new ProfilePage (geneQuery, idType, ncbiID, null, null, null, null, null, displayMax, 
						tCat, byPearson, aleProfile, rString, includeErrors, showWhole);
	    		writer.println(profilePage.getHTML());
				return;
			}
			
			ProfileSearch search = new ProfileSearch(ncbiID, expression, aleProfile, byPearson, rCut, displayMax, tCat);
			Expression[] expressList = search.getExpressList();
			Gene[] geneList = search.getGeneList();	
			ProfileTissueData[] profileDataList = search.getDataList();
			
			ProfilePage profilePage = new ProfilePage (geneQuery, idType, ncbiID, searchGene, expression, expressList, geneList, profileDataList, displayMax, 
					tCat, byPearson, aleProfile, rString, includeErrors, showWhole);
			
    		writer.println(profilePage.getHTML());
			writer.close();			
		}		
	}
 
}
