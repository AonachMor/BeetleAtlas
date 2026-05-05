// Performs repeated Gene Searches on list of identifiers
// DPL 06.06.2023


public class BulkSearch 
{
	int numIDs;				// Total No. IDs in submitted list
	int validIDs = 0;		// Total of valid IDs in list
	int invalidIDs = 0;		// Total of invalid IDs in list
	
	final static String ID_TYPE = "geneID";	// Only TC-type GeneIDs accepted
	
	Expression [] expressList;
	final int LIST_LENGTH = 100;
	int expressListSize = 0;	
	
	String[] geneIDs;
	
	Gene [] geneList;
	int geneListSize = 0;
	
	String [] invalidIDList;
	int invalidListSize = 0;
	
	public BulkSearch(String searchList, TissueCatalogue tCat)
	{	
		// Split search list into individual Strings and place in array (length is geneIDs.length)
		geneIDs = searchList.split(",");
	
	    // Restrict to EXPR_LENGTH (100)
	    numIDs = geneIDs.length;
	    if(numIDs > LIST_LENGTH)
	    {
	    	numIDs = LIST_LENGTH;
	    }
	    
	    if(numIDs>0)
	    {
			expressList = new Expression[LIST_LENGTH];
			geneList = new Gene[LIST_LENGTH];			// same length as Expression array
			invalidIDList = new String[LIST_LENGTH];
			
	    	for(int i=0; i<numIDs; i++)
	    	{
	    		GeneSearch gs = new GeneSearch(geneIDs[i].trim(), ID_TYPE, tCat);	// need to trim in case space at end of lines
	    		// Get expression data
	    		Expression express = gs.getExpression();
	    		Gene gene = gs.getGene();
	    		if(express != null)
	    		{
					expressList[i] = express;
					expressListSize++;			
					geneList[i] = gene;
					geneListSize++;
					validIDs++;
	    		}
	    		else
	    		{
	    			invalidIDList[invalidListSize] = geneIDs[i];
					invalidListSize++;
					invalidIDs++;	
	    		}
	    	}
	    }
	}
	
	// get array of Expression objects from this search
	public Expression[] getExpressList()
	{
		return expressList;
	}
	
	// get array of Gene objects from this search	
	public Gene[] getGeneList()
	{
		return geneList;
	}
	
	// get array of Gene objects from this search	
	public String[] getInvalidIDList()
	{
		return invalidIDList;
	}
	
	// Number of IDs submitted
	public int getNumIDs()
	{
		return numIDs;
	}
	
	public int getNumValidIDs()
	{
		return validIDs;
	}
	
	public int getNumInvalidIDs()
	{
		return invalidIDs;
	}
	
	public String getQueryID()
	{
		return geneIDs[0];
	}
	
}
	