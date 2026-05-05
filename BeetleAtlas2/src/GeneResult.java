/*
	One-method class to generate Results block for a single gene containing Adult/Larval Gene and Transcript tables 
	and an Embryo table with or without profile statistics
	David P. Leader 13.10.2020
	BeetleAtlas2 update 19.09.2024
	Last Update:18.10.2025 
*/

import java.awt.Color;
import java.text.NumberFormat;

public class GeneResult 
{
	private Gene gene;				// Gene object for which results are tabulated
	private TissueCatalogue tCat;	// TissueCatalogue object which knows about all tissues
	private GeneExpression expn;	// Expression object with results
	private int resNum;				// Numerical suffix to append to HTML ID to allow multiple results having unique ids for JavaScript methods
	private boolean conceal;		// If true, provide a hide/show button to conceal table (used where multiple results)
	private boolean includeErrors;	// If true, display SD with FPKMs (for toggle button)
	private boolean showWhole;		// If true, display Whole Body abundance data (for toggle button)
	private int adultIndex;
	private int larvalIndex;		// current id be used for cells for larval tissues
	private boolean isEmbryoQuery;	// Determines order of display of results
	private double rStat;			// Statistic for Profile searches (need to set at zero so ignored in output of other searches
	private double pStat;			// Statistic for Profile searches

	// standard constructor
	public GeneResult(Gene gene, TissueCatalogue tCat, GeneExpression expn, int resNum, boolean conceal, 
			boolean includeErrors, boolean showWhole)
	{
		isEmbryoQuery = false;
		rStat = 0.0;
		pStat = 1.0;
		this.gene = gene;
		this.tCat = tCat;
		this.expn = expn;
		this.resNum = resNum;
		this.conceal = conceal;
		this.includeErrors = includeErrors;
		this.showWhole = showWhole;
	}	
	
	// constructor for results from embryo query
	public GeneResult(Gene gene, TissueCatalogue tCat, GeneExpression expn, int resNum, boolean conceal, 
			boolean isEmbryoQuery, boolean includeErrors, boolean showWhole)
	{
		rStat = 0.0;
		pStat = 1.0;
		this.gene = gene;
		this.tCat = tCat;
		this.expn = expn;
		this.resNum = resNum;
		this.conceal = conceal;
		this.isEmbryoQuery = isEmbryoQuery;
		this.includeErrors = includeErrors;
		this.showWhole = showWhole;
	}
	
	// constructor for results from profile query
	public GeneResult(Gene gene, TissueCatalogue tCat, GeneExpression expn, int resNum, boolean conceal, 
			boolean includeErrors, boolean showWhole, double rStat, double pStat)
	{
		isEmbryoQuery = false;;
		this.gene = gene;
		this.tCat = tCat;
		this.expn = expn;
		this.resNum = resNum;
		this.conceal = conceal;
		this.includeErrors = includeErrors;
		this.showWhole = showWhole;
		this.rStat = rStat;
		this.pStat = pStat;
	}	
	
