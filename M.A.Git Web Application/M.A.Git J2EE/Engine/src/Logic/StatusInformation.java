package Logic;

public class StatusInformation
{
	private Status currentStatus;
	private Status lastStatus;
	private String state;
	private String path;
	
	public StatusInformation(Status currentStatus, Status lastStatus, String state, String path)
	{
		this.currentStatus = currentStatus;
		this.lastStatus = lastStatus;
		this.state = state;
		this.path = path;
	}
	
	public Status getCurrentStatus()
	{
		return currentStatus;
	}
	
	public void setCurrentStatus(Status currentStatus)
	{
		this.currentStatus = currentStatus;
	}
	
	public Status getLastStatus()
	{
		return lastStatus;
	}
	
	public void setLastStatus(Status lastStatus)
	{
		this.lastStatus = lastStatus;
	}
	
	public String getState()
	{
		return this.state;
	}
	
	public void setState(String state)
	{
		this.state = state;
	}
	
	public String getPath()
	{
		return path;
	}
	
	public void setPath(String path)
	{
		this.path = path;
	}
}
