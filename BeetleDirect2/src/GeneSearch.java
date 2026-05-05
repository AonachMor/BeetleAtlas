/*
Searches DB for gene info and experimental results
Modified 22.09.2024 for BeetleAtlas 2
Latest update 12.02.2025
*/

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GeneSearch 
{		
	GeneExpression geneExpn;		// Expression object holding results for gene and transcript from search
	Gene gene;						// Gene object holding info on gene and transcripts
	TissueCatalogue tCat; 
	
	String [] idList;				// array to hold GeneIDs retrieved in initial query
	final int GENEID_LENGTH = 100;	// Should be enough, currently max is 68
	int idListSize = 0;			
	Gene [] geneList;				// array to hold Genes retrieved in initial query
	int geneListSize = 0;
	Expression [] expressList;		// array to hold Expression objects retrieved in initial query
	int expressListSize = 0;	
	
	// constructor takes search term for gene query and type of identifier (ID, symbol etc.)
	public GeneSearch(String searchTerm, String idType, TissueCatalogue tCat)
	{		
		this.tCat = tCat;
		// Set names of two types of query on basis of idType
		String geneinfoQuery = "INFO_FROM_GENEID";

		// Make queries if valid search term
		if(!geneinfoQuery.equals(""))
		{
			// Make connection
			Connect cnt = new Connect();
			Connection conn = cnt.getConnection();
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
		
			// close connection
			if(conn != null)
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
				String geneID = resSet.getString("NCBI_ID");
				String symbol = resSet.getString("Symbol");
				String locus = resSet.getString("Locus");
				String product = resSet.getString("Product");				
				gene = new Gene(geneID, symbol, locus, product);		
			}
		}
		catch (SQLException e)
		{
			System.out.println("SQL Exception: " + e.toString());
		}
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
		}
		catch (SQLException e)
		{
			System.out.println("SQL Exception: " + e.toString());
		}
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
					
					// Add to GeneDataset
					gDataset.add(geneData);
				}
				// Having completed GeneDataset now calculate enrichment
				gDataset.calculateEnrichments();
				// Add GeneDataset to Expression object
				geneExpn.setGeneData(gDataset);
			}
		}
		catch (SQLException e)
		{
			System.out.println("SQL Exception: " + e.toString());
		}
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
			}
			catch (SQLException e)
			{
				System.out.println("SQL Exception: " + e.toString());
			}	
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
	
	// get list of Genes from Drosophila search
	public Gene[] getGeneList()
	{
		return geneList;
	}
	
	// get array of Expression objects from Drosophila search
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