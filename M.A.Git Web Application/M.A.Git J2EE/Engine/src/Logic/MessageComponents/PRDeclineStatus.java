package Logic.MessageComponents;

public class PRDeclineStatus extends PRStatus
{
	private String declineMessage;
	
	public PRDeclineStatus(String fromUserName, String baseBranchName, String targetBranchName, String message,String repositoryName, String declineMessage)
	{
		super(fromUserName, baseBranchName, targetBranchName, message,repositoryName);
		this.declineMessage = declineMessage;
	}
	
	public PRDeclineStatus(PRBasicMessage prBasicMessage, String declineMessage)
	{
		super(prBasicMessage);
		this.declineMessage = declineMessage;
	}
	
	public String getDeclineMessage()
	{
		return declineMessage;
	}
	
	public void setDeclineMessage(String declineMessage)
	{
		this.declineMessage = declineMessage;
	}
}
