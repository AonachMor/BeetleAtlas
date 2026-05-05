// Searches database for genes with peak at particular embryo stage
// 21.02.2021
// Last Update 01.12.2023

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class EmbryoSearch 
{	
	Expression [] expressList;
	final int EXPR_LENGTH = 1000;
	int expressListSize = 0;	
	
	Gene [] geneList;							// array to hold genes to be sent for processing
	int geneListSize = 0;
		
	GeneFPKMpair [] pairList;					// array to hold  GeneFPKMPair objects retrieved from query
	final int GENE_FPKM_PAIR_LENGTH = 1000;
	int pairListSize = 0;

	public EmbryoSearch(String stage, int displayMax, boolean exclusive, TissueCatalogue tCat)
	{	
		int stg = -1;
		if(stage.equals("zero")){stg = 0;}
		else if(stage.equals("one")){stg = 1;}
		else if(stage.equals("two")){stg = 2;}
		else if(stage.equals("three")){stg = 3;}
		
		expressList = new Expression[EXPR_LENGTH];
		geneList = new Gene[EXPR_LENGTH];
	
		//Connect cnt = new Connect();
		//Connection conn = cnt.getConnection();
		
		pairList = new GeneFPKMpair[GENE_FPKM_PAIR_LENGTH];	
		
		if(exclusive)		// i.e. genes expressed only in embryo
		{
			makeExclusiveEmbryoQuery(stg);
		}
		else
		{
			makeGeneralEmbryoQuery(stg);
		}		
		
		int max = displayMax;
		if(pairListSize < displayMax)
		{
			max = pairListSize;
		}
		
		for(int i=0; i<max; i++)
		{
			GeneSearch gs = new GeneSearch(pairList[i].getGeneID(), "geneID", tCat);
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
		}	*/	
	}
		
	private void makeGeneralEmbryoQuery(int stage)
	{
		// Get list of GeneIDs of genes that peak at a specific time in embryo development
		String embryoQuery = new String();
		if(stage == 0)
		{
			embryoQuery = DBQuery.getEmbryoGen50Query();
		}
		else if(stage == 1)
		{
			embryoQuery = DBQuery.getEmbryoGen51Query();
		}
		else if(stage == 2)
		{
			embryoQuery = DBQuery.getEmbryoGen52Query();
		}
		else if(stage == 3)
		{
			embryoQuery = DBQuery.getEmbryoGen53Query();
		}
		
		Connect cnt = new Connect();
		Connection conn = cnt.getConnection();
		try 
		{		
			Statement stmt = conn.createStatement();
			ResultSet resSet = stmt.executeQuery(embryoQuery);
			if(resSet.first())
			{
				resSet.beforeFirst();		// hack to reset cursor as 'if' moves it on a row!
				while (resSet.next())		// moves to next row while rows remain
				{	
					String geneID = resSet.getString("GeneID");
					double fpkm = resSet.getDouble("FPKM");
					// Generate an GeneFPKMpair object and add to array
					GeneFPKMpair eg = new GeneFPKMpair(geneID, fpkm);					
					pairList[pairListSize] = eg;
					pairListSize++;
				}
			}
			try { if (resSet != null) resSet.close(); } catch (Exception e) {}; 	// added as server precaution
		    try { if (stmt != null) stmt.close(); } catch (Exception e) {};			// added as server precaution
		}
		catch (SQLException e)
		{
			System.out.println("SQL Exception: " + e.toString());
		}
		finally
		{
			 try { if (conn != null) conn.close(); } catch (Exception e) {};		// previously unclosed
		}
	}
	
	private void makeExclusiveEmbryoQuery(int stage)
	{
		// Get list of GeneIDs of embryo-only genes that peak at a specific time in embryo development
		String embryoQuery = new String();
		embryoQuery = "EMBRYO_ONLY_PEAK";
		
		String age = new String();	
		if(stage == 0) {age = "0_1";}
		if(stage == 1) {age = "1_24";}
		if(stage == 2) {age = "24_36";}
		if(stage == 3) {age = "36_72";}
		
		Connect cnt = new Connect();
		Connection conn = cnt.getConnection();
		
		ParamQuery parQ = DBQuery.getParamQuery(embryoQuery);
		try 
		{
			parQ.setPrepStatement(conn);
		} 
		catch (SQLException e) 
		{System.out.println(e.toString());}	
		
		try 
		{		
			PreparedStatement prepStat = parQ.getPrepStatement();
			prepStat.setString(1, age);
			
			ResultSet resSet = prepStat.executeQuery();
			if(resSet.first())
			{
				resSet.beforeFirst();		// hack to reset cursor as 'if' moves it on a row!
				while (resSet.next())		// moves to next row while rows remain
				{	
					String geneID = resSet.getString("GeneID");
					double fpkm = resSet.getDouble("FPKM");
					// Generate a GeneFPKMpair object and add to array
					GeneFPKMpair eg = new GeneFPKMpair(geneID, fpkm);					
					pairList[pairListSize] = eg;
					pairListSize++;
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
			 try { if (conn != null) conn.close(); } catch (Exception e) {};		// previously unclosed
		}
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
	
	public int getGeneListSize()
	{
		return geneListSize;
	}
	
	// INNER CLASS
	// For storing a geneID and FPKM from an embryo search
 	class GeneFPKMpair
 	{
 		String geneID;	// geneID
 		double fpkm;	// FPKM of peak age
 		
 		GeneFPKMpair(String geneID, double fpkm)
 		{
 			this.geneID = geneID;
 			this.fpkm = fpkm;
 		}
 		public double getFPKM()
 		{
 			return fpkm;
 		}
 		public String getGeneID()
 		{
 			return geneID;
 		}
 	}
	
}
