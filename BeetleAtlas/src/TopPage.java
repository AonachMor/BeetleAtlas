// Generates an HTML page for a "Top" search, with or without results
// Now flagged as "Tissue" as decided not to have tissue search with category, and Tissue is clearer.
// David P. Leader 22.06.2018
// Last update 22.03.2026

public class TopPage
{	
	private TissueCatalogue tCat;
	private boolean byAbundance = true;					// enrichment or abundance criterion
	private final int PAGE_POS = PageUtility.TISSUE; 	// generally position of page in menu
	private StringBuilder htmlBuilder;					// for accumulating html output
	private String intro = "Find which genes have the greatest expression in a particular tissue.";
	
	// Constructor for page WITHOUT results
	public TopPage(TissueCatalogue tCat, boolean includeErrors, boolean showWhole)
	{		
		this.tCat = tCat;
		String stage = "";				// start with select instruction
		int tissueID = 0;				// start with no tissue selected
		byAbundance = false; 			// default enrichment
		int displayMax = 20;			// default is lowest value
		boolean atStart = true;			// start with search button dimmed
		
					//-------- Build initial page ----------//
		htmlBuilder = new StringBuilder();
		// Boiler-plate sections
		PageUtility pu = new PageUtility(includeErrors, showWhole);
		htmlBuilder = new StringBuilder(pu.getPageTop(PAGE_POS));
		
		htmlBuilder.append("<div class=\"explanation\">" + intro + "</div>\n");
		
		htmlBuilder.append(getControls(stage, tissueID, byAbundance, displayMax, atStart));
		
		// Switch Button
		htmlBuilder.append("<p style=\"text-align:right;\">\n");
		htmlBuilder.append("<a href=\"https://motif.mvls.gla.ac.uk/BeetleAtlas2/?page=top\">\n");
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
		else	// not really needed, but useful for testing
		{
			htmlBuilder.append("<input style=\"display:none\" type=\"checkbox\" id=\"whole_0\" value=\"hideWhole\" />");				
		}
		
		// Finish off with footer section	
		htmlBuilder.append(PageUtility.PAGE_FOOT);
	}
	
