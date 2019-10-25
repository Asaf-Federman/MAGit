package Logic;

import java.io.File;
import java.io.IOException;

public class Blob implements Node
{
	private final String m_Content;
	
	public Blob(String content)
	{
		m_Content = content;
	}
	
	public static Blob createInstanceFromString(String contentOfBlob)
	{
		return new Blob(contentOfBlob);
	}
	
	public String getContent()
	{
		return m_Content;
	}
	
	@Override
	public String toString()
	{
		String result = "";
		result += "The node content is: " + this.getContent() + System.lineSeparator();
		result += "================================================================================================";
		result += System.lineSeparator();
		
		return result;
	}
	
	@Override
	public Node Clone()
	{
		return new Blob(m_Content);
	}
	
	@Override
	public void createWorkingCopyFile(File file) throws IOException
	{
		file.createNewFile();
	}
	
	public void writeContentToWorkingCopyFile(String path)
	{
		Utilz.writeFileAsString(path,getContent());
	}
	
	@Override
	public void createMagitFile(String repositoryPath,String encryptionKey) throws IOException
	{
		String path=repositoryPath+"\\.magit\\objects\\"+ encryptionKey +".txt";
		String zipPath=repositoryPath+"\\.magit\\objects\\"+ encryptionKey +".zip";
		File file = new File(path);
		file.createNewFile();
		String context=getContent();
		Utilz.writeFileAsString(path, context);
		Utilz.zipFile(path,zipPath);
	}
	
}
