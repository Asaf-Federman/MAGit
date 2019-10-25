package Logic;

import java.util.Collection;
import java.util.LinkedList;

public class FileInformation
{
	private String name;
	private String content;
	private String encryptionKey;
	private String creationDate;
	private String author;
	private Collection<FileInformation> children;
	
	public FileInformation(String name, String encryptionKey,Collection<FileInformation> children)
	{
		this.name = name;
		this.encryptionKey = encryptionKey;
		this.children=children;
	}
	
	public FileInformation(String name, String encryptionKey, String creationDate, String author)
	{
		this.name = name;
		this.encryptionKey = encryptionKey;
		this.creationDate = creationDate;
		this.author = author;
		this.children=new LinkedList<>();
	}
	
	public FileInformation(String name, String encryptionKey, String creationDate, String author,Collection<FileInformation> children)
	{
		this.name = name;
		this.encryptionKey = encryptionKey;
		this.creationDate = creationDate;
		this.author = author;
		this.children=children;
	}
	
	
	public String getName()
	{
		return name;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	public String getEncryptionKey()
	{
		return encryptionKey;
	}
	
	public void setEncryptionKey(String encryptionKey)
	{
		this.encryptionKey = encryptionKey;
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
	
	public Collection<FileInformation> getChildren()
	{
		return children;
	}
	
	public void setChildren(Collection<FileInformation> children)
	{
		this.children = children;
	}
	
	public String getContent()
	{
		return content;
	}
	
	public void setContent(String content)
	{
		this.content = content;
	}
}
