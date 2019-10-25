package Logic.MessageComponents;

import java.util.ArrayList;

public class MessageManager
{
	private ArrayList<IMessage> messages;
	private int currentVersion;
	
	public MessageManager(){
		messages = new ArrayList<>();
		currentVersion = 0;
	}
	
	public ArrayList<IMessage> getMessages()
	{
		return messages;
	}
	
	public void setMessages(ArrayList<IMessage> messages)
	{
		this.messages = messages;
	}
	
	public int getCurrentVersion()
	{
		return currentVersion;
	}
	
	public void setCurrentVersion(int currentVersion)
	{
		this.currentVersion = currentVersion;
	}
	
	public void addMessage(IMessage message)
	{
		getMessages().add(message);
		++currentVersion;
	}
	
	public IMessage getMessage(int ID) throws Exception
	{
		if(ID<getMessages().size())
		{
			return getMessage(ID);
		}
		
		throw new Exception("ID out of array bounds");
	}
	
	public void changeMessage(int index, IMessage message) throws Exception
	{
		if(index<getMessages().size())
		{
			getMessages().set(index,message);
		}else{
			throw new Exception("ID out of array bounds");
		}
	}
}
