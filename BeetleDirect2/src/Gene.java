/*
Simple class to model Gene and its transcripts
DPL 16.04.2019
Last update 06.03.2021
Modified 22.09.2024 for BeetleAtlas 2
*/

public class Gene
{
	private String geneID;
	private String symbol;
	private String product;
	private String locus;
	private Transcript [] transcriptList;
	private final int TRANSCRIPT_LENGTH = 10;
	private int transcriptListSize;
	
	public Gene(String geneID, String symbol, String locus, String product)
	{
		this.geneID = geneID;
		this.symbol = symbol;
		this.product = product;
		this.locus = locus;
		
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
	
	// multi-page version SURELY NOT NEEDED
	public String getHTMLFormatted(int index, boolean conceal)
	{
		StringBuilder sb = new StringBuilder();

		// span with toggle visibility button (down-pointing and up-pointing black triangles, &#9660; and &#9652;
		if(conceal)
		{
			sb.append("<div class=\"geneInfo\">");
			sb.append("<a href=\"javascript:toggleConcealed('bt_" + index + "','hs_" + index + "','&#9654;','&#9660;');\" title=\"reveal results\">");
			sb.append("<span id=\"bt_" + index + "\" class=\"infoContent onOff\">&#9654;</span></a>");
			sb.append("</div>");
		}
		
		sb.append("<div class=\"geneInfo\"><span class=\"infoCaption\">Gene ID</span><span class=\"mobileHide\"><br /></span>");
		sb.append("<span class=\"infoContent\"> " + geneID + "</span></div>\n");
		
		if(symbol != null)
		{
			sb.append("<div class=\"geneInfo\"><span class=\"infoCaption\">Symbol</span><span class=\"mobileHide\"><br /></span>");
			sb.append("<span class=\"infoContent\"> " + symbol + "</span></div>\n");
		}
		if(product != null)
		{
			sb.append("<div class=\"geneInfo\"><span class=\"infoCaption\">Product</span><span class=\"mobileHide\"><br /></span>");
			sb.append("<span class=\"infoContent\"> " + PageUtility.checkSuper(product) + "</span></div>\n");
		}
		
		if(geneID != null)
		{
			sb.append("<div class=\"geneInfo\"><span class=\"infoCaption\">External Link</span><span class=\"mobileHide\"><br /></span>");
			sb.append("<span class=\"infoContent\"><a href=\"javascript:linkToiBeetle('" + geneID + "');\"> iBeetle-Base: " + geneID + "</a></span></div>\n");
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
	
	// Header info re Gene for text file
	public String getGeneInfoText()
	{
		StringBuilder sb = new StringBuilder();		
		
		sb.append("Gene ID\t" + geneID + "\n");
		
		if(symbol != null)
		{
			sb.append("Gene Symbol\t" + symbol + "\n");
		}
		
		if(product != null)
		{
			sb.append("Gene Product\t" + product + "\n");
		}
		
		return sb.toString();
	}
}
