package Logic.MessageComponents;

import java.util.ArrayList;
import java.util.List;

public class ChatMessageManager extends MessageManager
{
	public ArrayList<IMessage> getCurrentMessages(int lastVersion)
	{
		List<IMessage> messages;
		
		messages = getMessages().subList(lastVersion, getCurrentVersion());
		
		return new ArrayList<>(messages);
	}
	
	public int getAmountOfMessages(int lastVersion){
		return getMessages().subList(lastVersion, getCurrentVersion()).size();
	}
}
