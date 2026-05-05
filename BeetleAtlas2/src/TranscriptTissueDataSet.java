// Class to hold a set of TranscriptTissuedata objects for all the tissues of an expt.for one transcript
// BeetleAtlas2 19.09.2024

public class TranscriptTissueDataSet
{
	private String ncbiID;
	private String transcriptID;
	private TranscriptTissueData [] transcriptDataList;
	private final int LIST_LENGTH = 30;
	private int listSize;
	
	public TranscriptTissueDataSet(String ncbiID, String transcriptID)
	{
		this.ncbiID = ncbiID;
		this.transcriptID = transcriptID;
		transcriptDataList = new TranscriptTissueData [LIST_LENGTH];
	}
	
	public void add(TranscriptTissueData data)
	{
		//check for occupancy of array and expand as required
		if(listSize>transcriptDataList.length - 1)
		{
			TranscriptTissueData[] newList = new TranscriptTissueData[listSize*2];
			System.arraycopy(transcriptDataList, 0, newList, 0, listSize);
			transcriptDataList = newList;
		}
		transcriptDataList[listSize] = data;
		listSize++;
	}
	
	public int getTranscriptDataListSize()
	{
		return listSize;
	}
	
	public String getNCBIid()
	{
		return ncbiID;
	}	
	
	public String getTranscriptID()
	{
		return transcriptID;
	}
	
	public TranscriptTissueData getTranscriptTissueData(int pos)
	{
		return transcriptDataList[pos];
	}
	
	public TranscriptTissueData getTranscriptTissueDataByID(int id)
	{
		for (int i=0; i<listSize; i++)
		{
			if(transcriptDataList[i].getTissueID() == id)
			{
				return transcriptDataList[i];
			}
		}
		return null;
	}
	
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		for (int i=0; i<listSize; i++)
		{
			sb.append(transcriptDataList[i].toString());
			sb.append("\n");
		}
		return sb.toString();
	}
}
