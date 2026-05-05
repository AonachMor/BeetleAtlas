 /*
Class to hold HTML code and write specific HTML menus 
for different pages of BeetleAtlas
Also contains various other utilities, most of which can be accessed in a static manner
DPL 10.10.2019
Last Updated: 22.03.2026
*/

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.NumberFormat;

public class PageUtility
{	
	final static  String PAGE_HEAD = 
		"<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n"
		+ "<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\" lang=\"en\">\n"
		+ "<head>\n"		
		+ "<!-- Google tag (gtag.js) -->"
		+ "<script async src=\"https://www.googletagmanager.com/gtag/js?id=G-XXY8P6ZWSG\"></script>"
		+ "<script>"
		+ "window.dataLayer = window.dataLayer || [];"
		+ "function gtag(){dataLayer.push(arguments);}"
		+ "gtag('js', new Date());"		
		+ "gtag('config', 'G-XXY8P6ZWSG');"
		+ "</script>"
		+ "<!-- end of Google tag -->"		
		+ "<title>BeetleAtlas: The Tribolium Gene Expression Atlas</title>\n<meta http-equiv=\"content-type\" content=\"text/html; charset=UTF-8\" />\n"
		+ "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, maximum-scale=1.0\" />\n"		
		+ "<meta name=\"description\" content=\"Database of gene expression in the tissues of Tribolium castaneum, the flour beetle\" />\n"
		+ "<meta name=\"keywords\" content=\"Tribolium castaneum, flour beetle, gene, transcript, RNAseq, tissue-specific, database\" />\n"		
		+ "<script type=\"text/javascript\" src=\"scripts/tribolium.js\"></script>\n"
		+ "<script type=\"text/javascript\" src=\"scripts/highlight.js\"></script>\n"
		+ "<script type=\"text/javascript\" src=\"scripts/beetleAuto.js\"></script>\n"
		+ "<script type=\"text/javascript\" src=\"scripts/beetleMenu.js\"></script>\n"
		+ "<script type=\"text/javascript\" src=\"scripts/drag.js\"></script>\n"
		+ "<link rel=\"stylesheet\" type=\"text/css\" href=\"scripts/tribolium.css\" />\n"
		+ "<link rel=\"stylesheet\" type=\"text/css\" href=\"scripts/beetleAuto.css\" />\n"
		+ "<link rel=\"stylesheet\" href=\"https://fonts.googleapis.com/css?family=Archivo\" />\n"
		+ "<link rel=\"stylesheet\" href=\"https://fonts.googleapis.com/css?family=Archivo+Narrow\" />\n"	
		+ "<link rel=\"stylesheet\" href=\"https://fonts.googleapis.com/css?family=Abril+Fatface\" />\n"	
		+ "<link rel=\"shortcut icon\" type=\"image/x-icon\" href=\"images/beetle.ico\" />\n"
		+ "<link rel=\"apple-touch-icon\" href=\"images/beetle-touch-icon-57x57.png\" />\n"
		+ "<link rel=\"apple-touch-icon\" href=\"images/beetle-touch-icon-72x72.png\" />\n"
		+ "<link rel=\"apple-touch-icon\" href=\"images/beetle-touch-icon-114x114.png\" />\n"
		+ "<link rel=\"apple-touch-icon\" href=\"images/beetle-touch-icon-144x144.png\" />\n"
		+ "</head>\n\n";
	
	// utility boilerplate
	final static String AUTO_DIV =		
		"<div style=\"position:absolute;\" id=\"popup\">\n"
		+ "<table id=\"menuTable\" cellspacing=\"0\" cellpadding=\"0\">\n"           
		+ "<tbody id=\"menuTableBody\"><tr><td></td></tr></tbody>\n"
		+ "</table>\n</div><!-- end of autocomplete div -->\n";
	
