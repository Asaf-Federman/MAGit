package Logic;

import java.util.Comparator;

public class NodeInformation
{
	public enum ItemType
	{
		Folder,
		Blob
	}
	
	static class sortByName implements Comparator<NodeInformation>
	{
		public int compare(NodeInformation a, NodeInformation b)
		{
			return a.getName().compareTo(b.getName());
		}
	}
	
	private final String r_Name;
	private final String r_Encryption;
	private final String r_LastChangeName;
	private final ItemType m_Type;
	private String m_LastChangeDate;
	
	public NodeInformation(String name, String encryption, String lastChangeName, ItemType type)
	{
		this.m_Type = type;
		this.r_Name = name;
		this.r_Encryption = encryption;
		this.r_LastChangeName = lastChangeName;
		m_LastChangeDate = Utilz.getCurrentTime();
	}
	
	public NodeInformation(String name, String encryption, String lastChangeName, ItemType type, String lastChangeDate)
	{
		this.m_Type = type;
		this.r_Name = name;
		this.r_Encryption = encryption;
		this.r_LastChangeName = lastChangeName;
		m_LastChangeDate = lastChangeDate;
	}
	
	public NodeInformation Clone()
	{
		return new NodeInformation(r_Name,r_Encryption,r_LastChangeName,m_Type,m_LastChangeDate);
	}
	
	public String getName()
	{
		return r_Name;
	}
	
	public String getEncryption()
	{
		return r_Encryption;
	}
	
	public String getLastChangeName()
	{
		return r_LastChangeName;
	}
	
	public String getLastChangeDate()
	{
		return m_LastChangeDate;
	}
	
	public ItemType getType()
	{
		return m_Type;
	}
	
	@Override
	public String toString()
	{
		int count = 0;
		String result;
		result = "================================================================================================";
		result +=System.lineSeparator();
		result += "The node information is:" + System.lineSeparator();
		result += ++count + ". The type of the node is: " + this.getType() + System.lineSeparator();
		result += ++count + ". The name of the node is: " + this.getName() + System.lineSeparator();
		result += ++count + ". The encryption key of the node is: " + this.getEncryption() + System.lineSeparator();
		result += ++count + ". The name of the person who edited the node last is: " + this.getLastChangeName() + System.lineSeparator();
		result += ++count + ". The time it was changed last " + this.getLastChangeDate() + System.lineSeparator();
		result += "================================================================================================";
		result += System.lineSeparator();
		
		return result;
	}
	
	public String nodeInformationSHA1()
	{
		return this.getName() + "," + this.getEncryption() + "," + this.getType() + "," + System.lineSeparator();
	}
	
	public String nodeInformationToFileString()
	{
		return this.getName() + "," + this.getEncryption() + "," + this.getType() + "," + this.getLastChangeName() + "," + this.getLastChangeDate() + System.lineSeparator();
	}
	
	public static NodeInformation StringToNodeInformation(String stringToParse)
	{
		String[] splittedString;
		ItemType nodeType;
		splittedString = stringToParse.split(",");
		
		if (splittedString[2].equals("Folder"))
		{
			nodeType = ItemType.Folder;
		}
		else
		{
			nodeType = ItemType.Blob;
		}
		
		return new NodeInformation(splittedString[0], splittedString[1], splittedString[3], nodeType, splittedString[4]);
	}
}
