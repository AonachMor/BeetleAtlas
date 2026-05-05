import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

// Generates an HTML page for a Category search, with or without results
// David P. Leader 14.06.2018
// Last update 22.03.2026

public class CategoryPage 
{
	private TissueCatalogue tCat;
	private boolean byAbundance = true;					// enrichment or abundance criterion
	private final int PAGE_POS = PageUtility.CATEGORY;	// Generally position of page in menu
	private StringBuilder htmlBuilder;					// For building HTML
	private String intro = "Find genes of a particular category most expressed in individual tissues.<br />";

	private String[] keywordList;
	private int KEYWORD_LENGTH = 100;
	private int keywordSize = 0;
	
	// Instantiate initial page with no results using defaults
	public CategoryPage(TissueCatalogue tCat, boolean includeErrors, boolean showWhole)
	{	
		this.tCat = tCat;
		String stage = "";				// start with select instruction
		int tissueID = 0;				// start with no tissue selected
		byAbundance = false; 			// default enrichment
		int displayMax = 20;			// default is lowest value
		boolean atStart = true;			// start with search button dimmed
		String keyword = "default";		// dummy value for selection of "selected" at start
		
		populateKeywords();				// set up keywordList array of keywords
		
		//-------- Build initial page ----------//
		htmlBuilder = new StringBuilder();
		// Boiler-plate sections
		PageUtility pu = new PageUtility(includeErrors, showWhole);
		htmlBuilder = new StringBuilder(pu.getPageTop(PAGE_POS));
		
		htmlBuilder.append("<div class=\"explanation\">" + intro + "</div>\n");
		htmlBuilder.append(getControls(stage, tissueID, keyword, byAbundance, displayMax, atStart));	// build controls with initial settings
		
		// Switch Button
		htmlBuilder.append("<p style=\"text-align:right;\">\n");
		htmlBuilder.append("<a href=\"https://motif.mvls.gla.ac.uk/BeetleAtlas2/?page=go\">\n");
		htmlBuilder.append("<button class=\"switchButton\">&#8634;</button></a></p>\n");
		
		// add anatomy graphic hide/show		
		htmlBuilder.append(PageUtility.getAnatomyLink());
		
		htmlBuilder.append("</div> <!-- end of controls div -->\n");
			
		// hidden errors checkbox for start page only
		if(includeErrors)
		{
			htmlBuilder.append("<input style=\"display:none\" type=\"checkbox\" id=\"errors_0\" value=\"errors\" checked=\"checked\" />");			
		}
		else	// not really needed but useful for testing
		{
			htmlBuilder.append("<input style=\"display:none\" type=\"checkbox\" id=\"errors_0\" value=\"noerrors\" />");				
		}
		// hidden show whole checkbox 
		if(showWhole)
		{
			htmlBuilder.append("<input style=\"display:none\" type=\"checkbox\" id=\"whole_0\" value=\"whole\" checked=\"checked\" />");			
		}
		else	// not really needed but useful for testing
		{
			htmlBuilder.append("<input style=\"display:none\" type=\"checkbox\" id=\"whole_0\" value=\"hideWhole\" />");				
		}

		// Finish off with footer section	
		htmlBuilder.append(PageUtility.PAGE_FOOT);
	}
	
