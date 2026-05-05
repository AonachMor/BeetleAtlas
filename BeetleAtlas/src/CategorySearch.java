// Searches database for genes by a keyword that are most expressed in a particular tissue
// 06.06.2017
// Last Update 01.12.2023

import java.sql.*;

public class CategorySearch 
{
	String [] idList;						// array to hold GeneIDs retrieved in query
	final int GENEID_LENGTH = 20000;
	int idListSize = 0;
	
	Expression [] expressList;
	final int EXPR_LENGTH = 250;			// not allowed more than 250 results
	int expressListSize = 0;	
	
	Gene [] geneList;
	int geneListSize = 0;
	
	int actualDisplayed = 0;				// actual number to be displayed (can be less than displayMax)
	int refID = 0;							// ID of whole Tissue corresponding to selected tissue (adult or larval)
	
	public CategorySearch(String stage, int tissueID, String order, boolean byAbundance, String keyword, int displayMax,TissueCatalogue tCat)
	{
		// Get tissueID of reference whole tissue correlated to user selection (for Enrichment calculation)		
		String refStage = tCat.getStageByID(tissueID);
		refID = tCat.getRefIDbyStage(refStage);				
		if(refID == -1)
		{ return; }
		
		expressList = new Expression[EXPR_LENGTH];
		geneList = new Gene[EXPR_LENGTH];			// same length as parallel array
			
		Connect cnt = new Connect();
		Connection conn = cnt.getConnection();
		
		if(byAbundance)
		{
			// get list of GeneIDs satisfying the keyword/tissue criteria
			idList = new String [GENEID_LENGTH];			
			makeCatAbundanceQuery(keyword, tissueID, conn);
			
			// allow for fewer hits than user has selected as max
			if(displayMax > idListSize) { actualDisplayed = idListSize;}
			else{ actualDisplayed = displayMax;}
			
			// get Expression object for each of GeneIDs retrieved (through GeneSearch)
			for(int i=0; i<actualDisplayed; i++)
			{
				GeneSearch gs = new GeneSearch(idList[i], "geneID", tCat);
				Expression express;
				express = gs.getExpression();
				expressList[i] = express;
				expressListSize++;
				Gene gene = gs.getGene();
				geneList[i] = gene;
				geneListSize++;
			}
		}
		else	// by Enrichment
		{
			// get list of GeneIDs satisfying the keyword/tissue criteria
			idList = new String [GENEID_LENGTH];			
			makeCatEnrichmentQuery(keyword, tissueID, conn);
			
			// allow for fewer hits than user has selected as max
			if(displayMax > idListSize) { actualDisplayed = idListSize;}
			else{ actualDisplayed = displayMax;}
			
			// get Expression object for each of GeneIDs retrieved (through GeneSearch)
			for(int i=0; i<actualDisplayed; i++)
			{
				GeneSearch gs = new GeneSearch(idList[i], "geneID", tCat);
				Expression express;
				express = gs.getExpression();
				expressList[i] = express;
				expressListSize++;
				Gene gene = gs.getGene();
				geneList[i] = gene;
				geneListSize++;
			}		
		}
		
		// close connection
		if(conn != null)
		{
			try { conn.close();}
			catch(Exception e){System.out.println("Can't close.");}
		}	
	}

	// Get sorted list of GeneIDs of genes in the selected category that are most abundant in a particular tissue
	private void makeCatAbundanceQuery(String keyword, int tissueID,  Connection conn)
	{
		String catAbundanceQuery = new String();
		catAbundanceQuery = "CAT_ABUNDANCE_GENES_BY_TISSUE";
			
		ParamQuery parCAQ = DBQuery.getParamQuery(catAbundanceQuery);
		try 
		{
			parCAQ.setPrepStatement(conn);
		} 
		catch (SQLException e) 
		{System.out.println(e.toString());}	

		try 
		{
			PreparedStatement prepStat = parCAQ.getPrepStatement();
			prepStat.setString(1,keyword);
			prepStat.setInt(2, tissueID);			
			ResultSet resSet = prepStat.executeQuery();
			if(resSet.first())
			{
				resSet.beforeFirst();		// hack to reset cursor as 'if' moves it on a row!
				while (resSet.next())		// moves to next row while rows remain
				{	
					String geneID = resSet.getString("GeneID");
					idList[idListSize] = geneID;
					idListSize++;
				}
			}
			try { if (resSet != null) resSet.close(); } catch (Exception e) {}; 		// added as server precaution
		    try { if (prepStat != null) prepStat.close(); } catch (Exception e) {};		// added as server precaution	    
		}
		catch (SQLException e)
		{
			System.out.println("SQL Exception: " + e.toString());
		}
	}

	// Get sorted list of GeneIDs  of genes in the selected category that are most enhanced in a particular tissue
	private void makeCatEnrichmentQuery(String keyword, int tissueID, Connection conn)
	{
		String catEnrichmentQuery = new String();
		catEnrichmentQuery = "CAT_ENRICHMENT_GENES_BY_TISSUE";
		
		ParamQuery parCEQ = DBQuery.getParamQuery(catEnrichmentQuery);
		try 
		{
			parCEQ.setPrepStatement(conn);
		} 
		catch (SQLException e) 
		{System.out.println(e.toString());}	

		try 
		{
			PreparedStatement prepStat = parCEQ.getPrepStatement();
			prepStat.setString(1,keyword);		
			prepStat.setInt(2, refID);
			prepStat.setInt(3, tissueID);
			ResultSet resSet = prepStat.executeQuery();
			if(resSet.first())
			{
				resSet.beforeFirst();		// hack to reset cursor as 'if' moves it on a row!
				while (resSet.next())		// moves to next row while rows remain
				{	
					String geneID = resSet.getString("GeneID");
					idList[idListSize] = geneID;
					idListSize++;
				}
			}
			try { if (resSet != null) resSet.close(); } catch (Exception e) {}; 		// added as server precaution
		    try { if (prepStat != null) prepStat.close(); } catch (Exception e) {};		// added as server precaution
		}
		catch (SQLException e)
		{
			System.out.println("SQL Exception: " + e.toString());
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

	// get size of ID list from the search — needed to inform user of results not displayed
	public int getIDListSize()
	{
		return idListSize;
	}
	
	// get number of results actually displayed
	public int getActualDisplayed()
	{
		return actualDisplayed;
	}
}
