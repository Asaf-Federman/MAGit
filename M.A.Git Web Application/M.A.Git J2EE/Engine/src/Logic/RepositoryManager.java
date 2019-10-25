package Logic;

import Logic.MessageComponents.IMessage;
import Logic.MessageComponents.MagitMessageManager;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.File;
import java.io.IOException;

import java.util.*;
import java.util.stream.Collectors;

public class RepositoryManager
{
	private Repository m_LocalRepository;
	private Repository m_RemoteRepository;
	private RemoteRepositoryDetails remoteRepositoryDetails;
	private String m_Username;
	private MagitMessageManager PRStatusManager;
	
	public RepositoryManager()
	{
		this.m_Username = "Administrator";
		PRStatusManager=new MagitMessageManager();
	}
	
	private MagitMessageManager getPRStatusManager()
	{
		return PRStatusManager;
	}
	
	public String getUsername()
	{
		return m_Username;
	}
	
	public void setUsername(String username)
	{
		this.m_Username = username;
	}
	
	public Repository getRepository()
	{
		return m_LocalRepository;
	}
	
	public void setRepository(Repository repository)
	{
		this.m_LocalRepository = repository;
	}
	
	public String getRepositoryName()
	{
		return this.getRepository().getRepositoryName();
	}
	
	private Repository getRemoteRepository()
	{
		return m_RemoteRepository;
	}
	
	private void setRemoteRepository(Repository m_RemoteRepository)
	{
		this.m_RemoteRepository = m_RemoteRepository;
	}
	
	public RemoteRepositoryDetails getRemoteRepositoryDetails()
	{
		return remoteRepositoryDetails;
	}
	
	public void setRemoteRepositoryDetails(RemoteRepositoryDetails remoteRepositoryDetails)
	{
		this.remoteRepositoryDetails = remoteRepositoryDetails;
	}
	
	public void addMessage(IMessage message){
		getPRStatusManager().addMessage(message);
	}
	
	public Collection<IMessage> getMessages(){
		return getPRStatusManager().getMessages();
	}
	
	public IMessage getMessage(int id) throws Exception
	{
		return getPRStatusManager().getMessage(id-1);
	}
	
	public void changeMessage(int index,IMessage message) throws Exception
	{
		getPRStatusManager().changeMessage(index-1,message);
	}
	
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////// fetch working copy information
	
	public void fetchWorkingCopyInfo(Repository repository) throws IOException
	{
		Utilz.deleteMagitDirectory(repository.getPath());
		File rootFolder = new File(repository.getPath());
		Node node = repository.getRootFolderByBranchName(repository.getHeadBranch().getBranchName());
		if (node != null)
		{
			deployCommitRec(rootFolder, node, repository);
		}
	}
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////// fetch working copy from M.A.Git
	
	private Repository parseInformationFromMagit(String repositoryPath) throws IOException
	{
		String branchesPath = repositoryPath + "\\.magit\\branches\\";
		String objectsPath = repositoryPath + "\\.magit\\objects\\";
		String headPath = branchesPath + "Head.txt";
		String repositoryNamePath = branchesPath + "Repository.txt";
		Head newHead = new Head(Utilz.readFileAsString(headPath));
		String repositoryName = Utilz.readFileAsString(repositoryNamePath);
		Repository newRepository = new Repository(newHead, repositoryPath, repositoryName);
		File branches = new File(branchesPath);
		createBranchRec(branches,newRepository,objectsPath);
		
		return newRepository;
	}
	
	private void createBranchRec(File branches, Repository repository, String objectsPath) throws IOException
	{
		List<File> files=Arrays.stream(Objects.requireNonNull(branches.listFiles())).collect(Collectors.toList());
		for (File branchFile : files)
		{
			if(branchFile.isFile())
			{
				if(branchFile.getName().equals("RemotePath.txt"))
				{
					remoteDetailsFromFile(branchFile.getAbsolutePath());
				}
				else if (!branchFile.getName().equals("Head.txt") && !branchFile.getName().equals("Repository.txt"))
				{
					Branch branch = BranchFactory.createBranchFromFile(branchFile);
					repository.addBranch(branch.getName(), branch);
					List<String> commitList = new ArrayList<>();
					commitList.add(branch.getKeyOfCommit());
					fetchCommitsRec(objectsPath, commitList, repository);
				}
			}
			else if(branchFile.isDirectory())
			{
				createBranchRec(branchFile,repository,objectsPath);
			}
		}
	}
	
	private void fetchCommitsRec(String objectsPath, List<String> keysOfCommit, Repository newRepository) throws IOException
	{
		for (String keyOfCommit : keysOfCommit)
		{
			if (keyOfCommit != null)
			{
				String contentOfCommit = Utilz.readZippedFile(objectsPath + keyOfCommit + ".zip", objectsPath, keyOfCommit + ".txt");
				Commit newCommit = Commit.createInstanceFromString(contentOfCommit);
				newRepository.addCommit(keyOfCommit, newCommit);
				fetchProgramInfoRec(newCommit.getRootFolderEncryptionKey(), NodeInformation.ItemType.Folder, objectsPath, true, newRepository);
				fetchCommitsRec(objectsPath, newCommit.getPreviousCommitKeys(), newRepository);
			}
		}
	}
	
