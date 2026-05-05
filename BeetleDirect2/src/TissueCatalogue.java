/*
	TissueCatalogue
	Class for maintaining an array of Tissue objects, with associated accessor methods
	And array of TissuedoubletList for laying out tables with A, L for same tissue
	Called from Servlet class immediately on invoking Servlet
	May still be  classes with old code that use array of all tissues where Adult/Larval should be used!
	DPL 09.10.2019
	Last Update: 16.12.2025
*/

import java.sql.*;
import java.io.*;
import java.util.Arrays;	// for Arrays.sort

public class TissueCatalogue
{
	private Tissue[] tissueList;			// array of all Tissue objects
	private int TISSUE_LIST_LEN = 100;		// length of array (currently only needs 26, so should be ok for a while)
	private int tissueListSize = 0;			// occupancy
	
	private Tissue[] adularvList;			// array of all Adult and Larval Tissue objects  — Sorted version used for Transcript FPKM table?
	private int ADULARV_LIST_LEN = 100;		// length of array (currently only needs 22, so should be ok for a while)
	private int adularvListSize = 0;		// occupancy	
	
	private Tissue[] embryoList;			// array of all Embryo Tissue objects
	private int EMBRYO_LIST_LEN = 10;		// length of array (currently only needs 4, so should be ok for a while)
	private int embryoListSize = 0;			// occupancy		
	
	private TissueDoublet[] doubletList;	// array of TissueDoublet objects — Sorted version used for Gene FPKM table
	private int DOUB_LIST_LEN = 100;		// length of array (currently only needs 11, so should be ok for a while)
	private int doubletListSize = 0;		// occupancy — but also specifies TissueDoublet 'listPosition' 
	
	private TissueDoublet[] devList;		// array of TissueDoublet objects — Sorted version used for Developmental dropdown
	private int DEV_LIST_LEN = 100;			// length of array (currently only needs 10, so should be ok for a while)
	private int devListSize = 0;			// occupancy — but also specifies TissueDoublet 'listPosition' 
	
	private final String DOUBLET_FILE = "files/uniTissues.txt";	// file that lists displayOrder/tab/unifying names for adult and larval tissues
	private DoubletDisplay[] doubletDisplays;					// array from DOUBLET_FILE
	private int doubletDisplaysSize = 0;						// occupancy
	
	// constructor calls methods to creates arrays of Tissue and TissueTriplet objects
	public TissueCatalogue()
	{
		// Parse text file and create objects of unitissueID and table display position - store in array
		doubletDisplays = new DoubletDisplay[DOUB_LIST_LEN];	
		populateDoubletDisplayList();
		
		// Construct arrays of Tissues with sub-arrays of A/L or E. Then array of Doublets for A/L 
		tissueList = new Tissue[TISSUE_LIST_LEN];
		adularvList = new Tissue[ADULARV_LIST_LEN];
		embryoList = new Tissue[EMBRYO_LIST_LEN];
		
		doubletList = new TissueDoublet[DOUB_LIST_LEN];
		devList = new TissueDoublet[DEV_LIST_LEN];
		
		populateLists();
		
		// sort Pairs (ensures Doublet[] is in Display Order for Gene table)
		TissueDoubletComparator fspComparator = new TissueDoubletComparator();
		Arrays.sort(doubletList, 0, doubletListSize, fspComparator);

		// sort adularvList (ensures Tissue[] is in Display Order for Adult/Larval Transcript table)
		sortAdularvList();
		
		// sorts devList so dropdown menu for development is in alphabetical order
		sortUniList();
	}
	
