import org.apache.commons.codec.digest.DigestUtils;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Folder implements Node
{
	public enum eRoot
	{
		Root, Regular
	}
	
	private eRoot m_TypeOfFolder;
	private Map<String,NodeInformation> m_InformationMap;
	
	public Folder(eRoot typeOfFolder)
	{
		this.m_TypeOfFolder = typeOfFolder;
		m_InformationMap = new TreeMap<>();
	}
	
	public Folder(eRoot typeOfFolder, Map<String,NodeInformation> informationMap)
	{
		this.m_TypeOfFolder = typeOfFolder;
		m_InformationMap = informationMap;
	}
	
	public static Folder createInstanceFromString(String contentOfFolder, eRoot typeOfFolder)
	{
		Folder newFolderInstance = new Folder(typeOfFolder);
		NodeInformation newNodeInformation;
		String[] splittedString = contentOfFolder.split(System.lineSeparator());
		
		for (String nodeInformationString : splittedString)
		{
			newNodeInformation = NodeInformation.StringToNodeInformation(nodeInformationString);
			newFolderInstance.getInformationMap().putIfAbsent(newNodeInformation.getEncryption(),newNodeInformation);
		}
		
		return newFolderInstance;
	}
	
	public eRoot getTypeOfFolder()
	{
		return m_TypeOfFolder;
	}
	
	public void setTypeOfFolder(eRoot typeOfFolder)
	{
		this.m_TypeOfFolder = typeOfFolder;
	}
	
	public Map<String,NodeInformation> getInformationMap()
	{
		return m_InformationMap;
	}
	
	public void setInformationMap(Map<String,NodeInformation> informationMap)
	{
		this.m_InformationMap = informationMap;
	}
	
	@Override
	public String toString()
	{
		String result = "";
		for (NodeInformation nf : m_InformationMap.values())
		{
			result += nf.toString();
		}
		return result;
	}
	
	public static String folderSHA1(Map<String,NodeInformation> map)
	{
		String nodeInfo = "";
		for (NodeInformation ni : map.values())
		{
			nodeInfo += ni.nodeInformationSHA1();
		}
		
		return DigestUtils.sha1Hex(nodeInfo);
	}
	
	public String folderToFileString()
	{
		String nodeInfo = "";
		for (NodeInformation nf : this.getInformationMap().values())
		{
			nodeInfo += nf.nodeInformationToFileString();
		}
		
		return nodeInfo;
	}
	
	@Override
	public boolean createWorkingCopyFile(File file)
	{
		return file.mkdirs();
	}
}