	// Constructor for page WITH results (displayMax chosen by user, but totalDisplayed could be less than displayMax if fewer found)
	public CategoryPage(Expression[] expressList, Gene[] geneList, String stage, int tissueID, String keyword, boolean byAbundance,
			int foundNum, int displayMax, int totalDisplayed, boolean includeErrors, boolean showWhole, TissueCatalogue tCat)
	{
		this.tCat = tCat;
		boolean atStart = false;		// start with search button enabled
		
		populateKeywords();				// set up keywordList array of keywords
		
		//-------- Build Results page ----------//
		htmlBuilder = new StringBuilder();
		// Boiler-plate sections
		PageUtility pu = new PageUtility(includeErrors, showWhole);
		htmlBuilder = new StringBuilder(pu.getPageTop(PAGE_POS));
		
		htmlBuilder.append("<div class=\"explanation\">" + intro + "</div>\n");
		htmlBuilder.append(getControls(stage, tissueID, keyword, byAbundance, displayMax, atStart));	// build controls with previous settings
		
		// Switch Button
		htmlBuilder.append("<p style=\"text-align:right;\">\n");
		htmlBuilder.append("<a href=\"https://motif.mvls.gla.ac.uk/BeetleAtlas2/?page=go\">\n");
		htmlBuilder.append("<button class=\"switchButton\">&#8634;</button></a></p>\n");
		
		// add anatomy graphic hide/show		
		htmlBuilder.append(PageUtility.getAnatomyLink());
		htmlBuilder.append("</div> <!-- end of controls div -->\n");	
		// div just to act as target
		htmlBuilder.append("<div id=\"mobileTargetC\"></div>\n");

		// Results info line
		String numGenes = ""; String genePhrase = ""; String qualifier = "";
		if(foundNum == 0)
		{ numGenes = "No"; genePhrase = "genes";}
		else if(foundNum == 1)
		{ numGenes = "1"; genePhrase = "gene";}
		else
		{ numGenes = foundNum + ""; genePhrase = "genes";}
		
		if(foundNum > totalDisplayed)
		{ qualifier = "; " + totalDisplayed + " shown";}
		
		String criterion = new String();
		if(byAbundance)
		{ criterion = "abundant";}
		else
		{criterion = "enriched";}
		
		String revealAllPhrase = "<a href=\"javascript:toggleAll('master'," + totalDisplayed + ",'&#9658;','&#9660;');\" title=\"show all\"> <span id=\"master\" class=\"infoContent onOff\">&#9655;</span></a>";
		
		if(foundNum > 0)
		{
			htmlBuilder.append("<div class=\"explanation2\">" + numGenes + " " + genePhrase + " in the category &lsquo;" + keyword +  "&rsquo; that are highly "
				+ criterion + " in "  + stage + " " + tCat.getTissueNameByID(tissueID)  + qualifier + ":" + revealAllPhrase + "</div><!-- end of explanation div -->\n");
		}
		else
		{
			htmlBuilder.append("<div class=\"explanation2\">" + numGenes + " " + genePhrase + " in the category &lsquo;" + keyword +  "&rsquo; that are highly "
					+ criterion + " in "  + stage + " " + tCat.getTissueNameByID(tissueID)  + qualifier + ".</div><!-- end of explanation div -->\n");
			
		}
		
		// Go through each of the Expression objects in list and format results
		boolean conceal = true; 			// whether to provide hide/show button (for multiple results)
		boolean isEmbryoQuery = false;		// present embryo results first

		for(int i=0; i<totalDisplayed; i++)
		{
			GeneExpression express = (GeneExpression) expressList[i];
			Gene gene = geneList[i];	
			if(expressList[i]!=null)
			{	
				GeneResult gr = new GeneResult(gene, tCat, express, i, conceal, isEmbryoQuery, includeErrors, showWhole);
				htmlBuilder.append(gr.getResultsHTML());
			}
		}
		
		if(foundNum > 0)
		{
			// Summary section start		
			htmlBuilder.append("<div class=\"results\"> <!-- Start of summary section-->\n");	
			htmlBuilder.append("<div class=\"summarySet\">\n<div class=\"summary\">\n");
			htmlBuilder.append("<a href=\"javascript:toggleConcealed('sumBut','summaryList','&#9658;','&#9660;');\" title=\"reveal summary\"><span id=\"sumBut\" class=\"summaryButton onOff\">&#9658;</span></a>\n");
			htmlBuilder.append("</div>");
			htmlBuilder.append("<div class=\"summary\">\n<span class=\"summaryTitle\">Summary: " + totalDisplayed + 
					" highly " + criterion + " genes of "  +  tCat.getStageByID(tissueID) + " " + tCat.getTissueNameByID(tissueID) + " in the category ‘" + keyword + "’" +  "</span>\n</div>\n");
			htmlBuilder.append("<div id=\"summaryList\" class=\"conceal\">\n");
			
			for(int i=0; i<totalDisplayed; i++)
			{
				htmlBuilder.append(geneList[i].getGeneID() + "<br />");		// 		Add more info?
			}
			
			htmlBuilder.append("</div>\n");	
			htmlBuilder.append("</div>\n");	
			htmlBuilder.append("</div><!-- end of summary section-->\n");		
		}
			
		// Finish off with footer section	
		htmlBuilder.append(PageUtility.PAGE_FOOT);
	}
	