	// Make SQL query to DB to populate ordered (?) list of Tissue objects, and subarrays for Adult/Larval and Embryo
	// then call method to make TissuedoubletList
	private void populateLists()
	{		
		String query = DBQuery.getTissueQuery();
		Connect cnt = new Connect();
		Connection conn = cnt.getConnection();
		try 
		{		
			Statement stmt = conn.createStatement();
			ResultSet resSet = stmt.executeQuery(query);
			if(resSet.first())
			{
				resSet.beforeFirst();		// hack to reset cursor as 'if' moves it on a row!
				while (resSet.next())		// moves to next row while rows remain
				{	
					int tissueID = resSet.getInt("TissueID");
					String stage = resSet.getString("Stage");
					String age = resSet.getString("Age");
					String sex = resSet.getString("Sex");
					String tissueName = resSet.getString("TissueName");
					String abbreviation = resSet.getString("Abbreviation");
					int uniTissueID = resSet.getInt("UniTissueID");
					String referenceString = resSet.getString("Reference");

					boolean reference = false;
					if(referenceString.equals("Yes"))
					{
						reference = true;
					}

					Tissue next = new Tissue(tissueID, stage, age,sex, tissueName, abbreviation, uniTissueID, reference);
					tissueList[tissueListSize] = next;
					tissueListSize++;
					
					if((stage.equals("Adult") || stage.equals("Larval"))) // Exclude experimental for this list
					{
						adularvList[adularvListSize] = next;
						adularvListSize++;
					}
					else if (stage.equals("Embryo"))
					{
						embryoList[embryoListSize] = next;
						embryoListSize++;		
					}
				}
			}
		}
		catch (SQLException e)
		{
			System.out.println("SQL Exception: " + e.toString());
		}
		finally // close the connection
		{
			if(conn != null)
			{
				try { conn.close();}
				catch(Exception e){System.out.println("Can't close.");}
			}
		}
		
		// Generate lists of pairs (only possible after population of Tissue list)	
		populateDoubletList(); 
		populateUniList(); 
	}
	
	// Creates doubletList by taking each Doublet and using UniTisssue to find component A and L tissue objects
	private void populateDoubletList()
	{
		for(int i=0; i<doubletDisplaysSize; i++)
		{
			int uniTissueID = doubletDisplays[i].getUniTissueID();
			int displayPos = doubletDisplays[i].getDisplayPos();
			String uniTissueName = doubletDisplays[i].getUniTissueName();
			Tissue adultTiss = getTissueByUniID(uniTissueID, PageUtility.ADULT);
			Tissue larvalTiss = getTissueByUniID(uniTissueID, PageUtility.LARVAL);			
			TissueDoublet doublet = new TissueDoublet(uniTissueID, adultTiss, larvalTiss, displayPos, uniTissueName);		
			doubletList[doubletListSize] = doublet;
			doubletListSize++;  
				// System.out.println(pair.toString());		// for debugging
		}
	}
	
	// Creates devList exactly as for doubletList (Could probably duplicate list instead)
	private void populateUniList()
	{
		for(int i=0; i<doubletDisplaysSize; i++)
		{
			int uniTissueID = doubletDisplays[i].getUniTissueID();
			int displayPos = doubletDisplays[i].getDisplayPos();
			String uniTissueName = doubletDisplays[i].getUniTissueName();
			Tissue adultTiss = getTissueByUniID(uniTissueID, PageUtility.ADULT);
			Tissue larvalTiss = getTissueByUniID(uniTissueID, PageUtility.LARVAL);			
			TissueDoublet doublet = new TissueDoublet(uniTissueID, adultTiss, larvalTiss, displayPos, uniTissueName);		
			devList[devListSize] = doublet;
			devListSize++;  
		}
	}
	
	public int getTissueCountByStage(String stage)
	{
		int count=0;
        for(int i=0; i<tissueListSize; i++)
        {
        	if(tissueList[i].getStage().equals(stage) && !tissueList[i].isReference())
        	{
        		count++;
        	}
        }
        return count;
	}
	
    // Sorts adularvList for transcript display: first by adult v. larval, then by TissueDoublet display order
    private void sortAdularvList()
    {
    	Tissue lowest;		// holder for Tissue with lowest value
		int lowestPos;		
		for (int i=0; i<adularvListSize-1; i++)		// do series of runs
		{
			for (int j=i+1; j<adularvListSize; j++)	// for each run process list
			{
				lowest = adularvList[i];			// first of unsorted assigned to lowest
				lowestPos = i;
				
				if(lowest.getStage().compareTo(adularvList[j].getStage()) > 0)
				{
					lowestPos = j;
				}
				else if(lowest.getStage().compareTo(adularvList[j].getStage()) == 0)
				{
					int displayPosLowest = getTissueDoubletByTissueID(lowest.getTissueID()).getDisplayPosition();
					int displayPosJ = getTissueDoubletByTissueID(adularvList[j].getTissueID()).getDisplayPosition();
					if(displayPosJ < displayPosLowest)
					{
						lowestPos = j;
					}
				}
				
				lowest = adularvList[lowestPos];
				adularvList[lowestPos] = adularvList[i]; 		// shift current first
				adularvList[i] = lowest;					// replace
			}
		}
    }
    
