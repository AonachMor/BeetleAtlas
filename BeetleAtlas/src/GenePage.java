// Generates an HTML page for a Gene search, with or without results
// David P. Leader 21.06.2018
// Last updated 22.03.2026

public class GenePage
{
	private StringBuilder htmlBuilder;				// For building HTML
	private final int PAGE_POS = PageUtility.GENE;	// Generally position of page in menu
	private String intro = "For a particular <em>Tribolium</em> gene, find the pattern of expression in different tissues.";		
	
	// Instantiate initial page with no results using defaults
	public GenePage(boolean includeErrors, boolean showWhole)
	{	
		htmlBuilder = new StringBuilder();
		// Build page starting with boiler-plate sections
		PageUtility pu = new PageUtility(includeErrors, showWhole);
		htmlBuilder = new StringBuilder(pu.getPageTop(PAGE_POS));
		htmlBuilder.append("<div class=\"explanation\">" + intro + "</div>\n");
		htmlBuilder.append("<div id=\"controls\">\n");
		
		// IDtype radio choice with geneID selected
		htmlBuilder.append(" • Choose search type, start entering text, then select from the autosuggest menu •<br />\n");
		
		htmlBuilder.append("<input type=\"radio\" name=\"idtype\" id=\"geneID\" checked=\"checked\" /> Gene ID (e.g. TC006446)<br />\n");
		htmlBuilder.append("<input type=\"radio\" name=\"idtype\" id=\"geneSymbol\" /> Gene Symbol (e.g. Hcf)<br />\n");
		htmlBuilder.append("<input type=\"radio\" name=\"idtype\" id=\"geneName\" /> Gene Product (enter partial term like ‘eye’, ‘inositol’)<br />\n");
		htmlBuilder.append("<input type=\"radio\" name=\"idtype\" id=\"ncbiID\" /> NCBI ID (switches modes)<br />\n");
/*		htmlBuilder.append("<a href=\"javascript:window.open('https://motif.mvls.gla.ac.uk/BeetleAtlas2/?page=gene','_self');\" "
				+ "title=\"Open BeetleAtlas in NCBI mode\"><button class=\"fauxRadio\"></button></a> NCBI ID (switches modes)\n");*/
		
		htmlBuilder.append("<p>");
		htmlBuilder.append("<em>or</em> use a <em>Drosophila</em> ID to search for related <em>Tribolium</em> genes<br />\n");
		
		htmlBuilder.append("<input type=\"radio\" name=\"idtype\" id=\"flyFBgn\" /> FlyBase ID (e.g. FBgn0016075)<br />\n");
		htmlBuilder.append("<input type=\"radio\" name=\"idtype\" id=\"flyCG\" /> FlyBase CG number (e.g. CG16858)<br />\n");
		htmlBuilder.append("<input type=\"radio\" name=\"idtype\" id=\"flySymbol\" /> FlyBase Symbol (e.g. vkg)<br />\n");
		htmlBuilder.append("</p>");
		
		// gene descriptor field empty as default
		htmlBuilder.append("<p>\n<span class=\"rightPad5\">Gene: </span>");
		htmlBuilder.append("<input type=\"text\" autocomplete=\"off\" id=\"inputField\" value=\"\" style=\"height:15px;\" onkeyup=\"findNames('gene');\" />");
		htmlBuilder.append("<button onclick=\"sendSearchGeneForm();\">Search</button>\n</p>\n");
			
		// Switch Button
		htmlBuilder.append("<p style=\"text-align:right;\">\n");
		htmlBuilder.append("<a href=\"https://motif.mvls.gla.ac.uk/BeetleAtlas2/?page=gene\">\n");
		htmlBuilder.append("<button class=\"switchButton\">&#8634;</button></a></p>\n");
		
		// Div with hidden table for autocomplete
		htmlBuilder.append(PageUtility.AUTO_DIV);
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
	
	// Instantiate a results page from Expression object, rebuilding  using gene search term, idType (id, symbol, name)
	public GenePage(Gene gene, Expression expression, String searchTerm, String idType, TissueCatalogue  tCat, boolean includeErrors, boolean showWhole)
	{		
		// Build page starting with boiler-plate sections
		PageUtility pu = new PageUtility(includeErrors, showWhole);
		htmlBuilder = new StringBuilder(pu.getPageTop(PAGE_POS));
		
		buildPage(idType, searchTerm);
		
		// RESULTS FORMATTED
		if(expression!=null)
		{
			if(expression instanceof GeneExpression)
			{
				GeneExpression expn = (GeneExpression) expression;
				int resNum = 0;					// for id
				boolean conceal = false; 		// whether to provide hide/show button (for multiple results)
				boolean isEmbryoQuery = false;	// present embryo results first
				GeneResult gr = new GeneResult(gene, tCat, expn, resNum, conceal, isEmbryoQuery, includeErrors, showWhole);
				htmlBuilder.append(gr.getResultsHTML());
			}		
		}
		else
		{
			htmlBuilder.append("<div class=\"explanation2\">");
			htmlBuilder.append("No results found for ‘" + searchTerm +"’.");
			// Attempt smart feedback
			if(idType.equals("geneID") && searchTerm.length()>1 && !searchTerm.substring(0,2).equals("TC"))
			{
				htmlBuilder.append("<br />Gene IDs start with ‘TC’. Please select the appropriate search type.");
			}
			else if(idType.equals("geneID") && searchTerm.length()<2)
			{
				htmlBuilder.append("<br />Gene IDs start with ‘TC’. Please select the appropriate search type.");
			}
			else
			{
				htmlBuilder.append("<br /> Was the appropriate search type selected? Did the query term autocomplete?");
			}
			htmlBuilder.append("</div><!-- end of explanation div -->");
		}	

		// Finish off with footer section
		htmlBuilder.append(PageUtility.PAGE_FOOT);
	}
	
	// Instantiate a results page from Expression array in case of an initial Drosophila query, Tribolium product or Paralogue query
	public GenePage(Gene[] geneList, Expression[] expressList, String searchTerm, String idType, TissueCatalogue  tCat, boolean includeErrors, boolean showWhole, int listSize)
	{
		// Build page starting with boiler-plate sections
		PageUtility pu = new PageUtility(includeErrors, showWhole);
		htmlBuilder = new StringBuilder(pu.getPageTop(PAGE_POS));
		
		buildPage(idType, searchTerm);
		
		// RESULTS FORMATTED	
		if (listSize > 0)
		{
			if(idType.equals("flyFBgn") || idType.equals("flyCG") || idType.equals("flySymbol"))
			{		
				if(listSize == 1)
				{
					htmlBuilder.append("<div class=\"explanation2\"> Results for single <em>Tribolium</em> gene listed as related to <em>Drosophila</em> " + searchTerm + ":</div><!-- end of explanation div -->\n");
				}
				else
				{
					String revealAllPhrase = "<a href=\"javascript:toggleAll('master'," + listSize + ",'&#9658;','&#9660;');\" title=\"show all\"> <span id=\"master\" class=\"infoContent onOff\">&#9655;</span></a>";
					htmlBuilder.append("<div class=\"explanation2\"> Results for " + listSize + " <em>Tribolium</em> genes listed as related to <em>Drosophila</em> " + searchTerm + ":"+ revealAllPhrase +"</div><!-- end of explanation div -->\n");
				}
			}
			else if(idType.equals("geneID"))
			{
				if(listSize > 1)
				{
					String revealAllPhrase = "<a href=\"javascript:toggleAll('master'," + listSize + ",'&#9658;','&#9660;');\" title=\"show all\"> <span id=\"master\" class=\"infoContent onOff\">&#9655;</span></a>";
					htmlBuilder.append("<div class=\"explanation2\"> ‘" + searchTerm + "’ and paralogues:"+ revealAllPhrase + "</div><!-- end of explanation div -->\n");							
				}
			}
			else
			{
				if(listSize > 1)
				{
					String revealAllPhrase = "<a href=\"javascript:toggleAll('master'," + listSize + ",'&#9658;','&#9660;');\" title=\"show all\"> <span id=\"master\" class=\"infoContent onOff\">&#9655;</span></a>";
					htmlBuilder.append("<div class=\"explanation2\"> Results for genes with product name ‘" + searchTerm + "’:"+ revealAllPhrase + "</div><!-- end of explanation div -->\n");							
				}
			}
			
			for(int i=0; i<listSize; i++)
			{
				GeneExpression express = (GeneExpression) expressList[i];
				Gene gene = geneList[i];	
				GeneResult gr = new GeneResult(gene, tCat, express, i, true, false, includeErrors, showWhole);
				htmlBuilder.append(gr.getResultsHTML());
			}
		}
		else
		{
			htmlBuilder.append("<div class=\"explanation2\">");
			
			if(idType.equals("geneName"))
			{
				htmlBuilder.append("No results found for ‘" + searchTerm +"’.");
			}
			else
			{
				htmlBuilder.append("No <em>Tribolium</em> gene listed as corresponding to <em>Drosophila</em> gene ‘" + searchTerm +"’.");
				// Attempt smart feedback
				if(idType.equals("flyFBgn") && searchTerm.length()>3 && !searchTerm.substring(0,4).equals("FBgn"))
				{
					htmlBuilder.append("<br />FlyBase IDs are of the type ‘FBgn + 7 digits’. Please select the appropriate search type.");			
				}
				else if(idType.equals("flyFBgn") && searchTerm.length()<4)
				{
					htmlBuilder.append("<br />FlyBase IDs are of the type ‘FBgn + 7 digits’. Please select the appropriate search type.");			
				}
				else if(idType.equals("flyFBgn") && searchTerm.length()>3 && searchTerm.length()!=11 && searchTerm.substring(0,4).equals("FBgn"))
				{
					htmlBuilder.append("<br />FlyBase IDs are of the type ‘FBgn + 7 digits’. Please check the ID you entered.");			
				}
				else if(idType.equals("flyCG") && searchTerm.length()>1 && !searchTerm.substring(0,2).equals("CG"))
				{
					htmlBuilder.append("<br />FlyBase CG numbers are of the type ‘CG + several digits’. Please select the appropriate search type.");			
				}
				else if(idType.equals("flyCG") && searchTerm.length()<2)
				{
					htmlBuilder.append("<br />FlyBase CG numbers are of the type ‘CG + several digits’. Please select the appropriate search type.");
				}
			}
			htmlBuilder.append("</div><!-- end of explanation div -->");
		}
		
		// Finish off with footer section
		htmlBuilder.append(PageUtility.PAGE_FOOT);		
	}
	
	// Builds bulk of page before results — same for Tribolium or Drosophila search
	private void buildPage(String idType, String searchTerm)
	{
		htmlBuilder.append("<div class=\"explanation\">" + intro + "</div>\n");
		htmlBuilder.append("<div id=\"controls\">\n");
		
		// idtype radio choice 	— set in tribolium.js
		htmlBuilder.append(" • Choose search type, start entering text, then select from the autosuggest menu •<br />\n");
		
		if(idType.equals("geneID"))
		{
			htmlBuilder.append("<input type=\"radio\" name=\"idtype\" id=\"geneID\" checked=\"checked\" /> Gene ID (e.g. TC006446)<br />\n");
		}
		else
		{
			htmlBuilder.append("<input type=\"radio\" name=\"idtype\" id=\"geneID\" /> Gene ID (e.g. TC006446)<br />\n");
		}
		
		if(idType.equals("geneSymbol"))
		{
			htmlBuilder.append("<input type=\"radio\" name=\"idtype\" id=\"geneSymbol\" checked=\"checked\" /> Gene Symbol (e.g. Hcf)<br />\n");
		}
		else
		{
			htmlBuilder.append("<input type=\"radio\" name=\"idtype\" id=\"geneSymbol\" /> Gene Symbol (e.g. Hcf)<br />\n");
		}
		
		if(idType.equals("geneName"))
		{
			htmlBuilder.append("<input type=\"radio\" name=\"idtype\" id=\"geneName\" checked=\"checked\" /> Gene Product (enter partial term like ‘eye’, ‘inositol’)<br />\n");
		}
		else
		{
			htmlBuilder.append("<input type=\"radio\" name=\"idtype\" id=\"geneName\" /> Gene Product (enter partial term like ‘eye’, ‘inositol’)<br />\n");
		}
		
		htmlBuilder.append("<input type=\"radio\" name=\"idtype\" id=\"ncbiID\" /> NCBI ID (switches modes)<br />\n");
		
/*		htmlBuilder.append("<a href=\"javascript:window.open('https://motif.mvls.gla.ac.uk/BeetleAtlas2/?page=gene','_self');\" "
				+ "title=\"Open BeetleAtlas in NCBI mode\"><button class=\"fauxRadio\"></button></a> NCBI ID (switches modes)\n");*/
		
		htmlBuilder.append("<p>");
		
		htmlBuilder.append("<em>or</em> use a <em>Drosophila</em> ID to search for related <em>Tribolium</em> genes<br />\n");
		
		if(idType.equals("flyFBgn"))
		{
			htmlBuilder.append("<input type=\"radio\" name=\"idtype\" id=\"flyFBgn\" checked=\"checked\" /> FlyBase ID (e.g. FBgn0016075)<br />\n");
		}
		else
		{
			htmlBuilder.append("<input type=\"radio\" name=\"idtype\" id=\"flyFBgn\" /> FlyBase ID (e.g. FBgn0016075)<br />\n");
		}
		
		if(idType.equals("flyCG"))
		{
			htmlBuilder.append("<input type=\"radio\" name=\"idtype\" id=\"flyCG\" checked=\"checked\" /> FlyBase CG number (e.g. CG16858)<br />\n");
		}
		else
		{
			htmlBuilder.append("<input type=\"radio\" name=\"idtype\" id=\"flyCG\" /> FlyBase CG number (e.g. CG16858)<br />\n");
		}
		
		if(idType.equals("flySymbol"))
		{
			htmlBuilder.append("<input type=\"radio\" name=\"idtype\" id=\"flySymbol\" checked=\"checked\" /> FlyBase Symbol (e.g. vkg)<br />\n");
		}
		else
		{
			htmlBuilder.append("<input type=\"radio\" name=\"idtype\" id=\"flySymbol\" /> FlyBase Symbol (e.g. vkg)<br />\n");
		}
		
		htmlBuilder.append("</p>");
		
		// gene descriptor field with previous choice — modified so id is inputField
		htmlBuilder.append("<p><span class=\"rightPad5\">Gene: </span>");
		htmlBuilder.append("<input type=\"text\" autocomplete=\"off\" size=\"40\" id=\"inputField\" value=\"" + searchTerm + "\" style=\"height:15px;\" onkeyup=\"findNames('gene');\" />");		
		htmlBuilder.append("<button onclick=\"sendSearchGeneForm();\">Search</button>\n</p>\n");
		
		// Switch Button
		htmlBuilder.append("<p style=\"text-align:right;\">\n");
		htmlBuilder.append("<a href=\"https://motif.mvls.gla.ac.uk/BeetleAtlas2/?page=gene\">\n");
		htmlBuilder.append("<button class=\"switchButton\">&#8634;</button></a></p>\n");
		
		// add anatomy graphic hide/show		
		htmlBuilder.append(PageUtility.getAnatomyLink());
			
		// Div with hidden table for autocomplete
		htmlBuilder.append(PageUtility.AUTO_DIV);
		htmlBuilder.append("</div> <!-- end of controls div -->\n");
		
		// div just to act as target
		htmlBuilder.append("<div id=\"mobileTargetG\"></div>\n");
	}
	
	// returns page
	public String getHTML()
	{
		return htmlBuilder.toString();
	}

}