	final static String PAGE_FOOT_HOME = 
			"\n<div style=\"float: right; padding-top:0; width: 46%; text-align: right;\">\n" 
			+ "<img class=\"mobileHide shrinkfit\" src=\"images/velux.png\" alt=\"\" title=\"Funded by the Velux Foundations\" />\n</div>\n" 
			+ "<div style=\"float: right; padding-top: 25px; width: 20%; text-align: center;\">\n" 
			+ "<img class=\"mobileHide shrinkfit\" src=\"images/UofGs.jpg\"  alt=\"\" title=\"University of Glasgow\" />\n</div>\n" 	
			+ "<div style=\"float: left; padding-top: 20px; width: 33%; text-align: left;\">\n" 
			+ "<img class=\"mobileHide shrinkfit\" src=\"images/UofC.png\" alt=\"\" title=\"University of Copenhagen\" />\n</div>\n" 	
			+ "<div style=\"clear: both;\"></div>\n" 
			+ "</div> <!-- end of bottomWrapper div -->\n" 
			+ "</body>\n</html>\n";
	
	final static String PAGE_FOOT = 
			"\n</div> <!-- end of bottomWrapper div -->\n"
			+ "</body>\n</html>\n";
	
	
	// Constants to allow safer comparisons
	final static String ADULT = "Adult";
	final static String LARVAL = "Larval";
	
	final static int HOME = 0;
	final static int GENE = 1;
	final static int TISSUE = 2;
	final static int CATEGORY = 3;
	final static int DEVELOPMENT = 4;
	final static int EMBRYO = 5;
	final static int PROFILE = 6;
	final static int DOCS = 7;
	final static int FEEDBACK = 8;
	
	private static PageDescriptor pageList[];			// list of page descriptor objects
	private final static int LENGTH = 9;				// Number of pages INCREMENT WHEN ADD NEW PAGE!

	public PageUtility(boolean includeErrors, boolean showWhole)
	{
		pageList = new PageDescriptor[LENGTH];
		initializePageList(includeErrors, showWhole);
	}
	
	///////////////////////////////////////////////
	
	// initializes array of PageDescriptors	— NB UPDATE pageList SIZE WHEN ADD NEW PAGE!
	private void initializePageList(boolean includeErrors, boolean showWhole)
	{ 
		pageList[HOME] = new PageDescriptor(HOME, "Home", "toHomeForm()", "<body>\n", false);		
		pageList[GENE] = new PageDescriptor(GENE, "Gene", "toGeneForm()", "<body onload=\"setSpan('.plusMinus'," + includeErrors + "); setRow('wholesome'," + showWhole + "); setFocus(); setHash('mobileTargetG'); createLink();\" onkeypress=\"geneKey(event);\">\n", true);
		pageList[TISSUE] = new PageDescriptor(TISSUE, "Tissue", "toTopForm()", "<body onload=\"setSpan('.plusMinus'," + includeErrors + "); setRow('wholesome'," + showWhole + "); setHash('mobileTargetT'); createLink();\" onkeypress=\"topKey(event);\">\n", true);
		pageList[CATEGORY] = new PageDescriptor(CATEGORY, "Category", "toGOForm()", "<body onload=\"setSpan('.plusMinus'," + includeErrors + "); setRow('wholesome'," + showWhole + "); setFocus(); setHash('mobileTargetC'); createLink();\" onkeypress=\"goKey(event);\">\n", true);
		pageList[DEVELOPMENT] = new PageDescriptor(DEVELOPMENT, "Adult/Larva", "toDevelForm()", "<body onload=\"setSpan('.plusMinus'," + includeErrors + "); setRow('wholesome'," + showWhole + "); setFocus(); setHash('mobileTargetD'); createLink();\" onkeypress=\"develKey(event);\">\n", true);		
		pageList[EMBRYO] = new PageDescriptor(EMBRYO, "Embryo", "toEmbryoForm()", "<body onload=\"setSpan('.plusMinus'," + includeErrors + "); setRow('wholesome'," + showWhole + "); setFocus(); setHash('mobileTargetE');\" onkeypress=\"embryoKey(event);\">\n", true);		
		pageList[PROFILE] = new PageDescriptor(PROFILE, "Profile", "toProfileForm()", "<body onload=\"setSpan('.plusMinus'," + includeErrors + "); setRow('wholesome'," + showWhole + "); setFocus(); setHash('mobileTargetP'); createLink();\" onkeypress=\"profileKey(event);\">\n", true);		
		pageList[DOCS] = new PageDescriptor(DOCS, "Docs", "toHelpForm()", "<body>\n", false);							
		pageList[FEEDBACK] = new PageDescriptor(FEEDBACK, "Feedback", "toFeedbackForm()", "<body>\n", false);
	}
	
