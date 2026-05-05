// Models Tissue table from database 
// 15.12.2025

public class Tissue
{
	private int tissueID;			// TissueID field
	private String stage;			// Adult or Larval or Embryo		NB case
	private String age;				// Age — "" for non-embryo
	private String sex;				// Sex (Male, Female or Both) NB case - Not really relevant, but easier for compatibility
	private String tissueName;		// TissueName (can include additional beetle info)
	private String abbreviation;	// Two-letter abbreviation of tissue name
	private int uniTissueID;		// adult/larval unified tissue ID (for matching on table layout)
	private boolean reference;		// reference 'tissue' (i.e. whole) or not
	
	public Tissue(int tissueID, String stage, String age, String sex, String tissueName, String abbreviation, 
					int uniTissueID, boolean reference)
	{
		this.tissueID = tissueID;
		this.stage = stage;
		this.age = age;
		this.sex = sex;
		this.tissueName = tissueName;
		this.abbreviation = abbreviation;
		this.uniTissueID = uniTissueID;
		this.reference = reference;
	}
	
	// Accessor methods	
	public int getTissueID()
	{
		return tissueID;
	}
	
	public String getStage()
	{
		return stage;
	}
	
	public String getAge()
	{
		return age;
	}
	
	public String getSex()
	{
		return sex;
	}
	
	public String getTissueName()
	{
		return tissueName;
	}
	
	public String getAbbreviation()
	{
		return abbreviation;
	}
	
	public int getUniTissueID()
	{
		return uniTissueID;
	}
	
	public boolean isReference()
	{
		return reference;
	}
	
	public String toString()
	{
		return("TissueID: " + tissueID + ", Stage: " + stage  + ", Age: " + age  + ", " + "Sex: " + sex + ", Name: " + tissueName + 
				", Abbrev: " + abbreviation + ", UniTissueID: " + uniTissueID + ", Ref: " + reference);
	}
}