	private void fetchProgramInfoRec(String SHA1, NodeInformation.ItemType type, String objectPath, boolean isRootFolder, Repository newRepository) throws IOException
	{
		if (type.equals(NodeInformation.ItemType.Folder))
		{
			String contentOfFolder = Utilz.readZippedFile(objectPath + SHA1 + ".zip", objectPath, SHA1 + ".txt");
			Folder.eRoot typeOfFolder = isRootFolder ? Folder.eRoot.Root : Folder.eRoot.Regular;
			Folder newFolder = Folder.createInstanceFromString(contentOfFolder, typeOfFolder);
			newRepository.addFile(SHA1, newFolder);
			for (NodeInformation node : newFolder.getInformationMap().values())
			{
				fetchProgramInfoRec(node.getEncryption(), node.getType(), objectPath, false, newRepository);
			}
		}
		else if (type.equals(NodeInformation.ItemType.Blob))
		{
			String contentOfBlob = Utilz.readZippedFile(objectPath + SHA1 + ".zip", objectPath, SHA1 + ".txt");
			Blob newBlob = Blob.createInstanceFromString(contentOfBlob);
			newRepository.addFile(SHA1, newBlob);
		}
	}
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////// status
	
	public List<StatusInformation> showCurrentStatus(Repository repository) throws IOException
	{
		Map<String, Status> workingCopyStatusMap = new HashMap<>();
		Map<String, Status> commitStatusMap = new HashMap<>();
		File rootFile = new File(repository.getPath());
		getStatusOfWorkingCopyRec(rootFile, workingCopyStatusMap);
		getStatusOfCommit(commitStatusMap, repository.getActiveBranch().getKeyOfCommit(), repository);
		
		return showStatus(workingCopyStatusMap, commitStatusMap);
	}
	
	public List<StatusInformation> showPRStatus(String baseBranchName,String targetBranchName)
	{
		Map<String,Status> baseStatusMap=new HashMap<>();
		Map<String,Status> targetStatusMap=new HashMap<>();
		Branch baseBranch=getRepository().getBranches().get(baseBranchName);
		Branch targetBranch=getRepository().getBranches().get(targetBranchName);
		
		getStatusOfCommit(baseStatusMap,baseBranch.getKeyOfCommit(),getRepository());
		getStatusOfCommit(targetStatusMap,targetBranch.getKeyOfCommit(),getRepository());
		
		return showStatus(baseStatusMap,targetStatusMap);
	}
	
	private List<StatusInformation> showStatus(Map<String, Status> currentMap, Map<String, Status> previousMap)
	{
		List<StatusInformation> result = new LinkedList<>();
		Status value;
		
		for (Map.Entry<String, Status> wcEntry : currentMap.entrySet())
		{
			value = previousMap.get(wcEntry.getKey());
			if (value != null)
			{
				if (!wcEntry.getValue().getEncryptionKey().equals(value.getEncryptionKey()))
				{
					StatusInformation statusInformation= new StatusInformation(wcEntry.getValue(),value,"Updated",value.getPath());
					result.add(statusInformation);
				}
			}
			else
			{
				StatusInformation statusInformation= new StatusInformation(wcEntry.getValue(),null,"Added",wcEntry.getValue().getPath());
				result.add(statusInformation);
			}
		}
		
		for (Map.Entry<String, Status> commitEntry : previousMap.entrySet())
		{
			value = currentMap.get(commitEntry.getKey());
			if (value == null)
			{
				StatusInformation statusInformation= new StatusInformation(null,commitEntry.getValue(),"Removed",commitEntry.getValue().getPath());
				result.add(statusInformation);
			}
		}
		
		return result;
	}
	
	private void getStatusOfWorkingCopyRec(File folder, Map<String, Status> workingCopyStatus) throws IOException
	{
		for (File file : Objects.requireNonNull(folder.listFiles(), "File collection can not be null"))
		{
			if (file.isFile())
			{
				String content = Utilz.readFileAsString(file.getAbsolutePath());
				String sha1 = DigestUtils.sha1Hex(content);
				String relativePath=relativePath(file.getAbsolutePath(),getRepository());
				Status status = new Status(sha1, file.getName(), relativePath, content);
				workingCopyStatus.put(file.getAbsolutePath(), status);
			}
			else if (!file.getName().equals(".magit") && file.isDirectory())
			{
				getStatusOfWorkingCopyRec(file, workingCopyStatus);
			}
		}
	}
	
	private void getStatusOfCommit(Map<String, Status> commitStatusMap, String keyOfCommit, Repository repository)
	{
		Commit commit =repository.getCommits().get(keyOfCommit);
		if (commit != null)
		{
			String nameOfRootFile = commit.getRootFolderEncryptionKey();
			String objectsPath = repository.getPath() + "\\.magit\\objects";
			String workingCopyPath = repository.getPath();
			File rootFile = new File(objectsPath + "\\" + nameOfRootFile);
			getStatusOfCommitRec(rootFile, commitStatusMap, workingCopyPath, objectsPath, repository);
		}
	}
	