	// inner class to hold a utility pageDescriptor object	
	class PageDescriptor
	{
		int pagePos;				// order of page in menu 0 to n
		String pageName;			// name of page as it appears on the menu
		String toMethodName;		// name of javascript method to generate new page	
		String bodyLine;			// html <body> line - differs depending on javascript
		boolean scroll;				// whether or not back-to-top function required
		static final String scrollText = "<button onclick=\"topFunction()\" id=\"upButton\" title=\"Go to top\">&#8963;</button>" + 
										"\n<script>window.onscroll = function() {scrollFunction()};</script>\n";
		String scrollLine = "";		// text
		PageDescriptor(int pagePos, String pageName, String toMethodName, String bodyLine, boolean scroll)
		{
			this.pagePos = pagePos;
			this.pageName = pageName;
			this.toMethodName = toMethodName;
			this.bodyLine = bodyLine;
			if(scroll==true)
			{
				scrollLine = scrollText;
			}
		}
		public int getPagePos()
		{
			return pagePos;
		}
		public String getPageName()
		{
			return pageName;
		}
		public String getToMethodName()
		{
			return toMethodName;
		}
		public String getBodyLine()
		{
			return bodyLine;
		}
		public String getScrollLine()
		{
			return scrollLine;
		}
	}
	
	///////////////////////////////////////////////
	
			// Static ACCESSOR method (depends on PageList being established) //
	
