 /*
DBQuery
Class with static method(s) to provide access to SQL query strings
Derived from earlier BeetleAtlas version
Last Update: 09.12.2023
Modified for BeetleAtlas2 22.09.2024
*/

public class DBQuery
{			    
			// PARALOGUE QUERY //
    // query16 get Paralogue(s) from Beetle NCBI_ID
    final static String name16 = "PARAS_FROM_NCBI_ID";
    final static String query16 = "SELECT DISTINCT ParaID FROM Paralogue "
    		+ "WHERE (NCBI_ID = ?) ";  
 
		// FLY ORTHOLOGUE QUERY //
    
    // query15 Get FlyOrthologue FBgns from NCBI_ID
    final static String name15 = "FLY_ORTHOLOGUES_FROM_NCBI_ID";
    final static String query15 = "SELECT DISTINCT FBgn FROM FlyCorrelate WHERE NCBI_ID = ? ";
     
	// creates an array of all ParamQuerys	
	static ParamQuery pqList [] =
	{		
		new ParamQuery(name15, query15),
		new ParamQuery(name16, query16)
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