package Logic.MessageComponents;

import Logic.Utilz;

public class ChatMessage implements IMessage
{
	public enum eMessageState {
		Message,
		Alert
	}

	private String userName;
	private String messageContent;
	private String date;
	private eMessageState state;
	
	public ChatMessage(String userName, String messageContent, eMessageState state)
	{
		this.userName = userName;
		this.messageContent = messageContent;
		this.state = state;
		initializeDate();
	}
	
	public void initializeDate(){
		date=Utilz.getCurrentTime();
	}
	
	public String getUserName()
	{
		return userName;
	}
	
	public void setUserName(String userName)
	{
		this.userName = userName;
	}
	
	public String getMessageContent()
	{
		return messageContent;
	}
	
	public void setMessageContent(String messageContent)
	{
		this.messageContent = messageContent;
	}
	
	public String getDate()
	{
		return date;
	}
	
	public eMessageState getState()
	{
		return state;
	}
	
	public void setState(eMessageState state)
	{
		this.state = state;
	}
}