	// builds top section of html page with appropriate names and links
	// this is a mess at the moment as I don't want to renumber the pages — should try to change to the new for statement
	public String getPageTop(int pagePos)
	{
		// Set up boolean for Home Page as this can differ
		boolean isHome = false;
		if(pageList[0].getPagePos() == pagePos)
		{ isHome = true;}
		
		StringBuilder pBuilder = new StringBuilder(PAGE_HEAD);		// boiler plate html head
		pBuilder.append(pageList[pagePos].getBodyLine());			// <body> line
		pBuilder.append(pageList[pagePos].getScrollLine());			// scroll lines
		pBuilder.append("<div id=\"topWrapper\">\n");						
		pBuilder.append("<div id=\"upper\">\n");					// upper area containing title and menu for normal view

		// start NAV DIV
		if(isHome)
		{
			pBuilder.append("<div id=\"navI\">\n");		
		}
		else
		{
			pBuilder.append("<div id=\"nav\">\n");
		}
		pBuilder.append("<ul>\n");
		
		// Write Nav section. The order here determines the order on the page, irrespective of the int values.
		// link for Home
		if(pageList[HOME].getPagePos() == pagePos)	
		{ pBuilder.append("<li><span class=\"current\">" + pageList[HOME].getPageName() + "</span></li>\n"); }
		else
		{ pBuilder.append("<li><a href=\"javascript:" + pageList[HOME].getToMethodName() + ";\">" + pageList[HOME].getPageName() + "</a></li>\n");}	
		// link for Gene
		if(pageList[GENE].getPagePos() == pagePos)		// Page calling the html block
		{ pBuilder.append("<li><span class=\"current\">" + pageList[GENE].getPageName() + "</span></li>\n"); }
		else
		{ pBuilder.append("<li><a href=\"javascript:" + pageList[GENE].getToMethodName() + ";\">" + pageList[GENE].getPageName() + "</a></li>\n");}		
		// link for Tissue (top)
		if(pageList[TISSUE].getPagePos() == pagePos)		
		{ pBuilder.append("<li><span class=\"current\">" + pageList[TISSUE].getPageName() + "</span></li>\n"); }
		else
		{ pBuilder.append("<li><a href=\"javascript:" + pageList[TISSUE].getToMethodName() + ";\">" + pageList[TISSUE].getPageName() + "</a></li>\n");}		
		// link for Category (go)
		if(pageList[CATEGORY].getPagePos() == pagePos)		
		{ pBuilder.append("<li><span class=\"current\">" + pageList[CATEGORY].getPageName() + "</span></li>\n"); }
		else
		{ pBuilder.append("<li><a href=\"javascript:" + pageList[CATEGORY].getToMethodName() + ";\">" + pageList[CATEGORY].getPageName() + "</a></li>\n");}	
		// link for Development (Adult/Larval devel)
		if(pageList[DEVELOPMENT].getPagePos() == pagePos)		
		{ pBuilder.append("<li><span class=\"current\">" + pageList[DEVELOPMENT].getPageName() + "</span></li>\n"); }
		else
		{ pBuilder.append("<li><a href=\"javascript:" + pageList[DEVELOPMENT].getToMethodName() + ";\">" + pageList[DEVELOPMENT].getPageName() + "</a></li>\n");}	
		// link for Embryo (embryo)
		if(pageList[EMBRYO].getPagePos() == pagePos)	
		{ pBuilder.append("<li><span class=\"current\">" + pageList[EMBRYO].getPageName() + "</span></li>\n"); }
		else
		{ pBuilder.append("<li><a href=\"javascript:" + pageList[EMBRYO].getToMethodName() + ";\">" + pageList[EMBRYO].getPageName() + "</a></li>\n");}	
		// link for Profile (profile)
		if(pageList[PROFILE].getPagePos() == pagePos)		
		{ pBuilder.append("<li><span class=\"current\">" + pageList[PROFILE].getPageName() + "</span></li>\n"); }
		else
		{ pBuilder.append("<li><a href=\"javascript:" + pageList[PROFILE].getToMethodName() + ";\">" + pageList[PROFILE].getPageName() + "</a></li>\n");}
		// link for Docs 
		if(pageList[DOCS].getPagePos() == pagePos)		
		{ pBuilder.append("<li><span class=\"current\">" + pageList[DOCS].getPageName() + "</span></li>\n"); }
		else
		{ pBuilder.append("<li><a href=\"javascript:" + pageList[DOCS].getToMethodName() + ";\">" + pageList[DOCS].getPageName() + "</a></li>\n");}
		// link for Feedback
		if(pageList[FEEDBACK].getPagePos() == pagePos)		
		{ pBuilder.append("<li><span class=\"current\">" + pageList[FEEDBACK].getPageName() + "</span></li>\n"); }
		else
		{ pBuilder.append("<li><a href=\"javascript:" + pageList[FEEDBACK].getToMethodName() + ";\">" + pageList[FEEDBACK].getPageName() + "</a></li>\n");}				
		
		// link for Switch to NCBI mode — hard-coded except for ensuring page maintained
/*		if(pageList[GENE].getPagePos() == pagePos)
		{ pBuilder.append("<li><a href=\"https://motif.mvls.gla.ac.uk/BeetleAtlas2/?page=gene\">NCBI mode</a></li>\n");}
		else if (pageList[TISSUE].getPagePos() == pagePos)
		{ pBuilder.append("<li><a href=\"https://motif.mvls.gla.ac.uk/BeetleAtlas2/?page=top\">NCBI mode</a></li>\n");}	
		else if (pageList[CATEGORY].getPagePos() == pagePos)
		{ pBuilder.append("<li><a href=\"https://motif.mvls.gla.ac.uk/BeetleAtlas2/?page=go\">NCBI mode</a></li>\n");}
		else if (pageList[DEVELOPMENT].getPagePos() == pagePos)
		{ pBuilder.append("<li><a href=\"https://motif.mvls.gla.ac.uk/BeetleAtlas2/?page=devel\">NCBI mode</a></li>\n");}
		else if (pageList[EMBRYO].getPagePos() == pagePos)
		{ pBuilder.append("<li><a href=\"https://motif.mvls.gla.ac.uk/BeetleAtlas2/?page=embryo\">NCBI mode</a></li>\n");}
		else if (pageList[PROFILE].getPagePos() == pagePos)
		{ pBuilder.append("<li><a href=\"https://motif.mvls.gla.ac.uk/BeetleAtlas2/?page=profile\">NCBI mode</a></li>\n");}
		else if (pageList[DOCS].getPagePos() == pagePos)
		{ pBuilder.append("<li><a href=\"https://motif.mvls.gla.ac.uk/BeetleAtlas2/?page=help\">NCBI mode</a></li>\n");}
		else if (pageList[FEEDBACK].getPagePos() == pagePos)
		{ pBuilder.append("<li><a href=\"https://motif.mvls.gla.ac.uk/BeetleAtlas2/?page=contact\">NCBI mode</a></li>\n");}
		else
		{ pBuilder.append("<li><a href=\"https://motif.mvls.gla.ac.uk/BeetleAtlas2/index.html\">NCBI mode</a></li>\n"); }*/
		
		// Finish NAV div, finish topWrapper div, start bottomWrapper div
		pBuilder.append("</ul>\n</div><!-- end of nav div -->\n");				
		pBuilder.append("</div><!-- end of upper div -->\n");

		if(isHome)
		{ pBuilder.append(getMobileHomeTop());}
		else
		{ pBuilder.append(getMobileTopstrip(pagePos));}
		
		pBuilder.append("</div><!-- end of topWrapper div -->\n");
		pBuilder.append("<div id=\"bottomWrapper\">\n");			// the finishing div is in static PageFoot text string!

		return pBuilder.toString();
	}
	
