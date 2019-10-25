package Logic.MessageComponents;

import Logic.Utilz;

public class PRBasicMessage implements IMessage
{
	private String repositoryName;
	private String fromUserName;
	private String baseBranchName;
	private String targetBranchName;
	private String dateOfRequestCreation;
	private String message;
	
	public PRBasicMessage(String fromUserName, String baseBranchName, String targetBranchName, String message,String repositoryName)
	{
		this.fromUserName = fromUserName;
		this.baseBranchName = baseBranchName;
		this.targetBranchName = targetBranchName;
		this.message = message;
		this.repositoryName=repositoryName;
		initialize();
	}
	
	public PRBasicMessage(PRBasicMessage prBasicMessage)
	{
		this.fromUserName = prBasicMessage.getFromUserName();
		this.baseBranchName = prBasicMessage.getBaseBranchName();
		this.targetBranchName = prBasicMessage.getTargetBranchName();
		this.message = prBasicMessage.getMessage();
		this.repositoryName=prBasicMessage.getRepositoryName();
		initialize();
	}
	
	public void initialize(){
		this.dateOfRequestCreation = Utilz.getCurrentTime();
	}
	
	public String getFromUserName()
	{
		return fromUserName;
	}
	
	public void setFromUserName(String fromUserName)
	{
		this.fromUserName = fromUserName;
	}
	
	public String getBaseBranchName()
	{
		return baseBranchName;
	}
	
	public void setBaseBranchName(String baseBranchName)
	{
		this.baseBranchName = baseBranchName;
	}
	
	public String getTargetBranchName()
	{
		return targetBranchName;
	}
	
	public void setTargetBranchName(String targetBranchName)
	{
		this.targetBranchName = targetBranchName;
	}
	
	public String getDateOfRequestCreation()
	{
		return dateOfRequestCreation;
	}
	
	public void setDateOfRequestCreation(String dateOfRequestCreation)
	{
		this.dateOfRequestCreation = dateOfRequestCreation;
	}
	
	public String getMessage()
	{
		return message;
	}
	
	public void setMessage(String message)
	{
		this.message = message;
	}
	
	public String getRepositoryName()
	{
		return repositoryName;
	}
	
	public void setRepositoryName(String repositoryName)
	{
		this.repositoryName = repositoryName;
	}
}
