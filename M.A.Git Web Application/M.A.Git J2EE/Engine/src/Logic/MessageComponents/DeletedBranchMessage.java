package Logic.MessageComponents;

import Logic.Utilz;

public class DeletedBranchMessage implements IMessage
{
	private String repositoryName;
	private String deletedBranchName;
	private String deletedDate;
	private String deletedByUser;
	
	public DeletedBranchMessage(String repositoryName, String deletedBranchName, String deletedByUser)
	{
		this.repositoryName = repositoryName;
		this.deletedBranchName = deletedBranchName;
		this.deletedByUser = deletedByUser;
		initializeDate();
	}
	
	private void initializeDate()
	{
		deletedDate= Utilz.getCurrentTime();
	}
	
	public String getRepositoryName()
	{
		return repositoryName;
	}
	
	public void setRepositoryName(String repositoryName)
	{
		this.repositoryName = repositoryName;
	}
	
	public String getDeletedBranchName()
	{
		return deletedBranchName;
	}
	
	public void setDeletedBranchName(String deletedBranchName)
	{
		this.deletedBranchName = deletedBranchName;
	}
	
	public String getDeletedByUser()
	{
		return deletedByUser;
	}
	
	public void setDeletedByUser(String deletedByUser)
	{
		this.deletedByUser = deletedByUser;
	}
	
	public String getDeletedDate()
	{
		return deletedDate;
	}
}
