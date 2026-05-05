// Holds FPKM values for a gene in one tissue for profile comparison (formerly Abundance)
// 05.09.2012
// Modified for BeetleAtlas 24.02.2021
// Renamed 08.03.2021

public class ProfileDatum
{
	private double fpkm;			// Expression from Experiment table in DB
	private String status;			// SignalDetected from Experiment table in DB
	private int tissueID;			// FlyID from Experiment table in DB
	private String geneID;			// GeneID

	public ProfileDatum(String geneID, double fpkm, String status, int tissueID)
	{
		this.geneID = geneID;
		this.fpkm = fpkm;
		this.status = status;
		this.tissueID = tissueID;
	}
	
	public double getLogFPKM()
	{
		return Math.log(fpkm)/Math.log(2);
	}
	
	public double getFPKM()
	{
		return fpkm;
	}

	public String getStatus()
	{
		return status;
	}
	
	public int getTissueID()
	{
		return tissueID;
	}
	
	public String getGeneID()
	{
		return geneID;
	}	
}
