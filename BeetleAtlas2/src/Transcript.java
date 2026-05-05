// Simple class to model Transcript
// Also has method to output HTML using data input
// BeetleAtlas2 19.09.2024

public class Transcript
{
	private String transcriptID;
	private String ncbiID;
	
	//public Transcript(String transcriptID, String ncbiID, String name, char strand, int exonCount, String exonStarts, String exonEnds)
	public Transcript(String transcriptID, String ncbiID)
	{
		this.transcriptID = transcriptID;
		this.ncbiID = ncbiID;
	}
	
	public String getTranscriptID()
	{
		return transcriptID;
	}
	
	public String getNCBIid()
	{
		return ncbiID;
	}
	
	// Returns part of name after last "-" (RA, RAA etc)
	public String getIDSuffix()
	{
		return transcriptID.substring(transcriptID.lastIndexOf("-") + 1, transcriptID.length());
	}
	
	public String toString()
	{
		return transcriptID + "\t" + ncbiID ;
	}
}
