package Logic;

public class RepositoryDetail
{
	private String repositoryName;
	private String activeBranchName;
	private int numberOfBranches;
	private String lastCommit;
	private String lastCommitMessage;
	
	public RepositoryDetail(String repositoryName, String activeBranchName, int numberOfBranches, String lastCommit, String lastCommitMessage)
	{
		this.repositoryName = repositoryName;
		this.activeBranchName = activeBranchName;
		this.numberOfBranches = numberOfBranches;
		this.lastCommit = lastCommit;
		this.lastCommitMessage = lastCommitMessage;
	}
	
	public String getRepositoryName()
	{
		return repositoryName;
	}
	
	public void setRepositoryName(String repositoryName)
	{
		this.repositoryName = repositoryName;
	}
	
	public String getActiveBranchName()
	{
		return activeBranchName;
	}
	
	public void setActiveBranchName(String activeBranchName)
	{
		this.activeBranchName = activeBranchName;
	}
	
	public int getNumberOfBranches()
	{
		return numberOfBranches;
	}
	
	public void setNumberOfBranches(int numberOfBranches)
	{
		this.numberOfBranches = numberOfBranches;
	}
	
	public String getLastCommit()
	{
		return lastCommit;
	}
	
	public void setLastCommit(String lastCommit)
	{
		this.lastCommit = lastCommit;
	}
	
	public String getLastCommitMessage()
	{
		return lastCommitMessage;
	}
	
	public void setLastCommitMessage(String lastCommitMessage)
	{
		this.lastCommitMessage = lastCommitMessage;
	}
}
