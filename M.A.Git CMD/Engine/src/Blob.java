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
	public boolean createWorkingCopyFile(File file) throws IOException
	{
		return file.createNewFile();
	}
}