	private void getStatusOfCommitRec(File file, Map<String, Status> lastCommitStatusMap, String workingCopyPath, String objectsPath, Repository repository)
	{
		Node node = repository.getFiles().get(file.getName());
		if (node instanceof Folder)
		{
			for (NodeInformation nodeInformation : ((Folder) node).getInformationMap().values())
			{
				//restores the path as it would've looked in Working Copy
				String newPath = workingCopyPath + '\\' + nodeInformation.getName();
				//adds this path to the map
				if(nodeInformation.getType().equals(NodeInformation.ItemType.Blob))
				{
					String encryptionKey=nodeInformation.getEncryption();
					String fileName=nodeInformation.getName();
					Blob blob= (Blob) repository.getFiles().get(encryptionKey);
					String content=blob.getContent();
					String relativePath=relativePath(newPath,getRepository());
					Status status= new Status(encryptionKey,fileName,relativePath,content);
					lastCommitStatusMap.put(newPath, status);
				}
				
				//starts a new recursion
				File newFile = new File(objectsPath + '\\' + nodeInformation.getEncryption());
				getStatusOfCommitRec(newFile, lastCommitStatusMap, newPath, objectsPath, repository);
			}
		}
	}
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////// Commit
	
	public void commit(String commitMessage, String... otherBranchNames) throws IOException
	{
		File rootFile = new File(this.getRepository().getPath());
		String rootFolderSHA1, commitSHA1, branchName;
		Branch branch;
		branchName = this.getRepository().getHeadBranch().getBranchName();
		branch = this.getRepository().getBranches().get(branchName);
		Commit newCommit;
		
		if(otherBranchNames!=null && otherBranchNames.length!=0)
		{
			Branch otherBranch=this.getRepository().getBranches().get(otherBranchNames[0]);
			newCommit = new Commit("encryptionKeyOfRootFolder", branch.getKeyOfCommit(),otherBranch.getKeyOfCommit(), commitMessage, Utilz.getCurrentTime(), m_Username);
		}
		else
		{
			newCommit = new Commit("encryptionKeyOfRootFolder", branch.getKeyOfCommit(), commitMessage, Utilz.getCurrentTime(), m_Username);
		}
		
		Map<String, NodeInformation> info = fetchingInfoOfWorkingCopyRec(rootFile, this.getRepository());
		
		//creating a root folder
		Folder rootFolder = new Folder(Folder.eRoot.Root);
		rootFolder.setInformationMap(info);
		rootFolderSHA1 = Folder.getEncryption(rootFolder.getInformationMap());
		//creating a rootFolder file
		rootFolder.createMagitFile(getRepository().getPath(),rootFolderSHA1);
		this.getRepository().addFile(rootFolderSHA1, rootFolder);
		
		//commit settings
		newCommit.setRootFolderEncryptionKey(rootFolderSHA1);
		commitSHA1 = newCommit.CommitSHA1();
		//creating a commit file
		newCommit.createMagitCommit(getRepository().getPath());
		this.getRepository().addCommit(commitSHA1, newCommit);
		
		//branch settings
		branch.setKeyOfCommit(commitSHA1);
		//creating a branch file
		branch.createMagitFile(getRepository().getPath());
	}
	
	private Map<String, NodeInformation> fetchingInfoOfWorkingCopyRec(File folder, Repository rep) throws IOException
	{
		Map<String, NodeInformation> nodeInformationMap = new TreeMap<>();
		NodeInformation nodeInformation;
		for (File file : Objects.requireNonNull(folder.listFiles(), "File collection can not be null"))
		{
			if (!file.getName().equals(".magit"))
			{
				if (file.isFile())
				{
					//creating a Blob
					String content = Utilz.readFileAsString(file.getAbsolutePath());
					String sha1 = DigestUtils.sha1Hex(content);
					Blob blob=new Blob(content);
					//creating a Blob file
					blob.createMagitFile(getRepository().getPath(),sha1);
					//adding the information to the Map
					if (getRepository().getFiles().get(sha1) == null)
					{
						nodeInformation = new NodeInformation(file.getName(), sha1, m_Username, NodeInformation.ItemType.Blob);
					}
					else
					{
						nodeInformation = this.getRepository().searchForNodeInfo(sha1);
					}
					
					rep.addFile(sha1, blob);
					nodeInformationMap.put(sha1, nodeInformation);
				}
				else
				{
					Map<String, NodeInformation> infoMap = fetchingInfoOfWorkingCopyRec(file, rep);
					if (infoMap.size() > 0)
					{
						//creating a Folder
						Folder folderNode = new Folder(Folder.eRoot.Regular,infoMap);
						String sha1 = Folder.getEncryption(infoMap);
						//creating a Folder file
						folderNode.createMagitFile(getRepository().getPath(),sha1);
						//adding the information to the Map.
						if (getRepository().getFiles().get(sha1) == null)
						{
							nodeInformation = new NodeInformation(file.getName(), sha1, m_Username, NodeInformation.ItemType.Folder);
						}
						else
						{
							nodeInformation = this.getRepository().searchForNodeInfo(sha1);
						}
						
						rep.addFile(sha1, folderNode);
						nodeInformationMap.put(sha1, nodeInformation);
					}
					else
					{
						Utilz.deleteDirectory(file.getPath());
					}
				}
			}
		}
		
		return nodeInformationMap;
	}
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////// branch creation
	
