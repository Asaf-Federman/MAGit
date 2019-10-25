package Logic.MessageComponents;

import java.util.ArrayList;
import java.util.List;

public class MagitMessageManager extends MessageManager
{
	private int lastVersion;
	
	public MagitMessageManager()
	{
		lastVersion=0;
	}
	
	public int getLastVersion()
	{
		return lastVersion;
	}
	
	public void setLastVersion(int lastVersion)
	{
		this.lastVersion = lastVersion;
	}
	
	public ArrayList<IMessage> getCurrentMessages()
	{
		List<IMessage> messages;
		
		messages = getMessages().subList(getLastVersion(), getCurrentVersion());
		setLastVersion(getCurrentVersion());
		
		return new ArrayList<>(messages);
	}
	
	public int getAmountOfMessages(){
		return getMessages().subList(getLastVersion(), getCurrentVersion()).size();
	}
}
