// Searches database for top genes expressed preferentially in a particular tissue
// 22.06.2018
// Last Update 13.03.2021 Expression list length raised to 300 for testing 29.04.2023

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TopSearch 
{	
	String [] idList;						// array to hold GeneIDs retrieved in query
	final int GENEID_LENGTH = 20000;
	int idListSize = 0;
	
	Expression [] expressList;
	final int EXPR_LENGTH = 300;
	int expressListSize = 0;	
	
	Gene [] geneList;
	int geneListSize = 0;	
	
	int actualDisplayed = 0;				// actual number to be displayed (can be less than displayMax)
	int refID = 0;							// ID of whole Tissue corresponding to selected tissue (adult or larval)
	
	public TopSearch(int tissueID, boolean byAbundance, int displayMax, TissueCatalogue tCat)
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
			idList = new String [GENEID_LENGTH];			
			makeTopAbundanceQuery(tissueID, conn);
			
			// allow for fewer hits than user has selected as max (not really necessary)
			if(displayMax > idListSize) { actualDisplayed = idListSize;}
			else{ actualDisplayed = displayMax;}
			
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
		else		// by Enrichment
		{
			idList = new String [GENEID_LENGTH];			
			makeTopEnrichmentQuery(tissueID, conn);
			
			// allow for fewer hits than user has selected as max (not really necessary)
			if(displayMax > idListSize) { actualDisplayed = idListSize;}
			else{ actualDisplayed = displayMax;}
			
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
	
		if(conn != null)		// close connection
		{
			try { conn.close();}
			catch(Exception e){System.out.println("Can't close.");}
		}		
	}
	
	private void makeTopAbundanceQuery(int tissueID,  Connection conn)
	{
		// Get sorted list of GeneIDs of genes that are most abundant in a particular tissue
		String topAbundanceQuery = new String();
		topAbundanceQuery = "TOP_ABUNDANCE_GENES_BY_TISSUE";
			
		ParamQuery parTAQ = DBQuery.getParamQuery(topAbundanceQuery);
		try 
		{
			parTAQ.setPrepStatement(conn);
		} 
		catch (SQLException e) 
		{System.out.println(e.toString());}	

		try 
		{
			PreparedStatement prepStat = parTAQ.getPrepStatement();
			prepStat.setInt(1, tissueID);			
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
			try { if (resSet != null) resSet.close(); } catch (Exception e) {}; 	// added as server precaution
		    try { if (prepStat != null) prepStat.close(); } catch (Exception e) {};	// added as server precaution		
		}
		catch (SQLException e)
		{
			System.out.println("SQL Exception: " + e.toString());
		}
		// Connection closed in calling constructor
	}
	
	private void makeTopEnrichmentQuery(int tissueID, Connection conn)
	{
		// Get sorted list of GeneIDs of genes that are most enhanced in a particular tissue
		String topEnrichmentQuery = new String();
		topEnrichmentQuery = "TOP_ENRICHMENT_GENES_BY_TISSUE";
		
		ParamQuery parTEQ = DBQuery.getParamQuery(topEnrichmentQuery);
		try 
		{
			parTEQ.setPrepStatement(conn);
		} 
		catch (SQLException e) 
		{System.out.println(e.toString());}	

		try 
		{
			PreparedStatement prepStat = parTEQ.getPrepStatement();
			prepStat.setInt(1, refID);
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
			try { if (resSet != null) resSet.close(); } catch (Exception e) {}; 	// added as server precaution
		    try { if (prepStat != null) prepStat.close(); } catch (Exception e) {};	// added as server precaution
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
	
	public int getActualDisplayed()
	{
		return actualDisplayed;
	}
	
	public Gene[] getGeneList()
	{
		return geneList;
	}
	
}
