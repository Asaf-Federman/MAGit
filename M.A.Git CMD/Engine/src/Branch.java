public class Branch
{
	private final String m_BranchName;
	private String m_KeyOfCommit;
	
	public Branch(String i_BranchName, String i_KeyOfCommit)
	{
		m_BranchName = i_BranchName;
		m_KeyOfCommit = i_KeyOfCommit;
	}
	
	public String getName()
	{
		return m_BranchName;
	}
	
	public String getKeyOfCommit()
	{
		return m_KeyOfCommit;
	}
	
	public void setKeyOfCommit(String keyOfCommit)
	{
		this.m_KeyOfCommit = keyOfCommit;
	}
	
	@Override
	public String toString()
	{
		int count = 0;
		String result = "";
		result += "================================================================================================";
		result +=System.lineSeparator();
		result += "The branch information is:" + System.lineSeparator();
		result += ++count + ". The branch name is: " + this.getName() + System.lineSeparator();
		result += ++count + ". The commit encryption key is: " + this.getKeyOfCommit() + System.lineSeparator();
		result += "================================================================================================";
		result += System.lineSeparator();
		
		return result;
	}
	
}
