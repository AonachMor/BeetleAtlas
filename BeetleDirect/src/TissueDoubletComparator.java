
// TissueDoubletComparator
// Sorts TissueDoublet on Display Position in ascending order
// 14.06.2018

import java.util.Comparator;

public class TissueDoubletComparator implements Comparator<TissueDoublet>
{
	public TissueDoubletComparator()
	{			
	}
	
	 public int compare(TissueDoublet doublet1, TissueDoublet doublet2)
	 {     
	    // ascending order
	    if(doublet1.getDisplayPosition() < doublet2.getDisplayPosition())
	    {
	        return -1;
	    }
	    else if(doublet1.getDisplayPosition() > doublet2.getDisplayPosition())
	    {
	        return 1;
	    }
	    else
	    {
	        return 0;    
	    }
	}
}
