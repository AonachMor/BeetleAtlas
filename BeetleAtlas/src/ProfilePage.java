// Generates an HTML page for a Profile search, with or without results
// David P. Leader 24.02.2021 removed embryo inclusion option
// Last update 22.03.2026

public class ProfilePage
{
	private StringBuilder htmlBuilder;							// For building HTML
	private final int PAGE_POS = PageUtility.PROFILE;			// Generally position of page in menu
	private String intro = "For a particular <em>Tribolium</em> gene, find others with a similar profile of expression across tissues.";		

	private String rList[] = {"0.50","0.55","0.60","0.65","0.70","0.75","0.80","0.85","0.90"};	// cutoff values for r statistic
	final String DEFAULT_R = "0.70";								// default value of rString
	final int DEFAULT_DISPLAY_MAX = 20;
	
	// Instantiate initial page with no results using defaults
	public ProfilePage(boolean includeErrors, boolean showWhole)
	{			
		htmlBuilder = new StringBuilder();
		// Build page starting with boiler-plate sections
		PageUtility pu = new PageUtility(includeErrors, showWhole);
		htmlBuilder = new StringBuilder(pu.getPageTop(PAGE_POS));
		htmlBuilder.append("<div class=\"explanation\">" + intro + "</div>\n");
		htmlBuilder.append("<div id=\"controls\">\n");
		
		// IDtype radio choice with geneID selected
		htmlBuilder.append("<span class=\"mobileHide\">Choose search type before entering ID or symbol</span><br />\n");	
		htmlBuilder.append("<input type=\"radio\" name=\"idtype\" id=\"geneID\" checked=\"checked\" /> Gene ID (e.g. TC000470)<br />\n");
		htmlBuilder.append("<input type=\"radio\" name=\"idtype\" id=\"geneSymbol\" /> Gene Symbol (e.g. Sobp)<br />\n");
		
/*		htmlBuilder.append("<a href=\"javascript:window.open('https://motif.mvls.gla.ac.uk/BeetleAtlas2/?page=profile','_self');\" "
				+ "title=\"Open BeetleAtlas in NCBI mode\"><button class=\"fauxRadio\"></button></a> NCBI ID (switches modes)<br />\n");*/
		
		// line for Pearson/Spearman and...
		htmlBuilder.append("<br />Correlation statistic and stringency (Defaults advised initially)<br />\n");
		htmlBuilder.append("<input type=\"radio\" name=\"correlation\" id=\"pearson\" checked=\"checked\" /> Pearson ");
		htmlBuilder.append("<input type=\"radio\" name=\"correlation\" id=\"spearman\" /> Spearman\n");		
		// ...and rcut options
		htmlBuilder.append("<span class=\"leftPadSelect\">r &gt; </span><select name=\"rcut\" id=\"rcut\">\n");
		for(int i=0; i<rList.length; i++)
		{
			String cutoff = rList[i];
			if(cutoff.equals(DEFAULT_R))
			{
				htmlBuilder.append("<option selected=\"selected\" value=\"" + cutoff + "\">" + cutoff + "</option>\n");					
			}
			else
			{
				htmlBuilder.append("<option value=\"" + cutoff + "\">" + cutoff + "</option>\n");
			}
		}
		htmlBuilder.append("</select><br />\n");	// finish off select	
		
		// gene descriptor field empty as default
		htmlBuilder.append("<p>\n<span class=\"rightPad5\">Gene: </span>");
		htmlBuilder.append("<input type=\"text\" autocomplete=\"off\" id=\"inputField\" value=\"\" style=\"height:15px;\" onkeyup=\"findNames('gene');\" />");				
		// max choice (20 to 50) or 100
		htmlBuilder.append("<span class=\"immobileHideS\"></span><span style=\"white-space:nowrap\">Display:&nbsp;<select name=\"maxdisplayed\" id=\"maxdisplayed\" dir=\"rtl\">");
		for(int i=20; i<60; i+=10)
		{
			if(i==DEFAULT_DISPLAY_MAX)
			{
				htmlBuilder.append("<option selected=\"selected\" value=\"" + i + "\">" + i + "</option>");			
			}
			else
			{
				htmlBuilder.append("<option value=\"" + i + "\">" + i + "</option>");		
			}
		}
		htmlBuilder.append("<option value=\"" + 100 + "\">" + "100" + "</option>");		
		htmlBuilder.append("</select></span>");				
		// search button
		htmlBuilder.append("<button onclick=\"sendSearchProfileForm();\">Search</button>\n</p>\n");		
		
		// Switch Button
		htmlBuilder.append("<p style=\"text-align:right;\">\n");
		htmlBuilder.append("<a href=\"https://motif.mvls.gla.ac.uk/BeetleAtlas2/?page=profile\">\n");
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
	
	public ProfilePage(String searchTerm, String idType, String searchGeneID, Gene searchGene, Expression searchEx, Expression[] expressList, Gene[] geneList, ProfileTissueData[] profileDataList, 
							int displayMax, TissueCatalogue tCat, boolean byPearson, boolean aleProfile, String rString, boolean includeErrors, boolean showWhole)
	{		
		// Build page starting with boiler-plate sections
		PageUtility pu = new PageUtility(includeErrors, showWhole);
		htmlBuilder = new StringBuilder(pu.getPageTop(PAGE_POS));
		
		htmlBuilder.append("<div class=\"explanation\">" + intro + "</div>\n");
		htmlBuilder.append("<div id=\"controls\">\n");
		
		// IDtype radio choice with previous choice selected
		htmlBuilder.append("<span class=\"mobileHide\">Choose search type before entering ID or symbol</span><br />\n");	
		if(idType.equals("geneID"))
		{
			htmlBuilder.append("<input type=\"radio\" name=\"idtype\" id=\"geneID\" checked=\"checked\" /> Gene ID (e.g. TC000470)<br />\n");
			htmlBuilder.append("<input type=\"radio\" name=\"idtype\" id=\"geneSymbol\" /> Gene Symbol (e.g. Sobp)<br />\n");
/*			htmlBuilder.append("<a href=\"javascript:window.open('https://motif.mvls.gla.ac.uk/BeetleAtlas2/?page=profile','_self');\" "
					+ "title=\"Open BeetleAtlas in NCBI mode\"><button class=\"fauxRadio\"></button></a> NCBI ID (switches modes)<br />\n");*/
		}
		else
		{
			htmlBuilder.append("<input type=\"radio\" name=\"idtype\" id=\"geneID\" /> Gene ID (e.g. TC000470)<br />\n");
			htmlBuilder.append("<input type=\"radio\" name=\"idtype\" id=\"geneSymbol\" checked=\"checked\" /> Gene Symbol (e.g. Sobp)<br />\n");
/*			htmlBuilder.append("<a href=\"javascript:window.open('https://motif.mvls.gla.ac.uk/BeetleAtlas2/?page=profile','_self');\" "
					+ "title=\"Open BeetleAtlas in NCBI mode\"><button class=\"fauxRadio\"></button></a> NCBI ID (switches modes)<br />\n");*/
		}
		
		// line for Pearson/Spearman and...
		htmlBuilder.append("<br />Correlation statistic and stringency (Defaults advised initially)<br />\n");
		if(byPearson)
		{
			htmlBuilder.append("<input type=\"radio\" name=\"correlation\" id=\"pearson\" checked=\"checked\" /> Pearson ");
			htmlBuilder.append("<input type=\"radio\" name=\"correlation\" id=\"spearman\" /> Spearman\n");		
		}
		else
		{
			htmlBuilder.append("<input type=\"radio\" name=\"correlation\" id=\"pearson\" /> Pearson ");
			htmlBuilder.append("<input type=\"radio\" name=\"correlation\" id=\"spearman\" checked=\"checked\" /> Spearman\n");				
		}
		// ...and rcut options
		htmlBuilder.append("<span class=\"leftPadSelect\">r &gt; </span><select name=\"rcut\" id=\"rcut\">\n");
		for(int i=0; i<rList.length; i++)
		{
			String cutoff = rList[i];
			if(cutoff.equals(rString))
			{
				htmlBuilder.append("<option selected=\"selected\" value=\"" + cutoff + "\">" + cutoff + "</option>\n");					
			}
			else
			{
				htmlBuilder.append("<option value=\"" + cutoff + "\">" + cutoff + "</option>\n");
			}
		}
		htmlBuilder.append("</select><br>\n");	// finish off select	
		// gene descriptor field with previous query
		htmlBuilder.append("<p>\n<span class=\"rightPad5\">Gene: </span>");
		htmlBuilder.append("<input type=\"text\" autocomplete=\"off\" id=\"inputField\" value=\"" + searchTerm + "\" style=\"height:15px;\" onkeyup=\"findNames('gene');\" />");				
		// max choice (20 to 50) or 100
		htmlBuilder.append("<span class=\"immobileHideS\"></span><span style=\"white-space:nowrap\">Display:&nbsp;<select name=\"maxdisplayed\" id=\"maxdisplayed\" dir=\"rtl\">");
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
		if(displayMax==100)
		{
			htmlBuilder.append("<option selected=\"selected\" value=\"" + 100 + "\">" + "100" + "</option>");	
		}
		else
		{
			htmlBuilder.append("<option value=\"" + 100 + "\">" + "100" + "</option>");				
		}
		htmlBuilder.append("</select></span>");				
		// search button
		htmlBuilder.append("<button onclick=\"sendSearchProfileForm();\">Search</button>\n</p>\n");
		
		// Switch Button
		htmlBuilder.append("<p style=\"text-align:right;\">\n");
		htmlBuilder.append("<a href=\"https://motif.mvls.gla.ac.uk/BeetleAtlas2/?page=profile\">\n");
		htmlBuilder.append("<button class=\"switchButton\">&#8634;</button></a></p>\n");
		
		// add anatomy graphic hide/show		
		htmlBuilder.append(PageUtility.getAnatomyLink());
		// Div with hidden table for autocomplete
		htmlBuilder.append(PageUtility.AUTO_DIV);
		htmlBuilder.append("</div> <!-- end of controls div -->\n");
		
		// hidden errors checkbox
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
				
		// RESULTS FORMATTED
		
		// div just to act as target
		htmlBuilder.append("<div id=\"mobileTargetP\"></div>\n");
		
		if(expressList!=null)
		{	
			int listSize = profileDataList.length;
			if(listSize > 0)
			{
				boolean conceal = true; 		// need to provide hide/show button for multiple results
				
				// Query Gene
				htmlBuilder.append("<div class=\"explanation2\">Query Gene (reference) </div><!-- end of explanation div -->\n");					
				int resNum = -1;
				GeneExpression qyExpress = (GeneExpression) searchEx;
				GeneResult grQy = new GeneResult(searchGene, tCat, qyExpress, resNum, conceal, includeErrors, showWhole);
				htmlBuilder.append(grQy.getResultsHTML());
				
				int actualDisplayed = 0;
				if(displayMax > listSize) 
				{ actualDisplayed = listSize;}
				else
				{ actualDisplayed = displayMax;}
				
				String revealAllPhrase = "<a href=\"javascript:toggleAll('master'," + actualDisplayed + ",'&#9658;','&#9660;');\" title=\"show all\"> <span id=\"master\" class=\"infoContent onOff\">&#9655;</span></a>";
				
				// Report summary
				if(idType.equals("geneID"))
				{
					htmlBuilder.append("<div class=\"explanation2\">" + actualDisplayed + " of " + listSize + " profile(s) resembling ‘" + searchTerm +  "’ at <em>r≥</em>" 
									+ rString + ", <em>P<sub>B</sub></em>≤0.05:" + revealAllPhrase + "</div><!-- end of explanation div -->\n");	
				}
				else
				{
					htmlBuilder.append("<div class=\"explanation2\" style=\"padding-bottom:0;margin-bottom:-20px;\">" + actualDisplayed + " of " + listSize + " profile(s) resembling ‘" + searchTerm + "’ (" + searchGeneID + ") " + " at <em>r≥</em>" 
							+ rString + ", <em>P<sub>B</sub></em>≤0.05:" + revealAllPhrase + "</div><!-- end of explanation div -->\n");					
				}

				// Go through each of the Expression objects in list and format results
				for(int i=0; i<actualDisplayed; i++)
				{
					GeneExpression express = (GeneExpression) expressList[i];
					Gene gene = geneList[i];	
					double rStat = profileDataList[i].getRstat();
					double pStat = profileDataList[i].getPstat();
					if(expressList[i]!=null)
					{	
						GeneResult gr = new GeneResult(gene, tCat, express, i, conceal, includeErrors, showWhole, rStat, pStat);
						htmlBuilder.append(gr.getResultsHTML());
					}
					else
					{
						System.out.println("expressList[" + i + "] is null");
					}
				}	

				// Summary section				
				htmlBuilder.append("<div class=\"results\"> <!-- Start of summary section-->\n");	
				htmlBuilder.append("<div class=\"summarySet\">\n<div class=\"summary\">\n");
				htmlBuilder.append("<a href=\"javascript:toggleConcealed('sumBut','summaryList','&#9658;','&#9660;');\" title=\"reveal summary\"><span id=\"sumBut\" class=\"summaryButton onOff\">&#9658;</span></a>\n");
				htmlBuilder.append("</div>");
				htmlBuilder.append("<div class=\"summary\">\n<span class=\"summaryTitle\">Summary: " + actualDisplayed + 
						" genes with expression profiles resembling " + searchGeneID + "</span>\n</div>\n");
				htmlBuilder.append("<div id=\"summaryList\" class=\"conceal\">\n");
				
				htmlBuilder.append(searchGeneID + "<br />");
				for(int i=0; i<actualDisplayed; i++)
				{
					htmlBuilder.append(geneList[i].getGeneID() + "<br />");		// 		Add symbol and product?
				}
				
				htmlBuilder.append("</div>\n");	
				htmlBuilder.append("</div>\n");	
				htmlBuilder.append("</div><!-- end of summary section-->\n");	
			}
			else
			{
				htmlBuilder.append("<div class=\"explanation2\">");
				htmlBuilder.append("No profiles resembling ‘" + searchTerm +  "’at <em>r≥</em>" + rString + " <em>P<sub>B</sub></em>≤0.05.");
				htmlBuilder.append("</div><!-- end of explanation div -->");
			}
		
		}
		else
		{
			htmlBuilder.append("<div class=\"explanation2\">");
			htmlBuilder.append("No results found for ‘" + searchTerm +"’.");
			htmlBuilder.append("</div><!-- end of explanation div -->");
		}

		// Finish off with footer section
		htmlBuilder.append(PageUtility.PAGE_FOOT);
	}
	
	// returns page
	public String getHTML()
	{
		return htmlBuilder.toString();
	}

}