	public void createBranch(String branchName, String newBranchName) throws Exception
	{
		if (this.getRepository().getBranches().get(newBranchName) == null)
		{
			Branch branch = this.getRepository().getBranches().get(branchName);
			String keyOfCommit = branch.getKeyOfCommit();
			Branch newBranch = BranchFactory.createBranch(newBranchName, keyOfCommit);
			createBranch(newBranch);
		}
		else{
			throw new Exception("Branch with same name already exists.");
		}
	}
	
	private void createBranch(Branch branch) throws IOException
	{
			this.getRepository().addBranch(branch.getName(), branch);
			branch.createMagitFile(getRepository().getPath());
	}
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////// CMD number 9
	
	public void deleteBranch(String branchName) throws IOException
	{
		String activeBranchName = this.getRepository().getHeadBranch().getBranchName();
		
		if (!branchName.equals(activeBranchName))
		{
			if (this.getRepository().getBranches().get(branchName) != null)
			{
				deleteBranchLogic(branchName,getRepository());
			}
		}
	}
	
	public void deleteBranchCases(String branchName) throws IOException
	{
		Branch branch= this.getRepository().getBranches().get(branchName);
		if(branch.getType().equals(BranchFactory.eBranchType.localBranch)){
			deleteBranchLogic(branchName,getRepository());
		}else if(branch.getType().equals(BranchFactory.eBranchType.remoteTrackingBranch)){
			deleteBranchLogic(branchName,getRepository());
			deleteBranchLogic(branchName,getRemoteRepository());
			deleteBranchLogic(((RemoteTrackingBranch)branch).getFollowingAfterName(),getRepository());
		}
	}
	
	private void deleteBranchLogic(String branchName, Repository repository) throws IOException
	{
		Branch branch = repository.getBranches().get(branchName);
		repository.getBranches().remove(branchName);
		branch.removeMagitFile(repository.getPath());
//		Utilz.fileErasure(repository.getPath() + "\\.magit\\branches\\" + branchName + ".txt");
	
	}
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////// CMD number 10
	
	public void changeHeadBranch(String nameOfHeadBranch) throws IOException
	{
		File rootFolder = new File(m_LocalRepository.getPath());
		Utilz.deleteMagitDirectory(rootFolder);
		this.getRepository().getHeadBranch().setBranchName(nameOfHeadBranch,getRepository().getPath());
		Node node = this.getRepository().getRootFolderByBranchName(nameOfHeadBranch);
		if (node != null)
		{
			deployCommitRec(rootFolder, node,getRepository());
		}
	}
	
	private void deployCommitRec(File fileToDeploy, Node node,Repository repository) throws IOException
	{
		if (node instanceof Folder)
		{
			Map<String, NodeInformation> nodeInformationMap = ((Folder) node).getInformationMap();
			for (NodeInformation nodeInformation : nodeInformationMap.values())
			{
				File newFile = new File(fileToDeploy.getAbsolutePath() + "\\" + nodeInformation.getName());
				Node newNode = repository.getFiles().get(nodeInformation.getEncryption());
				newNode.createWorkingCopyFile(newFile);
				deployCommitRec(newFile, repository.getFiles().get(nodeInformation.getEncryption()),repository);
			}
		}
		else if (node instanceof Blob)
		{
			Utilz.writeFileAsString(fileToDeploy.getAbsolutePath(), ((Blob) node).getContent());
		}
	}
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////// Merge
	public void merge(String baseBranchName, String targetBranchName) throws Exception
	{
		if(!isContained(baseBranchName,targetBranchName)){
			throw new Exception("Base branch is not contained within the target branch");
		}
	}
	
	private boolean isContained(String baseBranchName,String targetBranchName) throws IOException
	{
		Branch baseBranch=getRepository().getBranches().get(baseBranchName);
		Branch targetBranch=getRepository().getBranches().get(targetBranchName);
		if(checkContainmentOfCommits(targetBranch.getKeyOfCommit(),baseBranch.getKeyOfCommit()))
		{
			baseBranch.setKeyOfCommit(targetBranch.getKeyOfCommit());
			baseBranch.createMagitFile(getRepository().getPath());
			fetchWorkingCopyInfo(getRepository());
			return true;
		}
		
		return false;
	}
	
	private boolean checkContainmentOfCommits(String keyOfContainsCommit, String keyOfContainedCommit)
	{
		return containmentRec(keyOfContainsCommit,keyOfContainedCommit);
	}
	
