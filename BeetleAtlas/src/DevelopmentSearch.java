// Searches database for genes with a high difference in tissue between adult and larval
// 18.01.2021
// Last update 01.12.2021

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DevelopmentSearch 
{	
	Expression [] expressList;
	final int EXPR_LENGTH = 250;
	int expressListSize = 0;	
	
	Gene [] geneList;							// array to hold genes to be sent for processing?
	int geneListSize = 0;
	
	DevelRatio [] develRatioList;				// array to hold devel ratios for individual genes
	final int DEVEL_LENGTH = 20000;
	int develRatioListSize = 0;
	
	int larvalRefID = 0;						// whole larval tissue reference for enhancement calculation
	int adultRefID = 0;							// whole adult tissue reference for enhancement calculation
	
	int actualDisplayed = 0;				// actual number to be displayed (can be less than displayMax)
	
	public DevelopmentSearch(String uniTissue, boolean adultGreater, int displayMax, TissueCatalogue tCat)
	{	
		// Get adult and larval TissueIDs corresponding to uniTissue		
		int larvalID = tCat.getTissueDoubletByUniTissueName(uniTissue).getLarvalTissue().getTissueID();
		int adultID = tCat.getTissueDoubletByUniTissueName(uniTissue).getAdultTissue().getTissueID();
		
		// Get IDs of reference larval and adult whole tissues corresponding to selected uniTissue (for Enrichment query)		
		String larvalRefStage = tCat.getStageByID(larvalID);
		larvalRefID = tCat.getRefIDbyStage(larvalRefStage);
		String adultRefStage = tCat.getStageByID(adultID);
		adultRefID = tCat.getRefIDbyStage(adultRefStage);				
		if(larvalRefID == -1 || adultRefID == -1)
		{ return; }
		
		expressList = new Expression[EXPR_LENGTH];
		geneList = new Gene[EXPR_LENGTH];			// same length as parallel array
		
		//Connect cnt = new Connect();
		//Connection conn = cnt.getConnection();
		
		develRatioList = new DevelRatio[DEVEL_LENGTH];
			
		makeDevelAbundanceQuery(larvalID, adultID, adultGreater);
		// Sort list created by query
		sortDevRatios();
		// Allow for fewer hits than user has selected as max 
		if(displayMax > develRatioListSize) 
		{ 
			actualDisplayed = develRatioListSize;
		}
		else
		{ 
			actualDisplayed = displayMax;
		}
		
		// Get Expression data for required number of genes
		for(int i=0; i<actualDisplayed; i++)
		{
			GeneSearch gs = new GeneSearch(develRatioList[i].getGeneID(), "geneID", tCat);
			Expression express;
			express = gs.getExpression();
			expressList[i] = express;
			expressListSize++;
			Gene gene = gs.getGene();
			geneList[i] = gene;
			geneListSize++;
		}
		// close connection
/*		if(conn != null)
		{
			try { conn.close();}
			catch(Exception e){System.out.println("Can't close.");}
		}*/	
	}
	
	private void makeDevelAbundanceQuery(int larvalID, int adultID, boolean adultGreater)
	{
		// Get sorted list of GeneIDs of genes that are most abundant in a particular tissue
		String develAbundanceQuery = new String();
		develAbundanceQuery = "DEVEL_ABUNDANCE";
			
		ParamQuery parDAQ = DBQuery.getParamQuery(develAbundanceQuery);
		Connect cnt = new Connect();
		Connection conn = cnt.getConnection();
		try 
		{
			parDAQ.setPrepStatement(conn);
		} 
		catch (SQLException e) 
		{System.out.println(e.toString());}	

		try 
		{
			PreparedStatement prepStat = parDAQ.getPrepStatement();
			prepStat.setInt(1, larvalID);	
			prepStat.setInt(2, adultID);	
			ResultSet resSet = prepStat.executeQuery();
			if(resSet.first())
			{
				resSet.beforeFirst();		// hack to reset cursor as 'if' moves it on a row!
				while (resSet.next())		// moves to next row while rows remain
				{	
					String geneID = resSet.getString("GeneID");
					double larvalFPKM = resSet.getDouble("larvalFPKM");
					double adultFPKM = resSet.getDouble("adultFPKM");
					
					// Set minimum values then calculate larval/adult ratio
					if(larvalFPKM < 2)
					{
						larvalFPKM = 2;
					}
					if(adultFPKM < 2)
					{
						adultFPKM = 2;
					}
					double ratio = 1.0;
					if(adultGreater)
					{
						ratio = adultFPKM/larvalFPKM;
					}
					else
					{
						ratio = larvalFPKM/adultFPKM;
					}
					// Generate a DevelRatio object and add to array only if > 4
					if (ratio > 4)
					{
						DevelRatio dr = new DevelRatio(geneID, ratio);					
						develRatioList[develRatioListSize] = dr;
						develRatioListSize++;
					}					
				}
			}
			try { if (resSet != null) resSet.close(); } catch (Exception e) {}; 	// added as server precaution
		    try { if (prepStat != null) prepStat.close(); } catch (Exception e) {};	// added as server precaution
		}
		catch (SQLException e)
		{
			System.out.println("SQL Exception: " + e.toString());
		}
		finally
		{
			 try { if (conn != null) conn.close(); } catch (Exception e) {}; // previously connection not closed
		}
	}

	// get array of Expression objects from this search
	public Expression[] getExpressList()
	{
		return expressList;
	}
	
	public int getActualDisplayed()
	{
		return actualDisplayed;
	}
	
	public Gene[] getGeneList()
	{
		return geneList;
	}
	
	// INNER CLASS
	
 	// For storing a developmental ratio for a particular gene
 	class DevelRatio
 	{
 		String geneID;	// geneID
 		double ratio;	// ratio of larval/adult or adult/larval
 		
 		DevelRatio(String geneID, double ratio)
 		{
 			this.geneID = geneID;
 			this.ratio = ratio;
 		}
 		public double getRatio()
 		{
 			return ratio;
 		}
 		public String getGeneID()
 		{
 			return geneID;
 		}
 	}
 	
 	// Sorts DevelRatios in reverse order of ratio, so highest ratios first in array
    private void sortDevRatios()
    {
    	DevelRatio first;		// holder for DevelRatio with highest value
		int firstPos;		
		for (int i=0; i<develRatioListSize-1; i++)		// do series of runs
		{
			for (int j=i+1; j<develRatioListSize; j++)	// for each run process list
			{
				first = develRatioList[i];			// first of unsorted assigned to first
				firstPos = i;

				if(first.getRatio() < develRatioList[j].getRatio())
				{
					firstPos = j;
				}
				
				first = develRatioList[firstPos];
				develRatioList[firstPos] = develRatioList[i]; 		// shift current first
				develRatioList[i] = first;							// replace
			}
		}   			
    }
    
}