	// Generates output HTML block with results (multiple gene version assigning different ids to tables)
	public String getResultsHTML()
	{	
		initializeIndices(tCat);	// need to reset each time for multiple genes
		
		String geneTableID = "tabGene_" + resNum;
		String transTableID = "tabTrans_" + resNum;
		
		// Explanatory and gene info
		StringBuilder sb = new StringBuilder();
		
		sb.append("<div class=\"results\">\n");		// changed to allow multiple instances
		sb.append("<div class=\"geneInfoSet\">\n");
		sb.append(gene.getHTMLFormatted(resNum, conceal));
		if(rStat > 0.0)
		{
			sb.append("<div class=\"geneInfo\"><span class=\"infoCaption\">Correlation</span><span class=\"mobileHide\"><br /></span>");
			sb.append("<span class=\"infoContent\"><em>r</em> = " + PageUtility.formatValues(rStat, 2) + ", <em>P<sub>B</sub></em> = "  + PageUtility.formatValues(pStat, 3) + "</span></div>\n");
		}
		
		sb.append("</div><!-- end of geneInfoSet div -->\n");
		
		if(conceal)
		{
			sb.append("<div id=\"hs_"+ resNum + "\" class=\"conceal\">\n");		// start of div for hide/show for multiple results		
		}
				
		if(expn == null)
		{
			System.out.println("expn is null!");
		}
		GeneTissueDataSet dataset = expn.getGeneData();
		if(dataset == null)
		{
			System.out.println("dataset is null!");
		}
		
		NumberFormat N = NumberFormat.getInstance();
		N.setMaximumFractionDigits(1);	
		
		// Gene title and SD and WholeBody checkboxes both within a wider div, so for float order is reversed
		sb.append("<div class=\"mobileHide\">");
		if(includeErrors)
		{
			sb.append("<div class=\"rightTHead\">" + "<input type=\"checkbox\" checked=\"checked\" class=\"sd\" id=\"errors_" + resNum + 
					"\" value=\"errors\" onclick=\"toggleSpan('.plusMinus'); synchBoxes(this,'sd');\" /> SDs&nbsp;&nbsp;"); 					
		}
		else
		{
			sb.append("<div class=\"rightTHead\">" + "<input type=\"checkbox\" class=\"sd\" id=\"errors_" + resNum + 
				"\" value=\"errors\" onclick=\"toggleSpan('.plusMinus'); synchBoxes(this,'sd');\" /> SDs&nbsp;&nbsp;"); 
		}
		if(showWhole)
		{
			sb.append(" <input type=\"checkbox\" style=\"margin-left:5px;\" checked=\"checked\" class=\"sw\" id=\"whole_" + resNum + 
					"\" value=\"whole\" onclick=\"toggleRow('wholesome'); synchBoxes(this,'sw');\" />  Whole Body&nbsp;&nbsp;");
		}
		else
		{
			sb.append(" <input type=\"checkbox\" style=\"margin-left:5px;\" class=\"sw\" id=\"whole_" + resNum + 
					"\" value=\"whole\" onclick=\"toggleRow('wholesome'); synchBoxes(this,'sw');\" />  Whole Body&nbsp;&nbsp;");			
		}	
		sb.append("</div>"); // close rightTHead
		
		// Add Results in appropriate order: Normally Adult/Larval first, but Embryo first for Embryo query	
		if(isEmbryoQuery)
		{
			sb.append(getEmbryoResults());
			sb.append(getAdultLarvalResults(geneTableID, transTableID, dataset));
		}
		else
		{
			sb.append(getAdultLarvalResults(geneTableID, transTableID, dataset));
			sb.append(getEmbryoResults());			
		}
		
		// close conceal div (if present)
		if(conceal)
		{
			sb.append("</div><!-- end of conceal div -->\n");
		}
		
		// close results div (rounded box)
		sb.append("</div> <!-- end of overall results div -->\n");
		return sb.toString();
	}

	// Lays out Embryo results
	private String getEmbryoResults()
	{
		StringBuilder sb = new StringBuilder();
				
		// Single table with both gene and transcript results
		sb.append("<div class=\"mobileHide\">");
		sb.append("\n<div class=\"leftTHead\">Embryo FPKMs</div>\n");
		sb.append("</div>");
		// Embryo Table start
		sb.append("<table class=\"embryoR\">\n");
		// TH row
		sb.append(getETranscriptHeaderRow(tCat));
		sb.append("\n");
		// Gene Results row
		sb.append(getEGeneTableRow(expn, gene, tCat));		
		// Transcript Results rows 
		sb.append(getETranscriptTableRows(expn, gene, tCat));
		// Embryo Table end
		sb.append("\n</table>\n");
		
		// SVG graphic div — must be unique for each result, class centres
		sb.append("<div class=\"svgImage\" id=\"svg_" + resNum + "\">\n");
		sb.append(getSVG(tCat));
		sb.append("</div>\n");		
		// add SVG download button	
		sb.append("<div class=\"svgButton\" style=\"text-align:right;\">");
		sb.append("<button onclick=\"sendSVG2("+ resNum +");\">Save as SVG</button>");
		sb.append("</div>");
		String ncbiID = gene.getNCBIid();
		sb.append("<div id=\"graphID_" + resNum + "\" style=\"display:none;\">" + ncbiID + "</div>");
		
		return sb.toString();
	}
	
