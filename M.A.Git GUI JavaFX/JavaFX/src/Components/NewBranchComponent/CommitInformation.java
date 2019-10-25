package Components.NewBranchComponent;

import logic.Utilz;

public class CommitInformation implements Comparable<CommitInformation>
{
	private String branchName;
	private String key;
	private String message;
	private String creationDate;
	private String author;
	private Boolean isRemote;
	
	public CommitInformation(String branchName, String key, String message, String creationDate, String author, Boolean isRemote)
	{
		this.branchName = branchName;
		this.key = key;
		this.message = message;
		this.creationDate = creationDate;
		this.author = author;
		this.isRemote = isRemote;
	}
	
	public String getKey()
	{
		return key;
	}
	
	public void setKey(String key)
	{
		this.key = key;
	}
	
	public String getCreationDate()
	{
		return creationDate;
	}
	
	public void setCreationDate(String creationDate)
	{
		this.creationDate = creationDate;
	}
	
	public String getAuthor()
	{
		return author;
	}
	
	public void setAuthor(String author)
	{
		this.author = author;
	}
	
	public boolean getRemote()
	{
		return isRemote;
	}
	
	public void setRemote(boolean remote)
	{
		this.isRemote = remote;
	}
	
	public String getMessage()
	{
		return message;
	}
	
	public void setMessage(String message)
	{
		this.message = message;
	}
	
	public int compareTo(CommitInformation other)
	{
		return Utilz.DateTimeComparator(this.creationDate,other.creationDate);
	}
	
	public String getBranchName()
	{
		return branchName;
	}
	
	public void setBranchName(String branchName)
	{
		this.branchName = branchName;
	}
}
