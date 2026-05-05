// Stores details of the FPKMs for the tissues for a single gene and the correlation values for a query (Formerly Probeset)
// 29.08.2012
// Adapted for BeetleAtllas 24.02.2021
// BeetleAtlas2 19.09.2024

public class ProfileTissueData 
{
	private String ncbiID;					// Tribolium TC ID
	
	private ProfileDatum[] dataList;		// Array of expression objects corresponding to probe - i.e. one for ea tissue/stage combination
	private final int LIST_LENGTH = 100;	// length of array
	private int listSize = 0;				// occupancy of array
	
	private double pStat; 					// P statistic for correlation significance
	private double rStat;					// r statistic for correlation

	// Constructor initializes array to which individual tissue FPKMs etc are added
	public ProfileTissueData(String ncbiID)
	{
		this.ncbiID = ncbiID;
		dataList = new ProfileDatum [LIST_LENGTH];
	}

	public void addDatum(ProfileDatum datum)
	{
		dataList[listSize] = datum;
		listSize++;
	}
	
	// returns an individual ProfileDatum on the basis of TissueID
	public ProfileDatum getDatum(int tissueID)
	{
		for(int i=0; i<listSize; i++)
		{
			ProfileDatum datum = dataList[i];
			if(datum.getTissueID() == tissueID)
			{
				return dataList[i];
			}
		}
		return null;
	}
		
	public String getNCBIid()
	{
		return ncbiID;
	}	
	
	// Stats setters and getters
	
	public void setRstat(double rStat)
	{
		this.rStat = rStat;
	}
	
	public double getRstat()
	{
		return rStat;
	}
	
	public void setPstat(double pStat)
	{
		this.pStat = pStat;
	}
	
	public double getPstat()
	{
		return pStat;
	}
}
