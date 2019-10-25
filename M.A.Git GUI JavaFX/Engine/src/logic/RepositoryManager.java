package logic;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import org.apache.commons.codec.digest.DigestUtils;
import puk.team.course.magit.ancestor.finder.AncestorFinder;
import puk.team.course.magit.ancestor.finder.CommitRepresentative;

import java.io.File;
import java.io.IOException;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class RepositoryManager
{
	private Repository m_LocalRepository;
	private Repository m_RemoteRepository;
	private String m_Username;
	private SimpleStringProperty m_RemotePath;
	private SimpleBooleanProperty isRemote;
	
	public RepositoryManager(SimpleBooleanProperty isRemote, SimpleStringProperty remotePath)
	{
		this.isRemote=isRemote;
		m_RemotePath= remotePath;
		this.m_Username = "Adminstrator";
		isRemote.bind(Bindings.when(m_RemotePath.isNotEqualTo("")).then(true).otherwise(false));
	}
	
	public void setRemotePath(String path)
	{
		m_RemotePath.setValue(path);
	}
	
	public String getRemotePath()
	{
		return m_RemotePath.getValue();
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
	
	public Repository getRemoteRepository()
	{
		return m_RemoteRepository;
	}
	
	public void setRemoteRepository(Repository m_RemoteRepository)
	{
		this.m_RemoteRepository = m_RemoteRepository;
	}
	
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////// CMD number 2
	
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
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////// CMD number 3
	public Utilz.TwoParametersResult changeRepository(String repositoryPath) throws IOException
	{
		setRemoteRepository(null);
		m_RemotePath.setValue("");
		String magitPath = repositoryPath + "\\.magit";
		Repository newRepository;
		if (!Utilz.isExists(repositoryPath))
		{
			return new Utilz.TwoParametersResult(false, "Failed to change repository! Path does not exist");
		}
		else if (!Utilz.isExists(magitPath))
		{
			return new Utilz.TwoParametersResult(false, "Failed to change repository! Path does not contain a M.A.Git repository");
		}
		
		newRepository=parseInformationFromMagit(repositoryPath);
		setRepository(newRepository);
		return new Utilz.TwoParametersResult(true, "Succeeded in Fetching the repository's data!");
	}
	
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
	
	public void createBranchRec(File branches, Repository repository, String objectsPath) throws IOException
	{
		List<File> files=Arrays.stream(Objects.requireNonNull(branches.listFiles())).collect(Collectors.toList());
		for (File branchFile : files)
		{
			if(branchFile.isFile())
			{
				if(branchFile.getName().equals("RemotePath.txt"))
				{
					m_RemotePath.setValue(Utilz.readFileAsString(branchFile.getAbsolutePath()));
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
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////// CMD number 4
	
	public void branchHistoricData(List<NodeInformation> historicDataList)
	{
		Commit lastCommit = getRepository().getActiveCommit();
		if (lastCommit != null)
		{
			String nameOfRootFile = lastCommit.getRootFolderEncryptionKey();
			String rootPath = getRepository().getPath() + "\\.magit\\objects";
			String Path = getRepository().getPath();
			File rootFile = new File(rootPath + "\\" + nameOfRootFile);
			getHistoricDataRec(rootFile, historicDataList, Path, rootPath);
		}
	}
	
	private void getHistoricDataRec(File file, List<NodeInformation> historicDataList, String path, String rootFolderPath)
	{
		Node node = getRepository().getFiles().get(file.getName());
		if (node instanceof Folder)
		{
			for (NodeInformation nodeInformation : ((Folder) node).getInformationMap().values())
			{
				//restores the path as it would've looked in Working Copy
				String newPath = path + '\\' + nodeInformation.getName();
				historicDataList.add(new NodeInformation(newPath, nodeInformation.getEncryption(), nodeInformation.getLastChangeName(), nodeInformation.getType(), nodeInformation.getLastChangeDate()));
				//starts a new recursion
				File newFile = new File(rootFolderPath + '\\' + nodeInformation.getEncryption());
				getHistoricDataRec(newFile, historicDataList, newPath, rootFolderPath);
			}
		}
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////// CMD number 5
	public Map<String, String> showStatusOfCommit(String currentEncryptionKey, String fatherEncryptionKey) throws IOException
	{
		Map<String, String> currentCommitMap = new HashMap<>();
		Map<String, String> fatherPreviousCommitMap = new HashMap<>();

		getStatusOfCommit(currentCommitMap, currentEncryptionKey,getRepository());
		getStatusOfCommit(fatherPreviousCommitMap, fatherEncryptionKey,getRepository());

		return showStatus(currentCommitMap, fatherPreviousCommitMap);
	}
	
	public List<Map<String, String>> showStatusOfCommit(String encryptionKey)
	{
		Commit currentCommit = getRepository().getCommits().get(encryptionKey);
		Map<String, String> currentCommitMap = new HashMap<>();
		Map<String, String> firstPreviousCommitMap;
		Map<String, String> secondPreviousCommitMap;
		List<Map<String, String>> result = new ArrayList<>();
		
		getStatusOfCommit(currentCommitMap, encryptionKey,getRepository());
		
		if (currentCommit.getPreviousCommitKeys().size() >= 1)
		{
			firstPreviousCommitMap = new HashMap<>();
			getStatusOfCommit(firstPreviousCommitMap, currentCommit.getPreviousCommitKeys().get(0),getRepository());
			result.add(showStatus(currentCommitMap, firstPreviousCommitMap));
			if (currentCommit.getPreviousCommitKeys().size() == 2)
			{
				secondPreviousCommitMap = new HashMap<>();
				getStatusOfCommit(secondPreviousCommitMap, currentCommit.getPreviousCommitKeys().get(1),getRepository());
				result.add(showStatus(currentCommitMap, secondPreviousCommitMap));
			}
		}
		else
		{
			result.add(showStatus(currentCommitMap, new HashMap<>()));
		}
		
		return result;
	}
	
	public Map<String, String> showCurrentStatus(Repository repository) throws IOException
	{
		Map<String, String> workingCopyStatusMap = new HashMap<>();
		Map<String, String> commitStatusMap = new HashMap<>();
		File rootFile = new File(repository.getPath());
		getStatusOfWorkingCopyRec(rootFile, workingCopyStatusMap);
		getStatusOfCommit(commitStatusMap, repository.getActiveBranch().getKeyOfCommit(), repository);
		
		return showStatus(workingCopyStatusMap, commitStatusMap);
	}
	
	public Map<String, String> showStatus(Map<String, String> currentMap, Map<String, String> previousMap)
	{
		Map<String, String> result = new HashMap<>();
		String value;
		
		for (Map.Entry<String, String> wcEntry : currentMap.entrySet())
		{
			value = previousMap.get(wcEntry.getKey());
			if (value != null)
			{
				if (!wcEntry.getValue().equals(value))
				{
					result.put(wcEntry.getKey(), "Updated File");
				}
			}
			else
			{
				result.put(wcEntry.getKey(), "Added File");
			}
		}
		
		for (Map.Entry<String, String> commitEntry : previousMap.entrySet())
		{
			value = currentMap.get(commitEntry.getKey());
			if (value == null)
			{
				result.put(commitEntry.getKey(), "Removed File");
			}
		}
		
		return result;
	}
	
	private Map<String, NodeInformation> getStatusOfWorkingCopyRec(File folder, Map<String, String> workingCopyStatus) throws IOException
	{
		Map<String, NodeInformation> nodeInformationMap = new TreeMap<>();
		for (File file : Objects.requireNonNull(folder.listFiles(), "File collection can not be null"))
		{
			if (file.isFile())
			{
				//adding the file to the map
				String content = Utilz.readFileAsString(file.getAbsolutePath());
				String sha1 = DigestUtils.sha1Hex(content);
				workingCopyStatus.put(file.getAbsolutePath(), sha1);
				//adding the file to the Map
				NodeInformation nodeInformation = new NodeInformation(file.getName(), sha1, m_Username, NodeInformation.ItemType.Blob);
				nodeInformationMap.putIfAbsent(sha1, nodeInformation);
			}
			else if (!file.getName().equals(".magit") && file.isDirectory())
			{
				Map<String, NodeInformation> infoMap = getStatusOfWorkingCopyRec(file, workingCopyStatus);
				if (infoMap.size() > 0)
				{
					//adding the file to the map
					Folder folderNode = new Folder(Folder.eRoot.Regular, infoMap);
					String sha1 = Folder.getEncryption(folderNode.getInformationMap());
					workingCopyStatus.put(file.getAbsolutePath(), sha1);
					//adding the file to the Map
					NodeInformation nodeInformation = new NodeInformation(file.getName(), sha1, m_Username, NodeInformation.ItemType.Folder);
					nodeInformationMap.putIfAbsent(sha1, nodeInformation);
				}
			}
		}
		
		return nodeInformationMap;
	}
	
	private void getStatusOfCommit(Map<String, String> lastCommitStatusMap, String keyOfCommit, Repository repository)
	{
		Commit commit =repository.getCommits().get(keyOfCommit);
		if (commit != null)
		{
			String nameOfRootFile = commit.getRootFolderEncryptionKey();
			String objectsPath = repository.getPath() + "\\.magit\\objects";
			String workingCopyPath = repository.getPath();
			File rootFile = new File(objectsPath + "\\" + nameOfRootFile);
			getStatusOfCommitRec(rootFile, lastCommitStatusMap, workingCopyPath, objectsPath, repository);
		}
	}
	
	private void getStatusOfCommitRec(File file, Map<String, String> lastCommitStatusMap, String workingCopyPath, String objectsPath, Repository repository)
	{
		Node node = repository.getFiles().get(file.getName());
		if (node instanceof Folder)
		{
			for (NodeInformation nodeInformation : ((Folder) node).getInformationMap().values())
			{
				//restores the path as it would've looked in Working Copy
				String newPath = workingCopyPath + '\\' + nodeInformation.getName();
				//adds this path to the map
				lastCommitStatusMap.put(newPath, nodeInformation.getEncryption());
				//starts a new recursion
				File newFile = new File(objectsPath + '\\' + nodeInformation.getEncryption());
				getStatusOfCommitRec(newFile, lastCommitStatusMap, newPath, objectsPath, repository);
			}
		}
	}
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////// CMD number 6
	
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
	
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////// CMD number 7
	
	public String showAllBranches()
	{
		StringBuilder result = new StringBuilder();
		String activeBranchName = this.getRepository().getHeadBranch().getBranchName();
		Commit commitOfBranch;
		
		for (Map.Entry<String, Branch> branchMapEntry : this.getRepository().getBranches().entrySet())
		{
			result.append("================================================================================================");
			result.append(System.lineSeparator());
			result.append("The branch name is: ").append(branchMapEntry.getValue().getName());
			if (branchMapEntry.getValue().getName().equals(activeBranchName))
			{
				result.append("  <==========");
			}
			
			result.append(System.lineSeparator());
			if (branchMapEntry.getValue().getKeyOfCommit() != null)
			{
				commitOfBranch = this.getRepository().getCommits().get(branchMapEntry.getValue().getKeyOfCommit());
				result.append("The SHA1 of the commit pointed by this branch is: ").append(branchMapEntry.getValue().getKeyOfCommit()).append(System.lineSeparator());
				result.append("This commit' message is: ").append(commitOfBranch.getMessage()).append(System.lineSeparator());
			}
			else
			{
				result.append("This branch isn't pointing to any commit").append(System.lineSeparator());
			}
			
			result.append("================================================================================================");
			result.append(System.lineSeparator());
		}
		
		return result.toString();
	}
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////// CMD number 8
	
	public void createBranch(Branch branch) throws IOException
	{
			this.getRepository().addBranch(branch.getName(), branch);
			branch.createMagitFile(getRepository().getPath());
	}
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////// CMD number 9
	
	public void deleteBranch(String branchName)
	{
		String activeBranchName = this.getRepository().getHeadBranch().getBranchName();
		
		if (!branchName.equals(activeBranchName))
		{
			if (this.getRepository().getBranches().get(branchName) != null)
			{
				this.getRepository().getBranches().remove(branchName);
				Utilz.fileErasure(this.getRepository().getPath() + "\\.magit\\branches\\" + branchName + ".txt");
			}
		}
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
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////// CMD number 11
	
	private void fetchBranchInfoRec(String encryptionKey,String result)
	{
		if(encryptionKey!=null)
		{
			Commit commit = this.getRepository().getCommits().get(encryptionKey);
			result += "================================================================================================";
			result += System.lineSeparator();
			result += "The commit's SHA1 is: " + encryptionKey + System.lineSeparator();
			result += "The commit's message is: " + commit.getMessage() + System.lineSeparator();
			result += "The commit was created in: " + commit.getCreationDate() + System.lineSeparator();
			result += "The commit was created by: " + commit.getAuthor() + System.lineSeparator();
			result += "================================================================================================";
			result += System.lineSeparator();
			for (String encryption : commit.getPreviousCommitKeys())
			{
				fetchBranchInfoRec(encryptionKey, result);
			}
		}
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////// CMD number 12
	
	public void changeActiveCommit(String commitKey) throws IOException
	{
		this.m_LocalRepository.getActiveBranch().setKeyOfCommit(commitKey);
		Branch branch=getRepository().getActiveBranch();
		branch.createMagitFile(getRepository().getPath());
		changeCommit();
	}
	
	private void changeCommit() throws IOException
	{
		File rootFolder = new File(m_LocalRepository.getPath());
		Utilz.deleteMagitDirectory(rootFolder);
		Node node = this.getRepository().getRootFolderByBranchName(this.getRepository().getHeadBranch().getBranchName());
		if (node != null)
		{
			deployCommitRec(rootFolder, node,getRepository());
		}
	}
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////// CMD number 13
	
	public Utilz.TwoParametersResult localRepositoryInitialization(String repositoryPath, String repositoryName) throws IOException
	{
		if (Utilz.checkExistenceOfMagit(repositoryPath))
		{
			return new Utilz.TwoParametersResult(false,"Failed to create a local repository! The path entered already contains a repository!");
		}
		
		setRemoteRepository(null);
		m_RemotePath.setValue("");
		this.setRepository(new Repository(new Head("Master"), repositoryPath, repositoryName));
		this.getRepository().addBranch("Master", new LocalBranch("Master",null));
		Branch branch;
		String branchName = this.getRepository().getHeadBranch().getBranchName();
		branch = this.getRepository().getBranches().get(branchName);
		this.getRepository().createMagit(repositoryPath);
		branch.createMagitFile(getRepository().getPath());
		this.getRepository().createMagitFile("Head", branch.getName(), "branch");
		this.getRepository().createMagitFile("Repository", this.getRepository().getRepositoryName(), "branch");
		
		return new Utilz.TwoParametersResult(true,"Succeeded in creating a new repository!");
	}
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////// merge
	
	public Map<String,ConflictItem> merge(String branchName)
	{
		Map<String,String> activeCommitStatus=new HashMap<>();
		Map<String,String> otherCommitStatus=new HashMap<>();
		Map<String,String> fatherCommitStatus=new HashMap<>();
		Branch activeBranch = getRepository().getActiveBranch();
		Branch otherBranch = getRepository().getBranches().get(branchName);
		AncestorFinder newFinder = createAncestorFinder();
		String ancestorKey=newFinder.traceAncestor(activeBranch.getKeyOfCommit(), otherBranch.getKeyOfCommit());
		
		getStatusOfCommit(fatherCommitStatus,ancestorKey,getRepository());
		getStatusOfCommit(otherCommitStatus, otherBranch.getKeyOfCommit(),getRepository());
		getStatusOfCommit(activeCommitStatus, activeBranch.getKeyOfCommit(),getRepository());
		
		return getMergeStatus(activeCommitStatus,otherCommitStatus,fatherCommitStatus);
	}
	
	private Map<String,ConflictItem> getMergeStatus(Map<String, String> activeCommitStatus, Map<String, String> otherCommitStatus, Map<String, String> fatherCommitStatus)
	{
		Map<String, String> changesMap = new HashMap<>();
		Map<String, ConflictItem> conflictMap = new HashMap<>();
		Map<String,String> concatMap=new HashMap<>();
		String currentSHA1,otherSHA1,fatherSHA1;

		concatMap.putAll(activeCommitStatus);
		concatMap.putAll(otherCommitStatus);
		concatMap.putAll(fatherCommitStatus);

		for(Map.Entry<String,String> concatEntry : concatMap.entrySet())
		{
			List<ConflictItem.Encryption> newList = new ArrayList<>(2);
			currentSHA1 = activeCommitStatus.get(concatEntry.getKey());
			otherSHA1 = otherCommitStatus.get(concatEntry.getKey());
			fatherSHA1 = fatherCommitStatus.get(concatEntry.getKey());
			if (currentSHA1 != null)
			{
				newList.add(new ConflictItem.Encryption(ConflictItem.eFileOrigin.Ours,currentSHA1));
			}
			
			if (otherSHA1 != null)
			{
				newList.add(new ConflictItem.Encryption(ConflictItem.eFileOrigin.Theirs,otherSHA1));
			}
			
			ConflictItem conflictItem = new ConflictItem(newList, fatherSHA1, concatEntry.getKey());
			singleFileChangeStatus(conflictItem, changesMap, conflictMap);
		}
		fetchChanges(changesMap);
		return conflictMap;
	}
	
	private void fetchChanges(Map<String, String> changesMap)
	{
		changesMap.forEach(this::createFile);
	}
	
	private void createFile(String path,String encryptionKey)
	{
		File file = new File(path);
		Node node = getRepository().getFiles().get(encryptionKey);
		try
		{
			node.createWorkingCopyFile(file);
		}
		catch (IOException ignored)
		{
		}
		
		if(node instanceof Blob)
		{
			Utilz.writeFileAsString(path, ((Blob) node).getContent());
		}
	}
	
	
	private void singleFileChangeStatus(ConflictItem conflictItem, Map<String, String> newChangeMap, Map<String, ConflictItem> newConflictMap)
	{
		File file= new File(conflictItem.getPath());
		if(!conflictItem.getPath().contains("."))
		{
			file.mkdirs();
		}
		else if(conflictItem.getEncryptionList().size()==1 && conflictItem.getFatherKey()==null) // add - one side
		{
			newChangeMap.put(conflictItem.getPath(),conflictItem.getEncryptionList().get(0).getEncryptionKey());
		}
		else if(conflictItem.getEncryptionList().size()==1 && conflictItem.getFatherKey()!=null && conflictItem.getEncryptionList().get(0).getEncryptionKey().equals(conflictItem.getFatherKey())) // remove - one side
		{
			if(file.exists())
			{
				file.delete();
			}
		}
		else if(conflictItem.getEncryptionList().size()==2 && conflictItem.getFatherKey()!=null && conflictItem.differentKeyList().size()==1) // updated - one side
		{
			newChangeMap.put(conflictItem.getPath(), conflictItem.differentKeyList().get(0).getEncryptionKey());
		}
		else if(conflictItem.differentKeyList().size()==0 && conflictItem.getEncryptionList().size()==2) // no change
		{
			newChangeMap.put(conflictItem.getPath(),conflictItem.getEncryptionList().get(0).getEncryptionKey());
		}
		else // conflict
		{
			newConflictMap.put(conflictItem.getPath(), conflictItem);
		}
		
	}
	
	private AncestorFinder createAncestorFinder()
	{
		return new AncestorFinder(getRepresentative());
		
	}
	
	private Function<String,CommitRepresentative> getRepresentative()
	{
		return (String key)-> new CommitRepresentative()
		{
			Commit commit = getRepository().getCommits().get(key);
			
			@Override
			public String getSha1()
			{
				return key;
			}
			
			@Override
			public String getFirstPrecedingSha1()
			{
				if (commit.getPreviousCommitKeys().size() == 1)
				{
					return commit.getPreviousCommitKeys().get(0);
				}
				
				return "";
			}
			
			@Override
			public String getSecondPrecedingSha1()
			{
				if (commit.getPreviousCommitKeys().size() == 2)
				{
					return commit.getPreviousCommitKeys().get(1);
				}
				
				return "";
			}
		};
	}
	
	public boolean isContains(String othersBranchName) throws IOException
	{
		Branch activeBranch=getRepository().getActiveBranch();
		Branch otherBranch=getRepository().getBranches().get(othersBranchName);
		if(checkContainmentOfCommits(activeBranch.getKeyOfCommit(),otherBranch.getKeyOfCommit()))
		{
			otherBranch.setKeyOfCommit(activeBranch.getKeyOfCommit());
			otherBranch.createMagitFile(getRepository().getPath());
			return true;
		}
		
		return false;
	}
	
	public boolean isContained(String othersBranchName) throws IOException
	{
		Branch activeBranch=getRepository().getActiveBranch();
		Branch otherBranch=getRepository().getBranches().get(othersBranchName);
		if(checkContainmentOfCommits(otherBranch.getKeyOfCommit(),activeBranch.getKeyOfCommit()))
		{
			activeBranch.setKeyOfCommit(otherBranch.getKeyOfCommit());
			activeBranch.createMagitFile(getRepository().getPath());
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
	
	public Utilz.TwoParametersResult cloneRepository(String fetchFromPath, String deployInPath, String repositoryName) throws IOException
	{
		setRemoteRepository(null);
		m_RemotePath.setValue("");
		Utilz.TwoParametersResult validationRes;
		validationRes=cloneValidation(fetchFromPath,deployInPath);
		if(!validationRes.m_IsValid)
		{
			return validationRes;
		}
		
		m_RemotePath.setValue(fetchFromPath);
		createRemoteRepository(fetchFromPath);
		createLocalRepository(deployInPath,repositoryName);
		Utilz.createNewFile(deployInPath+"\\.magit\\branches\\RemotePath.txt",fetchFromPath);
		
		return new Utilz.TwoParametersResult(true,"cloned repository");
	}
	
	private void createLocalRepository(String deployInPath,String repositoryName) throws IOException
	{
		Repository newRepository;
		
		Utilz.moveFiles(getRemoteRepository().getPath(),deployInPath);
		newRepository = parseInformationFromMagit(deployInPath);
		newRepository.setRepositoryName(repositoryName);
		changeBranchToRemote(newRepository);
		createRTBHeadBranch(newRepository);
		
		this.setRepository(newRepository);
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
			String remoteBranchName=getRemoteRepository().getRepositoryName()+"/"+branchToDelete.getName();
			branch=BranchFactory.createBranch(getRemoteRepository().getRepositoryName(),branchToDelete.getName(),getRemoteRepository().getPath(),branchToDelete.getKeyOfCommit());
			branch.createMagitFile(newRepository.getPath());
			newBranchesMap.put(remoteBranchName,branch);
			branchToDelete.removeMagitFile(newRepository.getPath());
		}
		
		newRepository.setBranches(newBranchesMap);
	}
	
	private void createRemoteRepository(String fetchFromPath) throws IOException
	{
		Repository newRepository;

		newRepository= parseInformationFromMagit(fetchFromPath);
		this.setRemoteRepository(newRepository);
	}
	
	private Utilz.TwoParametersResult cloneValidation(String fetchFromPath, String deployInPath)
	{
		if(!Utilz.isExists(fetchFromPath))
		{
			return new Utilz.TwoParametersResult(false,"The path "+fetchFromPath+ " does not exist");
		}
		else if(!Utilz.checkExistenceOfMagit(fetchFromPath))
		{
			return new Utilz.TwoParametersResult(false,"The path "+fetchFromPath+ " does not contain M.A.Git repository");
		}
		else if(Utilz.isExists(deployInPath))
		{
			if(!Utilz.isEmpty(deployInPath))
			{
				return new Utilz.TwoParametersResult(false,"The path "+deployInPath+ " is not empty");
			}
		}
		
		return new Utilz.TwoParametersResult(true,"validation succeeded");
	}
	
	public void fetch() throws IOException
	{
		fetchRemotePath();
		createRemoteRepository(m_RemotePath.getValue());
		fetchRemoteChanges();
	}
	
	private void fetchRemotePath() throws IOException
	{
		if(m_RemotePath.getValue().equals(""))
		{
			String remotePathFile = getRepository().getPath() + "\\.magit\\branches\\RemotePath.txt";
			String remotePath=Utilz.readFileAsString(remotePathFile);
			m_RemotePath.setValue(remotePath);
		}
	}
	
	private void fetchRemoteChanges() throws IOException
	{
		Branch branch;
		for(Map.Entry<String,Branch> branchEntry : getRemoteRepository().getBranches().entrySet())
		{
			branch=BranchFactory.createBranch(getRemoteRepository().getRepositoryName(),branchEntry.getValue().getName(),getRemoteRepository().getPath(),branchEntry.getValue().getKeyOfCommit());
			getRepository().addBranch(branch.getName(),branch);
			branch.createMagitFile(getRepository().getPath());
			fetchRemoteCommitRec(branchEntry.getValue().getKeyOfCommit(),getRepository(),getRemoteRepository());
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
	
	private void fetchRemoteFiles(String encryptionKey,Repository oursRepository,Repository otherRepository) throws IOException
	{
		Node node = otherRepository.getFiles().get(encryptionKey);
		Node newNode = node.Clone();
		newNode.createMagitFile(oursRepository.getPath(), encryptionKey);
		oursRepository.getFiles().put(encryptionKey, newNode);
		if (newNode instanceof Folder)
		{
			for (NodeInformation nodeInformation : ((Folder) newNode).getInformationMap().values())
			{
				fetchRemoteFiles(nodeInformation.getEncryption(), oursRepository, otherRepository);
			}
		}
	}
	
	public boolean headBranchIsRemoteTrackingBranch()
	{
		Branch branch= getRepository().getActiveBranch();
		return branch instanceof RemoteTrackingBranch;
	}
	
	public boolean isPointingToRemote()
	{
		RemoteTrackingBranch remoteTrackingBranch = (RemoteTrackingBranch) getRepository().getActiveBranch();
		Branch remoteBranch = getRepository().getBranches().get(remoteTrackingBranch.getFollowingAfterName());
		return remoteTrackingBranch.getKeyOfCommit().equals(remoteBranch.getKeyOfCommit());
	}
	
	public void pull() throws IOException
	{
		fetchRemotePath();
		createRemoteRepository(m_RemotePath.getValue());
		fetchRemoteChangesInActiveBranch();
		fetchWorkingCopyInfo(getRepository());
	}
	
	private void fetchRemoteChangesInActiveBranch() throws IOException
	{
		Branch activeBranch = getRepository().getActiveBranch();
		String followingAfterName=((RemoteTrackingBranch)activeBranch).getFollowingAfterName();
		Branch followingAfterBranch=getRepository().getBranches().get(followingAfterName);
		Branch getRemotedBranch=getRemoteRepository().getBranches().get(activeBranch.getName());
		activeBranch.setKeyOfCommit(getRemotedBranch.getKeyOfCommit());
		activeBranch.createMagitFile(getRepository().getPath());
		followingAfterBranch.setKeyOfCommit(getRemotedBranch.getKeyOfCommit());
		followingAfterBranch.createMagitFile(getRepository().getPath());
		fetchRemoteCommitRec(activeBranch.getKeyOfCommit(),getRepository(),getRemoteRepository());
	}
	
	public boolean isRemotedPositionUnrendered() throws IOException
	{
		fetchRemotePath();
		createRemoteRepository(m_RemotePath.getValue());
		Branch activeBranch = getRepository().getActiveBranch();
		String followingAfterName=((RemoteTrackingBranch)activeBranch).getFollowingAfterName();
		Branch followingAfterBranch=getRepository().getBranches().get(followingAfterName);
		Branch getRemotedBranch=getRemoteRepository().getBranches().get(activeBranch.getName());
		
		return getRemotedBranch.getKeyOfCommit().equals(followingAfterBranch.getKeyOfCommit());
	}
	
	public void push() throws IOException
	{
		Branch activeBranch = getRepository().getActiveBranch();
		String followingAfterName=((RemoteTrackingBranch)activeBranch).getFollowingAfterName();
		Branch followingAfterBranch=getRepository().getBranches().get(followingAfterName);
		Branch getRemotedBranch=getRemoteRepository().getBranches().get(activeBranch.getName());
		getRemotedBranch.setKeyOfCommit(activeBranch.getKeyOfCommit());
		getRemotedBranch.createMagitFile(m_RemotePath.getValue());
		followingAfterBranch.setKeyOfCommit(activeBranch.getKeyOfCommit());
		followingAfterBranch.createMagitFile(getRepository().getPath());
		fetchRemoteCommitRec(getRemotedBranch.getKeyOfCommit(),getRemoteRepository(),getRepository());
		if(getRemotedBranch.getName().equals(getRemoteRepository().getActiveBranch().getName()))
		{
			fetchWorkingCopyInfo(getRemoteRepository());
		}
	}
	
	public boolean isRemoteOpenChanges() throws IOException
	{
		Branch activeBranch = getRepository().getActiveBranch();
		Branch getRemotedBranch=getRemoteRepository().getBranches().get(activeBranch.getName());
		
		if(getRemotedBranch.getName().equals(getRemoteRepository().getActiveBranch().getName()))
		{
			return checkIfExistUnsavedChanges(getRemoteRepository());
		}
		
		return false;
	}
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////// utilz
	
	public boolean checkIfExistUnsavedChanges(Repository repository) throws IOException
	{
		boolean isExistChanges;
		Map<String, String> currentStatus = showCurrentStatus(repository);
		
		isExistChanges = !currentStatus.isEmpty();
		
		return isExistChanges;
	}
	
	public Map<String,Commit> validCommits()
	{
		String encryptKey;
		Map<String,Commit> validCommitsMap= new HashMap<>();
		
		for(Map.Entry<String, Branch> branchEntry: getRepository().getBranches().entrySet())
		{
			encryptKey = branchEntry.getValue().getKeyOfCommit();
			validCommitsRec(encryptKey, validCommitsMap);
		}
		
		return validCommitsMap;
	}
	
	private void validCommitsRec(String encryptKey, Map<String, Commit> validCommitsMap)
	{
		Commit currentCommit = getRepository().getCommits().get(encryptKey);
		if(currentCommit!=null)
		{
			for(String previousCommitKey : currentCommit.getPreviousCommitKeys())
			{
				validCommitsRec(previousCommitKey,validCommitsMap);
			}
			
			validCommitsMap.putIfAbsent(encryptKey,currentCommit);
		}
	}
	
	public Map<String,String> branchPointers()
	{
		Map<String,String> branchPointers = new HashMap<>();

		for(Map.Entry<String, Branch> branchEntry : getRepository().getBranches().entrySet())
		{
			if(branchPointers.get(branchEntry.getValue().getKeyOfCommit())!=null)
			{
				String key=branchEntry.getValue().getKeyOfCommit();
				String value=branchPointers.get(key)+", "+branchEntry.getKey();
				branchPointers.put(key,value);
			}
			else
			{
				branchPointers.put(branchEntry.getValue().getKeyOfCommit(), branchEntry.getValue().getName());
			}
		}
		
		return branchPointers;
	}
}