    // Sorts devList for in alphabetical order for pulldown menu
    private void sortUniList()
    {
    	TissueDoublet lowest;		// holder for TissueDoublet with lowest value
		int lowestPos;		
		for (int i=0; i<devListSize-1; i++)		// do series of runs
		{
			for (int j=i+1; j<devListSize; j++)	// for each run process list
			{
				lowest = devList[i];			// first of unsorted assigned to lowest
				lowestPos = i;

				if(lowest.getUniTissueName().compareTo(devList[j].getUniTissueName()) > 0)
				{
					lowestPos = j;
				}
				
				lowest = devList[lowestPos];
				devList[lowestPos] = devList[i]; 		// shift current first
				devList[i] = lowest;					// replace
			}
		}   	
    }
  	
    						/* Accessor/Search methods for TISSUE lists etc */
	
    // number of distinct Tissue objects (i.e. occupancy of array)
    public int getTissueListSize()
    {
    	return tissueListSize;
    }
    
    public int getAdularvListSize()
    {
    	return adularvListSize;
    }
    
    public int getEmbryoListSize()
    {
    	return embryoListSize;
    }
    
    // returns Tissue object at a given position in the array
    public Tissue getTissue(int pos)
    {
    	return tissueList[pos];
    }  
    
    public Tissue getAdularvTissue(int pos)
    {
    	return adularvList[pos];
    }  
    
    public Tissue getEmbryoTissue(int pos)
    {
    	return embryoList[pos];
    }  
    
    // allows stage description to be retrieved for an id 
    public String getStageByID(int id)
    {
        for(int i=0; i<tissueListSize; i++)
        {
            int beetleID = tissueList[i].getTissueID();
            if(id == beetleID)
            {
            	return tissueList[i].getStage();
            }
        }
        return "none";	// back-stop that won't throw an npe
    }
    
    // allows determination if tissue is reference for an id 
    public boolean getRefStatusByID(int id)
    {
        for(int i=0; i<tissueListSize; i++)
        {
            int beetleID = tissueList[i].getTissueID();
            if(id == beetleID)
            {
            	return tissueList[i].isReference();
            }
        }
        return false;		// back-stop that won't throw an npe
    }
    
    // allows tissue name to be retrieved for an id
    public String getTissueNameByID(int id)
    {
        for(int i=0; i<tissueListSize; i++)
        {
            int beetleID = tissueList[i].getTissueID();
            if(id == beetleID)
            {
            	return tissueList[i].getTissueName();
            }
        }
        return "none";	// back-stop that won't throw an npe
    }
  
    // allows UniTissue name to be retrieved for an id
    public int getUniTissueByID(int id)
    {
        for(int i=0; i<tissueListSize; i++)
        {
            int beetleID = tissueList[i].getTissueID();
            if(id == beetleID)
            {
            	return tissueList[i].getUniTissueID();
            }
        }
        return -1;	// back-stop that won't throw an npe
    }
	
    // allows retrieval of ID of reference tissue for each stage (A or L)
	public int getRefIDbyStage(String stage)
	{
        for(int i=0; i<tissueListSize; i++)
        {
        	Tissue tiss = tissueList[i];
        	if(tiss.getStage().equals(stage) && tiss.isReference() == true)
        	{
        		return tiss.getTissueID();
        	}
        }
        return -1;	// back-stop that won't throw an npe
	}

	   // returns Tissue object corresponding to a uniTissueID and stage
	 private Tissue getTissueByUniID(int uniTissueID, String stage)
	 {
	     for(int i=0; i<tissueListSize; i++)
	     {
	         int utID = tissueList[i].getUniTissueID();
	         String stg = tissueList[i].getStage();	         
	         if(utID == uniTissueID && stg.equals(stage))
	         {
	         	return tissueList[i];
	         }
	     } 
	     return null;
	 }
	   
				/* Accessor/Search methods for TissueDoublet lists */
	
    // number of TissueDoublet (i.e. occupancy of array - gives number of lines in table)
    public int getDoubletListSize()
    {
    	return doubletListSize;
    } 
    
    public int getDevListSize()
    {
    	return devListSize;
    }
    
    // returns TissueDoublet object at a given position in the array
    public TissueDoublet getTissueDoublet(int pos)
    {
    	return doubletList[pos];
    }    
    
    public TissueDoublet getDevTissue(int pos)
    {
    	return devList[pos];
    }
    
