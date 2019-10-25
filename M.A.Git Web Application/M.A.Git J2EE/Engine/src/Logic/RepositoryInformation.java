package Logic;

public class RepositoryInformation
{
	private String repositoryName;
	private String remoteRepositoryName;
	private String remoteRepositoryUserName;
	
	public RepositoryInformation(String repositoryName, String remoteRepositoryName, String remoteRepositoryUserName)
	{
		this.repositoryName = repositoryName;
		this.remoteRepositoryName = remoteRepositoryName;
		this.remoteRepositoryUserName = remoteRepositoryUserName;
	}
	
	public String getRepositoryName()
	{
		return repositoryName;
	}
	
	public void setRepositoryName(String repositoryName)
	{
		this.repositoryName = repositoryName;
	}
	
	public String getRemoteRepositoryName()
	{
		return remoteRepositoryName;
	}
	
	public void setRemoteRepositoryName(String remoteRepositoryName)
	{
		this.remoteRepositoryName = remoteRepositoryName;
	}
	
	public String getRemoteRepositoryUserName()
	{
		return remoteRepositoryUserName;
	}
	
	public void setRemoteRepositoryUserName(String remoteRepositoryUserName)
	{
		this.remoteRepositoryUserName = remoteRepositoryUserName;
	}
}
