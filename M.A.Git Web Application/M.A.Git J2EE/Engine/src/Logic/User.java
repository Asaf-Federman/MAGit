package Logic;

import Logic.MessageComponents.DeletedBranchMessage;
import Logic.MessageComponents.IMessage;
import Logic.MessageComponents.MessagesManager;
import Logic.MessageComponents.PRStatus;

import javax.xml.bind.JAXBException;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class User
{
	
	enum eStatus{
		Exist,Invalid,New
	}
	
	private Map<String,RepositoryManager> repositoryManagerMap;
	private String userName;
	private String password;
	private XMLFetcher xmlFetcher;
	private String path;
	private MessagesManager messageManagers;
	
	public User(String userName, String password)
	{
		this.userName = userName;
		this.password = password;
	}
	
	public void initialize()
	{
		xmlFetcher=new XMLFetcher();
		repositoryManagerMap=new HashMap<>();
		path=Utilz.databasePath+"\\"+getUserName();
		messageManagers =new MessagesManager();
	}
	
	public String getPath()
	{
		return path;
	}
	
	public void setPath(String path)
	{
		this.path = path;
	}
	
	public void addRepositoryManager(RepositoryManager repositoryManager)
	{
		repositoryManagerMap.putIfAbsent(repositoryManager.getRepositoryName(),repositoryManager);
	}
	
	public Map<String, RepositoryManager> getRepositoryManagerMap()
	{
		return repositoryManagerMap;
	}
	
	public String getPassword()
	{
		return password;
	}
	
	public void setPassword(String password)
	{
		this.password = password;
	}
	
	public String getUserName()
	{
		return userName;
	}
	
	public void setUserName(String userName)
	{
		this.userName = userName;
	}
	
	@Override
	public String toString()
	{
		return getUserName()+","+getPassword();
	}
	
	public eStatus status(User user)
	{
		if(getUserName().equals(user.getUserName()))
		{
			if(getPassword().equals(user.getPassword()))
			{
				return eStatus.Exist;
			}
			else
			{
				return eStatus.Invalid;
			}
		}
		
		return eStatus.New;
	}
	
	public Utilz.TwoParametersResult validateXML(BufferedReader data) throws JAXBException
	{
		Utilz.TwoParametersResult res=xmlFetcher.checkValidationXML(data,getPath());
		
		if(!res.m_IsValid){
			xmlFetcher= new XMLFetcher();
		}
		
		return res;
	}
	
	public void fetchXMLRepository() throws IOException
	{
		RepositoryManager repositoryManager;
		Repository repository = xmlFetcher.fetchXMLRepository();
		String remotePath=xmlFetcher.remotePath();
		RemoteRepositoryDetails remoteRepositoryDetails;
		
		repositoryManager= new RepositoryManager();
		repositoryManager.setRepository(repository);
		repositoryManager.setUsername(getUserName());
		if(remotePath!=null && !remotePath.isEmpty())
		{
			String[] splittedString=remotePath.split("\\\\");
			remoteRepositoryDetails = new RemoteRepositoryDetails(remotePath, splittedString[splittedString.length-1],"adminstrator");
			repositoryManager.setRemoteRepositoryDetails(remoteRepositoryDetails);
		}
		
		repositoryManager.fetchWorkingCopyInfo(repositoryManager.getRepository());
		addRepositoryManager(repositoryManager);
		xmlFetcher=new XMLFetcher();
	}
	
	public void cloneRepositories(Collection<RepositoryManager> repositoriesCollection,String fromUser) throws Exception
	{
		for(RepositoryManager repositoryManager : repositoriesCollection){
			cloneRepository(repositoryManager,fromUser);
		}
	}
	
	public void cloneRepository(RepositoryManager repositoryManager,String fromUser) throws Exception
	{
		if(repositoryManager.getRemoteRepositoryDetails()!=null){
			throw new Exception("Requested repository is remote, and therefore can not be cloned");
		}
		else if(getRepositoryManagerMap().get(repositoryManager.getRepositoryName())!=null)
		{
			throw new Exception("You already have a repository with the name "+repositoryManager.getRepositoryName());
		}
		else
		{
			RepositoryManager newRepositoryManager= new RepositoryManager();
			String repositoryPath = getPath() + "\\" + repositoryManager.getRepositoryName();
			String repositoryName=repositoryManager.getRepositoryName();
			new File(repositoryPath).mkdirs();
			newRepositoryManager.cloneRepository(repositoryManager, repositoryPath,repositoryName,fromUser);
			newRepositoryManager.setUsername(getUserName());
			addRepositoryManager(newRepositoryManager);
		}
	}
	
	public MessagesManager getMessageManagers()
	{
		return messageManagers;
	}
	
	public void addMessage(IMessage message)
	{
		getMessageManagers().addMessage(message);
	}
	
	public ArrayList<IMessage> getCurrentMessages(MessagesManager.eMessageManager type){
		return getMessageManagers().getCurrentMessages(type);
	}
	
	public ArrayList<IMessage> getCurrentMessages(){
		return getMessageManagers().getCurrentMessages();
	}
	
	public List<BranchInformation> getBranchInformationList(String repositoryName){
		return getRepositoryManagerMap().get(repositoryName).branchesInformation();
	}
	
	public Collection<CommitInformation> getCommitInformationCollection(String repositoryName){
		return getRepositoryManagerMap().get(repositoryName).commitsInformation();
	}
	
	public FileInformation getFileInformation(String repositoryName,String commitKey){
		return getRepositoryManagerMap().get(repositoryName).getFileInformation(commitKey);
	}
	
	public WorkingCopyInformation getWorkingCopyInformation(String repositoryName) throws IOException
	{
		return getRepositoryManagerMap().get(repositoryName).getWorkingCopyInformation();
	}
	
	public void changeWorkingCopyInformation(String repositoryName, WorkingCopyInformation newWorkingCopyInformation, String action)
	{
		getRepositoryManagerMap().get(repositoryName).changeWorkingCopyInformation(newWorkingCopyInformation,action);
	}
	
	public List<StatusInformation> getStatusInformation(String repositoryName) throws IOException
	{
		RepositoryManager repositoryManager=getRepositoryManagerMap().get(repositoryName);
		return repositoryManager.showCurrentStatus(repositoryManager.getRepository());
	}
	
	public void commit(String repositoryName, String commitMessage) throws IOException
	{
		RepositoryManager repositoryManager=getRepositoryManagerMap().get(repositoryName);
		repositoryManager.commit(commitMessage);
	}
	
	public RepositoryInformation getRepositoryInformation(String repositoryName)
	{
		RepositoryManager repositoryManager=getRepositoryManagerMap().get(repositoryName);
		return repositoryManager.getRepositoryInformation();
	}
	
	public void changeHead(String repositoryName, String branchName) throws Exception
	{
		RepositoryManager repositoryManager=getRepositoryManagerMap().get(repositoryName);
		if(getStatusInformation(repositoryName).size()!=0)
		{
			throw new Exception("There are open changes that need to be commited before taking such an action");
		}
		
		repositoryManager.changeHeadBranch(branchName);
	}
	
	public void newBranch(String repositoryName, String branchName, String newBranchName) throws Exception
	{
		RepositoryManager repositoryManager=getRepositoryManagerMap().get(repositoryName);
		repositoryManager.createBranch(branchName,newBranchName);
	}
	
	public void deleteBranch(String repositoryName, String deleteBranchName) throws IOException
	{
		RepositoryManager repositoryManager=getRepositoryManagerMap().get(repositoryName);
		repositoryManager.deleteBranchCases(deleteBranchName);
	}
	
	public void deleteBranchMessage(String repositoryName,String deleteBranchName)
	{
		DeletedBranchMessage deletedBranchMessage = new DeletedBranchMessage(repositoryName, deleteBranchName, getUserName());
		addMessage(deletedBranchMessage);
	}
	
	public void push(String repositoryName) throws Exception
	{
		RepositoryManager repositoryManager=getRepositoryManagerMap().get(repositoryName);
		repositoryManager.push();
	}
	
	public void push(String repositoryName,String branchName) throws IOException
	{
		RepositoryManager repositoryManager=getRepositoryManagerMap().get(repositoryName);
		repositoryManager.push(branchName);
	}
	
	public void pull(String repositoryName) throws Exception
	{
		RepositoryManager repositoryManager=getRepositoryManagerMap().get(repositoryName);
		repositoryManager.pull();
	}
	
	public BranchesNames getBranchesNames(String repositoryName){
		RepositoryManager repositoryManager=getRepositoryManagerMap().get(repositoryName);
		return repositoryManager.branchNames();
	}
	
	public void addPRStatus(IMessage message,String repositoryName){
		RepositoryManager repositoryManager=getRepositoryManagerMap().get(repositoryName);
		repositoryManager.addMessage(message);
	}
	
	public Collection<IMessage> getPRStatuses(String repositoryName){
		RepositoryManager repositoryManager=getRepositoryManagerMap().get(repositoryName);
		return repositoryManager.getMessages();
	}
	
	public IMessage getPRStatus(String repositoryName, int id) throws Exception
	{
		RepositoryManager repositoryManager=getRepositoryManagerMap().get(repositoryName);
		return repositoryManager.getMessage(id);
	}
	
	public void changePRStatus(String repositoryName, PRStatus prStatus) throws Exception
	{
		RepositoryManager repositoryManager=getRepositoryManagerMap().get(repositoryName);
		repositoryManager.changeMessage(prStatus.getID(),prStatus);
	}
	
	public String getRemoteUserName(String repositoryName){
		RepositoryManager repositoryManager=getRepositoryManagerMap().get(repositoryName);
		return repositoryManager.getRemoteRepositoryDetails().getUserName();
	}
	
	public int getAmountOfMessages(){
		return getMessageManagers().getAmountOfMessages();
	}
	
	public List<StatusInformation> showPRFileStatus(String baseBranchName,String targetBranchName,String repositoryName){
		RepositoryManager repositoryManager=getRepositoryManagerMap().get(repositoryName);
		return repositoryManager.showPRStatus(baseBranchName,targetBranchName);
	}
	
	public void merge(String repositoryName, String baseBranchName, String targetBranchName) throws Exception
	{
		RepositoryManager repositoryManager=getRepositoryManagerMap().get(repositoryName);
		repositoryManager.merge(baseBranchName,targetBranchName);
	}
}
