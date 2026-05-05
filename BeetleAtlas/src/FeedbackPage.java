// Class to generate FeedBack HTML page
// Last Update DPL 15.03.2021

public class FeedbackPage
{
	private final int PAGE_POS = PageUtility.FEEDBACK;	// Generally position of page in menu
	private boolean includeErrors = false; 				// show SDs in results
	private boolean showWhole = false;					// show data for Whole Body
	
	public FeedbackPage(boolean includeErrors, boolean showWhole)
	{
		this.includeErrors = includeErrors;
		this.showWhole = showWhole;
	}	
	
	public String getFeedback()
	{
		PageUtility pu = new PageUtility(includeErrors, showWhole);
		StringBuilder htmlBuilder = new StringBuilder(pu.getPageTop(PAGE_POS));
		
		htmlBuilder.append(pu.readHTMLfile("htmlText/feedbackForm.txt"));
		
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
