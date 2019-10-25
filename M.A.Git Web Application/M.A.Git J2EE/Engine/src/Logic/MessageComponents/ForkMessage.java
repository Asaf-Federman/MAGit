package Logic.MessageComponents;

public class ForkMessage implements IMessage
{
	private String repositoryName;
	private String name;
	
	public ForkMessage(String repositoryName, String name)
	{
		this.repositoryName = repositoryName;
		this.name = name;
	}
	
	public String getRepositoryName()
	{
		return repositoryName;
	}
	
	public void setRepositoryName(String repositoryName)
	{
		this.repositoryName = repositoryName;
	}
	
	public String getName()
	{
		return name;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
}