	// build a top bar for mobile only — needs to be different for home page
	private String getMobileTopstrip (int pagePos)
	{
		StringBuilder mBuilder = new StringBuilder();
		mBuilder.append("<div id=\"topStrip\"><!-- topStrip only seen by mobiles -->\n");
		mBuilder.append("<div id=\"topL\"><a href=\"javascript:toHomeForm();\">&nbsp;☜</a></div>");
		mBuilder.append("<div id=\"topC\">BeetleAtlas2 – " + pageList[pagePos].getPageName() + "</div>\n");
		mBuilder.append("<div id=\"topR\"></div>\n");
		mBuilder.append("</div><!-- end of topStrip -->\n");	
		return mBuilder.toString();
	}
	// build a top section for mobile only home page
	private String getMobileHomeTop()
	{
		StringBuilder mBuilder = new StringBuilder();	
		mBuilder.append("<div id=\"title625\"> <img class=\"shrinkfit\" alt=\"\" src=\"images/titleTbO625.jpg\" /> </div>\n");
		mBuilder.append("<div id=\"title500\"> <img class=\"shrinkfit\" alt=\"\" src=\"images/titleTbO500.jpg\" /> </div>\n");	
		mBuilder.append("<div id=\"title400\"> <img class=\"shrinkfit\" alt=\"\" src=\"images/titleTbO400.jpg\" /> </div>\n");			
		mBuilder.append("<div id=\"navIM\">\n");
		mBuilder.append("<ul>\n");
		mBuilder.append("<li><a href=\"javascript:toGeneForm();\">Gene</a></li>\n");
		mBuilder.append("<li><a href=\"javascript:toTopForm();\">Tissue</a></li>\n");
		mBuilder.append("<li><a href=\"javascript:toGOForm();\">Category</a></li>\n");
		mBuilder.append("<li><a href=\"javascript:toDevelForm();\">Adult/Larva</a></li>\n");
		mBuilder.append("<li><a href=\"javascript:toEmbryoForm();\">Embryo</a></li>\n");
		mBuilder.append("<li><a href=\"javascript:toProfileForm();\">Profile</a></li>\n");
		mBuilder.append("<li><a href=\"javascript:toHelpForm();\">Docs</a></li>\n");
		mBuilder.append("<li><a href=\"javascript:toFeedbackForm();\">Feedback</a></li>\n");
		mBuilder.append("<li><a href=\"https://motif.mvls.gla.ac.uk/BeetleAtlas2/index.html\">NCBI mode</a></li>\n");
		mBuilder.append("</ul>\n");
		mBuilder.append("</div>\n");
		return mBuilder.toString();
	}

