// Class to hold FPKM data for a transcript in a single tissue
// BeetleAtlas2 update 19.09.2024

public class TranscriptTissueData
{
	private String ncbiID;
	private String transcriptID;
	private int tissueID;
	private double fpkm;
	private double sd;
	private String status;
	
	public TranscriptTissueData(String ncbiID, String transcriptID, int tissueID, double fpkm, double sd, String status)
	{
		this.ncbiID = ncbiID;
		this.transcriptID = transcriptID;
		this.tissueID = tissueID;
		this.fpkm = fpkm;
		this.sd = sd;
		this.status = status;
	}
	
	public String getNCBIid()
	{
		return ncbiID;
	}
	
	public String getTranscriptID()
	{
		return transcriptID;
	}
	
	public int getTissueID()
	{
		return tissueID;
	}
	
	public double getFPKM()
	{
		return fpkm;
	}
	
	public double getSD()
	{
		return sd;
	}
	public String getStatus()
	{
		return status;
	}
	
	// replaces default as tab-separated text suitable for output
	public String toString()
	{
		return ncbiID + "\t" +  transcriptID + "\t" + tissueID + "\t" + fpkm + "\t" + sd + "\t" + status;
	}
}
