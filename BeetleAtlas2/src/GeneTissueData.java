// Class to hold FPKM (and enrichment) data for a gene in a single tissue
// BeetleAtlas2 update 19.09.2024


public class GeneTissueData
{
	private String ncbiID;
	private int tissueID;
	private double fpkm;					// mean
	private double [] repFPKMlist;			// array should have a size of 3 (i.e. holds up to triplicate)
	private double sd;
	private double enrichment;
	private String status;
	
	public GeneTissueData(String ncbiID, int tissueID, double fpkm, double[] repFPKMlist, double sd, String status)
	{
		this.ncbiID = ncbiID;
		this.tissueID = tissueID;
		this.fpkm = fpkm;
		this.repFPKMlist = repFPKMlist;
		this.sd = sd;
		this.status = status;
	}
	
	public void setEnrichment(double value)
	{
		enrichment = value;
	}
	
			// Accessor methods //
	
	public String getNCBIid()
	{
		return ncbiID;
	}
	
	public int getTissueID()
	{
		return tissueID;
	}
	
	public double getFPKM()
	{
		return fpkm;
	}
	
	public double getRepFPKM(int pos)
	{
		return repFPKMlist[pos];
	}
	
	public double getSD()
	{
		return sd;
	}
	
	public double getEnrichment()
	{
		return enrichment;
	}
	
	public String getStatus()
	{
		return status;
	}
		
	// replaces default as tab-separated text suitable for output
	public String toString()
	{
		return ncbiID + "\t" + tissueID + "\t" + fpkm + "\t" + "(" + repFPKMlist[0] + ", " +  repFPKMlist[1] + ", " +  repFPKMlist[2] + ")" 
					+ "\t" + sd + "\t" + status;
	}
}
