import org.apache.commons.codec.digest.DigestUtils;

public class Commit
{
	private final String m_PreviousCommitKey;
	private final String m_Message;
	private final String m_CreationDate;
	private final String m_Author;
	private String m_rootFolderEncryptionKey;
	
	public Commit(String rootFolderEncryptionKey, String previousCommitKey, String message, String creationDate, String author)
	{
		m_rootFolderEncryptionKey = rootFolderEncryptionKey;
		m_PreviousCommitKey = previousCommitKey;
		m_Message = message;
		m_CreationDate = creationDate;
		m_Author = author;
	}
	
	public static Commit createInstanceFromString(String contentOfCommit)
	{
		String[] splittedString = contentOfCommit.split(",");
		return new Commit(splittedString[0], splittedString[1], splittedString[2], splittedString[3], splittedString[4]);
	}
	
	public String getPreviousCommitKey()
	{
		return m_PreviousCommitKey;
	}
	
	public void setRootFolderEncryptionKey(String encryption)
	{
		this.m_rootFolderEncryptionKey = encryption;
	}
	
	public String getRootFolderEncryptionKey()
	{
		return m_rootFolderEncryptionKey;
	}
	
	public String getMessage()
	{
		return m_Message;
	}
	
	public String getCreationDate()
	{
		return m_CreationDate;
	}
	
	public String getAuthor()
	{
		return m_Author;
	}
	
	public String CommitSHA1()
	{
		String values = getRootFolderEncryptionKey() + ", " + getPreviousCommitKey() + ", " + getMessage();
		return DigestUtils.sha1Hex(values);
	}
	
	@Override
	public String toString()
	{
		int count = 0;
		String result = "";
		result += "================================================================================================";
		result +=System.lineSeparator();
		result += "The commit information is:" + System.lineSeparator();
		result += ++count + ". The encryption key of the root folder is: " + this.getRootFolderEncryptionKey() + System.lineSeparator();
		result += ++count + ". The message of the commit is: " + this.getMessage() + System.lineSeparator();
		result += ++count + ". The name of the person who edited the node last is: " + this.getAuthor() + System.lineSeparator();
		result += ++count + ". The time it was changed last is: " + this.getCreationDate() + System.lineSeparator();
		if (this.getPreviousCommitKey() != null)
		{
			result += ++count + ". The previous commit keys is: " + System.lineSeparator();
		}
		else
		{
			result += ++count + ". There are no previous commits " + System.lineSeparator();
		}
		
		result += "================================================================================================";
		result += System.lineSeparator();
		
		return result;
	}
	
	public String commitToFileString()
	{
		return this.getRootFolderEncryptionKey() + "," + this.getPreviousCommitKey() + "," + this.getMessage() + "," + this.getCreationDate() + "," + this.getAuthor();
	}
}