	private boolean containmentRec(String keyOfContainsCommit, String keyOfContainedCommit)
	{
		boolean contains;
		Commit commit=getRepository().getCommits().get(keyOfContainsCommit);
		if(keyOfContainsCommit.equals(keyOfContainedCommit))
		{
			contains = true;
		}
		else
		{
			contains=false;
			for(String previousCommitKey:commit.getPreviousCommitKeys())
			{
				contains = contains || containmentRec(previousCommitKey,keyOfContainedCommit);
			}
		}
		
		return contains;
	}
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////// collaboration
	
	private void createLocalRepository(String deployInPath) throws IOException
	{
		Repository newRepository;
		
		Utilz.moveFiles(getRemoteRepository().getPath(),deployInPath);
		newRepository = parseInformationFromMagit(deployInPath);
		newRepository.setRepositoryName(getRemoteRepository().getRepositoryName());
		changeBranchToRemote(newRepository);
		createRTBHeadBranch(newRepository);
		
		this.setRepository(newRepository);
	}
	
	public void cloneRepository(RepositoryManager repositoryManager, String deployToPath,String repositoryName, String fromUser) throws IOException
	{
		String remotePath=repositoryManager.getRepository().getPath();
		RemoteRepositoryDetails remoteRepositoryDetails=
				new RemoteRepositoryDetails(remotePath,repositoryName,fromUser);
		
		createRemoteRepository(repositoryManager.getRepository());
		createLocalRepository(deployToPath);
		Utilz.createNewFile(deployToPath+"\\.magit\\branches\\RemotePath.txt",remoteRepositoryDetails.contentToFile());
		setRemoteRepositoryDetails(remoteRepositoryDetails);
	}
	
	private void createRTBHeadBranch(Repository repository) throws IOException
	{
		Branch activeRemoteBranch= getRemoteRepository().getActiveBranch();
		String activeRemoteBranchName=activeRemoteBranch.getName();
		String followingAfter= getRemoteRepository().getRepositoryName()+"/"+activeRemoteBranchName;
		Branch headBranch=new RemoteTrackingBranch(activeRemoteBranchName,activeRemoteBranch.getKeyOfCommit(),followingAfter);
		headBranch.createMagitFile(repository.getPath());
		repository.addBranch(activeRemoteBranchName,headBranch);
	}
	
	private void changeBranchToRemote(Repository newRepository) throws IOException
	{
		List<Branch> branchesToDelete=new LinkedList<>(newRepository.getBranches().values());
		Map<String,Branch> newBranchesMap= new HashMap<>();
		Branch branch;
		
		for(Branch branchToDelete : branchesToDelete)
		{
			branch=BranchFactory.createBranch(getRemoteRepository().getRepositoryName(),branchToDelete.getName(),getRemoteRepository().getPath(),branchToDelete.getKeyOfCommit());
			branch.createMagitFile(newRepository.getPath());
			newBranchesMap.put(branch.getName(),branch);
			branchToDelete.removeMagitFile(newRepository.getPath());
		}
		
		newRepository.setBranches(newBranchesMap);
	}
	
	private void createRemoteRepository(Repository repository)
	{
		this.setRemoteRepository(repository);
	}
	
	private void remoteDetailsFromFile(String path) throws IOException
	{
		String content=Utilz.readFileAsString(path);
		String[] spittedString=content.split(",");
		String remotePath=spittedString[0];
		String remoteName=spittedString[1];
		String userName=spittedString[2];
		RemoteRepositoryDetails remoteRepositoryDetails= new RemoteRepositoryDetails(remotePath,remoteName,userName);
		setRemoteRepositoryDetails(remoteRepositoryDetails);
	}
	
	public void pull() throws Exception
	{
		pullRemoteChangesInActiveBranch();
		fetchWorkingCopyInfo(getRepository());
	}
	
	private void pullRemoteChangesInActiveBranch() throws Exception
	{
		Branch activeBranch = getRepository().getActiveBranch();
		Branch remoteBranch=getRemoteRepository().getBranches().get(activeBranch.getName());
		
		pullValidation();
		
		String followingAfterName=((RemoteTrackingBranch)activeBranch).getFollowingAfterName();
		Branch followingAfterBranch=getRepository().getBranches().get(followingAfterName);
		
		pullChanges(activeBranch,followingAfterBranch,remoteBranch);
	}
	
	private void pullChanges(Branch activeBranch, Branch followingAfterBranch, Branch remoteBranch) throws IOException
	{
		activeBranch.setKeyOfCommit(remoteBranch.getKeyOfCommit());
		activeBranch.createMagitFile(getRepository().getPath());
		followingAfterBranch.setKeyOfCommit(remoteBranch.getKeyOfCommit());
		followingAfterBranch.createMagitFile(getRepository().getPath());
		fetchRemoteCommitRec(activeBranch.getKeyOfCommit(),getRepository(),getRemoteRepository());
	}
	