	// Lays out Adult and Larval results
	private String getAdultLarvalResults(String geneTableID, String transTableID, GeneTissueDataSet dataset)
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append("<div class=\"leftTHead\">Adult &amp; Larval Gene FPKMs and Enrichments</div>\n");		
		
		sb.append("</div>");	
		sb.append(PageUtility.getGeneHelp(resNum));
		
		// Table of FPKMs and enrichment for ADULT & LARVAL
		sb.append("<table id=\"" + geneTableID + "\" class=\"geneR pointer\">\n");
		sb.append("<tr class=\"noPointer\"><th></th><th colspan=\"2\">Adult</th><th colspan=\"2\">Larval</th></tr>\n");	
		sb.append("<tr class=\"noPointer\"><th>Tissue</th><th>FPKM</th><th>Enrich<span class=\"mobileHide625\">ment</span></th><th>FPKM</th><th>Enrich<span class=\"mobileHide625\">ment</span></th></tr>\n");	
		
		
		// Write each table row in order specified in tCat object's TissueDoublet list
		int nonrefCount = 0; 	// counter for included rows — i.e. those other than reference pairs
		for (int i=0; i < tCat.getDoubletListSize(); i++)
		{
			TissueDoublet thisDoub = tCat.getTissueDoublet(i);
			
			if(!thisDoub.isReference())
			{
				// Cell 1: common Tissue Name 
				String nextUniTissueName = thisDoub.getUniTissueName();		// next non-reference uniTissue in list
				if(nonrefCount > 0)
				{
					sb.append("</tr>\n<tr><td class=\"noPointer\">" + nextUniTissueName + "</td>");
				}
				else
				{
					sb.append("<tr><td class=\"noPointer\">" + nextUniTissueName + "</td>");
				}
				
				// ADULT //
				Tissue adultTissue = tCat.getTissueDoublet(i).getAdultTissue();
				
				if(adultTissue == null)	// if not present write empty cells 4 and 5 
				{
					sb.append("<td class=\"lVacant\">&nbsp;</td><td class=\"noPointer\">&nbsp;</td>");
				}
				else					// Get appropriate GeneTissueData object and build table columns
				{
					GeneTissueData adultData = dataset.getGeneTissueDataByID(adultTissue.getTissueID());
					
					if(adultData != null)
					{	
						getGeneTableColumns(sb, adultData, resNum, adultIndex);
						adultIndex++;
					}
					else		// this is for tissues with data pending
					{
						sb.append("<td class=\"pending\">pending</td><td class=\"pending\">—</td>"); // no highlighting to avoid confusing testers
						adultIndex++; // still need to increment count
					}
				}					
				
				// LARVAL //
				Tissue larvalTissue = tCat.getTissueDoublet(i).getLarvalTissue();
				
				if(larvalTissue == null)	// if not present write empty cells 2 and 3 
				{
					sb.append("<td class=\"lVacant\">&nbsp;</td><td class=\"noPointer\">&nbsp;</td>");
				}
				else					// Get appropriate GeneTissueData object and build table columns
				{
					GeneTissueData larvalData = dataset.getGeneTissueDataByID(larvalTissue.getTissueID());
					
					if(larvalData != null)
					{	
						getGeneTableColumns(sb, larvalData, resNum, larvalIndex);
						larvalIndex++;
					}
					else		// this is temp for tissues with data pending
					{
						sb.append("<td class=\"pending\">pending</td><td class=\"pending\">—</td>"); // no highlighting to avoid confusing testers
						larvalIndex++; // still need to increment count
					}
				}
				nonrefCount++;
			}
			// Insert "whole" row at end
			else
			{
				// blank spacer row
				sb.append("</tr>\n<tr class=\"wholesome\"><td colspan=\"5\" style=\"background-color:white;\"></td>");				
				// Cell 1: common Tissue Name 
				String nextUniTissueName = thisDoub.getUniTissueName();
				sb.append("</tr>\n<tr class=\"wholesome\"><td>" + nextUniTissueName + "</td>");
			
				// ADULT //
				Tissue adultTissue = tCat.getTissueDoublet(i).getAdultTissue();	
				GeneTissueData adultData = dataset.getGeneTissueDataByID(adultTissue.getTissueID());
				getGeneTableColumns(sb, adultData, resNum);									
				
				Tissue larvalTissue = tCat.getTissueDoublet(i).getLarvalTissue();	
				GeneTissueData larvalData = dataset.getGeneTissueDataByID(larvalTissue.getTissueID());
				getGeneTableColumns(sb, larvalData, resNum);
			}
		}
		
