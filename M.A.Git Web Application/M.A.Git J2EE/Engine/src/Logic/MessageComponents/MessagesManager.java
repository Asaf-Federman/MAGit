package Logic.MessageComponents;

import java.util.ArrayList;

public class MessagesManager
{
	public enum eMessageManager{
		ForkManager(0),
		PRAlertManager(1),
		PRStatus(2),
		deletedBranchMessage(3);
		
		private int value;
		
		eMessageManager(int value) {
			this.value = value;
		}
		
		public int getNumVal() {
			return value;
		}
		
		public static eMessageManager getType(int value){
			return eMessageManager.values()[value];
		}
	}
	
	private ArrayList<MagitMessageManager> messageManagers;
	private static final int AMOUNT_OF_MANAGERS=4;
	
	public MessagesManager(){
		messageManagers=new ArrayList<>(AMOUNT_OF_MANAGERS);
		for(int i=0;i<AMOUNT_OF_MANAGERS;++i){
			getMessageManagers().add(new MagitMessageManager());
		}
	}
	
	public ArrayList<MagitMessageManager> getMessageManagers()
	{
		return messageManagers;
	}
	
	public void setMessageManagers(ArrayList<MagitMessageManager> messageManagers)
	{
		this.messageManagers = messageManagers;
	}
	
	public void addMessage(IMessage message)
	{
		if(message instanceof ForkMessage)
		{
			getMessageManagers().get(eMessageManager.ForkManager.value).addMessage(message);
		}
		else if(message instanceof PRStatus){
			getMessageManagers().get(eMessageManager.PRStatus.value).addMessage(message);
		}
		else if(message instanceof PRBasicMessage){
			getMessageManagers().get(eMessageManager.PRAlertManager.value).addMessage(message);
		}else if(message instanceof DeletedBranchMessage){
			getMessageManagers().get(eMessageManager.deletedBranchMessage.value).addMessage(message);
		}
	}
	
	public ArrayList<IMessage> getCurrentMessages(eMessageManager type){
		return getMessageManagers().get(type.value).getCurrentMessages();
	}
	
	public ArrayList<IMessage> getCurrentMessages(){
		ArrayList<IMessage> arrayList=new ArrayList<>();
		for(MagitMessageManager messageManager : getMessageManagers()){
			arrayList.addAll(messageManager.getCurrentMessages());
		}
		
		return arrayList;
	}
	
	public int getAmountOfMessages(){
		int totalAmountOfMessages=0;
		for(int i=0;i<AMOUNT_OF_MANAGERS;++i){
			totalAmountOfMessages+=getMessageManagers().get(i).getAmountOfMessages();
		}
		
		return totalAmountOfMessages;
	}
	
}
