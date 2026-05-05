// Does all the Profile Search stuff — modified from Scott's version that did a lot of this in the Servlet class
// 06.03.2021
// Last update 08.03.2021

import java.util.Arrays;

public class ProfileSearch 
{	
	private Expression [] expressList;			// Array of Expression
	private Gene [] geneList;	
	private ProfileTissueData[] boncompList;
	
	int displayNum = 0;

	public ProfileSearch(String geneID, GeneExpression expresssion, boolean aleProfile, boolean byPearson, double rCut, int displayMax, TissueCatalogue tCat)
	{	
		// make a ProfileTissueDataSet for the query geneID and retrieve the array
		ProfileTissueDataSet dataSet = new ProfileTissueDataSet(geneID, aleProfile);
		ProfileTissueData[] dataList = dataSet.getList();
		int dataSetListSize = dataSet.getListSize();								// "probeCount" This was 16592 — total number of Tribolium genes
		
		// make a ProfileTissueData object for the query gene and populate it from GeneExpression object
		ProfileTissueData queryData = new ProfileTissueData(geneID);
		for (int i = 0; i< expresssion.getGeneData().getGeneDataLength(); i++)	
		{
			if(expresssion.getGeneData().getGeneTissuedata(i) != null)
			{
				ProfileDatum datum = new ProfileDatum(expresssion.getGeneData().getGeneTissuedata(i).getGeneID(),	
						expresssion.getGeneData().getGeneTissuedata(i).getFPKM(), expresssion.getGeneData().getGeneTissuedata(i).getStatus(),
						expresssion.getGeneData().getGeneTissuedata(i).getTissueID());
				queryData.addDatum(datum);
			}			
		}
				
		// Do the comparison
		ProfileComparison comparison = new ProfileComparison(queryData, dataList, dataSetListSize, byPearson, rCut);
		ProfileTissueData[] compList = comparison.getGenExCorList();						// The size of this list was 10122 for Tribolium genes	
		
		// Make the Bonferroni P correction
		double pCut = 0.05;			
		boncompList = bonferroniCorr(compList, pCut);				// The size of this list was 64 for TC006440
		
		// Find how many above rCutoff
		int aboveR = 0;		
		for(int i = 0; i < boncompList.length; i++)
		{
			if(boncompList[i].getRstat() > rCut)
			{
				aboveR++;
			}
		}	
		
		// Reorder list by r, sort, and cut to number above rCutoff
		CorrelationComparator correlationComparator = new CorrelationComparator();
		Arrays.sort(boncompList, correlationComparator);		
		ProfileTissueData[] newList = new ProfileTissueData[aboveR];
		System.arraycopy(boncompList, 0, newList, 0, aboveR);
		boncompList = newList;											
		int boncompListSize = boncompList.length;		// or could just use aboveR
		
		// Allow for fewer hits than user has selected as max 
		if(displayMax > boncompListSize) { displayNum = boncompListSize;}
		else{ displayNum = displayMax;}
		
		expressList = new Expression[displayNum];
		geneList = new Gene[displayNum];	
		
		for(int i=0; i<displayNum; i++)
		{
			GeneSearch gs = new GeneSearch(boncompList[i].getGeneID(), "geneID", tCat);
			Expression express;
			express = gs.getExpression();
			expressList[i] = express;
			Gene gene = gs.getGene();
			geneList[i] = gene;
		}	
	}
	
	// Bonferroni correction of P values in Profile Search 
	public ProfileTissueData[] bonferroniCorr(ProfileTissueData[] genExCorList, double pCut)
	{		
		for(int i = 0; i < genExCorList.length; i++)
		{	
			genExCorList[i].setPstat(genExCorList[i].getPstat() * genExCorList.length);
		}	
	
		ProfileTissueData[] cutList = new ProfileTissueData[genExCorList.length];	
		int count = 0;
		
		//cut out results with p value less than the cut-off
		for(int i = 0; i < genExCorList.length; i++)
		{	
			if(genExCorList[i].getPstat() < pCut)
			{
				cutList[count] = genExCorList[i];
				count++;
			}
		}
		
		ProfileTissueData[] shorterList = new ProfileTissueData[count];	
		System.arraycopy(cutList, 0, shorterList, 0, count);	
		genExCorList = shorterList;

		return genExCorList;
	}

	// get array of Expression objects from this search
	public Expression[] getExpressList()
	{
		return expressList;
	}
	
	public Gene[] getGeneList()
	{
		return geneList;
	}
	
	public int getDisplayNumber()
	{
		return displayNum;
	}

	public ProfileTissueData[] getDataList()
	{
		return boncompList;
	}
}