	// Help/Info pop-up for table of Gene FPKMs
	public static String getGeneHelp(int resNum)
	{
		StringBuilder sb = new StringBuilder("<!-- start of Gene help insert -->\n");
		sb.append("<div class=\"indexWindow\" id=\"indexWinG_" + resNum + "\" style=\"display:none; width:380px;\">\n");
		sb.append("<div class=\"indexBar\" style=\"width:370px;\" onmousedown=\"drag(this.parentNode, event);\">Gene Results");
		sb.append("<div class=\"closebox\"><a href=\"#\" onclick=\"closeDiv('indexWinG_" + resNum + "'); return(false);\">&nbsp;&times;&nbsp;</a>\n");
		sb.append("</div></div>");
		sb.append("<div class=\"indexContent\" style=\"width:360px;\">");
		sb.append("<img class=\"help\" src=\"images/GtoT.png\" alt=\"\" width=\"134\" height=\"180\" />\n");
		sb.append("<p class=\"help\"><strong>Locating Transcripts</strong>");
		sb.append("<br />Click on Gene FPKM or Enrichment value to highlight corresponding transcripts.</p>\n");
		sb.append("<p class=\"help\">\n");
		sb.append("<img src=\"images/download.png\" alt=\"\" style=\"vertical-align:sub\" /> <strong>Download</strong>\n");
		sb.append("<br />The results table may be downloaded as tab-separated text, suitable for importing into a spreadsheet program such as Microsoft Excel.</p>\n");
		sb.append("</div>\n</div>\n<!-- end of  Gene help insert -->\n");
		return sb.toString();
	}

	// Help/Info pop-up for table of Transcript FPKMs
	public static String getTranscriptHelp(int resNum)
	{
		StringBuilder sb = new StringBuilder("\n<!-- start of Transcript help insert -->\n");
		sb.append("<div class=\"indexWindow\" id=\"indexWinT_" + resNum + "\" style=\"display:none; width:430px;\">\n");
		sb.append("<div class=\"indexBar\" style=\"width:420px;\" onmousedown=\"drag(this.parentNode, event);\">Transcript Results");
		sb.append("<div class=\"closebox\"><a href=\"#\" onclick=\"closeDiv('indexWinT_" + resNum + "'); return(false);\">&nbsp;&times;&nbsp;</a>\n");
		sb.append("</div></div>");
		sb.append("<div class=\"indexContent\" style=\"width:410px;\">");
		sb.append("<img class=\"help\" src=\"images/GtoT.png\" alt=\"\" width=\"134\" height=\"180\" />\n");
		sb.append("<p class=\"help\"><strong>Correlating with Genes</strong>");
		sb.append("<br />Clicking on a Gene FPKM or Enrichment value corresponding to a set of transcripts will highlight both.</p>\n");
		sb.append("<p class=\"help\">\n");
		sb.append("<img src=\"images/download.png\" alt=\"\" style=\"vertical-align:sub\" /> <strong>Download</strong>\n");
		sb.append("<br />Although FPKM values are not presented in the table, they may be downloaded as tab-separated text, suitable for importing into a spreadsheet program such as Microsoft Excel.</p>\n");
		sb.append("</div>\n</div>\n<!-- end of Transcript help insert -->\n");
		return sb.toString();
	}
	
	public static String getAnatomyLink()
	{
		StringBuilder sb = new StringBuilder("\n<!-- add anatomy hide/show -->\n");
		sb.append("<div id=\"visible\"><strong>ANATOMICAL KEY</strong></div>");
		sb.append("<div id=\"hideme\" style=\"display:none;\">");
		sb.append("<p><img class=\"shrinkfit\" src=\"images/AdultAnat2x.png\" alt=\"\" width=\"725\" height=\"300\" /></p></div>");		
		return sb.toString();
	}

	///////////////////////////////////////////////
	
