package logic;

import org.apache.commons.codec.digest.DigestUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Commit
{
	private final List<String> m_PreviousCommitKeys;
	private final String m_Message;
	private final String m_CreationDate;
	private final String m_Author;
	private String m_rootFolderEncryptionKey;
	
	public Commit(String rootFolderEncryptionKey, List<String> previousCommitKeys, String message, String creationDate, String author)
	{
		m_PreviousCommitKeys=previousCommitKeys;
		m_rootFolderEncryptionKey = rootFolderEncryptionKey;
		m_Message = message;
		m_CreationDate = creationDate;
		m_Author = author;
	}
	
	public Commit(String rootFolderEncryptionKey, String firstPreviousCommitKey, String secondPreviousCommitKey, String message, String creationDate, String author)
	{
		m_PreviousCommitKeys= new ArrayList<>(2);
		m_PreviousCommitKeys.add(firstPreviousCommitKey);
		m_PreviousCommitKeys.add(secondPreviousCommitKey);
		m_rootFolderEncryptionKey = rootFolderEncryptionKey;
		m_Message = message;
		m_CreationDate = creationDate;
		m_Author = author;
	}
	
	public Commit(String rootFolderEncryptionKey, String firstPreviousCommitKey, String message, String creationDate, String author)
	{
		m_PreviousCommitKeys= new ArrayList<>(1);
		m_PreviousCommitKeys.add(firstPreviousCommitKey);
		m_rootFolderEncryptionKey = rootFolderEncryptionKey;
		m_Message = message;
		m_CreationDate = creationDate;
		m_Author = author;
	}
	
	public Commit(String rootFolderEncryptionKey, String message, String creationDate, String author)
	{
		m_PreviousCommitKeys= new ArrayList<>();
		m_rootFolderEncryptionKey = rootFolderEncryptionKey;
		m_Message = message;
		m_CreationDate = creationDate;
		m_Author = author;
	}
	
	public Commit(Commit commit)
	{
		m_PreviousCommitKeys=commit.m_PreviousCommitKeys;
		m_rootFolderEncryptionKey=commit.m_rootFolderEncryptionKey;
		m_Message=commit.m_Message;
		m_CreationDate=commit.m_CreationDate;
		m_Author=commit.m_Author;
	}
	
	public static Commit createInstanceFromString(String contentOfCommit)
	{
		String[] splittedString = contentOfCommit.split(",");
		
		if(splittedString.length==4)
		{
			return new Commit(splittedString[0], splittedString[1],splittedString[2], splittedString[3]);
		}
		else if(splittedString.length==5)
		{
			return new Commit(splittedString[0], splittedString[1],splittedString[2], splittedString[3],splittedString[4]);
		}
		else
		{
			return new Commit(splittedString[0], splittedString[1],splittedString[2], splittedString[3],splittedString[4],splittedString[5]);
		}
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
		StringBuilder values = new StringBuilder(getRootFolderEncryptionKey());
		for(String previousCommitKey : getPreviousCommitKeys())
		{
			values.append(", ").append(previousCommitKey);
		}
		
		values.append(", ").append(getMessage());
		
		return DigestUtils.sha1Hex(values.toString());
	}
	
	@Override
	public String toString()
	{
		int count = 0;
		StringBuilder result = new StringBuilder();
		result.append("================================================================================================");
		result.append(System.lineSeparator());
		result.append("The commit information is:").append(System.lineSeparator());
		result.append(++count).append(". The encryption key of the root folder is: ").append(this.getRootFolderEncryptionKey()).append(System.lineSeparator());
		result.append(++count).append(". The message of the commit is: ").append(this.getMessage()).append(System.lineSeparator());
		result.append(++count).append(". The name of the person who edited the node last is: ").append(this.getAuthor()).append(System.lineSeparator());
		result.append(++count).append(". The time it was changed last is: ").append(this.getCreationDate()).append(System.lineSeparator());
		if (!this.getPreviousCommitKeys().isEmpty())
		{
			result.append(++count).append(". The previous commit keys are: ").append(System.lineSeparator());
			for(String previousCommitKey : getPreviousCommitKeys())
			{
				result.append(previousCommitKey).append(System.lineSeparator());
			}
		}
		else
		{
			result.append(++count).append(". There are no previous commits ").append(System.lineSeparator());
		}
		
		result.append("================================================================================================");
		result.append(System.lineSeparator());
		
		return result.toString();
	}
	
	public String commitToFileString()
	{
		if(getPreviousCommitKeys().size()==2)
		{
			return this.getRootFolderEncryptionKey() + "," + this.getPreviousCommitKeys().get(0) + "," + this.getPreviousCommitKeys().get(1) +"," + this.getMessage() + "," + this.getCreationDate() + "," + this.getAuthor();
		}
		else if(getPreviousCommitKeys().size()==1)
		{
			return this.getRootFolderEncryptionKey() + "," + this.getPreviousCommitKeys().get(0) + "," + this.getMessage() + "," + this.getCreationDate() + "," + this.getAuthor();
		}
		else
		{
			return this.getRootFolderEncryptionKey() + "," + this.getMessage() + "," + this.getCreationDate() + "," + this.getAuthor();
		}
	}
	
	public List<String> getPreviousCommitKeys()
	{
		return m_PreviousCommitKeys;
	}
	
	public void createMagitCommit(String repositoryPath) throws IOException
	{
		String path=repositoryPath+"\\.magit\\objects\\"+CommitSHA1() +".txt";
		String zipPath=repositoryPath+"\\.magit\\objects\\"+CommitSHA1() +".zip";
		File file = new File(path);
		file.createNewFile();
		String context=commitToFileString();
		Utilz.writeFileAsString(path, context);
		Utilz.zipFile(path,zipPath);
	}
}
