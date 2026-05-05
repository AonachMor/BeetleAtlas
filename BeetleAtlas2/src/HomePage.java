// Class to generate HomePage HTML page
// DPL 23.06.2018
// Latest update 15.03.2021

public class HomePage
{
	private final int PAGE_POS = PageUtility.HOME;	// Generally position of page in menu — must be 0 for HomePage
	private boolean includeErrors = false; 			// show SDs in results
	private boolean showWhole = false;				// show data for Whole Body	
	
	public HomePage(boolean includeErrors, boolean showWhole)
	{
		this.includeErrors = includeErrors;
		this.showWhole = showWhole;
	}
	
	public String getHome()
	{
		PageUtility pu = new PageUtility(includeErrors, showWhole);
		StringBuilder htmlBuilder = new StringBuilder(pu.getPageTop(PAGE_POS));
		htmlBuilder.append(pu.readHTMLfile("htmlText/home.txt"));
		
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
		
		htmlBuilder.append(PageUtility.PAGE_FOOT_HOME);
		return htmlBuilder.toString();
	}
}
