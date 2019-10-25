package Components.AppWindowComponent;

public class FileInfo
{
	private String fileName;
	private String creationDate;
	private String author;
	private String encryptionKey;
	
	public FileInfo(String fileName, String creationDate, String author, String encryptionKey)
	{
		this.fileName = fileName;
		this.creationDate = creationDate;
		this.author = author;
		this.encryptionKey = encryptionKey;
	}
	
	
	public String getFileName()
	{
		return fileName;
	}
	
	public void setFileName(String fileName)
	{
		this.fileName = fileName;
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
}
