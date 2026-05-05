 /*
Simple class to model Gene and its transcripts
DPL 16.04.2019
09.12.2023 ortho and para booleans added, SQL components removed.
22–23.05.2025 ncbiID added and output HTML changed
*/

public class Gene
{
	private String geneID;
	private String symbol;
	private String locus;
	private String product;
	private String ncbiID;
	private boolean ortho;					// true if fly orthologues
	private boolean para;					// true if beetle paralogues
	private Transcript [] transcriptList;
	private final int TRANSCRIPT_LENGTH = 10;
	private int transcriptListSize;
	
	public Gene(String geneID, String symbol, String locus, String product, String ncbiID, boolean ortho, boolean para)
	{
		this.geneID = geneID;
		this.symbol = symbol;
		this.locus = locus;
		this.product = product;
		this.ncbiID = ncbiID;
		this.ortho = ortho;
		this.para = para;
		
		transcriptList = new Transcript [TRANSCRIPT_LENGTH];
	}
	
	public void addTranscript(Transcript trans)
	{
		//check for occupancy of array and expand as required
		if(transcriptListSize>transcriptList.length - 1)
		{
			Transcript[] newList = new Transcript[transcriptListSize*2];
			System.arraycopy(transcriptList, 0, newList, 0, transcriptListSize);
			transcriptList = newList;
		}
		transcriptList[transcriptListSize] = trans;
		transcriptListSize++;
	}
	
	public Transcript getTranscript(int pos)
	{
		return transcriptList[pos];
	}
	
	public int getTranscriptListSize()
	{
		return transcriptListSize;
	}

	// Returns transcript corresponding to a particular TranscriptID
	public Transcript getTranscriptByID(String id)
	{
		for(int i=0; i<transcriptListSize; i++)
		{
			if (id.equals(transcriptList[i].getTranscriptID()))
			{
				return transcriptList[i];
			}
		}
		return null;
	}
	
	//-----------------------------------------------//
	
	public String getGeneID()
	{
		return geneID;
	}
	
	public String getSymbol()
	{
		if(symbol==null){ symbol = "";}
		return symbol;
	}
	
	public String getProduct()
	{
		if(product==null){ product = "";}
		return product;
	}
	
	public String getLocus()
	{
		if(locus==null){ locus = "";}
		return locus;
	}
	
	public boolean hasOrtho()
	{
		return ortho;
	}
	
	public boolean hasPara()
	{
		return para;
	}
	
	// multi-page version
	public String getHTMLFormatted(int index, boolean conceal)
	{
		StringBuilder sb = new StringBuilder();

		// span with toggle visibility button (down-pointing and up-pointing black triangles, &#9660; and &#9652;
		if(conceal)
		{
			sb.append("<div class=\"geneInfo\">");
			sb.append("<a href=\"javascript:toggleConcealed('bt_" + index + "','hs_" + index + "','&#9658;','&#9660;');\" title=\"reveal results\">");
			sb.append("<span id=\"bt_" + index + "\" class=\"infoContent onOff\">&#9658;</span></a>");
			sb.append("</div>");
		}
		// TC ID
		sb.append("<div class=\"geneInfo\"><span class=\"infoCaption\">TC ID</span><span class=\"mobileHide\"><br /></span>");
		sb.append("<span class=\"infoContent\"><a href=\"javascript:linkToiBeetle('" + geneID + "');\" title=\"View info for " + geneID + " at iBeetle-Base\">" 
				+ geneID + "</a></span></div>\n");
		// Symbol
		if(symbol != null)
		{
			sb.append("<div class=\"geneInfo\"><span class=\"infoCaption\">Symbol</span><span class=\"mobileHide\"><br /></span>");
			sb.append("<span class=\"infoContent\"> " + symbol + "</span></div>\n");
		}
		// Product
		if(product != null)
		{
			sb.append("<div class=\"geneInfo\"><span class=\"infoCaption\">Product</span><span class=\"mobileHide\"><br /></span>");
			sb.append("<span class=\"infoContent\"> " + PageUtility.checkSuper(product) + "</span></div>\n");
		}
		// NCBI ID
		sb.append("<div class=\"geneInfo\"><span class=\"infoCaption\">NCBI Equivalent</span><span class=\"mobileHide\"><br /></span>");
		if(ncbiID != null)
		{
			sb.append("<span class=\"infoContent\">"
					+ "<a href=\"javascript:openLinkWindow('https://motif.mvls.gla.ac.uk/BeetleAtlas2/?search=gene&gene=" + ncbiID + "&idtype=ncbiID');\" "
					+ "title=\"View tissue expression of " + ncbiID + " in NCBI Mode\">" + ncbiID + "</a></span></div>\n");
		}
		else
		{
			sb.append("<span class=\"infoContent\"> not available</span></div>\n");
		}
		// Ortho
		if(geneID != null)	// modified Ortho version, not using local getOrtho() — js method needs changing
		{
			sb.append("<div class=\"geneInfo\"><span class=\"infoCaption mobileHide\">Fly Homologues</span><span class=\"mobileHide\"><br /></span>");			
			if(ortho)
			{
				sb.append("<span class=\"infoContent mobileHide\">"
						+ "<a href=\"javascript:listOrthologues('" + geneID + "');\" title=\"List Drosophila homologues available on FlyAtlas2\">"
								+ " Fly homologues</a></span></div>\n");			
			}
			else
			{
				sb.append("<span class=\"infoContent mobileHide\"> none identified</span></div>\n");
			}					
		}
		// Para
		if(geneID != null)	// modified Para version, not using local getPara()
		{
			sb.append("<div class=\"geneInfo\"><span class=\"infoCaption mobileHide\">Beetle Paralogues</span><span class=\"mobileHide\"><br /></span>");
			if(para)
			{
				sb.append("<span class=\"infoContent mobileHide\">"
						+ "<a href=\"javascript:listParalogues('" + geneID + "');\" title=\"List Tribolium paralogues available on BeetleAtlas\"> "
								+ "Beetle Paralogues</a></span></div>\n");						
			}
			else
			{
				sb.append("<span class=\"infoContent mobileHide\"> none identified</span></div>\n");
			}
		}
	
		return sb.toString();
	}
	

	
	// for testing
	public String geneInfoToString()
	{
		return geneID + "\t" + symbol   + "\t" + product  + "\t" + locus;
	}

	// for testing
	public String transcriptInfoToString()
	{
		StringBuilder sb = new StringBuilder();
		for(int i=0; i<transcriptListSize; i++)
		{
			sb.append(transcriptList[i].toString());
			sb.append("\n");
		}
		return sb.toString();
	}
}
