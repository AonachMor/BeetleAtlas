/* Tissue Doublet
   Models adult/larval Tissue 'pair' with same uniTissueID
   DPL 21.06.2018
*/ 

public class TissueDoublet
{
	private int uniTissueID;			// adult/larval unified tissue ID (e.g. for matching on table layout)
	private Tissue adultTissue;			// adult Tissue object (if exists)
	private Tissue larvalTissue;		// larval Tissue object (if exists)
	private int displayPosition;		// position in which the pair should be displayed (e.g. in a table)
	private final int REF_POS = 20;		// display position corresponding to reference — specified in uniTissue.txt Ugh!
	private String uniTissueName;		// adult/larval unified tissue Name (for matching on table layout)
	private boolean reference;			// flag for whole tissue
	
	public TissueDoublet(int uniTissueID, Tissue adultTissue, Tissue larvalTissue, int displayPosition, String uniTissueName)
	{
		this.uniTissueID = uniTissueID;
		this.adultTissue = adultTissue;
		this.larvalTissue = larvalTissue;
		this.displayPosition = displayPosition;
		this.uniTissueName = uniTissueName;
		if(displayPosition == REF_POS)
		{
			reference = true;
		}
		else
		{
			reference = false;
		}
	}
	
	// Accessor methods	
	
	public int getUniTissueID()
	{
		return uniTissueID;
	}
	
	public String getUniTissueName()
	{
		return uniTissueName;
	}
	
	public Tissue getAdultTissue()
	{
		return adultTissue;
	}
	
	public Tissue getLarvalTissue()
	{
		return larvalTissue;
	}
	
	public int getDisplayPosition()
	{
		return displayPosition;
	}
	
	public boolean hasAdultTissue()
	{
		if(adultTissue == null)
		{return false;}
		else
		{return true;}
	}	
	
	public boolean hasLarvalTissue()
	{
		if(larvalTissue == null)
		{return false;}
		else
		{return true;}
	}
	
	public boolean isReference()
	{
		return reference;
	}
	
	public String toString()
	{
		return("UniTissueID: " + uniTissueID + ", Adult tissue: " + adultTissue 
				+ ", Larval tissue: " + larvalTissue + ", Display position: " + displayPosition + ", UniTissue Name: " + uniTissueName);
	}
}