	// Build contents of controls div with instructions, selections and search button
	private String getControls(String stage, int tissueID, String keyword, boolean byAbundance, int displayMax, boolean atStart)
	{
		String stageLine =  new String();	// sets selected stage for repeat	
		String tissueLine = new String();	// sets selected tissue if stage set
		
		StringBuilder controlBuilder = new StringBuilder();
		// start of div and instructions
		controlBuilder.append("<div id=\"controls\">\n");
		controlBuilder.append("1. Select a ‘stage’ and next the tissue of interest. Then choose ‘enrichment’ or ‘abundance’ for greatest expression:\n");
		controlBuilder.append("<p>\n"
				+ "<select name=\"stage\" id=\"stage\" onchange=\"processData(); return true;\">\n");
		controlBuilder.append("<option value=\" --- Select a Stage --- \"> --- Select a Stage --- </option>");
		
		// 1. set stageLine; 
		if(stage.equals("Adult"))
		{
			stageLine = "<option value=\"Adult\" selected=\"selected\">Adult</option>\n<option value=\"Larval\">Larval</option>\n</select>&nbsp;<span class=\"immobileHide\"><br /></span>\n";
		}
		else if (stage.equals("Larval"))
		{
			stageLine = "<option value=\"Adult\">Adult</option>\n<option value=\"Larval\" selected=\"selected\">Larval</option>\n</select>&nbsp;<span class=\"immobileHide\"><br /></span>\n";
		}
		else
		{
			stageLine = "<option value=\"Adult\">Adult</option>\n<option value=\"Larval\">Larval</option>\n</select>&nbsp;<span class=\"immobileHide\"><br /></span>\n";
		}
		
		// 2. set tissueLine for Adult or Larval
		if(stage.equals("Adult") || stage.equals("Larval"))
		{
			StringBuilder tissBuilder = new StringBuilder("<select name=\"tissue\" id=\"tissue\">\n");	// starting <select>
			// sort TissueList alphabetically to populate list for returned page
			tCat.sortTissueList();
			for(int i=0; i<tCat.getTissueListSize(); i++)
			{
				Tissue ft = tCat.getTissue(i);	// get next Tissue obj in list
				if(ft.getStage().equals(stage) && ft.getTissueID() == tissueID)		// correct stage and repeat id - set selected
				{
					tissBuilder.append("<option selected=\"selected\" value=\"" + ft.getTissueID() + "\">" + ft.getTissueName() + "</option>\n");
				}
				else if(ft.getStage().equals(stage) && !ft.isReference())
				{
					tissBuilder.append("<option value=\"" + ft.getTissueID() + "\">" + ft.getTissueName() + "</option>\n");
				}
			}		
			tissBuilder.append("</select><span class=\"immobileHide\"><br /></span>\n");	// ending </select>
			tissueLine = tissBuilder.toString();
		}	
		else		// at startup
		{
			tissueLine = "<select name=\"tissue\" id=\"tissue\">\n<option value=\"0\"> --- First select a Stage --- </option>\n</select>&nbsp;<span class=\"immobileHide\"><br /></span>\n";		
		}
		
		controlBuilder.append(stageLine + tissueLine);
		
		// 3. set enrichment or abundance choice
		controlBuilder.append("\n<select id=\"order\">\n");
		if(byAbundance)
		{
			controlBuilder.append("<option value=\"enrichment\">Enrichment</option>\n");
			controlBuilder.append("<option selected=\"selected\" value=\"abundance\">Abundance</option>\n</select>\n");
		}
		else
		{
			controlBuilder.append("<option selected=\"selected\" value=\"enrichment\">Enrichment</option>");
			controlBuilder.append("<option value=\"abundance\">Abundance</option></select>\n");
		}
		controlBuilder.append("</p>\n");
		
		// Add instructions to tissue line
		controlBuilder.append("2. Select a category (keyword), how many genes you wish displayed, and press ‘Search’.\n");
		
		// after line split from para have div containing two floating divs to allow right-alignment of search button
		controlBuilder.append("<div class=\"standard\">\n");
		
		// 4. populate keyword list				//
		StringBuilder kwBuilder = new StringBuilder("Category: <select name=\"keyword\" id=\"keyword\">");	 // starting <select>
		if(atStart || keyword.equals(keywordList[0]))
		{
			kwBuilder.append("<option  selected=\"selected\" value=\"" + keywordList[0] + "\">" + keywordList[0] + "</option>"); // set first item selected
		}
		else
		{
			kwBuilder.append("<option  value=\"" + keywordList[0] + "\">" + keywordList[0] + "</option>");
		}
		for(int i=1; i<keywordSize; i++)
		{
			if(keyword.equals(keywordList[i]))
			{
				kwBuilder.append("<option selected=\"selected\" value=\"" + keywordList[i] + "\">" + keywordList[i] + "</option>");
			}
			else
			{
				kwBuilder.append("<option value=\"" + keywordList[i] + "\">" + keywordList[i] + "</option>");				
			}
		}
		kwBuilder.append("</select>&nbsp;&nbsp;");	// ending </select>
		controlBuilder.append(kwBuilder.toString());
				
		// 5. set max choice (20 to 50) or 100
		controlBuilder.append("<span class=\"immobileHideS\"><br /></span><span style=\"white-space:nowrap\">Display:&nbsp;<select name=\"maxdisplayed\" id=\"maxdisplayed\" dir=\"rtl\">");
		for(int i=20; i<60; i+=10)
		{
			if(i==displayMax)
			{
				controlBuilder.append("<option selected=\"selected\" value=\"" + i + "\">" + i + "</option>");			
			}
			else
			{
				controlBuilder.append("<option value=\"" + i + "\">" + i + "</option>");		
			}
		}
		if(displayMax==100)
		{
			controlBuilder.append("<option selected=\"selected\" value=\"" + 100 + "\">" + 100 + "</option>");			
		}
		else
		{
			controlBuilder.append("<option value=\"" + 100 + "\">" + 100 + "</option>");		
		}
		controlBuilder.append("</select></span>");
		
		// 6. Set availability of search button (disabled at start, enables at repeat)
		if(atStart)
		{
			controlBuilder.append("<button id=\"runButton\" disabled=\"disabled\" onclick=\"sendSearchGoForm();\">Search</button>");
		}
		else
		{
			controlBuilder.append("<button id=\"runButton\" onclick=\"sendSearchGoForm();\">Search</button>");
		}
		
		// End of div for second line
		controlBuilder.append("</div>\n");
	
		return controlBuilder.toString();
	}	
	
