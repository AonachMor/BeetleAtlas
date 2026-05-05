 /*
Class to hold HTML code and write specific HTML menus 
for different pages of BeetleAtlas
Also contains various other utilities, most of which can be accessed in a static manner
DPL 10.10.2019
Last Updated: 01.02.2021
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
		+ "<head>\n<title>BeetleAtlas: The Tribolium Gene Expression Atlas</title>\n<meta http-equiv=\"content-type\" content=\"text/html; charset=UTF-8\" />\n"
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
		+ "<link rel=\"stylesheet\" href=\"https://fonts.googleapis.com/css?family=Archivo+Narrow\" />\n"		
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
			"\n<div style=\"float: right; padding-top: 20px; width: 49%; text-align: right;\">\n"
			+ "<img class=\"mobileHide\" src=\"images/velux.png\" alt=\"\" width=\"383\" height=\"93\" title=\"Funded by the Velux Foundations\" />\n</div>\n"			
			+ "<div style=\"float: right; padding-top: 20px; width: 1%; text-align: center;\">\n"
			+ "<img class=\"mobileHide\" src=\"images/blank.png\"  alt=\"\" width=\"1\" height=\"93\" />\n</div>\n"		
			+ "<div style=\"float: left; padding-top: 20px; width: 49%; text-align: left;\">\n"
			+ "<img class=\"mobileHide\" src=\"images/UofC.png\" alt=\"\" width=\"268\" height=\"93\" title=\"University of Copenhagen\" />\n</div>\n"					
			+ "<div style=\"clear: both;\"></div>\n"
			+ "</div> <!-- end of wrapper div -->\n"
			+ "<script type=\"text/javascript\">\n" 
	        +  "var _gaq = _gaq || [];\n"
	        + "_gaq.push(['_setAccount', 'UA-3315042-24']);\n"
	        + "_gaq.push(['_trackPageview']);\n"
	        + "(function() {\n"
	        + "var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;\n"
	        + "ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';\n"
	        + "var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);\n"
	        + "})();\n"
	        + "</script>\n"
			+ "</body>\n</html>\n";
	
	final static String PAGE_FOOT = 
			"\n</div> <!-- end of wrapper div -->\n"
			+ "</body>\n</html>\n";
	
	
	// Constants to allow safer comparisons
	final static String ADULT = "Adult";
	final static String LARVAL = "Larval";
	
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
		pageList[0] = new PageDescriptor(1, "Home", "toHomeForm()", "<body>\n");		
		pageList[1] = new PageDescriptor(2, "Gene", "toGeneForm()", "<body onload=\"setSpan('.plusMinus'," + includeErrors + "); setRow('wholesome'," + showWhole + "); setFocus(); setHash('mobileTargetG');\" onkeypress=\"geneKey(event);\">\n");
		pageList[2] = new PageDescriptor(3, "Category", "toGOForm()", "<body onload=\"setSpan('.plusMinus'," + includeErrors + "); setRow('wholesome'," + showWhole + "); setFocus(); setHash('mobileTargetC');\" onkeypress=\"goKey(event);\">\n");
		pageList[3] = new PageDescriptor(4, "Tissue", "toTopForm()", "<body onload=\"setSpan('.plusMinus'," + includeErrors + "); setRow('wholesome'," + showWhole + "); setHash('mobileTargetT');\" onkeypress=\"topKey(event);\">\n");
		
		pageList[4] = new PageDescriptor(5, "Feedback", "toFeedbackForm()", "<body>\n");
		pageList[5] = new PageDescriptor(6, "Docs", "toHelpForm()", "<body>\n");
		
		pageList[6] = new PageDescriptor(7, "Adult/Larva", "toDevelForm()", "<body onload=\"setSpan('.plusMinus'," + includeErrors + "); setRow('wholesome'," + showWhole + "); setFocus(); setHash('mobileTargetD');\" onkeypress=\"develKey(event);\">\n");		
		pageList[7] = new PageDescriptor(8, "Embryo", "toEmbryoForm()", "<body onload=\"setSpan('.plusMinus'," + includeErrors + "); setRow('wholesome'," + showWhole + "); setFocus(); setHash('mobileTargetE');\" onkeypress=\"embryoKey(event);\">\n");		
		pageList[8] = new PageDescriptor(9, "Profile", "toProfileForm()", "<body onload=\"setSpan('.plusMinus'," + includeErrors + "); setRow('wholesome'," + showWhole + "); setFocus(); setHash('mobileTargetP');\" onkeypress=\"profileKey(event);\">\n");		
	}
	
	// inner class to hold a utility pageDescriptor object	
	class PageDescriptor
	{
		int pagePos;				// order of page in menu 1 to n
		String pageName;			// name of page as it appears on the menu
		String toMethodName;		// name of javascript method to generate new page	
		String bodyLine;			// html <body> line - differs depending on javascript
		PageDescriptor(int pagePos, String pageName, String toMethodName, String bodyLine)
		{
			this.pagePos = pagePos;
			this.pageName = pageName;
			this.toMethodName = toMethodName;
			this.bodyLine = bodyLine;
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
	}
	
	///////////////////////////////////////////////
	
			// Static ACCESSOR method (depends on PageList being established) //
	
	// builds top section of html page with appropriate names and links
	// this is a mess at the moment as I don't want to renumber the pages — should try to change to the new for statement
	public String getPageTop(int pagePos)
	{
		StringBuilder pBuilder = new StringBuilder(PAGE_HEAD);		// boiler plate html head
		pBuilder.append(pageList[pagePos-1].getBodyLine());			// <body> line
		pBuilder.append("<div id=\"wrapper\">\n");					// the finishing div is elsewhere !		
		pBuilder.append("<div id=\"upper\">\n");					// upper area containing title and menu for normal view
		pBuilder.append("<div class=\"menuContainer\">\n");
		pBuilder.append("<div class=\"menuL\">\n<ul>\n");
		
		// link for Gene
		if(pageList[1].getPagePos() == pagePos)		// Page calling the html block
		{ pBuilder.append("<li><span class=\"current\">" + pageList[1].getPageName() + "</span></li>\n"); }
		else
		{ pBuilder.append("<li><a class=\"linkPage\" href=\"javascript:" + pageList[1].getToMethodName() + ";\">" + pageList[1].getPageName() + "</a></li>\n");}
		
		// link for Tissue (top)
		if(pageList[3].getPagePos() == pagePos)		
		{ pBuilder.append("<li><span class=\"current\">" + pageList[3].getPageName() + "</span></li>\n"); }
		else
		{ pBuilder.append("<li><a class=\"linkPage\" href=\"javascript:" + pageList[3].getToMethodName() + ";\">" + pageList[3].getPageName() + "</a></li>\n");}
		
		// link for Category (go)
		// if(pageList[2].getPagePos() == pagePos)		
		// { pBuilder.append("<li><span class=\"current\">" + pageList[2].getPageName() + "</span></li>\n"); }
		// { pBuilder.append("<li><span class=\"current\">&nbsp;</span></li>\n"); } padding no longer needed
		// else
		// { pBuilder.append("<li><a class=\"linkPage\" href=\"javascript:" + pageList[2].getToMethodName() + ";\">" + pageList[2].getPageName() + "</a></li>\n");}	
		// { pBuilder.append("<li><a class=\"linkPage\" href=\"javascript:#;\">&nbsp;</a></li>\n");}	 padding no longer needed

		// link for Development (devel)
		if(pageList[6].getPagePos() == pagePos)		
		{ pBuilder.append("<li><span class=\"current\">" + pageList[6].getPageName() + "</span></li>\n"); }
		else
		{ pBuilder.append("<li><a class=\"linkPage\" href=\"javascript:" + pageList[6].getToMethodName() + ";\">" + pageList[6].getPageName() + "</a></li>\n");}
		
		// COLUMN DIVISION		
		pBuilder.append("</ul>\n</div><!-- end of menuL div -->\n");
		pBuilder.append("<div class=\"menuR\">\n<ul>\n");
		
		// link for Docs
/*		if(pageList[5].getPagePos() == pagePos)			// Page calling the html block
		{ pBuilder.append("<li><span class=\"current\">" + pageList[5].getPageName() + "</span></li>\n"); }
		else
		{ pBuilder.append("<li><a class=\"linkPage\" href=\"javascript:" + pageList[5].getToMethodName() + ";\">" + pageList[5].getPageName() + "</a></li>\n");}
*/		
		// link for Embryo (embryo)
		if(pageList[7].getPagePos() == pagePos)		
		{ pBuilder.append("<li><span class=\"current\">" + pageList[7].getPageName() + "</span></li>\n"); }
		else
		{ pBuilder.append("<li><a class=\"linkPage\" href=\"javascript:" + pageList[7].getToMethodName() + ";\">" + pageList[7].getPageName() + "</a></li>\n");}
		
		// link for Profile (profile)
		if(pageList[8].getPagePos() == pagePos)		
		{ pBuilder.append("<li><span class=\"current\">" + pageList[8].getPageName() + "</span></li>\n"); }
		else
		{ pBuilder.append("<li><a class=\"linkPage\" href=\"javascript:" + pageList[8].getToMethodName() + ";\">" + pageList[8].getPageName() + "</a></li>\n");}

		// link for Feedback
		if(pageList[4].getPagePos() == pagePos)		
		{ pBuilder.append("<li><span class=\"current\">" + pageList[4].getPageName() + "</span></li>\n"); }
		else
		{ pBuilder.append("<li><a class=\"linkPage\" href=\"javascript:" + pageList[4].getToMethodName() + ";\">" + pageList[4].getPageName() + "</a></li>\n");}				
		
		// link for Home
		if(pageList[0].getPagePos() == pagePos)	// no home item on homepage
		{ /*pBuilder.append("<li><span class=\"current\">" + pageList[0].getPageName() + "</span></li>\n")*/; }
		else
		{ pBuilder.append("<li><a class=\"linkPage\" href=\"javascript:" + pageList[0].getToMethodName() + ";\">" + pageList[0].getPageName() + "</a></li>\n");}
		
		pBuilder.append("</ul>\n</div><!-- end of menuR div -->\n");		
		pBuilder.append("</div><!-- end of menuContainer div -->\n");		
		pBuilder.append("</div><!-- end of upper div -->\n");
		return pBuilder.toString();
	}
	
	// build a top bar for mobile only — needs to be different for home page
	public String getMobileTopstrip (int pagePos)
	{
		StringBuilder mBuilder = new StringBuilder();
		mBuilder.append("<div id=\"topStrip\"><!-- topStrip only seen by mobiles -->\n");
		mBuilder.append("<div id=\"topL\"><a class=\"linkPage\" href=\"javascript:toHomeForm();\">&nbsp;☜</a></div>");
		mBuilder.append("<div id=\"topC\">BeetleAtlas – " + pageList[pagePos-1].getPageName() + "</div>\n");
		mBuilder.append("<div id=\"topR\"></div>\n");
		mBuilder.append("</div><!-- end of topStrip -->\n");	
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
		green = red; blue = red;
		
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
	
	
	// Temporarily inactivated because of need to write equivalent of FA2Direct, which is a pain.
/*	public static String getDownloadLink(Gene gene, boolean transcript, boolean mir)
	{
		//String fbgn = gene.getGeneID();
		String tableOut = new String();
		if(transcript && mir)
		{
			tableOut = "transcriptMir";
		}
		else if(transcript)
		{
			tableOut = "transcriptGene";
		}
		else
		{
			tableOut = "gene";			
		}
		//return("<a href=\"/FA2Direct/index.html?fbgn=" + fbgn + "&amp;tableOut=" + tableOut + 
				//";\" title=\"Download " + tableOut + " table\"><img src=\"images/download.png\" alt=\"download\" class=\"downloadImg\" /></a>");
		return("");
	}*/
	
}