	// Constructor for page WITH results (displayMax chosen by user, but totalDisplayed could be less than displayMax if fewer found)
	public TopPage(Expression[] expressList, Gene[] geneList, String stage, int tissueID, boolean byAbundance, int displayMax, 
			int totalDisplayed, TissueCatalogue tCat, boolean includeErrors, boolean showWhole)
	{
		this.tCat = tCat;
		boolean atStart = false;		// start with search button enabled
		
								//-------- Build results page ----------//
		htmlBuilder = new StringBuilder();
		// Boiler-plate sections
		PageUtility pu = new PageUtility(includeErrors, showWhole);
		htmlBuilder = new StringBuilder(pu.getPageTop(PAGE_POS));
		
		htmlBuilder.append("<div class=\"explanation\">" + intro + "</div>\n");
		
		htmlBuilder.append(getControls(stage, tissueID, byAbundance, displayMax, atStart));
		
		// Switch Button
		htmlBuilder.append("<p style=\"text-align:right;\">\n");
		htmlBuilder.append("<a href=\"https://motif.mvls.gla.ac.uk/BeetleAtlas2/?page=top\">\n");
		htmlBuilder.append("<button class=\"switchButton\">&#8634;</button></a></p>\n");
		
		// add anatomy graphic hide/show		
		htmlBuilder.append(PageUtility.getAnatomyLink());
		
		htmlBuilder.append("</div> <!-- end of controls div -->\n");
		
		// div just to act as target
		htmlBuilder.append("<div id=\"mobileTargetT\"></div>\n");
		
		// Build info line to appear above all results
		StringBuilder tissuePhrase = new StringBuilder();
		tissuePhrase.append(tCat.getStageByID(tissueID));
		tissuePhrase.append(" ");
		tissuePhrase.append(tCat.getTissueNameByID(tissueID));
		if(byAbundance)
		{
			tissuePhrase.append(", by abundance");		
		}
		else
		{
			tissuePhrase.append(", by enrichment");		
		}
		
		String geneExpressedPhrase = " most expressed genes in ";	
		String revealAllPhrase = "<a href=\"javascript:toggleAll('master'," + totalDisplayed + ",'&#9658;','&#9660;');\" title=\"show all\"> <span id=\"master\" class=\"infoContent onOff\">&#9655;</span></a>";
		
		htmlBuilder.append("<div class=\"explanation2\">Top " + totalDisplayed + geneExpressedPhrase + tissuePhrase.toString() + ":"+ revealAllPhrase +"</div><!-- end of explanation div -->\n");		

		boolean conceal = true; 		// whether to provide hide/show button (for multiple results)
		boolean isEmbryoQuery = false;	// present embryo results first
		// Go through each of the Expression objects in list and format results
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
		
		// Summary section
		String criterion = new String();
		if(byAbundance)
		{
			criterion = "by abundance";
		}
		else
		{
			criterion = "by enrichment";			
		}
		
		htmlBuilder.append("<div class=\"results\"> <!-- Start of summary section-->\n");	
		htmlBuilder.append("<div class=\"summarySet\">\n<div class=\"summary\">\n");
		htmlBuilder.append("<a href=\"javascript:toggleConcealed('sumBut','summaryList','&#9658;','&#9660;');\" title=\"reveal summary\"><span id=\"sumBut\" class=\"summaryButton onOff\">&#9658;</span></a>\n");
		htmlBuilder.append("</div>");
		htmlBuilder.append("<div class=\"summary\">\n<span class=\"summaryTitle\">Summary: top " 
			+ totalDisplayed + " genes in " + tCat.getStageByID(tissueID) + " " + tCat.getTissueNameByID(tissueID) + ", " + criterion +  "</span>\n</div>\n");
		htmlBuilder.append("<div id=\"summaryList\" class=\"conceal\">\n");
		
		//htmlBuilder.append("<br />");
		for(int i=0; i<totalDisplayed; i++)
		{
			htmlBuilder.append(geneList[i].getGeneID() + "<br />");		// 		Add symbol and product and value?
		}
		
		htmlBuilder.append("</div>\n");	
		htmlBuilder.append("</div>\n");	
		htmlBuilder.append("</div><!-- end of summary section-->\n");	
			
		// Finish off with footer section	
		htmlBuilder.append(PageUtility.PAGE_FOOT);
	}
	
	// Build contents of controls div with instructions, selections and search button
	private String getControls(String stage, int tissueID, boolean byAbundance, int displayMax, boolean atStart)
	{
		String stageLine =  new String();	// sets selected stage for repeat	
		String tissueLine = new String();	// sets selected tissue if stage set
		
		StringBuilder controlBuilder = new StringBuilder();
		// start of div and instructions
		controlBuilder.append("<div id=\"controls\">\n");
		controlBuilder.append("First select a ‘stage’ and next the tissue of interest. Then choose ‘enrichment’ or ‘abundance’ for greatest expression:\n");
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
				
				// System.out.println("TissueID: " + ft.getTissueID());
				
				if(ft.getStage().equals(stage) && ft.getTissueID() == tissueID)		// repeat id - set selected
				{
					tissBuilder.append("<option selected=\"selected\" value=\"" + ft.getTissueID() + "\">" + ft.getTissueName() + "</option>\n");
				}
				else if(ft.getStage().equals(stage)  && !ft.isReference())				
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
		
		// after line split from para have div containing two floating divs to allow right-alignment of search button
		controlBuilder.append("<div class=\"standard\">\n");
				
		// 4. set max choice (20 to 50) or 100
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
		
		// 5. Set availability of search button (disabled at start, enables at repeat)
		if(atStart)
		{
			controlBuilder.append("<button id=\"runButton\" disabled=\"disabled\" onclick=\"sendSearchTopForm();\">Search</button>");
		}
		else
		{
			controlBuilder.append("<button id=\"runButton\" onclick=\"sendSearchTopForm();\">Search</button>");
		}
		
		
		
		// End of div for second line
		controlBuilder.append("</div>\n");
	
		return controlBuilder.toString();
	}

	public String getHTML()
	{
		return htmlBuilder.toString();
	}

}