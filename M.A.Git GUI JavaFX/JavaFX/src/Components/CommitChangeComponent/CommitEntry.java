package Components.CommitChangeComponent;

import logic.Utilz;

public class CommitEntry implements Comparable<CommitEntry>
{
	private String key;
	private String message;
	private String creationDate;
	private String author;
	private boolean status;
	
	public CommitEntry(String key, String message, String creationDate, String author, boolean status)
	{
		this.key = key;
		this.message = message;
		this.creationDate = creationDate;
		this.author = author;
		this.status = status;
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
	
	public boolean getStatus()
	{
		return status;
	}
	
	public void setStatus(boolean status)
	{
		this.status = status;
	}
	
	public String getMessage()
	{
		return message;
	}
	
	public void setMessage(String message)
	{
		this.message = message;
	}
	
	public int compareTo(CommitEntry other)
	{
		return Utilz.DateTimeComparator(this.creationDate,other.creationDate);
	}
}
