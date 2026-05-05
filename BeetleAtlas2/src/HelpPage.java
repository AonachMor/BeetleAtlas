// Class to generate Help Page HTML page
// DPL 14.06.2018
// Last Update 15.03.2021

public class HelpPage
{
	private final int PAGE_POS = PageUtility.DOCS;	// Generally position of page in menu
	private boolean includeErrors = false; 			// show SDs in results
	private boolean showWhole = false;				// show data for Whole Body
	private String intro = "Click or tap on the triangles to expand and collapse the different sections of documentation.";
	
	public HelpPage(boolean includeErrors, boolean showWhole)
	{
		this.includeErrors = includeErrors;
		this.showWhole = showWhole;
		//this.sexStats = sexStats;
	}
	
	public String getHelp()
	{
		PageUtility pu = new PageUtility(includeErrors, showWhole);
		StringBuilder htmlBuilder = new StringBuilder(pu.getPageTop(PAGE_POS));
		
		htmlBuilder.append("<div class=\"explanation\">" + intro + "</div>\n");		
		
		htmlBuilder.append(pu.readHTMLfile("htmlText/help.txt"));
		
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
	
		htmlBuilder.append(PageUtility.PAGE_FOOT);
		return htmlBuilder.toString();
	}
}