		// close table
		sb.append("</tr>\n</table>\n");
		
		sb.append(PageUtility.getDownloadLink(gene, false) + "\n");
		
								// TRANSCRIPTS //
		// Start of section with transcripts table
		sb.append("<div class=\"mobileTurn\">Rotate to see Transcript Table</div>\n");
		
		sb.append("<div class=\"mobileHide\">\n");
		sb.append("<div class=\"UCSClink\">\n");
		sb.append("<a href=\"javascript:linkToUCSC2('" + gene.getNCBIid() + "','" + gene.getLocus() + "');\" "
				+ "title=\"Load RNAseq reads in UCSC browser in a new window\">View in UCSC Genome Browser to check results / gene predictions"
				+ "<img width=\"20\" height=\"20\" src=\"images/linkout.png\" alt=\"linkout\" class=\"linkoutImg\" /></a></div>\n");
		
		sb.append("<div class=\"transcript\"><!-- start of transcript div -->\n");		
		sb.append("<div class=\"leftTHead\">Adult &amp; Larval Transcript FPKMs</div>\n");
		
		sb.append("</div>");				
		sb.append(PageUtility.getTranscriptHelp(resNum));
		
		// Transcripts Table start and hard-coded first header row		
		sb.append("<table id=\"" + transTableID + "\" class=\"transcriptR\">\n");
		sb.append("<tr><th>Transcript</th><th colspan=\"13\">Adult</th><th colspan=\"9\">Larval</th></tr>\n");
		
		// TH row
		sb.append(getALTranscriptHeaderRow(tCat));
		sb.append("\n");
		
		// Results rows
		sb.append(getALTranscriptTableRows(expn, gene, tCat));
		
		sb.append("</table>\n");
		sb.append(PageUtility.getDownloadLink(gene, true));
		
		// CHECK IF THIS IS WHERE IT SHOULD GO
		String bioType = gene.getBioType();
		if(bioType.equals("pseudogene"))
		{
			sb.append(PageUtility.PSEUDOGENE);					
		}
		else if(bioType.equals("lincRNA"))
		{
			sb.append(PageUtility.LINC_RNA);					
		}
		else if(bioType.equals("snoRNA"))
		{
			sb.append(PageUtility.SNO_RNA);					
		}
		else if(bioType.equals("ncRNA"))
		{
			sb.append(PageUtility.NC_RNA);					
		}
		else if(bioType.equals("snRNA"))
		{
			sb.append(PageUtility.SN_RNA);					
		}
		else if(bioType.equals("misc_RNA")) 
		{
			sb.append(PageUtility.MISC_RNA);
		}
		
		boolean mito = gene.isMito();
		if(mito == true)
		{
			sb.append(PageUtility.MITO);
		}
		
		boolean para99 = gene.isPara99();
		if(para99 == true)
		{
			sb.append(PageUtility.REPEAT_GENE);
		}
		
		// close second results div
		sb.append("</div><!-- end of AL transcript div -->\n");	
		
