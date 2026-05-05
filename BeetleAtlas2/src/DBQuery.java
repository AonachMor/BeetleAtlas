 /*
DBQuery
Class with static method(s) to provide access to SQL query strings
Last Update: 16.12.2025
*/

public class DBQuery
{			
    		// QUERIES FOR GENE & TRANSCRIPT INFO //
    
    // query0 get gene info from NCBI_ID in a non-case-sensitive manner — requires query cast to lower case!
    final static String name0 = "INFO_FROM_GENEID";
    final static String query0 = "SELECT DISTINCT NCBI_ID, Symbol, TC_ID, Locus, Product, BioType, Ortho, Para, Para99, Mito, Status FROM Gene "
    		+ "WHERE (LOWER(NCBI_ID) = ?) ";  

    // query1 get gene info from Product name in a non-case-sensitive manner — requires query cast to lower case!
   final static String name1 = "INFO_FROM_PRODUCT";
   final static String query1 = "SELECT DISTINCT NCBI_ID, Symbol, TC_ID, Locus, Product, BioType, Ortho, Para, Para99, Mito, Status FROM Gene "
   		+ "WHERE (LOWER(Product) = ?) ";

	// query3 get Beetle NCBI_ID(s) from Beetle Product (name can be redundant — should replace query1)
	final static String name3 = "GENEIDS_FROM_PRODUCT";
	final static String query3 = "SELECT DISTINCT NCBI_ID, Symbol, TC_ID, Locus, Product, BioType, Ortho, Para, Para99, Mito, Status FROM Gene "
		+ "WHERE (LOWER(Product) = ?) ";
         
    // query2 get gene info from Symbol in a non-case-sensitive manner — requires query cast to lower case!
    final static String name2 = "INFO_FROM_SYMBOL";
    final static String query2 = "SELECT DISTINCT NCBI_ID, Symbol, TC_ID, Locus, Product, BioType, Ortho, Para, Para99, Mito , Status FROM Gene "
    		+ "WHERE (LOWER(Symbol) = ?) ";

    // query4 get transcript ids corresponding to a single gene 
    final static String name4 = "TRANSID_FROM_GENEID";
    final static String query4 = "SELECT DISTINCT TranscriptID FROM Transcript WHERE NCBI_ID = ? ORDER BY TranscriptID"; 
    
    // query5 get transcript info from TranscriptID
    final static String name5 = "INFO_FROM_TRANSID";
    final static String query5 = "SELECT DISTINCT NCBI_ID FROM Transcript WHERE TranscriptID = ?"; 
    
			// QUERIES TO RETRIEVE EXPERIMENTAL DATA //
	
    // query6 get gene FPKM data from NCBI_ID
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
    
    		// QUERIES TO RETRIEVE BEETLE ID FROM FLY IDS //
     
    // query9 get Beetle NCBI_ID(s) from Fly FBgn
    final static String name9 = "GENEIDS_FROM_FLY_FBGN";
    final static String query9 = "SELECT DISTINCT NCBI_ID FROM FlyCorrelate "
    		+ "WHERE (LOWER(FBgn) = ?) ";  
    
    // query10 get Beetle NCBI_ID(s) from Fly CGNum
    final static String name10 = "GENEIDS_FROM_FLY_CG";
    final static String query10 = "SELECT DISTINCT NCBI_ID FROM FlyCorrelate "
    		+ "WHERE (LOWER(CGNum) = ?) "; 
    
    // query11 get Beetle NCBI_ID(s) from Fly Symbol
    final static String name11 = "GENEIDS_FROM_FLY_SYMBOL";
    final static String query11 = "SELECT DISTINCT NCBI_ID FROM FlyCorrelate "
    		+ "WHERE (FlySymbol = ?) ";
    
			// QUERY TO RETRIEVE BEETLE NCBI ID FROM TC ID //  
    final static String name20 = "GENEID_FROM_TC_ID";
    final static String query20 = "SELECT DISTINCT NCBI_ID FROM Gene "  
    		+ "WHERE (TC_ID = ?) ";
    
