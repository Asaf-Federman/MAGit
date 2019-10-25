package Logic;

import java.util.List;

public class WorkingCopyInformation
{
	private String fileName;
	private String path;
	private List<WorkingCopyInformation> childrenNames;
	private String content;
	
	public WorkingCopyInformation(String fileName,String path, List<WorkingCopyInformation> childrenNames,String content)
	{
		this.fileName = fileName;
		this.path=path;
		this.childrenNames = childrenNames;
		this.content=content;
	}
	
	public WorkingCopyInformation(String fileName,String path, List<WorkingCopyInformation> childrenNames)
	{
		this.fileName = fileName;
		this.path=path;
		this.childrenNames = childrenNames;
	}
	
	public String getFileName()
	{
		return fileName;
	}
	
	public void setFileName(String fileName)
	{
		this.fileName = fileName;
	}
	
	public List<WorkingCopyInformation> getChildrenNames()
	{
		return childrenNames;
	}
	
	public void setChildrenNames(List<WorkingCopyInformation> childrenNames)
	{
		this.childrenNames = childrenNames;
	}
	
	public String getContent()
	{
		return content;
	}
	
	public void setContent(String content)
	{
		this.content = content;
	}
	
	public String getPath()
	{
		return path;
	}
	
	public void setPath(String path)
	{
		this.path = path;
	}
}
