package Logic;

public class Head
{
	private String m_BranchName;
	
	public Head(String name)
	{
		m_BranchName = name;
	}
	
	public String getBranchName()
	{
		return m_BranchName;
	}
	
	public void setBranchName(String name, String repositoryPath)
	{
		this.m_BranchName = name;
		changeBranchName(repositoryPath);
	}
	
	private void changeBranchName(String repositoryPath)
	{
		String path=repositoryPath+"\\.magit\\branches\\Head.txt";
		Utilz.writeFileAsString(path,getBranchName());
	}
}
