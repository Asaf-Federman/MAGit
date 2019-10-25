package Logic;

public class BranchInformation
{
	private String branchName;
	private String commitKey;
	private String commitMessage;
	private BranchFactory.eBranchType type;
	private boolean isHead;
	
	public BranchInformation(String branchName, String commitKey, String commitMessage, BranchFactory.eBranchType type, boolean isHead)
	{
		this.branchName = branchName;
		this.commitKey = commitKey;
		this.commitMessage = commitMessage;
		this.type = type;
		this.isHead = isHead;
	}
	
	public boolean isHead()
	{
		return isHead;
	}
	
	public void setHead(boolean head)
	{
		isHead = head;
	}
	
	public String getCommitMessage()
	{
		return commitMessage;
	}
	
	public void setCommitMessage(String commitMessage)
	{
		this.commitMessage = commitMessage;
	}
	
	public String getCommitKey()
	{
		return commitKey;
	}
	
	public void setCommitKey(String commitKey)
	{
		this.commitKey = commitKey;
	}
	
	public String getBranchName()
	{
		return branchName;
	}
	
	public void setBranchName(String branchName)
	{
		this.branchName = branchName;
	}
	
	public BranchFactory.eBranchType getType()
	{
		return type;
	}
	
	public void setType(BranchFactory.eBranchType type)
	{
		this.type = type;
	}
}
