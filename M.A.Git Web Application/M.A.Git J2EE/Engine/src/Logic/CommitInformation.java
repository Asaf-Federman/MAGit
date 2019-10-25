package Logic;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class CommitInformation implements Comparable<CommitInformation>
{
	private String encryptionKey;
	private String message;
	private String creationDate;
	private String author;
	private Collection<String> relatedBranches;
	
	public CommitInformation(String encryptionKey, String message, String creationDate, String author)
	{
		this.encryptionKey = encryptionKey;
		this.message = message;
		this.creationDate = creationDate;
		this.author = author;
		relatedBranches=new LinkedList<>();
	}
	
	public CommitInformation(String encryptionKey, String message, String creationDate, String author, Collection<String> relatedBranches)
	{
		this.encryptionKey = encryptionKey;
		this.message = message;
		this.creationDate = creationDate;
		this.author = author;
		this.relatedBranches=relatedBranches;
	}
	
	public String getEncryptionKey()
	{
		return encryptionKey;
	}
	
	public void setEncryptionKey(String encryptionKey)
	{
		this.encryptionKey = encryptionKey;
	}
	
	public String getMessage()
	{
		return message;
	}
	
	public void setMessage(String message)
	{
		this.message = message;
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
	
	public Collection<String> getRelatedBranches()
	{
		return relatedBranches;
	}
	
	public void setRelatedBranches(Collection<String> relatedBranches)
	{
		this.relatedBranches = relatedBranches;
	}
	
	@Override
	public int compareTo(CommitInformation otherCommit)
	{
		return Utilz.DateTimeComparator(getCreationDate(),otherCommit.getCreationDate());
	}
}
