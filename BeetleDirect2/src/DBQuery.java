 /*
DBQuery
Class with static method(s) to provide access to SQL query strings
Modified 22.09.2024 for BeetleAtlas 2 — Kept query names the same for Beetle2Direct for simplicity
Last Update: 16.12.2025
*/

public class DBQuery
{		
    // QUERIES FOR GENE & TRANSCRIPT INFO
    
    // query0 get gene info from GeneID in a non-case-sensitive manner — requires query cast to lower case!
    final static String name0 = "INFO_FROM_GENEID";
    final static String query0 = "SELECT DISTINCT NCBI_ID, Symbol, Locus, Product FROM Gene "
    		+ "WHERE (LOWER(NCBI_ID) = ?) ";  

    // query4 get transcript ids corresponding to a single gene 
    final static String name4 = "TRANSID_FROM_GENEID";
    final static String query4 = "SELECT DISTINCT TranscriptID FROM Transcript WHERE NCBI_ID = ? ORDER BY TranscriptID"; 
    
    // query5 get transcript info from TranscriptID
    final static String name5 = "INFO_FROM_TRANSID";
    final static String query5 = "SELECT DISTINCT NCBI_ID FROM Transcript WHERE TranscriptID = ?"; 
    
    // QUERIES TO RETRIEVE EXPERIMENTAL DATA
	
    // query6 get gene FPKM data from GeneID
    final static String name6 = "GENE_DATA_FROM_GENEID";
    final static String query6 =
		"SELECT DISTINCT TissueID, FPKM, Replicate1, Replicate2, Replicate3, SD, Status "
		+ "FROM GeneFPKM "
		+ "WHERE NCBI_ID = ? "
		+ "ORDER BY TissueID ";
    
    // query7 get transcript FPKM data from TranscriptID
    final static String name7 = "TRANSCRIPT_DATA_FROM_TRANSID";
    final static String query7 =
		"SELECT DISTINCT TissueID, FPKM, SD, Status  "
		+ "FROM TranscriptFPKM "
		+ "WHERE TranscriptID = ? "
		+ "ORDER BY TissueID ";
    
    // QUERY FOR UNITISSUE NAME
 
    // query14 Get UniTissueName from ID
    final static String name14 = "UNITISSUENAME_FROM_ID";
    final static String query14 = "SELECT UniTissueName FROM UniTissue WHERE UniTissueID = ? ";
    
     
	// creates an array of all ParamQuerys	
	static ParamQuery pqList [] =
	{		
		new ParamQuery(name0, query0),
		new ParamQuery(name4, query4),
		new ParamQuery(name5, query5),
		new ParamQuery(name6, query6),
		new ParamQuery(name7, query7),
		new ParamQuery(name14, query14)
	};
    
	// finds ParamQuery object by queryName and returns
	public static ParamQuery getParamQuery(String name)
	{
		for (int i=0; i < pqList.length; i++)
		{
		 	if (pqList[i].getQueryName().equals(name))
		 	{
		 		return pqList[i];
		 	}
		}
		return null;
	}
	
	/* --- Constants for simple entity queries --- */
	
	static String tissueQuery = 
			"SELECT DISTINCT TissueID, Stage, Age, Sex, TissueName, Abbreviation, UniTissueID, Reference " +
			"FROM Tissue " + 
			"ORDER BY Stage, Sex, UniTissueID ";	// Not happy about sorting by Stage and UniTissueID now
		
	// returns SQL query to retrieve all details from Tissue table	
	public static String getTissueQuery()
	{
		return tissueQuery;
	}

	
}