package Logic;

import java.util.*;

public class ChatUserManager
{
	private Collection<String> users;
	
	public ChatUserManager(){
		users=new HashSet<>();
	}
	
	public void addUser(String userName){
		users.add(userName);
	}
	
	public void removeUser(String userName){
		users.remove(userName);
	}
	
	public Collection<String> getUsers(){
		return users;
	}
}