			// PARALOGUE QUERY //
    // query16 get Paralogue(s) from Beetle NCBI_ID
    final static String name16 = "PARAS_FROM_GENEID";
    final static String query16 = "SELECT DISTINCT ParaID FROM Paralogue "
    		+ "WHERE (NCBI_ID = ?) ";  
    
    		// DEVELOPMENT QUERIES //
    
    // query8 get developmental abundance data
    final static String name8 = "DEVEL_ABUNDANCE";
    final static String query8 =
		"SELECT DISTINCT l.NCBI_ID AS NCBI_ID, l.FPKM AS larvalFPKM, a.FPKM AS adultFPKM  "
		+ "FROM GeneFPKM l, GeneFPKM a "
		+ "WHERE a.NCBI_ID = l.NCBI_ID "
		+ "AND l.TissueID = ? "
		+ "AND a.TissueID = ? "
		+ "AND l.Status = 'OK' "
		+ "AND a.Status = 'OK' "
		+ "ORDER BY l.NCBI_ID ";   
    
    		// TOP QUERIES //
    
    // query12  Genes with high abundance (arbitrarily above FPKM = 10)
    final static String name12 = "TOP_ABUNDANCE_GENES_BY_TISSUE";
    final static String query12 = 
    		"SELECT Distinct f.NCBI_ID, f.FPKM "
    		+ "FROM GeneFPKM f, Gene g "
    		+ "WHERE f.NCBI_ID = g.NCBI_ID "
    		+ "AND f.FPKM > 10 "
    		+ "AND f.TissueID = ? "
    		+ "ORDER BY f.FPKM DESC ";  
    
    // query13  Genes with high enrichment (arbitrarily above 2 for FPKMs > 2)
    final static String name13 = "TOP_ENRICHMENT_GENES_BY_TISSUE";
    final static String query13 = 
    		"SELECT DISTINCT fStd.NCBI_ID AS NCBI_ID, fTiss.FPKM/fStd.FPKM AS Enrichment "
    		+ "FROM GeneFPKM fStd, GeneFPKM fTiss, Gene g "	
    		+ "WHERE fStd.NCBI_ID = g.NCBI_ID "
    		+ "AND fStd.NCBI_ID = fTiss.NCBI_ID "
    		+ "AND fStd.TissueID = ? "
    		+ "AND fTiss.TissueID = ? "
    		+ "AND fStd.FPKM > 2 "
    		+ "AND fTiss.FPKM > 2 "
    		+ "AND fTiss.FPKM > fStd.FPKM "
    		+ "ORDER BY Enrichment DESC "; 
    
    		// CATEGORY QUERIES //
    
    // query17: Genes in a particular category with high abundance (arbitrarily above FPKM = 10)
    final static String name17 = "CAT_ABUNDANCE_GENES_BY_TISSUE";
    final static String query17 =    
    		"SELECT Distinct f.NCBI_ID, f.FPKM "
    		+ "FROM GeneFPKM f, Gene g, GeneKeyword k "
    		+ "WHERE f.NCBI_ID = g.NCBI_ID "
    		+ "AND f.NCBI_ID = k.NCBI_ID "
    		+ "AND k.keyword = ? "
    		+ "AND f.FPKM > 10 "
    		+ "AND f.TissueID = ? "
    		+ "ORDER BY f.FPKM DESC "; 
    
    // query18: Genes in a particular category with high enrichment (arbitrarily above 2 for FPKMs > 2)
    final static String name18 = "CAT_ENRICHMENT_GENES_BY_TISSUE";;
    final static String query18 =    
    		"SELECT DISTINCT fStd.NCBI_ID AS NCBI_ID, fTiss.FPKM/fStd.FPKM AS Enrichment "
    		+ "FROM GeneFPKM fStd, GeneFPKM fTiss, Gene g, GeneKeyword k "
    		+ "WHERE fStd.NCBI_ID = g.NCBI_ID "
    		+ "AND fStd.NCBI_ID = fTiss.NCBI_ID "
    		+ "AND fStd.NCBI_ID = k.NCBI_ID "
    		+ "AND k.keyword = ? "
    		+ "AND fStd.TissueID = ? "
    		+ "AND fTiss.TissueID = ? "
    		+ "AND fStd.FPKM > 2 "
    		+ "AND fTiss.FPKM > 2 "
    		+ "AND fTiss.FPKM > (3*fStd.FPKM) "
    		+ "ORDER BY Enrichment DESC "; 
 
