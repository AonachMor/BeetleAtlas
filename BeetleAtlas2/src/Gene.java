 /*
Simple class to model Gene and its transcripts
DPL 16.04.2019
09.12.2023 ortho and para booleans added, SQL components removed.
BeetleAtlas2 update 25.09.2024
Last update: 18.10.2025 
*/

public class Gene
{
	private String ncbiID;
	private String symbol;
	private String tcID;
	private String product;
	private String biotype;
	private String locus;
	private boolean ortho;					// true if fly orthologues
	private boolean para;					// true if beetle paralogues
	private boolean para99;					// true if beetle paralogues with >=99% identity
	private boolean mito;					// true if mitochondrial-encoded gene
	private boolean discontinued;			// true if NCBI ID is in Tcas5.2, but not TriCast 1.1
	private Transcript [] transcriptList;
	private final int TRANSCRIPT_LENGTH = 10;
	private int transcriptListSize;
	
	public Gene(String ncbiID, String symbol, String tcID, String locus, String product, String biotype, boolean ortho, boolean para, boolean para99, boolean mito, boolean discontinued)
	{
		this.ncbiID = ncbiID;
		this.symbol = symbol;
		this.tcID = tcID;
		this.product = product;
		this.biotype = biotype;
		this.locus = locus;
		this.ortho = ortho;
		this.para = para;
		this.para99 = para99;
		this.mito = mito;
		this.discontinued = discontinued;
		
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
	
	public String getNCBIid()
	{
		return ncbiID;
	}
	
	public String getSymbol()
	{
		if(symbol==null){ symbol = "";}
		return symbol;
	}
	
	public String getTCid()
	{
		if(tcID==null){ tcID = "";}
		return tcID;
	}
	
	public String getProduct()
	{
		if(product==null){ product = "";}
		return product;
	}
	
	public String getBioType()
	{
		return biotype;
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
	
	public boolean isPara99()
	{
		return para99;
	}
	
	public boolean isMito()
	{
		return mito;
	}
	
	public boolean isDiscontinued()
	{
		return discontinued;
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
		// NCBI ID
		sb.append("<div class=\"geneInfo\"><span class=\"infoCaption\">NCBI ID</span><span class=\"mobileHide\"><br /></span>");
		sb.append("<span class=\"infoContent\"><a href=\"javascript:linkToNCBI('" + ncbiID + "');\" title=\"View info for " + ncbiID + " at NCBI/NLM\">" + ncbiID + "</a></span></div>\n");
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
		// TC ID and mode switch link
		sb.append("<div class=\"geneInfo\"><span class=\"infoCaption\">TC Equivalent</span><span class=\"mobileHide\"><br /></span>");
		if(tcID != null)
		{
			sb.append("<span class=\"infoContent\"><a href=\"javascript:openLinkWindow('https://motif.mvls.gla.ac.uk/BeetleAtlas/?search=gene&gene=" + tcID + "&idtype=geneID');\" "
					+ "title=\"View tissue expression of " + tcID + " in OGS3 Mode\">" + tcID + "</a></span></div>\n");
		}
		else
		{	
			sb.append("<span class=\"infoContent mobileHide\"> not available</span></div>\n");
		}
		//Ortho
		if(ncbiID != null)	// modified Ortho version, not using local getOrtho() — js method needs changing ???
		{
			sb.append("<div class=\"geneInfo\"><span class=\"infoCaption mobileHide\">Fly Homologues</span><span class=\"mobileHide\"><br /></span>");			
			if(ortho)
			{
				sb.append("<span class=\"infoContent mobileHide\">"
						+ "<a href=\"javascript:listOrthologues2('" + ncbiID + "');\" title=\"List Drosophila homologues available on FlyAtlas2\">"
								+ " Fly homologues</a></span></div>\n");			
			}
			else
			{
				sb.append("<span class=\"infoContent mobileHide\"> none identified</span></div>\n");
			}					
		}
		// Para
		if(ncbiID != null)	// modified Para version, not using local getPara()
		{
			sb.append("<div class=\"geneInfo\"><span class=\"infoCaption mobileHide\">Beetle Paralogues</span><span class=\"mobileHide\"><br /></span>");
			if(para)
			{
				sb.append("<span class=\"infoContent mobileHide\">"
						+ "<a href=\"javascript:listParalogues2('" + ncbiID + "');\" title=\"List Tribolium paralogues available on BeetleAtlas\"> "
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
		return ncbiID + "\t" + symbol   + "\t" + tcID + "\t" + product + "\t" + biotype  + "\t" + locus  
				+ "\t" + ortho  + "\t" + para  + "\t" + mito  + "\t" + discontinued;
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
