 /*
DBQuery
Class with static method(s) to provide access to SQL query strings
DPL 24.05.2016
Update for BeetleAtlas2: 20.09.2024
Last update 22.03.2026
*/

public class DBQuery
{	
    // query6 search Gene NCBI ID by initial letters
    final static String name6 = "NCBI_ID";
    final static String query6 =
		"SELECT NCBI_ID "
		+ "FROM Gene "
        + "WHERE NCBI_ID LIKE ? "
        + "ORDER BY NCBI_ID ";
    
    // query0 search Gene Symbol by initial letters
    final static String name0 = "SYMBOL";
    final static String query0 =
		"SELECT Symbol "
		+ "FROM Gene "
        + "WHERE Symbol LIKE ? "
        + "ORDER BY Symbol ";
 
    // query1 search OGS3 TC ID by initial letters
    final static String name1 = "TC_ID";
    final static String query1 =
		"SELECT TC_ID "
		+ "FROM OGS3 "
        + "WHERE TC_ID LIKE ? "
        + "ORDER BY TC_ID ";
    
    // query2 search Gene Product by initial letters for all matches
    final static String name2 = "PRODUCT";
    final static String query2 =
		"SELECT DISTINCT Product "
		+ "FROM Gene "
        + "WHERE Product LIKE ? "
        + "ORDER BY Product ";
    
    // query3 search Fly FBgn by initial letters
    final static String name3 = "FLY_FB";
    final static String query3 =
		"SELECT DISTINCT FBgn "
		+ "FROM FlyCorrelate "
        + "WHERE FBgn LIKE ? "
        + "ORDER BY FBgn ";
    
    // query4 search Fly FBgn by initial letters
    final static String name4 = "FLY_CG";
    final static String query4 =
		"SELECT DISTINCT CGNum "
		+ "FROM FlyCorrelate "
        + "WHERE CGNum LIKE ? "
        + "ORDER BY CGNum ";
    
    // query0 search Gene Symbol by initial letters
    final static String name5 = "FLY_SYMBOL";
    final static String query5 =
    		"SELECT DISTINCT FlySymbol "
    		+ "FROM FlyCorrelate "
    		+ "WHERE FlySymbol LIKE ? "
    		+ "ORDER BY FlySymbol ";
    
	// creates an array of all ParamQuerys	
	static ParamQuery pqList [] =
	{		
		new ParamQuery(name0, query0),
		new ParamQuery(name1, query1),
		new ParamQuery(name2, query2),
		new ParamQuery(name3, query3),
		new ParamQuery(name4, query4),
		new ParamQuery(name5, query5),
		new ParamQuery(name6, query6)
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
}