		// FLY ORTHOLOGUE QUERY //
    
    // query15 Get FlyOrthologue FBgns from NCBI_ID
    final static String name15 = "FLY_ORTHOLOGUES_FROM_ID";
    final static String query15 = "SELECT DISTINCT FBgn FROM FlyCorrelate WHERE NCBI_ID = ? ";

    	// QUERY FOR UNITISSUE NAME //
 
    // query14 Get UniTissueName from ID
    final static String name14 = "UNITISSUENAME_FROM_ID";
    final static String query14 = "SELECT UniTissueName FROM UniTissue WHERE UniTissueID = ? ";
    
		// EMBRYO-ONLY PEAK QUERY //
    // query19 Get FPKM from Age for Genes only expressed in embryo
    final static String name19 = "EMBRYO_ONLY_PEAK";
    final static String query19 = "SELECT DISTINCT NCBI_ID, FPKM FROM EmbyroPeakFPKM " 
	    	+ "WHERE FPKM > 10 "
    		+ "AND Age = ?"
	    	+ "ORDER BY FPKM DESC ";
     
	// creates an array of all ParamQuerys	
	static ParamQuery pqList [] =
	{		
		new ParamQuery(name0, query0),
		new ParamQuery(name1, query1),
		new ParamQuery(name2, query2),
		new ParamQuery(name3, query3),
		new ParamQuery(name4, query4),
		new ParamQuery(name5, query5),
		new ParamQuery(name6, query6),
		new ParamQuery(name7, query7),
		new ParamQuery(name8, query8),
		new ParamQuery(name9, query9),
		new ParamQuery(name10, query10),
		new ParamQuery(name11, query11),
		new ParamQuery(name12, query12),
		new ParamQuery(name13, query13),
		new ParamQuery(name14, query14),
		new ParamQuery(name15, query15),
		new ParamQuery(name16, query16),	
		new ParamQuery(name17, query17),
		new ParamQuery(name18, query18),
		new ParamQuery(name19, query19),
		new ParamQuery(name20, query20)
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
	
	static String allGeneQuery = "SELECT NCBI_ID FROM Gene ";
	
	static String allTranscriptQuery = "SELECT TranscriptID FROM Transcript  ";
	
	static String embryoGen50Query =
			"SELECT DISTINCT f1.NCBI_ID AS 'NCBI_ID', f1.FPKM AS 'FPKM' "
			+ "FROM GeneFPKM f1, GeneFPKM f2, GeneFPKM f3, GeneFPKM f4 "
			+ "WHERE f2.NCBI_ID = f1.NCBI_ID "
			+ "AND f3.NCBI_ID = f1.NCBI_ID "
			+ "AND f4.NCBI_ID = f1.NCBI_ID "
			+ "AND f1.FPKM > (f2.FPKM + f3.FPKM + f4.FPKM) "
			+ "AND f1.FPKM > 10 "
			+ "AND f1.TissueID = 50 "
			+ "AND f2.TissueID = 51 "
			+ "AND f3.TissueID = 52 "
			+ "AND f4.TissueID = 53 "
			+ "ORDER BY f1.FPKM DESC ";
	
	static String embryoGen51Query =
			"SELECT DISTINCT f2.NCBI_ID AS 'NCBI_ID', f2.FPKM AS 'FPKM' "
			+ "FROM GeneFPKM f1, GeneFPKM f2, GeneFPKM f3, GeneFPKM f4 "
			+ "WHERE f2.NCBI_ID = f1.NCBI_ID "
			+ "AND f3.NCBI_ID = f1.NCBI_ID "
			+ "AND f4.NCBI_ID = f1.NCBI_ID "
			+ "AND f2.FPKM > (f1.FPKM + f3.FPKM + f4.FPKM) "
			+ "AND f2.FPKM > 10 "
			+ "AND f1.TissueID = 50 "
			+ "AND f2.TissueID = 51 "
			+ "AND f3.TissueID = 52 "
			+ "AND f4.TissueID = 53 "
			+ "ORDER BY f2.FPKM DESC ";
	
	static String embryoGen52Query =
			"SELECT DISTINCT f3.NCBI_ID AS 'NCBI_ID', f3.FPKM AS 'FPKM' "
			+ "FROM GeneFPKM f1, GeneFPKM f2, GeneFPKM f3, GeneFPKM f4 "
			+ "WHERE f2.NCBI_ID = f1.NCBI_ID "
			+ "AND f3.NCBI_ID = f1.NCBI_ID "
			+ "AND f4.NCBI_ID = f1.NCBI_ID "
			+ "AND f3.FPKM > (f1.FPKM + f2.FPKM + f4.FPKM) "
			+ "AND f3.FPKM > 10 "
			+ "AND f1.TissueID = 50 "
			+ "AND f2.TissueID = 51 "
			+ "AND f3.TissueID = 52 "
			+ "AND f4.TissueID = 53 "
			+ "ORDER BY f3.FPKM DESC ";
	
	static String embryoGen53Query =
			"SELECT DISTINCT f4.NCBI_ID AS 'NCBI_ID', f4.FPKM AS 'FPKM' "
			+ "FROM GeneFPKM f1, GeneFPKM f2, GeneFPKM f3, GeneFPKM f4 "
			+ "WHERE f2.NCBI_ID = f1.NCBI_ID "
			+ "AND f3.NCBI_ID = f1.NCBI_ID "
			+ "AND f4.NCBI_ID = f1.NCBI_ID "
			+ "AND f4.FPKM > (f1.FPKM + f2.FPKM + f3.FPKM) "
			+ "AND f4.FPKM > 10 "
			+ "AND f1.TissueID = 50 "
			+ "AND f2.TissueID = 51 "
			+ "AND f3.TissueID = 52 "
			+ "AND f4.TissueID = 53 "
			+ "ORDER BY f4.FPKM DESC ";
	
	// TissueID < 50 excludes whole tissue and embryo
	static String profileALQuery =
			"SELECT NCBI_ID, FPKM, Status, TissueID " 
			+ "FROM GeneFPKM "
			+ "WHERE TissueID < 50 "
			+ "ORDER BY NCBI_ID, TissueID ";
	
	// TissueID < 99 excludes whole tissues only
	static String profileALEQuery =
			"SELECT NCBI_ID, FPKM, Status, TissueID " 
			+ "FROM GeneFPKM "
			+ "WHERE TissueID < 99 "
			+ "ORDER BY NCBI_ID, TissueID ";
	
	static String keywordQuery = 
			"SELECT DISTINCT Keyword FROM GeneKeyword "
			+ "ORDER BY Keyword ";
			
	
	// returns SQL query to retrieve all details from Tissue table	
	public static String getTissueQuery()
	{
		return tissueQuery;
	}
	
	// returns SQL query to retrieve all Gene IDs	
	public static String getAllGeneQuery()
	{
		return allGeneQuery;
	}
	
	// returns SQL query to retrieve all Transcript IDs
	public static String getAllTranscriptQuery()
	{
		return allTranscriptQuery;
	}
	
	// returns SQL query to retrieve Embryo Qy with peak at tissue 50 (0–1 h)
	public static String getEmbryoGen50Query()
	{
		return embryoGen50Query;
	}
	// returns SQL query to retrieve Embryo Qy with peak at tissue 51 (1–24 h)
	public static String getEmbryoGen51Query()
	{
		return embryoGen51Query;
	}
	// returns SQL query to retrieve Embryo Qy with peak at tissue 52 (24–36 h)
	public static String getEmbryoGen52Query()
	{
		return embryoGen52Query;
	}
	// returns SQL query to retrieve Embryo Qy with peak at tissue 53 (36–72 h)
	public static String getEmbryoGen53Query()
	{
		return embryoGen53Query;
	}
	
	// Excludes whole tissue and embryo values
	public static String getProfileALQuery()
	{
		return profileALQuery;
	}
	
	// Excludes whole tissue values only
	public static String getProfileALEQuery()
	{
		return profileALEQuery;
	}
	
	// 
	public static String getKeywordQuery()
	{
		return keywordQuery;
	}	
	
}