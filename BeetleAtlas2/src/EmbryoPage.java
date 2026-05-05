// Generates an HTML page for a "Embryo" search, with or without results
// Latest Update: 21.03.2026

public class EmbryoPage
{	
	TissueCatalogue tCat;
	private final int PAGE_POS = PageUtility.EMBRYO;	// generally position of page in menu
	private StringBuilder htmlBuilder;					// for accumulating html output
	private String intro = "Find genes expressed in the embryo.";
	
	// Constructor for page WITHOUT results
	public EmbryoPage(TissueCatalogue tCat, boolean includeErrors, boolean showWhole)
	{		
		int displayMax = 50;			// default 
		
		this.tCat = tCat;
					//-------- Build initial page ----------//
		// Build page starting with boiler-plate sections
		htmlBuilder = new StringBuilder();
		PageUtility pu = new PageUtility(includeErrors, showWhole);
		htmlBuilder = new StringBuilder(pu.getPageTop(PAGE_POS));
		
		htmlBuilder.append("<div class=\"explanation\">" + intro + "</div>\n");
		
		// start of div and instructions
		htmlBuilder.append("<div id=\"controls\">\n");
		htmlBuilder.append("Select a peak of embryo expression.<br />\n");
		
		htmlBuilder.append("<input type=\"radio\" name=\"idtype\" id=\"zero\" checked=\"checked\" /> 0–1 h<br />\n");
		htmlBuilder.append("<input type=\"radio\" name=\"idtype\" id=\"one\" /> 1–24 h<br />\n");
		htmlBuilder.append("<input type=\"radio\" name=\"idtype\" id=\"two\" /> 24–36 h<br />\n");
		htmlBuilder.append("<input type=\"radio\" name=\"idtype\" id=\"three\" /> 36–72 h<br />\n");
		
		htmlBuilder.append("<br />Do you wish to see genes expressed only in embryo? <br />\n");
		
		htmlBuilder.append("<input type=\"radio\" name=\"embryoOnly\" id=\"yes\" /> Yes ");
		htmlBuilder.append("<input type=\"radio\" name=\"embryoOnly\" id=\"no\" checked=\"checked\" /> No<br />\n");
		
		// set max choice (20 to 50) or 500 (all)
		htmlBuilder.append("<br /><span class=\"immobileHideS\"><br /></span><span style=\"white-space:nowrap\">Display:&nbsp;<select name=\"maxdisplayed\" id=\"maxdisplayed\" dir=\"rtl\">");
		for(int i=20; i<60; i+=10)
		{
			if(i==displayMax)
			{
				htmlBuilder.append("<option selected=\"selected\" value=\"" + i + "\">" + i + "</option>");			
			}
			else
			{
				htmlBuilder.append("<option value=\"" + i + "\">" + i + "</option>");		
			}
		}
		if(displayMax==500)
		{
			htmlBuilder.append("<option selected=\"selected\" value=\"" + 500 + "\">" + "all" + "</option>");			
		}
		else
		{
			htmlBuilder.append("<option value=\"" + 500 + "\">" + "all" + "</option>");		
		}
		htmlBuilder.append("</select></span>");
		
		// Search button
		htmlBuilder.append("<button id=\"runButton\" onclick=\"sendSearchEmbryoForm();\">Search</button>");

		// Switch Button
		htmlBuilder.append("<p style=\"text-align:right;\">\n");
		htmlBuilder.append("<a href=\"https://motif.mvls.gla.ac.uk/BeetleAtlas/?page=embryo\">\n");
		htmlBuilder.append("<button class=\"switchButton\">&#8634;</button></a></p>\n");	

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
	
	// Page with results
	public EmbryoPage(Expression[] expressList, Gene[] geneList, String stage, boolean exclusive, int displayMax, int totalGenes, 
			TissueCatalogue tCat, boolean includeErrors, boolean showWhole)
	{
		this.tCat = tCat;
		String time = getTime(stage);
		//-------- Build results page ----------//
		htmlBuilder = new StringBuilder();
		// Boiler-plate sections
		PageUtility pu = new PageUtility(includeErrors, showWhole);
		htmlBuilder = new StringBuilder(pu.getPageTop(PAGE_POS));

		htmlBuilder.append("<div class=\"explanation\">" + intro + "</div>\n");
		
		// start of div and instructions
		htmlBuilder.append("<div id=\"controls\">\n");
		htmlBuilder.append("Select a peak of embryo expression.<br />\n");
		
		// set stage (age) choice on the basis of previous search
		if(stage.equals("zero"))
		{
			htmlBuilder.append("<input type=\"radio\" name=\"idtype\" id=\"zero\" checked=\"checked\" /> 0–1 h<br />\n");
			htmlBuilder.append("<input type=\"radio\" name=\"idtype\" id=\"one\" /> 1–24 h<br />\n");
			htmlBuilder.append("<input type=\"radio\" name=\"idtype\" id=\"two\" /> 24–36 h<br />\n");
			htmlBuilder.append("<input type=\"radio\" name=\"idtype\" id=\"three\" /> 36–72 h<br />\n");
		}
		else if(stage.equals("one"))
		{
			htmlBuilder.append("<input type=\"radio\" name=\"idtype\" id=\"zero\" /> 0–1 h<br />\n");
			htmlBuilder.append("<input type=\"radio\" name=\"idtype\" id=\"one\" checked=\"checked\" /> 1–24 h<br />\n");
			htmlBuilder.append("<input type=\"radio\" name=\"idtype\" id=\"two\" /> 24–36 h<br />\n");
			htmlBuilder.append("<input type=\"radio\" name=\"idtype\" id=\"three\" /> 36–72 h<br />\n");
		}
		else if(stage.equals("two"))
		{
			htmlBuilder.append("<input type=\"radio\" name=\"idtype\" id=\"zero\" /> 0–1 h<br />\n");
			htmlBuilder.append("<input type=\"radio\" name=\"idtype\" id=\"one\" /> 1–24 h<br />\n");
			htmlBuilder.append("<input type=\"radio\" name=\"idtype\" id=\"two\" checked=\"checked\" /> 24–36 h<br />\n");
			htmlBuilder.append("<input type=\"radio\" name=\"idtype\" id=\"three\" /> 36–72 h<br />\n");
		}
		else if(stage.equals("three"))
		{
			htmlBuilder.append("<input type=\"radio\" name=\"idtype\" id=\"zero\" /> 0–1 h<br />\n");
			htmlBuilder.append("<input type=\"radio\" name=\"idtype\" id=\"one\" /> 1–24 h<br />\n");
			htmlBuilder.append("<input type=\"radio\" name=\"idtype\" id=\"two\" /> 24–36 h<br />\n");
			htmlBuilder.append("<input type=\"radio\" name=\"idtype\" id=\"three\" checked=\"checked\" /> 36–72 h<br />\n");
		}
		else
		{
			htmlBuilder.append("<input type=\"radio\" name=\"idtype\" id=\"zero\" /> 0–1 h<br />\n");
			htmlBuilder.append("<input type=\"radio\" name=\"idtype\" id=\"one\" /> 1–24 h<br />\n");
			htmlBuilder.append("<input type=\"radio\" name=\"idtype\" id=\"two\" /> 24–36 h<br />\n");
			htmlBuilder.append("<input type=\"radio\" name=\"idtype\" id=\"three\" /> 36–72 h<br />\n");
		}
		
		htmlBuilder.append("<br />Do you wish to see genes expressed only in embryo? <br />\n");
		
		if(exclusive)
		{
			htmlBuilder.append("<input type=\"radio\" name=\"embryoOnly\" id=\"yes\" checked=\"checked\" /> Yes ");
			htmlBuilder.append("<input type=\"radio\" name=\"embryoOnly\" id=\"no\" /> No<br />\n");	
		}
		else
		{
			htmlBuilder.append("<input type=\"radio\" name=\"embryoOnly\" id=\"yes\" /> Yes ");
			htmlBuilder.append("<input type=\"radio\" name=\"embryoOnly\" id=\"no\" checked=\"checked\" /> No<br />\n");		
		}
		
		// set max choice (20 to 50) or 500 (all)
		htmlBuilder.append("<br /><span class=\"immobileHideS\"><br /></span><span style=\"white-space:nowrap\">Display:&nbsp;<select name=\"maxdisplayed\" id=\"maxdisplayed\" dir=\"rtl\">");
		for(int i=20; i<60; i+=10)
		{
			if(i==displayMax)
			{
				htmlBuilder.append("<option selected=\"selected\" value=\"" + i + "\">" + i + "</option>");			
			}
			else
			{
				htmlBuilder.append("<option value=\"" + i + "\">" + i + "</option>");		
			}
		}
		if(displayMax==500)
		{
			htmlBuilder.append("<option selected=\"selected\" value=\"" + 500 + "\">" + "all" + "</option>");			
		}
		else
		{
			htmlBuilder.append("<option value=\"" + 500 + "\">" + "all" + "</option>");		
		}
		htmlBuilder.append("</select></span>");
		
		// Search button
		htmlBuilder.append("<button id=\"runButton\" onclick=\"sendSearchEmbryoForm();\">Search</button>");

		// Switch Button
		htmlBuilder.append("<p style=\"text-align:right;\">\n");
		htmlBuilder.append("<a href=\"https://motif.mvls.gla.ac.uk/BeetleAtlas/?page=embryo\">\n");
		htmlBuilder.append("<button class=\"switchButton\">&#8634;</button></a></p>\n");
		
		htmlBuilder.append("</div> <!-- end of controls div -->\n");

		// Build info line to appear above all results
		// div just to act as target
		htmlBuilder.append("<div id=\"mobileTargetE\"></div>\n");
		String revealAllPhrase = "<a href=\"javascript:toggleAll('master'," + totalGenes + ",'&#9658;','&#9660;');\" title=\"show all\"> <span id=\"master\" class=\"infoContent onOff\">&#9655;</span></a>";
		
		// displayMax genes with peak embryo expression at x hours
		if(displayMax==500 || totalGenes < displayMax )
		{
			htmlBuilder.append("<div class=\"explanation2\"> All " + totalGenes + " genes with peak embryo expression at " + time + ":" + revealAllPhrase + " </div><!-- end of explanation div -->\n");
		}
		else
		{
			htmlBuilder.append("<div class=\"explanation2\"> The " + totalGenes + " genes with highest peak embryo expression at " + time + ":" + revealAllPhrase + " </div><!-- end of explanation div -->\n");
		}
		
		boolean conceal = true; 		// whether to provide hide/show button (for multiple results)
		boolean isEmbryoQuery = true;	// present embryo results first
		// Go through each of the Expression objects in list and format results
		for(int i=0; i<totalGenes; i++)
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
		htmlBuilder.append("<div class=\"results\"> <!-- Start of summary section-->\n");	
		htmlBuilder.append("<div class=\"summarySet\">\n<div class=\"summary\">\n");
		htmlBuilder.append("<a href=\"javascript:toggleConcealed('sumBut','summaryList','&#9658;','&#9660;');\" title=\"reveal summary\"><span id=\"sumBut\" class=\"summaryButton onOff\">&#9658;</span></a>\n");
		htmlBuilder.append("</div>");
		if(displayMax==500 || totalGenes < displayMax )
		{
			if(exclusive)
			{
				htmlBuilder.append("<div class=\"summary\">\n<span class=\"summaryTitle\">Summary: All " + totalGenes + 
				" genes expressed only in embryo with peak embryo expression at " + time + " </span>\n</div>\n");
			}
			else
			{
				htmlBuilder.append("<div class=\"summary\">\n<span class=\"summaryTitle\">Summary: All " + totalGenes + 
						" genes with peak embryo expression at " + time + "</span>\n</div>\n");			
			}
		}
		else
		{
			if(exclusive)
			{
				htmlBuilder.append("<div class=\"summary\">\n<span class=\"summaryTitle\">Summary: The " + totalGenes + 
					" genes expressed only in embryo with highest peak expression at " + time + "</span>\n</div>\n");
			}
			else
			{
				htmlBuilder.append("<div class=\"summary\">\n<span class=\"summaryTitle\">Summary: The " + totalGenes + 
						" genes with highest peak embryo expression at " + time + "</span>\n</div>\n");				
			}
		}
		htmlBuilder.append("<div id=\"summaryList\" class=\"conceal\">\n");
		
		for(int i=0; i<totalGenes; i++)
		{
			htmlBuilder.append(geneList[i].getNCBIid() + "<br />");		// 		Add symbol and product and value?
		}
		
		htmlBuilder.append("</div>\n");	
		htmlBuilder.append("</div>\n");	
		htmlBuilder.append("</div><!-- end of summary section-->\n");
			
		// Finish off with footer section	
		htmlBuilder.append(PageUtility.PAGE_FOOT);
	}

	public String getHTML()
	{
		return htmlBuilder.toString();
	}
	
	// Convenience method to convert numerical stage flag into time range
	private String getTime(String stage)
	{
		if(stage.equals("zero"))
		{
			return "0–1 h";
		}
		else if(stage.equals("one"))
		{
			return "1–24 h";
		}
		else if(stage.equals("two"))
		{
			return "24–36 h";
		}
		else if(stage.equals("three"))
		{
			return "36–72 h";
		}
		return stage;
	}

}