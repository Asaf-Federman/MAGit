package Components.AppWindowComponent;

import com.fxgraph.graph.ICell;
import logic.Utilz;
import java.util.List;

public class CommitTreeInfo implements Comparable<CommitTreeInfo>
{
	private String message;
	private String creationDate;
	private String author;
	private String encryptionKey;
	private String firstPreviousCommitKey=null;
	private String secondPreviousCommitKey=null;
	private ICell node;
	
	public CommitTreeInfo(String message, String creationDate, String author, String encryptionKey, List<String> previousCommitKeys, ICell node)
	{
		this.message = message;
		this.creationDate = creationDate;
		this.author = author;
		this.encryptionKey = encryptionKey;
		this.node = node;
		if(previousCommitKeys.size()==2)
		{
			this.secondPreviousCommitKey = previousCommitKeys.get(1);
		}
		
		if(previousCommitKeys.size()>=1)
		{
			this.firstPreviousCommitKey= previousCommitKeys.get(0);
		}
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
	
	public ICell getNode()
	{
		return node;
	}
	
	public void setNode(ICell node)
	{
		this.node = node;
	}
	
	@Override
	public int compareTo(CommitTreeInfo other)
	{
		return Utilz.DateTimeComparator(this.creationDate,other.creationDate);
	}
}