	private void pullValidation() throws Exception
	{
		Branch activeBranch = getRepository().getActiveBranch();
		
		if(getRemoteRepositoryDetails().getRemotePath()==null)
		{
			throw new Exception("Repository has no remote repository");
		}
		
		if(!activeBranch.getType().equals(BranchFactory.eBranchType.remoteTrackingBranch)){
			throw new Exception("Active Branch is not a remote tracking branch");
		}
		
		String followingAfterName=((RemoteTrackingBranch)activeBranch).getFollowingAfterName();
		Branch followingAfterBranch=getRepository().getBranches().get(followingAfterName);
		
		if(showCurrentStatus(getRepository()).size()!=0){
			throw new Exception("There are open changes");
		}
		
		if(!activeBranch.getKeyOfCommit().equals(followingAfterBranch.getKeyOfCommit())){
			throw new Exception("There are changes in the active branch that needs to be pushed first");
		}
	}
	
	private void fetchRemoteCommitRec(String keyOfCommit,Repository oursRepository,Repository otherRepository) throws IOException
	{
		Commit commit=otherRepository.getCommits().get(keyOfCommit);
		Commit newCommit = new Commit(commit);
		oursRepository.getCommits().put(keyOfCommit,newCommit);
		newCommit.createMagitCommit(oursRepository.getPath());
		fetchRemoteFiles(newCommit.getRootFolderEncryptionKey(),oursRepository,otherRepository);
		for(String previousKey : newCommit.getPreviousCommitKeys())
		{
			fetchRemoteCommitRec(previousKey,oursRepository,otherRepository);
		}
	}
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////// utils
	
	private String relativePath(String path,Repository repository)
	{
		return repository.getRepositoryName() + path.replace(repository.getPath(), "");
	}
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////// branch information
	
	public List<BranchInformation> branchesInformation(){
		List<BranchInformation> branchInformationList=new LinkedList<>();
		BranchInformation branchInformation;
		String branchName,keyOfCommit,commitMessage;
		Commit commitRef;
		boolean isHead;
		
		for(Branch branch : getRepository().getBranches().values()){
			branchName=branch.getName();
			keyOfCommit=branch.getKeyOfCommit();
			isHead=branchName.equals(getRepository().getHeadBranch().getBranchName());
			commitRef=getRepository().getCommits().get(keyOfCommit);
			if(commitRef!=null){
				commitMessage=commitRef.getMessage();
			}else{
				keyOfCommit="";
				commitMessage="";
			}
			
			branchInformation=new BranchInformation(branchName,keyOfCommit,commitMessage, branch.getType(), isHead);
			branchInformationList.add(branchInformation);
		}
		
		return branchInformationList;
	}
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////// commit information
	
	public Collection<CommitInformation> commitsInformation(){
		List<CommitInformation> commitInformationCollection = new LinkedList<>();
		String encryptionKey;
		encryptionKey=getRepository().getActiveBranch().getKeyOfCommit();
		commitsInformationRec(encryptionKey,commitInformationCollection);
		Collections.sort(commitInformationCollection);
		
		return commitInformationCollection;
	}
	
	private void commitsInformationRec(String encryptionKey, Collection<CommitInformation> commitInformationCollection)
	{
		String creationDate,message,author;
		Collection<String> relatedBranches;
		CommitInformation commitInformation;
		Commit commit=getRepository().getCommits().get(encryptionKey);
		if(commit!=null){
			creationDate=commit.getCreationDate();
			message=commit.getMessage();
			author=commit.getAuthor();
			relatedBranches=getRelatedBranches(encryptionKey);
			commitInformation=new CommitInformation(encryptionKey,message,creationDate,author,relatedBranches);
			commitInformationCollection.add(commitInformation);
			for(String previousKey:commit.getPreviousCommitKeys()){
				commitsInformationRec(previousKey,commitInformationCollection);
			}
		}
	}
	
	private Collection<String> getRelatedBranches(String encryptionKey)
	{
		Collection<String> relatedBranchesCollection= new LinkedList<>();
		
		for(Branch branch : getRepository().getBranches().values()){
			if(branch.getKeyOfCommit().equals(encryptionKey)){
				relatedBranchesCollection.add(branch.getName());
			}
		}
		
		return relatedBranchesCollection;
	}
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////// working file information
	
	public FileInformation getFileInformation(String commitEncryptionKey){
		Commit commit=getRepository().getCommits().get(commitEncryptionKey);
		String rootFolderEncryptionKey=commit.getRootFolderEncryptionKey();
		List<FileInformation> children=getFileInformationRec(rootFolderEncryptionKey);
		String name=getRepositoryName();
		return new FileInformation(name,rootFolderEncryptionKey,children);
	}
	