			// Non-static UTILITY method (calls non-static getClass()) //
	
	// Reads a file into a UTF-8 String - typically a file in the same directory, e.g. "htmlText/mypage"
	public String readHTMLfile(String path)
	{
		String outString;
		InputStream stream = getClass().getResourceAsStream(path);
		if (stream !=null)
		{
			try
			{
				byte [] b = new byte[8092];
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				int i = 0;
				while( (i=stream.read(b)) > 0)
				{
					out.write(b, 0, i);
				}
				stream.close();
				outString = out.toString("UTF-8");	// !
			}
			catch (IOException x)
			{
				outString = "Text Misread. Please notify the site owner.";
			}
		}
		else
		{
			outString = "Text Misread. Please notify the site owner.";		
		}
		return outString;
	}
	
	////////////////// GENERAL UTILITY METHODS /////////////////////////////
	
	// Legacy method for Enrichments — calls two parameter method with decDigits = 1
	public static String formatValues(double value)
	{	
		return formatValues(value, 1);
	}
	
	// returns a String value with visually appropriate number of decimal places (not strict num sig figs)
	public static String formatValues(double value, int decDigits)
	{
		NumberFormat N = NumberFormat.getInstance();
		N.setGroupingUsed(false);		// no comma separators for thousands (mainly single digit thous which shouldn't have them)
		if(value < 1.0)
		{
				N.setMaximumFractionDigits(decDigits);
				N.setMinimumFractionDigits(decDigits);
		}
		else if(value < 2.0)
		{
				N.setMaximumFractionDigits(decDigits);
				N.setMinimumFractionDigits(decDigits);
		}
		else if(value < 10.0)
		{
				N.setMaximumFractionDigits(1);
				N.setMinimumFractionDigits(1);
		}
		else
		{
			N.setMaximumFractionDigits(0); 
			N.setMinimumFractionDigits(0);
		}	
		return N.format(value);
	}
	
	// Checks for [+] indication of superscript. If present, marks up for HTML
	public static String checkSuper(String name)
	{
		if(name.indexOf("[+]") != -1)
		{
			int start = name.indexOf("[+]");
			name = name.substring(0, start) + "<sup>+</sup>" + name.substring(start+3);
			// check for second case as in Na[+]/H[+]
			if(name.indexOf("[+]") != -1)
			{
				start = name.indexOf("[+]");
				name = name.substring(0, start) + "<sup>+</sup>" + name.substring(start+3);
			}
		}
		else if(name.indexOf("[2+]") != -1)
		{
			int start = name.indexOf("[2+]");
			name = name.substring(0, start) + "<sup>2+</sup>" + name.substring(start+4);			
		}
		return name;
	}
	
	// Returns background colour for enrichment cells on a yellow/white/red divergent scale
	public static Color getEnrichmentColor(double enrichment)
	{
		int red = 0;
		int green = 0;
		int blue = 0;
		Color colour = new Color(red, green, blue);
		// double base = 1.55;		// For log — FlyAtlas 2013
		double base = 1.4;		// For log — adjusted for slightly narrower range cf. FlyAtlas 2013
		int numHighSteps = 7;	// Number of log steps for e > 1
		int numLowSteps = 4;	// Number of log steps for e < 1
		int gbRange = 210;		// For reds e > 1
		
		if(enrichment > Math.pow(base, numHighSteps))	// deal with extreme high values first - base 1.55 with 7 steps = ca.21.5
		{
			green = 230 - gbRange;
			blue = 230 - gbRange;
			int rRange = 50;
			int addSteps = 15;
			double logVal = Math.log(enrichment) / Math.log(base);	
			red = 250 - (int) (logVal*rRange ) / addSteps;
			colour = new Color(red, green, blue);
		}
		else if(enrichment > 1)
		{
    		double logVal = Math.log(enrichment) / Math.log(base);
    		int rRange = 5;
    		int gRange = 230;
    		int bRange = 15;
    		double rDecrement = (logVal*rRange ) / numHighSteps ;
    		double gDecrement = (logVal*gRange ) / numHighSteps ;
    		double bDecrement = (logVal*bRange ) / numHighSteps ;
    		red = 255 - (int) rDecrement;
    		green = 255 - (int) gDecrement;
    	    blue = 40 - (int) bDecrement;
    		colour = new Color(red, green, blue);
		}
		else if(enrichment == 1)
		{
			red = 255; green = 255; blue = 40;		// yellow
			colour = new Color(red, green, blue);
		}
		else if(enrichment < 1)			// yellow range below 1
		{		
			double lowBase = 1.8;
			if(enrichment < 0)
			{
				enrichment = 0;
			}
			
			int bRange = 215;			// B range from 255 to 40
    		double logVal = Math.log(enrichment) / Math.log(lowBase);
    		double bDecrement = (logVal* bRange)  / numLowSteps; 	
    		
    		if(bDecrement < -bRange)
    		{
    			bDecrement = -bRange;		// 40 minimum value for yellow
    		}
    		red = 255;
    		green = 255;
    		blue = 40 - (int) bDecrement;
    		colour = new Color(red, green, blue);
		}				    
		return colour;
	}
	
