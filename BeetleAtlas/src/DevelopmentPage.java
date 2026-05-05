// Generates an HTML page for a "Development" search — Adult v. Larva with or without results
// David P. Leader 14.01.2021
// Last update 22.03.2026

public class DevelopmentPage
{	
	TissueCatalogue tCat;
	private boolean adultGreater = true;					// adult>larval or larval>adult
	private final int PAGE_POS = PageUtility.DEVELOPMENT;	// generally position of page in menu
	private StringBuilder htmlBuilder;						// for accumulating html output
	private String intro = "Find which genes show the greatest difference in expression between larva and adult for a given tissue.";
	
	// Constructor for page WITHOUT results
	public DevelopmentPage(TissueCatalogue tCat, boolean includeErrors, boolean showWhole)
	{		
		this.tCat = tCat;
		adultGreater = true;			// default adult>larval
		int displayMax = 20;			// default is lowest value
		String uniTissue = "none";		// start with no tissue selected
		
					//-------- Build initial page ----------//
		htmlBuilder = new StringBuilder();
		// Boiler-plate sections
		PageUtility pu = new PageUtility(includeErrors, showWhole);
		htmlBuilder = new StringBuilder(pu.getPageTop(PAGE_POS));
		htmlBuilder.append("<div class=\"explanation\">" + intro + "</div>\n");
		
		htmlBuilder.append(getControls(uniTissue, adultGreater, displayMax));
		
		// Switch Button
		htmlBuilder.append("<p style=\"text-align:right;\">\n");
		htmlBuilder.append("<a href=\"https://motif.mvls.gla.ac.uk/BeetleAtlas2/?page=devel\">\n");
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
	public DevelopmentPage(Expression[] expressList, Gene[] geneList, String uniTissue, boolean adultGreater, int displayMax, 
			int totalDisplayed, TissueCatalogue tCat, boolean includeErrors, boolean showWhole)
	{
		this.tCat = tCat;	
								//-------- Build results page ----------//
		htmlBuilder = new StringBuilder();
		// Boiler-plate sections
		PageUtility pu = new PageUtility(includeErrors, showWhole);
		htmlBuilder = new StringBuilder(pu.getPageTop(PAGE_POS));
		
		htmlBuilder.append("<div class=\"explanation\">" + intro + "</div>\n");
		htmlBuilder.append(getControls(uniTissue, adultGreater, displayMax));
		
		// Switch Button
		htmlBuilder.append("<p style=\"text-align:right;\">\n");
		htmlBuilder.append("<a href=\"https://motif.mvls.gla.ac.uk/BeetleAtlas2/?page=devel\">\n");
		htmlBuilder.append("<button class=\"switchButton\">&#8634;</button></a></p>\n");
		
		// add anatomy graphic hide/show		
		htmlBuilder.append(PageUtility.getAnatomyLink());
		
		htmlBuilder.append("</div> <!-- end of controls div -->\n");
		
		// div just to act as target
		htmlBuilder.append("<div id=\"mobileTargetD\"></div>\n");
		
		// Build info line to appear above all results
		// The x stageHigh unitissue genes showing most difference in abundance/enrichment from stageLow:		
		String stageLow = new String();
		String stageHigh = new String();	
		if(adultGreater)
		{

			stageLow = "Larval";
			stageHigh = "Adult";
		}
		else
		{
			stageHigh = "Larval";
			stageLow = "Adult";			
		}
		
		String criterion =  " abundance ";		// no choice here
		
		String geneDiffPhrase = " genes showing most difference in ";	
		String revealAllPhrase = "<a href=\"javascript:toggleAll('master'," + totalDisplayed + ",'&#9658;','&#9660;');\" title=\"show all\"> <span id=\"master\" class=\"infoContent onOff\">&#9655;</span></a>";
		
		htmlBuilder.append("<div class=\"explanation2\">The " + totalDisplayed + " " + stageHigh + " "  +  uniTissue +  geneDiffPhrase + criterion + " from " + stageLow + ":" + revealAllPhrase + "</div><!-- end of explanation div -->\n");
		
		// Go through each of the Expression objects in list and format results
		for(int i=0; i<totalDisplayed; i++)
		{
			GeneExpression express = (GeneExpression) expressList[i];
			Gene gene = geneList[i];	
			if(expressList[i]!=null)
			{	
				GeneResult gr = new GeneResult(gene, tCat, express, i, true, false, includeErrors, showWhole);
				htmlBuilder.append(gr.getResultsHTML());
			}
		}
		
		// Summary section	
		htmlBuilder.append("<div class=\"results\"> <!-- Start of summary section-->\n");	
		htmlBuilder.append("<div class=\"summarySet\">\n<div class=\"summary\">\n");
		htmlBuilder.append("<a href=\"javascript:toggleConcealed('sumBut','summaryList','&#9658;','&#9660;');\" title=\"reveal summary\"><span id=\"sumBut\" class=\"summaryButton onOff\">&#9658;</span></a>\n");
		htmlBuilder.append("</div>");
		htmlBuilder.append("<div class=\"summary\">\n<span class=\"summaryTitle\">Summary: " + totalDisplayed + " " + uniTissue +
				" genes in which " + stageHigh + " abundance most greatly exceeds " + 
				stageLow +  "</span>\n</div>\n");
		htmlBuilder.append("<div id=\"summaryList\" class=\"conceal\">\n");		
	
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
	private String getControls(String uniTissue, boolean adultMinusLarval, int displayMax)
	{		
		StringBuilder controlBuilder = new StringBuilder();
		
		// start of div and instructions
		controlBuilder.append("<div id=\"controls\">\n");
		controlBuilder.append("1. Select whether expression should be greater in the adult or larval stage:\n");
		
		// 1. set radioLine
		String radioLine =  new String();	// sets selected stage for repeat
		if(adultMinusLarval)
		{
			radioLine = "<p><input type=\"radio\" name=\"devField\" id=\"devAdult\" checked=\"checked\" /> Adult > Larval<br />" +
						"<input type=\"radio\" name=\"devField\" id=\"devLarval\" /> Larval > Adult<br /></p>";
		}
		else
		{
			radioLine = "<p><input type=\"radio\" name=\"devField\" id=\"devAdult\" /> Adult > Larval<br />" +
					"<input type=\"radio\" name=\"devField\" id=\"devLarval\" checked=\"checked\" /> Larval > Adult<br /></p>";
		}		
		controlBuilder.append(radioLine);
		
		// Add instructions to tissue line
		controlBuilder.append("2. Choose the tissue, how many genes you wish displayed, and press ‘Search’.\n");

		// 2. set unitissue drop-down
		String tissuePhrase = new String();	// sets selected tissue if stage set
		StringBuilder tissBuilder = new StringBuilder("<p> Tissue: <select name=\"uniTissue\" id=\"uniTissue\">");	 // starting <select>
		for(int i=0; i<tCat.getDevListSize(); i++)
		{
			TissueDoublet td = tCat.getDevTissue(i);	// get next Doublet object in list
			if(td.hasAdultTissue() && td.hasLarvalTissue())	
			{
				if(i==0 && uniTissue.equals("none") )		// at start set selected to first tissue on list
				{
					tissBuilder.append("<option selected=\"selected\" value=\"" + td.getUniTissueName() + "\">" + td.getUniTissueName() + "</option>");
				}
				else										// set selected to previous choice
				{
					if(td.getUniTissueName().equals(uniTissue))
					{
						tissBuilder.append("<option selected=\"selected\" value=\"" + td.getUniTissueName() + "\">" + td.getUniTissueName() + "</option>");
					}
					else
					{
						tissBuilder.append("<option value=\"" + td.getUniTissueName() + "\">" + td.getUniTissueName() + "</option>");
					}
				}
			}
		}		
		tissBuilder.append("</select>&nbsp;");	// ending </select>
		tissuePhrase = tissBuilder.toString();
		controlBuilder.append(tissuePhrase);		
			
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
		
		// 5. Search button
		controlBuilder.append("<button id=\"runButton\" onclick=\"sendSearchDevelForm();\">Search</button>");
		controlBuilder.append("</p>\n");
	
		return controlBuilder.toString();
	}

	public String getHTML()
	{
		return htmlBuilder.toString();
	}

}