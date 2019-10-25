package Logic;

public class RemoteRepositoryDetails
{
	private String remotePath;
	private String userName;
	private String repositoryName;
	
	public RemoteRepositoryDetails(String remotePath,String repositoryName, String userName)
	{
		this.remotePath = remotePath;
		this.userName = userName;
		this.repositoryName=repositoryName;
	}
	
	public String getRemotePath()
	{
		return remotePath;
	}
	
	public void setRemotePath(String remotePath)
	{
		this.remotePath = remotePath;
	}
	
	public String getUserName()
	{
		return userName;
	}
	
	public void setUserName(String userName)
	{
		this.userName = userName;
	}
	
	public String getRepositoryName()
	{
		return repositoryName;
	}
	
	public String contentToFile(){
		return remotePath+","+repositoryName+","+userName;
	}
	
}
