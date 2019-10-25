package Logic;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ConflictItem
{
	public enum eFileOrigin {
		Ours, Theirs
	}
	
	public static class Encryption{
		private eFileOrigin fileOrigin;
		private String encryptionKey;
		
		public Encryption(eFileOrigin fileOrigin, String encryptionKey)
		{
			this.fileOrigin = fileOrigin;
			this.encryptionKey = encryptionKey;
		}
		
		public eFileOrigin getFileOrigin()
		{
			return fileOrigin;
		}
		
		public void setFileOrigin(eFileOrigin fileOrigin)
		{
			this.fileOrigin = fileOrigin;
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
	
	private List<Encryption> encryptionList;
	private String fatherKey;
	private String path;
	
	public ConflictItem(List<Encryption> encryptionList, String fatherKey, String path)
	{
		this.encryptionList=encryptionList;
		this.fatherKey = fatherKey;
		this.path = path;
	}
	
	public List<Encryption> getEncryptionList()
	{
		return encryptionList;
	}
	
	public void setEncryptionList(List<Encryption> encryptionList)
	{
		this.encryptionList = encryptionList;
	}
	
	public String getFatherKey()
	{
		return fatherKey;
	}
	
	public void setFatherKey(String fatherKey)
	{
		this.fatherKey = fatherKey;
	}
	
	public String getPath()
	{
		return path;
	}
	
	public void setPath(String path)
	{
		this.path = path;
	}
	
	public List<Encryption> differentKeyList()
	{
		return this.encryptionList.stream().filter(encryptionKey-> !encryptionKey.encryptionKey.equals(fatherKey)).collect(Collectors.toList());
	}
	
	public int differentKeyLength()
	{
		return differentKeyList().size();
	}
	
	public Set<Encryption> differentKeySet()
	{
		return this.encryptionList.stream().filter(encryptionKey-> !encryptionKey.encryptionKey.equals(fatherKey)).collect(Collectors.toSet());
	}
	
	public int differentKeyItemsLength()
	{
		return this.differentKeySet().size();
	}
	
	public Set<Encryption> getEncryptionSet()
	{
		return new HashSet<>(this.encryptionList);
	}
	
	@Override
	public String toString()
	{
		return getPath();
	}
}