	private List<FileInformation> getFileInformationRec(String encryptionKey)
	{
		FileInformation fileInformation;
		List<FileInformation> fileInformationList=null;
		Node node=getRepository().getFiles().get(encryptionKey);
		if(node instanceof Folder){
			fileInformationList=new LinkedList<>();
			for(NodeInformation nodeInformation : ((Folder) node).getInformationMap().values()){
				fileInformation = new FileInformation(nodeInformation.getName(),nodeInformation.getEncryption(),nodeInformation.getLastChangeDate(),nodeInformation.getLastChangeName());
				if(nodeInformation.getType().equals(NodeInformation.ItemType.Blob)){
					Blob blob=(Blob)getRepository().getFiles().get(nodeInformation.getEncryption());
					String content=blob.getContent();
					fileInformation.setContent(content);
				}else{
					fileInformation.setChildren(getFileInformationRec(nodeInformation.getEncryption()));
				}
				
				fileInformationList.add(fileInformation);
			}
		}
		
		return fileInformationList;
	}
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////// working copy information
	
	public WorkingCopyInformation getWorkingCopyInformation() throws IOException
	{
		File file = new File(getRepository().getPath());
		return getWorkingCopyInformationRec(file);
	}
	
	private WorkingCopyInformation getWorkingCopyInformationRec(File rootFile) throws IOException
	{
		List<WorkingCopyInformation> fileNames;
		if(rootFile.isDirectory())
		{
			fileNames = new LinkedList<>();
			WorkingCopyInformation workingCopyInformation;
			for (File file : Objects.requireNonNull(rootFile.listFiles()))
			{
				if (!file.getName().equals(".magit"))
				{
					workingCopyInformation = getWorkingCopyInformationRec(file);
					if(workingCopyInformation!=null)
					{
						fileNames.add(workingCopyInformation);
					}
				}
			}
			
			if(Objects.requireNonNull(rootFile.listFiles()).length==0){
				Utilz.deleteDirectory(rootFile.getAbsolutePath());
				return null;
			}
			else if(fileNames.size()==0)
			{
				return null;
			}
			else{
				return new WorkingCopyInformation(rootFile.getName(),rootFile.getAbsolutePath(),fileNames);
			}
		}
		else{
			String content=Utilz.readFileAsString(rootFile.getAbsolutePath());
			return new WorkingCopyInformation(rootFile.getName(),rootFile.getAbsolutePath(),null,content);
		}
	}
	
	public void changeWorkingCopyInformation(WorkingCopyInformation newWorkingCopyInformation, String action)
	{
		switch (action)
		{
			case "save":
				saveWorkingCopyInformation(newWorkingCopyInformation);
				break;
			case "remove":
				removeWorkingCopyInformation(newWorkingCopyInformation);
				break;
			case "newFile":
				newFileWorkingCopyInformation(newWorkingCopyInformation);
				break;
		}

	}
	
	private void newFileWorkingCopyInformation(WorkingCopyInformation newWorkingCopyInformation)
	{
		String folderPath=getRepository().getPath()+"\\"+newWorkingCopyInformation.getPath();
		new File(folderPath).mkdirs();
		String filePath=folderPath+"\\"+newWorkingCopyInformation.getFileName();
		String content=newWorkingCopyInformation.getContent();
		Utilz.createNewFile(filePath,content);
	}
	
	private void removeWorkingCopyInformation(WorkingCopyInformation newWorkingCopyInformation)
	{
		String path=newWorkingCopyInformation.getPath();
		Utilz.deleteFile(path);
	}
	
	private void saveWorkingCopyInformation(WorkingCopyInformation newWorkingCopyInformation)
	{
		String path=newWorkingCopyInformation.getPath();
		Utilz.writeFileAsString(path,newWorkingCopyInformation.getContent());
	}
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////// get repository information
	
	public RepositoryInformation getRepositoryInformation()
	{
		String remoteRepositoryUserName=null;
		String remoteRepositoryName=null;
		
		if(getRemoteRepositoryDetails()!=null)
		{
			remoteRepositoryUserName=getRemoteRepositoryDetails().getUserName();
			remoteRepositoryName=getRemoteRepositoryDetails().getRepositoryName();
		}
		
		String repositoryName=getRepositoryName();
		
		return new RepositoryInformation(repositoryName,remoteRepositoryName,remoteRepositoryUserName);
	}
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////// push
	
	public void push() throws Exception
	{
		Branch branch=getRepository().getActiveBranch();
		String branchName=branch.getName();
		
		pushValidation(branchName);
		pushChanges(branch);
	}
	
	private void pushChanges(Branch branch) throws IOException
	{
		cloneLocalBranch(branch);
		String branchName=branch.getName();
		Branch remoteBranch=BranchFactory.createBranch(getRemoteRepositoryDetails().getRepositoryName(),branchName,getRemoteRepositoryDetails().getRemotePath(),branch.getKeyOfCommit());
		Branch trackingBranch=BranchFactory.createBranch(branchName,branch.getKeyOfCommit(),remoteBranch.getName());
		createBranch(remoteBranch);
		deleteBranchLogic(branchName,getRepository());
		createBranch(trackingBranch);
	}
	
	public void push(String branchName) throws IOException
	{
		Branch branch=getRepository().getBranches().get(branchName);
		
		pushChanges(branch);
	}
	
