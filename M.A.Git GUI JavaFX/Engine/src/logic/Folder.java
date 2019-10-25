package logic;

import org.apache.commons.codec.digest.DigestUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
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
	
	public Folder(eRoot typeOfFolder, Map<String, NodeInformation> informationMap)
	{
		this.m_TypeOfFolder = typeOfFolder;
		m_InformationMap = informationMap;
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
	
	public static String getEncryption(Map<String,NodeInformation> map)
	{
		String nodeInfo=getString(map);
		
		return DigestUtils.sha1Hex(nodeInfo);
	}
	
	public String folderToFileString()
	{
		StringBuilder nodeInfo = new StringBuilder();
		for (NodeInformation ni : getInformationMap().values())
		{
			nodeInfo.append(ni.nodeInformationToFileString());
		}
		
		return nodeInfo.toString();
	}
	
	private static String getString(Map<String,NodeInformation> map)
	{
		StringBuilder nodeInfo = new StringBuilder();
		for (NodeInformation ni : map.values())
		{
			nodeInfo.append(ni.nodeInformationSHA1());
		}
		
		return nodeInfo.toString();
	}
	
	@Override
	public Node Clone()
	{
		Map<String,NodeInformation> newNodeInformationMap= new HashMap<>();
		for(Map.Entry<String, NodeInformation> nodeInformationEntry : getInformationMap().entrySet())
		{
			newNodeInformationMap.put(nodeInformationEntry.getKey(),nodeInformationEntry.getValue().Clone());
		}
		
		return new Folder(m_TypeOfFolder,newNodeInformationMap);
	}
	
	@Override
	public void createWorkingCopyFile(File file)
	{
		file.mkdirs();
	}
	
	@Override
	public void createMagitFile(String repositoryPath,String encryptionKey) throws IOException
	{
		String path=repositoryPath+"\\.magit\\objects\\"+ encryptionKey +".txt";
		String zipPath=repositoryPath+"\\.magit\\objects\\"+ encryptionKey +".zip";
		File file = new File(path);
		file.createNewFile();
		String context=folderToFileString();
		Utilz.writeFileAsString(path, context);
		Utilz.zipFile(path,zipPath);
	}
}

