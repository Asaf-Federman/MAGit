package Logic.MessageComponents;

public class PRStatus extends PRBasicMessage
{
	enum ePRStatus{
		Open,
		Accepted,
		Declined
	}
	
	private ePRStatus status;
	private int ID;
	private static int serialNumber=0;
	
	public PRStatus(String fromUserName, String baseBranchName, String targetBranchName, String message,String repositoryName)
	{
		super(fromUserName, baseBranchName, targetBranchName, message,repositoryName);
		initializeStatus();
	}
	
	public PRStatus(PRBasicMessage prBasicMessage){
		super(prBasicMessage.getFromUserName(),prBasicMessage.getBaseBranchName(),prBasicMessage.getTargetBranchName(),prBasicMessage.getMessage(),prBasicMessage.getRepositoryName());
		setDateOfRequestCreation(prBasicMessage.getDateOfRequestCreation());
		initializeStatus();
	}
	
	private void initializeStatus()
	{
		setStatus(ePRStatus.Open);
		++serialNumber;
		setID(getSerialNumber());
	}
	
	public ePRStatus getStatus()
	{
		return status;
	}
	
	public void setStatus(ePRStatus status)
	{
		this.status = status;
	}
	
	public static int getSerialNumber()
	{
		return serialNumber;
	}
	
	public static void setSerialNumber(int serialNumber)
	{
		PRStatus.serialNumber = serialNumber;
	}
	
	public int getID()
	{
		return ID;
	}
	
	public void setID(int ID)
	{
		this.ID = ID;
	}
}
