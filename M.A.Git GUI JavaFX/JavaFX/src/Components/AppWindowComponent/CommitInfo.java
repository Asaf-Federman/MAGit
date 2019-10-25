package Components.AppWindowComponent;

import logic.Utilz;

import java.util.Comparator;
import java.util.List;

public class CommitInfo implements Comparable<CommitInfo>
{
	private String branchName;
	private String message;
	private String creationDate;
	private String author;
	private String encryptionKey;
	private String firstPreviousCommitKey=null;
	private String secondPreviousCommitKey=null;
	
	public CommitInfo(String branchName, String message, String creationDate, String author, String encryptionKey, List<String> previousCommitKeys)
	{
		this.branchName = branchName;
		this.message = message;
		this.creationDate = creationDate;
		this.author = author;
		this.encryptionKey = encryptionKey;
		if(previousCommitKeys.size()==2)
		{
			this.secondPreviousCommitKey = previousCommitKeys.get(1);
		}
		
		if(previousCommitKeys.size()>=1)
		{
			this.firstPreviousCommitKey= previousCommitKeys.get(0);
		}
	}
	
	public String getBranchName()
	{
		return branchName;
	}
	
	public void setBranchName(String branchName)
	{
		this.branchName = branchName;
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
	
	public String getEncryptionKey()
	{
		return encryptionKey;
	}
	
	public void setEncryptionKey(String encryptionKey)
	{
		this.encryptionKey = encryptionKey;
	}
	
	public void addBranchName(String branch)
	{
		if(!branch.equals("") && !branch.isEmpty())
		{
			if (!branchName.equals("") && !branchName.isEmpty())
			{
				this.branchName += ", " + branch;
			}
			else
			{
				setBranchName(branch);
			}
		}
	}
	
	public String getFirstPreviousCommitKey()
	{
		return firstPreviousCommitKey;
	}
	
	public void setFirstPreviousCommitKey(String firstPreviousCommitKey)
	{
		this.firstPreviousCommitKey = firstPreviousCommitKey;
	}
	
	public String getSecondPreviousCommitKey()
	{
		return secondPreviousCommitKey;
	}
	
	public void setSecondPreviousCommitKey(String secondPreviousCommitKey)
	{
		this.secondPreviousCommitKey = secondPreviousCommitKey;
	}
	
	@Override
	public int compareTo(CommitInfo other)
	{
		return Utilz.DateTimeComparator(this.creationDate,other.creationDate);
	}
}