	// Make DB call to create list of all keywords to populate drop-down
	private void populateKeywords()
	{
		keywordList = new String [KEYWORD_LENGTH];	// initialize list of keywords for search		
		String query = DBQuery.getKeywordQuery();
		Connect cnt = new Connect();
		Connection conn = cnt.getConnection();
		try 
		{		
			Statement stmt = conn.createStatement();
			ResultSet resSet = stmt.executeQuery(query);
			if(resSet.first())
			{
				resSet.beforeFirst();		// hack to reset cursor as 'if' moves it on a row!
				while (resSet.next())		// moves to next row while rows remain
				{
					String kword = resSet.getString("Keyword");
					keywordList[keywordSize] = kword;
					keywordSize++;
				}
			}
			try { if (resSet != null) resSet.close(); } catch (Exception e) {}; 	// added as server precaution
		    try { if (stmt != null) stmt.close(); } catch (Exception e) {};			// added as server precaution
		}
		catch (SQLException e)
		{
			System.out.println("SQL Exception: " + e.toString());
		}
		finally // close the connection
		{
			if(conn != null)
			{
				try { conn.close();}
				catch(Exception e){System.out.println("Can't close.");}
			}
		}
	}
	
	// returns page
	public String getHTML()
	{
		return htmlBuilder.toString();
	}
}
