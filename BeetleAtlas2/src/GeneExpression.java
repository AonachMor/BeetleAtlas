// Class to hold all the expression results for a single gene, including transcript data
// BeetleAtlas2 19.09.2024

public class GeneExpression extends Expression
{
	private String ncbiID;								// NCBI ID of gene searched for
	private GeneTissueDataSet dataset;
	private TranscriptTissueDataSet [] transcriptDataList;
	private final int TRANSCRIPSET_LENGTH = 10;
	private int transcriptListSize = 0;
	
	public GeneExpression(String ncbiID)
	{
		this.ncbiID = ncbiID;
		transcriptDataList = new TranscriptTissueDataSet [TRANSCRIPSET_LENGTH];
	}
	
	public void addTranscriptDataset(TranscriptTissueDataSet set)
	{
		//check for occupancy of array and expand as required
		if(transcriptListSize>transcriptDataList.length - 1)
		{
			TranscriptTissueDataSet[] newList = new TranscriptTissueDataSet[transcriptListSize*2];
			System.arraycopy(transcriptDataList, 0, newList, 0, transcriptListSize);
			transcriptDataList = newList;
		}
		transcriptDataList[transcriptListSize] = set;
		transcriptListSize++;
	}
		
	public void setGeneData(GeneTissueDataSet dataset)
	{
		this.dataset = dataset;
	}
	
	// 'Get' methods
	
	public GeneTissueDataSet getGeneData()
	{
		return dataset;
	}
	
	public TranscriptTissueDataSet getTranscriptData(int pos)
	{
		return transcriptDataList[pos];
	}
	
	public int getTranscriptDataSize()
	{
		return transcriptListSize;
	}
	
	public String getNCBIid()
	{
		return ncbiID;
	}
	
	// for testing
	public String geneDataToString()
	{
		return dataset.toString();
	}

	// for testing
	public String transcriptDataToString()
	{
		StringBuilder sb = new StringBuilder();
		for(int i=0; i<transcriptListSize; i++)
		{
			sb.append(transcriptDataList[i].toString());
			sb.append("\n");
		}
		return sb.toString();
	}

}
