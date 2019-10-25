package Logic;

import Logic.MessageComponents.IMessage;
import Logic.MessageComponents.MagitMessageManager;

import java.io.IOException;
import java.util.*;

public class UserManager
{
    private final Map<String,User> usersMap;
    
    private ChatManager chatManager ;
    
    public UserManager() {
        usersMap = new HashMap<>();
        chatManager=new ChatManager();
    }

    public synchronized void addUser(String key,User value) {
        usersMap.putIfAbsent(key,value);
    }

    public synchronized void removeUser(String key) {
        usersMap.remove(key);
    }

    public synchronized Map<String,User> getUsers() {
        return Collections.unmodifiableMap(usersMap);
    }

    public synchronized void isUserExists(User newUser) throws Exception
    {
        for (User user : getUsers().values())
        {
            if (user.status(newUser).equals(User.eStatus.Exist))
            {
                return;
            }
        }
        
       throw new Exception("user with the same name and password does not exist");
    }
    
    public synchronized void isUserValid(User newUser) throws Exception
    {
        for (User user : getUsers().values())
        {
            if (user.status(newUser).equals(User.eStatus.Invalid))
            {
                throw new Exception("User with that name already exists.");
            }
        }
    }
    
    public synchronized Set<String> getActiveUsersNames()
    {
        Set<String> userNamesSet= new HashSet<>();
        for (User user : getUsers().values())
        {
            if(user.getRepositoryManagerMap().size()>0)
            {
                userNamesSet.add(user.getUserName());
            }
        }
        
        return userNamesSet;
    }
    
    public synchronized void deleteBranch(String userName,String repositoryName,String branchToDelete) throws IOException
    {
        boolean isRTB;
        User user=getUsers().get(userName);
        RepositoryManager repositoryManager=user.getRepositoryManagerMap().get(repositoryName);
        isRTB=repositoryManager.isRTB(branchToDelete);
        if(isRTB){
            String remoteUserName=repositoryManager.getRemoteRepositoryDetails().getUserName();
            User remoteUser=getUsers().get(remoteUserName);
            remoteUser.deleteBranchMessage(repositoryName,branchToDelete);
        }
        
        user.deleteBranch(repositoryName,branchToDelete);
    }
    
    public synchronized int getChatVersion(){
        return chatManager.getVersion();
    }
    
    public synchronized void addChatMessage(IMessage message){
        chatManager.addMessage(message);
    }
    
    public synchronized Collection<IMessage> getChatMessages(int lastVersion){
        return chatManager.getMessages(lastVersion);
    }
    
    public synchronized Collection<String> getChatUsers(){
        return chatManager.getUsers();
    }
    
    public synchronized void addChatUser(String userName){
        chatManager.addUser(userName);
    }
    
    public synchronized void removeChatUser(String userName){
        chatManager.removeUser(userName);
    }
}
