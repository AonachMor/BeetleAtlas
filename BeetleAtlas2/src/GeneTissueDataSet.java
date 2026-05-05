// Class to hold a set of GeneTissueData objects for all the tissues of an experiment
// BeetleAtlas2 19.09.2024

public class GeneTissueDataSet
{
	private String ncbiID;
	private GeneTissueData [] geneDataList;
	private final int LIST_LENGTH = 30;
	private int listSize;							// occupancy i.e. number of tissues
	private final int NUM_REPLICATES = 3;			// This refers to the replicates in ea GeneTissuedata object which should be the same
	private TissueCatalogue tCat;
	
	public GeneTissueDataSet(String ncbiID, TissueCatalogue tCat)
	{
		this.ncbiID = ncbiID;
		geneDataList = new GeneTissueData [LIST_LENGTH];
		this.tCat = tCat;
	}
	
	public void add(GeneTissueData data)
	{
		//check for occupancy of array and expand as required
		if(listSize>geneDataList.length - 1)
		{
			GeneTissueData[] newList = new GeneTissueData[listSize*2];
			System.arraycopy(geneDataList, 0, newList, 0, listSize);
			geneDataList = newList;
		}
		geneDataList[listSize] = data;
		listSize++;
	}
	
	// For all non-reference GeneTissuedata objects in geneDataList[], enrichment is now calculated and set
	public void calculateEnrichments()
	{
		// Working values for use in calculations - may be massaged to prevent division by zero etc.
		double adultRefFPKM = 0.0;
		double larvalRefFPKM = 0.0;
		// Actual values so can check how to present edge cases
		double rawAdultRefFPKM = 0.0;
		double rawLarvalRefFPKM = 0.0;
		
		// retrieve FPKMs from adult/larval whole tissue references
		for (int i=0; i<listSize; i++)
		{
			GeneTissueData gtd = geneDataList[i];
			int id = gtd.getTissueID();
			boolean reference = tCat.getRefStatusByID(id);
			if(reference == true)
			{
				String stage = tCat.getStageByID(id);

				if(stage.equals(PageUtility.ADULT))
				{
					adultRefFPKM = gtd.getFPKM();
					rawAdultRefFPKM = adultRefFPKM;
				}
				else if(stage.equals(PageUtility.LARVAL))
				{
					larvalRefFPKM = gtd.getFPKM();
					rawLarvalRefFPKM = larvalRefFPKM;
				}
			}
		}
		// Set reference FPKMs to minimum of 2 to avoid misleadingly large enrichments
		if(adultRefFPKM < 2.0)
		{
			adultRefFPKM = 2.0;
		}
		if(larvalRefFPKM < 2.0)
		{
			larvalRefFPKM = 2.0;
		}
		
		// Now go through geneDataList array retrieving objects and setting enrichment
		for (int i=0; i<listSize; i++)
		{		
			// Get tissue FPKM and adjust to 2.0 as minimum for enrichment calculation 
			GeneTissueData data = geneDataList[i];
			double fpkm = data.getFPKM();

			// Get tissueID and find if male/female/larval
			int id = data.getTissueID();
			String stage = tCat.getStageByID(id);
			
			double refFPKM = 0.0;	// declare reference FPKM
			double rawRefFPKM = 0.0;
			if(stage.equals(PageUtility.ADULT))
			{
				refFPKM = adultRefFPKM;
				rawRefFPKM = rawAdultRefFPKM;
			}
			else if(stage.equals(PageUtility.LARVAL))
			{
				refFPKM = larvalRefFPKM;
				rawRefFPKM = rawLarvalRefFPKM;
			}
			// Calculate and set enrichment 
			double enrichment = fpkm / refFPKM;		// Standard
			
			// low expt and low ref — flag that this is meaningless
			if(rawRefFPKM < 2 && fpkm < 2)
			{
				enrichment = -1;
			}
		
			data.setEnrichment(enrichment);
		}
	}
	
			// Accessor methods  //
	
	public GeneTissueData[] getGeneTissuedata()
	{
		return geneDataList;
	}
	
	public GeneTissueData getGeneTissuedata(int pos)
	{
		return geneDataList[pos];
	}
	
	public int getGeneDataSize()
	{
		return listSize;
	}
	
	public int getGeneDataLength()
	{
		return LIST_LENGTH;
	}
	
	public String getNCBIid()
	{
		return ncbiID;
	}
	
	public int getNumReplicates()
	{
		return NUM_REPLICATES;
	}
	
		// Get method for members of geneDataList

	public GeneTissueData getGeneTissueDataByID(int id)
	{
		for (int i=0; i<listSize; i++)
		{
			if(geneDataList[i].getTissueID() == id)
			{
				return geneDataList[i];
			}
		}
		return null;
	}
	
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		for (int i=0; i<listSize; i++)
		{
			sb.append(geneDataList[i].toString());
			sb.append("\n");
		}
		return sb.toString();
	}
}
