package Logic;

public class Status
{
	private String encryptionKey;
	private String fileName;
	private String path;
	private String content;
	
	public Status(String encryptionKey, String fileName, String path, String content)
	{
		this.encryptionKey = encryptionKey;
		this.fileName = fileName;
		this.path = path;
		this.content = content;
	}
	
	public String getEncryptionKey()
	{
		return encryptionKey;
	}
	
	public void setEncryptionKey(String encryptionKey)
	{
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
	
	public String getPath()
	{
		return path;
	}
	
	public void setPath(String path)
	{
		this.path = path;
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