	// Returns background colour for FPKM cells on a white to black log scale
	public static Color getAbundanceColor(double abundance)
	{	
		int red = 0;
		int green = 0;
		int blue = 0;	
		//double base = 2;		// FlyAtlas 2013
		double base = 1.6;		// Altered from FlyAtlas 2013 because of changed distribution and effect of transcripts
		int numSteps = 15;
		int range = 255;
		
		double logVal = Math.log(abundance) / Math.log(base);
		red = range - (int) (logVal*range) / numSteps;
		if(red>255)
		{
			red = 255;
		}
		else if(red<0)		// occurs if Abundance > ca 30000 or perhaps lower now — Just a few dozen cases
		{
			red = 0;
		}
		green = red; blue = red;
		
		return new Color(red, green, blue);
	}
	
	// Returns background colour for FPKM cells on a white to black log scale
	public static Color getRPMColor(int rpm)
	{	
		int red = 0;
		int green = 0;
		int blue = 0;	
		//double base = 1.6;		// for genes
		double base = 2.6;			// Adjusted by eye
		int numSteps = 15;
		int range = 255;
		
		double logVal = Math.log(rpm) / Math.log(base);
		red = range - (int) (logVal*range) / numSteps;
		if(red>255)
		{
			red = 255;
		}
		else if(red<0)		// occurs if Abundance > ca 30000 or perhaps lower now 
		{
			red = 0;
		}
		green = red; blue = red;		// this creates a grey on the basis of the red value
		
		return new Color(red, green, blue);
	}

	// utility method gets brightness of a colour
	public static int getBrightness(Color c) 
	{
	    return (int) Math.sqrt(c.getRed() * c.getRed() * 0.241 +
	      						c.getGreen() * c.getGreen() * 0.691 +
	      						c.getBlue() * c.getBlue() * 0.068);
	}
	
	// utility method takes a brightness value and determines whether above a darkness threshold
	public static boolean isDark(int brightness)
	{
	    if (brightness < 130)
	    {
	    	return true;
	    }
	    else
	    {
	    	return false;
		}
	}
	
	// utility method to generate html colour string of the type rgb(215,65,98) from java Color
	public static String getHTMLcolour(Color colour)
	{
		return "rgb(" + colour.getRed() + "," + colour.getGreen() + "," + colour.getBlue() + ")";
	}
	
	// Writes HTML for link to download results as text
	public static String getDownloadLink(Gene gene, boolean transcript)
	{
		String geneID = gene.getGeneID();
		String tableOut = new String();
		if(transcript)
		{
			tableOut = "transcriptGene";
		}
		else
		{
			tableOut = "gene";			
		}
		return("<a href=\"/BeetleDirect/index.html?geneID=" + geneID + "&amp;tableOut=" + tableOut + 
				";\" title=\"Download " + tableOut + " table\"><img src=\"images/download.png\" alt=\"download\" class=\"downloadImg\" /></a>");
	}
	
}
