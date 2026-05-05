 /*
Searches DB for gene info and experimental results
Last Update: 04.01.2025 added screen for experimental conditions, and handled duplicates
15.12.2025
*/

// Uses the same connection for four queries and relying on the calling method to close — not for one single query — careful

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GeneSearch 
{		
	GeneExpression geneExpn;		// Expression object holding results for gene and transcript from search
	Gene gene;						// Gene object holding info on gene and transcripts
	TissueCatalogue tCat; 
	
	boolean flyQy = false;			// Flag for Drosophila query as this has to be handled differently
	boolean prodQy = false;			// Flag for Beetle product query as this has to be handled differently
	
	// ONLY INITIALIZED IF flyQy TRUE i.e DROSOPHILA QUERY
	String [] idList;				// array to hold Tribolium GeneIDs retrieved in initial Drosophila query
	final int GENEID_LENGTH = 100;	// Should be enough, currently max is 68
	int idListSize = 0;			
	// ONLY INITIALIZED IF flyQy TRUE i.e DROSOPHILA QUERY or TRIBOLIUM PRODUCT QUERY	
	Gene [] geneList;				// array to hold Tribolium Genes retrieved in initial Drosophila query
	int geneListSize = 0;
	Expression [] expressList;		// array to hold Expression objects retrieved in initial Drosophila query
	int expressListSize = 0;	
	
	// constructor takes search term for gene query and type of identifier (ID, symbol etc.)
	public GeneSearch(String searchTerm, String idType, TissueCatalogue tCat)
	{		
		this.tCat = tCat;
		// Set names of two types of query on basis of idType
		String geneinfoQuery = "";
		if(idType.equals("geneID"))
		{
			geneinfoQuery = "INFO_FROM_GENEID";
			flyQy = false;
			prodQy = false;
		}
		else if(idType.equals("geneSymbol"))
		{
			geneinfoQuery = "INFO_FROM_SYMBOL";
			flyQy = false;
			prodQy = false;
		}
		else if(idType.equals("geneName"))
		{
			geneinfoQuery = "GENEIDS_FROM_PRODUCT";
			flyQy = false;
			prodQy = true;
		}
		else if(idType.equals("flyFBgn"))
		{
			geneinfoQuery = "GENEIDS_FROM_FLY_FBGN";
			flyQy = true;
			prodQy = false;
		}
		else if(idType.equals("flyCG"))
		{
			geneinfoQuery = "GENEIDS_FROM_FLY_CG";
			flyQy = true;
			prodQy = false;
		}	
		else if(idType.equals("flySymbol"))
		{
			geneinfoQuery = "GENEIDS_FROM_FLY_SYMBOL";
			flyQy = true;
			prodQy = false;
		}
		
		// DROSOPHILA ID selected
		if(!geneinfoQuery.equals("") && flyQy)
		{
			// Initialize list to hold IDs from first query
			idList = new String [GENEID_LENGTH];
			// Make connection
			Connect cnt = new Connect();
			Connection conn = cnt.getConnection();
			// Make first query
			makeBeetleFromFlyQuery(searchTerm, geneinfoQuery, conn);

			// Initialize lists for Gene and Expression objects, same length is list of GeneIDs
			expressList = new Expression[GENEID_LENGTH];
			geneList = new Gene[GENEID_LENGTH];	
			
			// Run a GeneSearch using these IDs
			for(int i=0; i<idListSize; i++)
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
		
		// TRIBOLIUM ID selected but redundant Name/Product idType
		if(!geneinfoQuery.equals("") && prodQy)
		{
			// Initialize list 
			geneList = new Gene [GENEID_LENGTH];
			geneListSize = 0;
			expressList = new Expression[GENEID_LENGTH];
			expressListSize = 0;
			// Make connection
			Connect cnt = new Connect();
			Connection conn = cnt.getConnection();
			
			ParamQuery parPQ = DBQuery.getParamQuery(geneinfoQuery);
			try 
			{
				parPQ.setPrepStatement(conn);
			} 
			catch (SQLException e) 
			{System.out.println(e.toString());}	
			
			try 
			{
				PreparedStatement prepStat = parPQ.getPrepStatement();
				// convert query term to lower case so query is non-case sensitive (sic)
				prepStat.setString(1, searchTerm.toLowerCase());

				ResultSet resSet = prepStat.executeQuery();
				if(resSet.first())	// move to single tuple
				{
					resSet.beforeFirst();		// hack to reset cursor as 'if' moves it on a row!
					while (resSet.next())		// moves to next row while rows remain
					{
						String geneID = resSet.getString("GeneID");
						String symbol = resSet.getString("Symbol");
						String locus = resSet.getString("Locus");
						String product = resSet.getString("Product");	
						String ncbiID = resSet.getString("NCBI_ID");
						boolean ortho = false;
							String orthoString = resSet.getString("Ortho");
							if(orthoString.equals("Yes")) {ortho = true;}
						boolean para = false;
							String paraString = resSet.getString("Para");			
							if(paraString.equals("Yes")) {para = true;}			
						gene = new Gene(geneID, symbol, locus, product, ncbiID, ortho, para);
						geneList[geneListSize] = gene;
						geneListSize++;	
						
						GeneSearch gs = new GeneSearch(geneID, "geneID", tCat);
						GeneExpression express  = gs.getExpression();
						expressList[expressListSize] = express;
						expressListSize++;
					}
				}
			    try { if (resSet != null) resSet.close(); } catch (Exception e) {}; 	// added as server precaution
			    try { if (prepStat != null) prepStat.close(); } catch (Exception e) {};	// added as server precaution				
			}
			catch (SQLException e)
			{
				System.out.println("SQL Exception: " + e.toString());
			}
			finally			// close connection
			{
			    try { if (conn != null) conn.close(); } catch (Exception e) {};			
			}
		}

		// TRIBOLIUM ID selected: make queries if valid search term and not Fly or product
		if(!geneinfoQuery.equals("") && !flyQy && !prodQy)
		{
			// Make connection
			Connect cnt = new Connect();
			Connection conn = cnt.getConnection();			// Same connection conn used for four queries
			// Make first query
			makeGeneInfoQuery(searchTerm, geneinfoQuery, conn);
	
			// Check that gene has been found and if so make other queries
			if(gene != null)
			{
				String geneID = gene.getGeneID();
				makeTranscriptInfoQuery(geneID, conn);

				geneExpn = new GeneExpression(geneID);		// instantiate object to hold GeneDataset and TranscriptDataset		
				makeGeneDataQuery(geneID, conn);	
				makeTranscriptDataQuery(geneID, conn);
			}
		
			if(conn != null)			// close connection after all four queries made
			{
				try { conn.close();}
				catch(Exception e){System.out.println("Can't close.");}
			}
		}
	}
	
	// Query for gene info for Gene object
	private void makeGeneInfoQuery(String searchTerm, String geneinfoQuery, Connection conn)
	{
		ParamQuery parIQ = DBQuery.getParamQuery(geneinfoQuery);
		try 
		{
			parIQ.setPrepStatement(conn);
		} 
		catch (SQLException e) 
		{System.out.println(e.toString());}	
		
		try 
		{
			PreparedStatement prepStat = parIQ.getPrepStatement();
			// convert query term to lower case so query is non-case sensitive (sic)
			prepStat.setString(1, searchTerm.toLowerCase());

			ResultSet resSet = prepStat.executeQuery();
			if(resSet.first())	// move to single tuple
			{
				String geneID = resSet.getString("GeneID");
				String symbol = resSet.getString("Symbol");
				String locus = resSet.getString("Locus");
				String product = resSet.getString("Product");
				String ncbiID = resSet.getString("NCBI_ID");
				boolean ortho = false;
					String orthoString = resSet.getString("Ortho");
						// This is kill point!!!
					if(orthoString.equals("Yes")) {ortho = true;}
				boolean para = false;
					String paraString = resSet.getString("Para");			
					if(paraString.equals("Yes")) {para = true;}			
				gene = new Gene(geneID, symbol, locus, product, ncbiID, ortho, para);		
			}
			try { if (resSet != null) resSet.close(); } catch (Exception e) {}; 	// added as server precaution
			try { if (prepStat != null) prepStat.close(); } catch (Exception e) {};	// added as server precaution
		}
		catch (SQLException e)
		{
			System.out.println("SQL Exception: " + e.toString());
		}
		// Do not close conn here as closed in parent where reused
	}
		
	// Query for transcripts
	private void makeTranscriptInfoQuery(String geneID, Connection conn)
	{
		// First find and order transcripts
		String transQuery = "TRANSID_FROM_GENEID";
		
		ParamQuery parTrQ = DBQuery.getParamQuery(transQuery);
		try 
		{
			parTrQ.setPrepStatement(conn);
		} 
		catch (SQLException e) 
		{System.out.println(e.toString());}	

		try 
		{
			PreparedStatement prepStat = parTrQ.getPrepStatement();
			prepStat.setString(1, geneID);
			ResultSet resSet = prepStat.executeQuery();
			if(resSet.first())
			{
				resSet.beforeFirst();		// hack to reset cursor as 'if' moves it on a row!
				while (resSet.next())		// moves to next row while rows remain
				{	
					String transID = resSet.getString("TranscriptID");
					
					Transcript trans = new Transcript(transID, geneID);
					gene.addTranscript(trans);
				}
			}
			try { if (resSet != null) resSet.close(); } catch (Exception e) {}; 	// added as server precaution
			try { if (prepStat != null) prepStat.close(); } catch (Exception e) {};	// added as server precaution
		}
		catch (SQLException e)
		{
			System.out.println("SQL Exception: " + e.toString());
		}
		// Do not close conn here as closed in parent where reused
	}
	
	// Query for Experimental data for a gene
	private void makeGeneDataQuery(String geneID, Connection conn)
	{
		GeneTissueDataSet gDataset = new GeneTissueDataSet(geneID, tCat);
		
		String geneDataQuery = "GENE_DATA_FROM_GENEID";		
		ParamQuery parGDQ = DBQuery.getParamQuery(geneDataQuery);
		try 
		{
			parGDQ.setPrepStatement(conn);
		} 
		catch (SQLException e) 
		{System.out.println(e.toString());}	
		
		try 
		{
			PreparedStatement prepStat = parGDQ.getPrepStatement();
			prepStat.setString(1, geneID);
			ResultSet resSet = prepStat.executeQuery();
			if(resSet.first())
			{				
				resSet.beforeFirst();		// hack to reset cursor as 'if' moves it on a row!
				while (resSet.next())		// moves to next row while rows remain
				{
					int tissueID = resSet.getInt("TissueID");
					double fpkm = resSet.getDouble("FPKM");
					
					double replicate1 = resSet.getDouble("Replicate1");	
					double replicate2 = resSet.getDouble("Replicate2");	
					double replicate3 = resSet.getDouble("Replicate3");

					double [] repFPKMlist;
					if(resSet.wasNull())
					{
						repFPKMlist = new double[2];
						repFPKMlist[0] = replicate1;
						repFPKMlist[1] = replicate2;
					}
					else
					{
						repFPKMlist = new double[3];
						repFPKMlist[0] = replicate1;
						repFPKMlist[1] = replicate2;
						repFPKMlist[2] = replicate3;
					}
						
					double sd = resSet.getDouble("SD");	
					String status = resSet.getString("Status");
					
					// Construct GeneTissuedata object from query
					GeneTissueData geneData = new GeneTissueData(geneID, tissueID, fpkm, repFPKMlist, sd, status);
					gDataset.add(geneData);
				}
				// Having completed GeneDataset now calculate enrichment
				gDataset.calculateEnrichments();
				// Add GeneDataset to Expression object
				geneExpn.setGeneData(gDataset);
			}
			try { if (resSet != null) resSet.close(); } catch (Exception e) {}; 	// added as server precaution
			try { if (prepStat != null) prepStat.close(); } catch (Exception e) {};	// added as server precaution
		}
		catch (SQLException e)
		{
			System.out.println("SQL Exception: " + e.toString());
		}
		// Do not close conn here as closed in parent where reused
	}
		
	private void makeTranscriptDataQuery(String geneID, Connection conn)
	{
		String transcriptDataQuery = "TRANSCRIPT_DATA_FROM_TRANSID";
		
		// go through set of transcripts belonging to the gene
		for(int i=0; i<gene.getTranscriptListSize(); i++)
		{		
			String transID = gene.getTranscript(i).getTranscriptID();
			TranscriptTissueDataSet tDataset = new TranscriptTissueDataSet(geneID, transID);
			
			ParamQuery parTDQ = DBQuery.getParamQuery(transcriptDataQuery);
			try 
			{
				parTDQ.setPrepStatement(conn);
			} 
			catch (SQLException e) 
			{System.out.println(e.toString());}	

			try 
			{
				PreparedStatement prepStat = parTDQ.getPrepStatement();
				prepStat.setString(1, transID);
				ResultSet resSet = prepStat.executeQuery();
				if(resSet.first())
				{
					resSet.beforeFirst();		// hack to reset cursor as 'if' moves it on a row!
					while (resSet.next())		// moves to next row while rows remain
					{	
						int tissueID = resSet.getInt("TissueID"); 
						double fpkm = resSet.getDouble("FPKM");
						double sd = resSet.getDouble("SD");	
						String status = resSet.getString("Status");
						
						// Construct TranscriptTissuedata object from query
						TranscriptTissueData transcriptData = new TranscriptTissueData(geneID, transID, tissueID, fpkm, sd, status);					
						// Add to TranscriptDataset
						tDataset.add(transcriptData);
					}
					geneExpn.addTranscriptDataset(tDataset);
				}
				try { if (resSet != null) resSet.close(); } catch (Exception e) {}; 	// added as server precaution
				try { if (prepStat != null) prepStat.close(); } catch (Exception e) {};	// added as server precaution		
			}
			catch (SQLException e)
			{
				System.out.println("SQL Exception: " + e.toString());
			}
			// Do not close conn here as closed in parent where reused
		}
	}
	
	// retrieve Beetle GeneID(s) corresponding to Drosophila FBgn or CG
	private void makeBeetleFromFlyQuery(String searchTerm, String geneinfoQuery, Connection conn)
	{
		ParamQuery parBFQ = DBQuery.getParamQuery(geneinfoQuery);
		try 
		{
			parBFQ.setPrepStatement(conn);
		} 
		catch (SQLException e) 
		{System.out.println(e.toString());}
		
		try 
		{
			PreparedStatement prepStat = parBFQ.getPrepStatement();
			// convert query term to lower case so query is non-case sensitive (sic)
			prepStat.setString(1, searchTerm.toLowerCase());
			
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
		finally		// close the connection as it is not closed in parent
		{
		    try { if (conn != null) conn.close(); } catch (Exception e) {};
		}
	}

	
	// 'Get' methods to give program access to query results
	
	public Gene getGene()
	{
		return gene;
	}
	
	public GeneExpression getExpression()
	{
		return geneExpn;
	}
	
	// get list of Genes from Drosophila search (or Tribolium product search)
	public Gene[] getGeneList()
	{
		return geneList;
	}
	
	// get array of Expression objects from Drosophila search (or Tribolium product search)
	public Expression[] getExpressList()
	{
		return expressList;
	}
	
	// get size of expression and gene arrays
	public int getListSize()
	{
		return expressListSize;
	}

}