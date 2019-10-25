package Logic;

import Logic.MessageComponents.ChatMessageManager;
import Logic.MessageComponents.IMessage;

import java.util.Collection;
import java.util.List;

public class ChatManager
{
	private ChatMessageManager chatMessageManager;
	private ChatUserManager chatUserManager;
	
	public ChatManager(){
		chatMessageManager=new ChatMessageManager();
		chatUserManager=new ChatUserManager();
	}
	
	public int getVersion(){
		return chatMessageManager.getCurrentVersion();
	}
	
	public void addMessage(IMessage message){
		chatMessageManager.addMessage(message);
	}
	
	public Collection<IMessage> getMessages(int lastVersion){
		return chatMessageManager.getCurrentMessages(lastVersion);
	}
	
	public Collection<String> getUsers(){
		return chatUserManager.getUsers();
	}
	
	public void addUser(String userName){
		chatUserManager.addUser(userName);
	}
	
	public void removeUser(String userName){
		chatUserManager.removeUser(userName);
	}
}