    // returns a TissueDoublet object if either adult or larval component ID matches search ID
    public TissueDoublet getTissueDoubletByTissueID(int id)
    {
    	 for(int i=0; i<doubletListSize; i++)
    	{	
    		if(doubletList[i].getAdultTissue() != null &&
    				doubletList[i].getAdultTissue().getTissueID() == id)
    		{
    			return doubletList[i];
    		}
    		else if(doubletList[i].getLarvalTissue() != null &&
    				doubletList[i].getLarvalTissue().getTissueID() == id)
    		{
    			return doubletList[i];
    		}
    	}
    	return null;
    }
    
    // returns the TissueDoublet object that matches UniTissue name
    public TissueDoublet getTissueDoubletByUniTissueName(String name)
    {
    	 for(int i=0; i<doubletListSize; i++)
    	{	
    		if(doubletList[i].getUniTissueName().equals(name) )
    		{
    			return doubletList[i];
    		}
    	}
    	return null;
    }
    
    								// INNER CLASS //
 	
 	// For storing a line from uniTissues.txt and lookup Name
 	class DoubletDisplay
 	{
 		int displayPos;		// order of tissue for display (e.g. in table)
 		int uniTissueID;	// uniTissueID
 		String uniTissueName;	// uniTissueID
 		
 		DoubletDisplay(int displayPos, int uniTissueID, String uniTissueName)
 		{
 			this.displayPos = displayPos;
 			this.uniTissueID = uniTissueID;
 			this.uniTissueName = uniTissueName;
 		}
 		public int getDisplayPos()
 		{
 			return displayPos;
 		}
 		public int getUniTissueID()
 		{
 			return uniTissueID;
 		}
 		public String getUniTissueName()
 		{
 			return uniTissueName;
 		}
 	}
 	
		// METHOD FOR PARSING TEXT FILE WITH DISPLAY POSITIONS TO POPULATE ARRAY OF DISPLAY OBJECTS //
 	
	// Parses uniTissues.txt (A and L only), queries DB for corresponding UniTissue Name, creates DoubletDisplay obj and adds to DoubletDisplay array
	private void populateDoubletDisplayList()
	{
		String unitissueQy = "UNITISSUENAME_FROM_ID";		// Query to get UniTissue Name from ID
		
		StreamFile sf = new StreamFile(DOUBLET_FILE, true);
		StreamTokenizer st = sf.getStream();
		st.slashSlashComments(true);		// May use Java-style (//) comments
		st.commentChar('%');				// 'Official' comment char is '%'
		st.wordChars(' ', ' ');				// Do not regard ' ' as delimiter
		
		boolean goOn = true; 
		try
		{
			while (goOn)
			{
				int displayPos = 0;
				int uniTissID = 0;
				String uniTissName = new String();
				
				int tok = st.nextToken();
				if (tok != StreamTokenizer.TT_EOF) //check at start of 'line'
				{
					if (tok == StreamTokenizer.TT_NUMBER)
					{
						displayPos = (int) st.nval;
						//System.out.println("displayPos: " + displayPos);
					}
					else
					{System.out.println("Expected displayPos");}
		
					tok = st.nextToken();					
					if (tok == StreamTokenizer.TT_NUMBER)
					{
						uniTissID = (int) st.nval;
						//System.out.println("uniTissID: " + uniTissID);
					}
					else
					{System.out.println("Expected uniTiss");}
					
					// Now get UniTissueName by SQL unitissueQy using UniTissueID
					Connect cnt = new Connect();
					Connection conn = cnt.getConnection();
					ParamQuery parQy = DBQuery.getParamQuery(unitissueQy);
					try 
					{
						parQy.setPrepStatement(conn);
					} 
					catch (SQLException e) {System.out.println("SQL Exception 1: " + e.toString());}	
					try 
					{
						PreparedStatement prepStat = parQy.getPrepStatement();
						prepStat.setInt(1, uniTissID);
						ResultSet resSet = prepStat.executeQuery();
						if(resSet.first())	// move to single tuple
						{
							uniTissName = resSet.getString("UniTissueName");	
						}
					}
					catch (SQLException e)
					{
						System.out.println("SQL Exception 2: " + e.toString());
					}
					finally // close the connection
					{
						if(conn != null)
						{
							try { conn.close();}
							catch(Exception e){System.out.println("Can't close.");}
						}
					}
		    				
					DoubletDisplay display = new DoubletDisplay(displayPos, uniTissID, uniTissName);
					doubletDisplays[doubletDisplaysSize] = display;
					doubletDisplaysSize++;  
				}
				else
				{
					goOn = false;
				}
			}
		}
		catch(IOException ioe)
		{
			System.out.println("Problem reading " + DOUBLET_FILE);
		}
	}
 	
}