		return sb.toString();
	}

	// initializes index for cell IDs used to link gene and transcript cell selection
	public void initializeIndices(TissueCatalogue  tCat)
	{
		adultIndex = 1;
		int adultCount = tCat.getTissueCountByStage(PageUtility.ADULT);
		larvalIndex = adultCount + 1;		
	}
	
	// Create Columns for FPKM (abundance) and Enrichment for a particular condition (e.g. Adult, Larvae)
	private void getGeneTableColumns(StringBuilder sb, GeneTissueData data, int tableID, int cellID)
	{		
		String sd = "<span class=\"plusMinus\"> &plusmn; " + PageUtility.formatValues(data.getSD()) + "</span>";

		// deal with colour
		Color enrichCol = PageUtility.getEnrichmentColor(data.getEnrichment());
		boolean enrichTextWhite = PageUtility.isDark(PageUtility.getBrightness(enrichCol));	
		String enrichHTMLcolour = PageUtility.getHTMLcolour(enrichCol);
		
		Color abundCol = PageUtility.getAbundanceColor(data.getFPKM());
		boolean abundTextWhite = PageUtility.isDark(PageUtility.getBrightness(abundCol));
		String abundHTMLcolour = PageUtility.getHTMLcolour(abundCol);
		
		String fID = "f" + tableID + "_" + cellID;		// id for FPKM cell
		String eID = "e" + tableID + "_" + cellID;		// id for enrichment cell
		
		// Cell 2, 4 (or 6): FPKM
		if(data.getFPKM() >= 2) // no colour if less than 2
		{
			sb.append("<td onclick=\"hiliteGene(this)\" id=\"" + fID + "\" style=\"background-color:" + abundHTMLcolour + ";");
		}
		else
		{
			sb.append("<td onclick=\"hiliteGene(this)\" id=\"" + fID + "\" style=\"background-color:white;");
		}
		if(abundTextWhite)
		{
			sb.append("color: white;");
		}
		if(data.getStatus().equals("OK"))
		{
			sb.append("\">" + PageUtility.formatValues(data.getFPKM())  + sd + "</td>");
		}
		else
		{
			sb.append("\">n.a.</td>");
		}
		
		// Cell 3, 5 (or 7): enrichment
		double enrich = data.getEnrichment();
		if(enrich != -1)
		{
			sb.append("<td onclick=\"hiliteGene(this)\" id=\"" + eID + "\" style=\"background-color:" + enrichHTMLcolour + ";");
			if(enrichTextWhite)
			{
				sb.append("color: white;");
			}
			if(data.getStatus().equals("OK"))
			{
				sb.append("\">" + PageUtility.formatValues(enrich)  + "</td>");
			}
			else
			{
				sb.append("\">n.a.</td>");
			}
		}
		else
		{
			sb.append("<td onclick=\"hiliteGene(this)\" id=\"" + eID + "\" style=\"background-color:white\">n.a.</td>");
		}					
	}	
	
	// Create Columns for FPKM (abundance) for Whole (e.g. Adult, Larvae)
	private void getGeneTableColumns(StringBuilder sb, GeneTissueData data, int tableID)
	{
		String sd = "<span class=\"plusMinus\"> &plusmn; " + PageUtility.formatValues(data.getSD()) + "</span>";

		// deal with colour
		Color abundCol = PageUtility.getAbundanceColor(data.getFPKM());
		boolean abundTextWhite = PageUtility.isDark(PageUtility.getBrightness(abundCol));
		String abundHTMLcolour = PageUtility.getHTMLcolour(abundCol);
		// Cell 2, 4 (or 6): FPKM
		if(data.getFPKM() >= 2) // no colour if less than 2
		{
			sb.append("<td style=\"background-color:" + abundHTMLcolour + ";");
		}
		else
		{
			sb.append("<td style=\"background-color:white;");
		}
		if(abundTextWhite)
		{
			sb.append("color: white;");
		}
		if(data.getStatus().equals("OK"))
		{
			sb.append("\">" + PageUtility.formatValues(data.getFPKM())  + sd + "</td>");
		}
		else
		{
			sb.append("\">n.a.</td>");
		}
		// Cell 3, 5 (or 7): blank (don't present enrichment of 1)
		sb.append("<td style=\"background-color:white;");
		sb.append("\"></td>");
	}
	
	 // Returns a HTML table header row with sorted Adult and Larval tissue abbreviations
    public String getALTranscriptHeaderRow(TissueCatalogue tCat)
    { 
    	StringBuilder sb = new StringBuilder("<tr><td></td>");
  		// Go through array of adult/larval tissues adding abbreviation to StringBuilder
		for (int i=0; i<tCat.getAdularvListSize(); i++)
		{
			if(!tCat.getAdularvTissue(i).isReference())
			{
				sb.append("<td>" + tCat.getAdularvTissue(i).getAbbreviation() + "</td>");
			}
		}
    	sb.append("</tr>");
    	return sb.toString();
    }
 
	 // Returns a HTML table header row with Embryonic "ages"
    public String getETranscriptHeaderRow(TissueCatalogue tCat)
    { 
    	StringBuilder sb = new StringBuilder("");
    	sb.append("<tr><th><span class=\"mobileHide\">Stage</span><span class=\"immobileHide\">Embryo</span></th>");	// Stage or Embryo
    	
  		// Go through array of embryo tissues adding abbreviation to StringBuilder
		for (int i=0; i<tCat.getEmbryoListSize(); i++)
		{
			sb.append("<th>" + tCat.getEmbryoTissue(i).getAge() + "</th>");
		}  	
    	sb.append("</tr>");
    	return sb.toString();
    }
    
    // Returns a HTML table row for Embryo Gene data
    public String getEGeneTableRow(GeneExpression expn, Gene gene, TissueCatalogue tCat)
    {
    	GeneTissueDataSet gtds = expn.getGeneData( );   	
    	StringBuilder sb = new StringBuilder("<tr><td><span class=\"mobileHide\">" + gene.getNCBIid() + "</span></td>");	// ncbiID in first column
    	
    	// go through array and get TissueID
    	for (int i=0; i<tCat.getEmbryoListSize(); i++)
		{
    		// get data
			int id = tCat.getEmbryoTissue(i).getTissueID();
			GeneTissueData data = gtds.getGeneTissueDataByID(id);
			
			// deal with colour
			Color abundCol = PageUtility.getAbundanceColor(data.getFPKM());
			boolean abundTextWhite = PageUtility.isDark(PageUtility.getBrightness(abundCol));
			String abundHTMLcolour = PageUtility.getHTMLcolour(abundCol);
			// deal with sd
	    	String sd = "<span class=\"plusMinus\"> &plusmn; " + PageUtility.formatValues(data.getSD()) + "</span>";
	
			if(data.getFPKM() >= 2) // no colour if less than 2
			{
				sb.append("<td style=\"background-color:" + abundHTMLcolour + ";");
			}
			else
			{
				sb.append("<td style=\"background-color:white;");
			}
	    	
			if(abundTextWhite)
			{
				sb.append("color: white;");
			}  
			
			if(data.getStatus().equals("OK"))
			{	    	
				sb.append("\">" + PageUtility.formatValues(data.getFPKM())  + sd + "</td>");
			}
			else
			{
				sb.append("\">n.a.</td>");
			}
		}   	
    	sb.append("</tr>");
    	return sb.toString();
    }
    
    // Returns a HTML table For Embryo Transcript data
	private String getETranscriptTableRows(GeneExpression expn, Gene gene, TissueCatalogue tCat)
	{
		StringBuilder sb = new StringBuilder();
		// blank spacer row (not on mobile)
		sb.append("<tr class=\"mobileHide\"><td colspan=\"5\" style=\"background-color:white;\"></td></tr>");
		// Go through array of TranscriptTissueDataSet objects (for several, test with TC030701 or TC034347)
		for (int i=0; i<expn.getTranscriptDataSize(); i++)
		{
			TranscriptTissueDataSet ttds = expn.getTranscriptData(i);
			// get transcript details for first cell (suppress for mobile)
			String transcriptID = ttds.getTranscriptID();
			sb.append("<tr class=\"embryoT\"><td>" + transcriptID + "</td>");
	    	// go through array and get TissueID
	    	for (int j=0; j<tCat.getEmbryoListSize(); j++)
			{
	    		// get data
				int id = tCat.getEmbryoTissue(j).getTissueID();
				TranscriptTissueData data = ttds.getTranscriptTissueDataByID(id);
				// deal with colour
				Color abundCol = PageUtility.getAbundanceColor(data.getFPKM());
				boolean abundTextWhite = PageUtility.isDark(PageUtility.getBrightness(abundCol));
				String abundHTMLcolour = PageUtility.getHTMLcolour(abundCol);				
				// deal with sd
		    	String sd = "<span class=\"plusMinus\"> &plusmn; " + PageUtility.formatValues(data.getSD()) + "</span>";

				if(data.getFPKM() >= 2) // no colour if less than 2
				{
					sb.append("<td style=\"background-color:" + abundHTMLcolour + ";");
				}
				else
				{
					sb.append("<td style=\"background-color:white;");
				}
		    	
				if(abundTextWhite)
				{
					sb.append("color: white;");
				} 
				
				if(data.getStatus().equals("OK"))
				{	    	
					sb.append("\">" + PageUtility.formatValues(data.getFPKM())  + sd + "</td>");
				}
				else
				{
					sb.append("\">n.a.</td>");
				}
			}
	    	sb.append("</tr>");
		}	
		return sb.toString();	
	}

    // Returns a HTML table For Adult and Larval Transcript data
	private String getALTranscriptTableRows(GeneExpression expn, Gene gene, TissueCatalogue tCat)
	{
		StringBuilder sb = new StringBuilder();
	
		// Go through array of TranscriptTissueDataSet objects (each for a distinct transcript) held in Expression object
		// Might be better sorted by Transcript name suffix
		for (int i=0; i<expn.getTranscriptDataSize(); i++)
		{
			TranscriptTissueDataSet ttds = expn.getTranscriptData(i);
			// get transcript details for left two identifier cells
			String transcriptID = ttds.getTranscriptID();
			
			sb.append("<tr><td>" + transcriptID + "</td>");
			
			// go through array of pre-sorted Tissue objects for Adult and Larval and get TissueIDs
			for (int j=0; j<tCat.getAdularvListSize(); j++)
			{	
				if(!tCat.getAdularvTissue(j).isReference())
				{
					int id = tCat.getAdularvTissue(j).getTissueID();
					TranscriptTissueData data = ttds.getTranscriptTissueDataByID(id);
					if(data != null)
					{
						Color abundCol = PageUtility.getAbundanceColor(data.getFPKM());
						String abundHTMLcolour = PageUtility.getHTMLcolour(abundCol);
						sb.append("<td style=\"background-color:" + abundHTMLcolour + "; \"></td>");
					}
					else
					{
						sb.append("<td style=\"background-color:white; \"></td>");
					}
				}
			}
			
			sb.append("</tr>\n");
		}				
    	return sb.toString();		
	}
	
	// generates html string for SVG graphic
	private String getSVG(TissueCatalogue tCat)
	{
		int listSize = tCat.getEmbryoListSize();		
		double[] fpkms = new double[listSize];
		double[] errors = new double[listSize];
		for(int i=0; i<listSize; i++)
		{
			int id = tCat.getEmbryoTissue(i).getTissueID();
			GeneTissueDataSet dataset = expn.getGeneData();
			GeneTissueData data = dataset.getGeneTissueDataByID(id);
			fpkms[i] = data.getFPKM();
			errors[i] = data.getSD();
		}		
		ImageCreator ic = new ImageCreator(gene.getNCBIid(), tCat, fpkms, errors, false);
		return ic.getSVG();
	}
	
}