	private void pushValidation(String branchName) throws Exception
	{
		Branch branch=getRepository().getBranches().get(branchName);
		
		if(getRemoteRepositoryDetails().getRemotePath()==null)
		{
			throw new Exception("Repository has no remote repository");
		}
		
		if(!branch.getType().equals(BranchFactory.eBranchType.localBranch))
		{
			throw new Exception("Active Branch is not a local branch");
		}
		
		if(getRemoteRepository().getBranches().get(branchName)!=null){
			throw new Exception("Branch name: "+branchName+" already exists in the remote repository");
		}
	}
	
	private void cloneLocalBranch(Branch branchToClone) throws IOException
	{
		List<CommitInformation> newestCommitList= fetchNewestRemoteCommitList(branchToClone);
		getRemoteRepository().addBranch(branchToClone.getName(),branchToClone);
		branchToClone.createMagitFile(getRemoteRepository().getPath());
		cloneCommitList(newestCommitList,getRemoteRepository(),getRepository());
	}
	
	private List<CommitInformation> fetchNewestRemoteCommitList(Branch branch){
		List<CommitInformation> branchCommitList=getBranchCommitList(branch);
		return getNewestRemoteCommitList(branchCommitList);
	}
	
	private List<CommitInformation> getBranchCommitList(Branch branch)
	{
		List<CommitInformation> branchCommitList = new LinkedList<>();
		String commitEncryptionKey=branch.getKeyOfCommit();
		getBranchCommitListRec(commitEncryptionKey,branchCommitList);
		branchCommitList.sort(CommitInformation::compareTo);
		Collections.reverse(branchCommitList);
		
		return branchCommitList;
	}
	
	private void getBranchCommitListRec(String commitEncryptionKey, List<CommitInformation> branchCommitList)
	{
		CommitInformation commitInformation;
		Commit commit=getRepository().getCommits().get(commitEncryptionKey);
		if(commit!=null){
			commitInformation=new CommitInformation(commitEncryptionKey,commit.getMessage(),commit.getCreationDate(),commit.getAuthor());
			branchCommitList.add(commitInformation);
			for(String commitPreviousKey : commit.getPreviousCommitKeys()){
				getBranchCommitListRec(commitPreviousKey,branchCommitList);
			}
		}
	}
	
	private List<CommitInformation> getNewestRemoteCommitList(List<CommitInformation> branchCommitList)
	{
		String encryptionKey;
		List<CommitInformation> commitInformationList=new LinkedList<>();
		Commit commit;
		for(CommitInformation commitInformation : branchCommitList){
			encryptionKey=commitInformation.getEncryptionKey();
			commit=getRemoteRepository().getCommits().get(encryptionKey);
			if(commit!=null){
				break;
			}else{
				commitInformationList.add(commitInformation);
			}
		}
		
		return commitInformationList;
	}
	
	
	private void cloneCommitList(List<CommitInformation> commitInformationList,Repository cloneToRepository,Repository cloneFromRepository) throws IOException
	{
		String encryptionKey;
		for(CommitInformation commitInformation : commitInformationList)
		{
			encryptionKey=commitInformation.getEncryptionKey();
			Commit commit = cloneFromRepository.getCommits().get(encryptionKey);
			Commit newCommit = new Commit(commit);
			cloneToRepository.getCommits().put(encryptionKey, newCommit);
			newCommit.createMagitCommit(cloneToRepository.getPath());
			fetchRemoteFiles(newCommit.getRootFolderEncryptionKey(), cloneToRepository, cloneFromRepository);
		}
	}
	
	private void fetchRemoteFiles(String encryptionKey,Repository cloneToRepository,Repository cloneFromRepository) throws IOException
	{
		Node node = cloneFromRepository.getFiles().get(encryptionKey);
		Node newNode = node.Clone();
		newNode.createMagitFile(cloneToRepository.getPath(), encryptionKey);
		cloneToRepository.getFiles().put(encryptionKey, newNode);
		if (newNode instanceof Folder)
		{
			for (NodeInformation nodeInformation : ((Folder) newNode).getInformationMap().values())
			{
				fetchRemoteFiles(nodeInformation.getEncryption(), cloneToRepository, cloneFromRepository);
			}
		}
	}
	
	public BranchesNames branchNames(){
		BranchesNames branchesNames= new BranchesNames();
		branchesNames.setTargetBranchesNames(getLocalBranchesNames(getRepository()));
		branchesNames.setBaseBranchesNames(getLocalBranchesNames(getRemoteRepository()));
		
		return branchesNames;
	}
	
	private static Collection<String> getLocalBranchesNames(Repository repository)
	{
		Collection<String> localBranchesNames = new LinkedList<>();
		
		for (Branch branch : repository.getBranches().values())
		{
			if (branch.getType().equals(BranchFactory.eBranchType.localBranch))
			{
				localBranchesNames.add(branch.getName());
			}
		}
		
		return localBranchesNames;
	}
	
	public boolean isRTB(String branchName){
		Branch branch=getRepository().getBranches().get(branchName);
		if(branch!=null){
			if(branch.getType().equals(BranchFactory.eBranchType.remoteTrackingBranch)){
				return true;
			}
		}
		
		return false;
	}
}
