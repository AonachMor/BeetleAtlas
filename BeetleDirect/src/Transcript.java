// Simple class to model Transcript
// Also has method to output HTML using data input
// DPL 20.06.2018

public class Transcript
{
	private String transcriptID;
	private String geneID;
	
	//public Transcript(String transcriptID, String geneID, String name, char strand, int exonCount, String exonStarts, String exonEnds)
	public Transcript(String transcriptID, String geneID)
	{
		this.transcriptID = transcriptID;
		this.geneID = geneID;
	}
	
	public String getTranscriptID()
	{
		return transcriptID;
	}
	
	public String getGeneID()
	{
		return geneID;
	}
	
	// Returns part of name after last "-" (RA, RAA etc)
	public String getIDSuffix()
	{
		return transcriptID.substring(transcriptID.lastIndexOf("-") + 1, transcriptID.length());
	}
	
	public String toString()
	{
		return transcriptID + "\t" + geneID ;
	}
}
