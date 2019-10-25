import org.apache.commons.codec.digest.DigestUtils;

import java.io.File;
import java.io.IOException;

import java.util.*;

public class RepositoryManager
{
	private Repository m_Repository;
	private String m_Username;
	
	public RepositoryManager()
	{
		this.m_Username = "Adminstrator";
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
		return m_Repository;
	}
	
	public void setRepository(Repository repository)
	{
		this.m_Repository = repository;
	}
	
	public String getRepositoryName()
	{
		return this.getRepository().getRepositoryName();
	}
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////// CMD number 2
	
	public String fetchWorkingCopyInfo() throws IOException
	{
		File rootFolder = new File(m_Repository.getPath());
		Node node = this.getRepository().getRootFolderByBranchName(this.getRepository().getHeadBranch().getBranchName());
		if (node != null)
		{
			deployCommitRec(rootFolder, node);
			return "Successfully changed and fetched the head branch!";
		}
		
		return "Successfully changed the head branch!";
	}
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////// CMD number 3
	public String changeRepository(String repositoryPath) throws IOException
	{
		String magitPath=repositoryPath + "\\.magit";
		if (!Utilz.isExists(repositoryPath))
		{
			return "Failed to change repository! Path does not exist";
		}
		else if(!Utilz.isExists(magitPath))
		{
			return "Failed to change repository! Path does not contains a M.A.Git repository";
		}
		
		parseInformationFromMagit(repositoryPath);
		return "Succeeded in Fetching the repository's data!";
	}
	
	private void parseInformationFromMagit(String repositoryPath) throws IOException
	{
		String branchesPath = repositoryPath + "\\.magit\\branches\\";
		String objectsPath = repositoryPath + "\\.magit\\objects\\";
		String headPath = branchesPath + "Head.txt";
		String repositoryNamePath = branchesPath + "Repository.txt";
		Head newHead = new Head(Utilz.readFileAsString(headPath));
		String repositoryName = Utilz.readFileAsString(repositoryNamePath);
		Repository newRepository = new Repository(newHead, repositoryPath, repositoryName);
		
		File branches = new File(branchesPath);
		for (File branch : Objects.requireNonNull(branches.listFiles(), "Branches collection is null"))
		{
			if (!branch.getName().equals("Head.txt") && !branch.getName().equals("Repository.txt"))
			{
				String branchName = branch.getName().substring(0, branch.getName().length() - 4);
				String keyOfCommit = Utilz.readFileAsString(branch.getAbsolutePath());
				newRepository.addBranch(branchName, new Branch(branchName, keyOfCommit));
				fetchCommitsRec(objectsPath, keyOfCommit, newRepository);
			}
		}
		
		this.setRepository(newRepository);
	}
	
	private void fetchCommitsRec(String objectsPath, String keyOfCommit, Repository newRepository) throws IOException
	{
		if (!keyOfCommit.equals("null"))
		{
			String contentOfCommit = Utilz.readZippedFile(objectsPath + keyOfCommit + ".zip", objectsPath, keyOfCommit + ".txt");
			Commit newCommit = Commit.createInstanceFromString(contentOfCommit);
			newRepository.addCommit(keyOfCommit, newCommit);
			fetchProgramInfoRec(newCommit.getRootFolderEncryptionKey(), NodeInformation.ItemType.Folder, objectsPath, true, newRepository);
			fetchCommitsRec(objectsPath, newCommit.getPreviousCommitKey(), newRepository);
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
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////// CMD number 5
	
	public Map<String, String> showStatus() throws IOException
	{
		Map<String, String> workingCopyStatusMap = new HashMap<>();
		Map<String, String> commitStatusMap = new HashMap<>();
		File rootFile = new File(this.getRepository().getPath());
		getStatusOfWorkingCopyRec(rootFile, workingCopyStatusMap);
		getStatusOfLastCommit(commitStatusMap);
		Map<String, String> result = new HashMap<>();
		String value;
		
		for (Map.Entry<String, String> wcEntry : workingCopyStatusMap.entrySet())
		{
			value = commitStatusMap.get(wcEntry.getKey());
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
		
		for (Map.Entry<String, String> commitEntry : commitStatusMap.entrySet())
		{
			value = workingCopyStatusMap.get(commitEntry.getKey());
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
					String sha1 = Folder.folderSHA1(folderNode.getInformationMap());
					workingCopyStatus.put(file.getAbsolutePath(), sha1);
					//adding the file to the Map
					NodeInformation nodeInformation = new NodeInformation(file.getName(), sha1, m_Username, NodeInformation.ItemType.Folder);
					nodeInformationMap.putIfAbsent(sha1, nodeInformation);
				}
			}
		}
		
		return nodeInformationMap;
	}
	
	private void getStatusOfLastCommit(Map<String, String> lastCommitStatusMap)
	{
		Commit lastCommit = this.getRepository().getActiveCommit();
		if (lastCommit != null)
		{
			String nameOfRootFile = lastCommit.getRootFolderEncryptionKey();
			String objectsPath = getRepository().getPath() + "\\.magit\\objects";
			String workingCopyPath = getRepository().getPath();
			File rootFile = new File(objectsPath + "\\" + nameOfRootFile);
			getStatusOfLastCommitRec(rootFile, lastCommitStatusMap, workingCopyPath, objectsPath);
		}
	}
	
	private void getStatusOfLastCommitRec(File file, Map<String, String> lastCommitStatusMap, String workingCopyPath, String objectsPath)
	{
		Node node = getRepository().getFiles().get(file.getName());
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
				getStatusOfLastCommitRec(newFile, lastCommitStatusMap, newPath, objectsPath);
			}
		}
	}
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////// CMD number 6
	
	public void commit(String commitMessage) throws IOException
	{
		File rootFile = new File(this.getRepository().getPath());
		String rootFolderSHA1, commitSHA1, branchName;
		Branch branch;
		branchName = this.getRepository().getHeadBranch().getBranchName();
		branch = this.getRepository().getBranches().get(branchName);
		Commit newCommit = new Commit("encryptionKeyOfRootFolder", branch.getKeyOfCommit(), commitMessage, Utilz.getCurrentTime(), m_Username);
		Map<String, NodeInformation> info = fetchingInfoOfWorkingCopyRec(rootFile, this.getRepository());
		
		//creating a root folder
		Folder rootFolder = new Folder(Folder.eRoot.Root);
		rootFolder.setInformationMap(info);
		rootFolderSHA1 = Folder.folderSHA1(rootFolder.getInformationMap());
		//creating a rootFolder file
		String rootFolderContent = rootFolder.folderToFileString();
		this.getRepository().createMagitFile(rootFolderSHA1, rootFolderContent, "object");
		this.getRepository().addFile(rootFolderSHA1, rootFolder);
		
		//commit settings
		newCommit.setRootFolderEncryptionKey(rootFolderSHA1);
		commitSHA1 = newCommit.CommitSHA1();
		//creating a commit file
		String commitContent = newCommit.commitToFileString();
		this.getRepository().createMagitFile(commitSHA1, commitContent, "object");
		this.getRepository().addCommit(commitSHA1, newCommit);
		
		//branch settings
		branch.setKeyOfCommit(commitSHA1);
		//creating a branch file
		this.getRepository().createMagitFile(branchName, branch.getKeyOfCommit(), "branch");
	}
	
	private Map<String, NodeInformation> fetchingInfoOfWorkingCopyRec(File folder, Repository rep) throws IOException
	{
		Map<String, NodeInformation> nodeInformationMap = new TreeMap<>();
		NodeInformation nodeInformation;
		for (File file : Objects.requireNonNull(folder.listFiles(), "File collection can not be null"))
		{
			if(!file.getName().equals(".magit"))
			{
				if (file.isFile())
				{
					//creating a Blob
					String content = Utilz.readFileAsString(file.getAbsolutePath());
					String sha1 = DigestUtils.sha1Hex(content);
					//creating a Blob file
					this.getRepository().createMagitFile(sha1, content, "object");
					//adding the information to the Map
					if (getRepository().getFiles().get(sha1) == null)
					{
						nodeInformation = new NodeInformation(file.getName(), sha1, m_Username, NodeInformation.ItemType.Blob);
					}
					
					else
					{
						nodeInformation = this.getRepository().searchForNodeInfo(sha1);
					}
					
					rep.addFile(sha1, new Blob(content));
					nodeInformationMap.put(sha1, nodeInformation);
				}
				else
				{
					Map<String, NodeInformation> infoMap = fetchingInfoOfWorkingCopyRec(file, rep);
					if (infoMap.size() > 0)
					{
						//creating a Folder
						Folder folderNode = new Folder(Folder.eRoot.Regular, infoMap);
						String sha1 = Folder.folderSHA1(infoMap);
						//creating a Folder file
						String folderContent = folderNode.folderToFileString();
						this.getRepository().createMagitFile(sha1, folderContent, "object");
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
				}
			}
		}
		
		return nodeInformationMap;
	}
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////// CMD number 7
	
	public String showAllBranches()
	{
		String result = "";
		String activeBranchName = this.getRepository().getHeadBranch().getBranchName();
		Commit commitOfBranch;
		
		for (Map.Entry<String, Branch> branchMapEntry : this.getRepository().getBranches().entrySet())
		{
			result += "================================================================================================";
			result += System.lineSeparator();
			result += "The branch name is: " + branchMapEntry.getValue().getName();
			if (branchMapEntry.getValue().getName().equals(activeBranchName))
			{
				result += "  <==========";
			}
			
			result += System.lineSeparator();
			if (!branchMapEntry.getValue().getKeyOfCommit().equals("null"))
			{
				commitOfBranch = this.getRepository().getCommits().get(branchMapEntry.getValue().getKeyOfCommit());
				result += "The SHA1 of the commit pointed by this branch is: " + branchMapEntry.getValue().getKeyOfCommit() + System.lineSeparator();
				result += "This commit' message is: " + commitOfBranch.getMessage() + System.lineSeparator();
			}
			else
			{
				result += "This branch isn't pointing to any commit" + System.lineSeparator();
			}
			
			result += "================================================================================================";
			result += System.lineSeparator();
		}
		
		return result;
	}
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////// CMD number 8
	
	public Utilz.TwoParametersResult createBranch(String branchName) throws IOException
	{
		if (this.getRepository().getBranches().get(branchName) == null)
		{
			String activeBranchName = this.getRepository().getHeadBranch().getBranchName();
			Branch activeBranch = this.getRepository().getBranches().get(activeBranchName);
			String keyOfCommit = activeBranch.getKeyOfCommit();
			Branch newBranch = new Branch(branchName, keyOfCommit);
			this.getRepository().addBranch(branchName, newBranch);
			this.getRepository().createMagitFile(branchName, keyOfCommit, "branch");
			return new Utilz.TwoParametersResult(true, "New branch created");
		}
		
		return new Utilz.TwoParametersResult(false, "Failed to create a new Branch... Branch by that name already exists!");
	}
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////// CMD number 9
	
	public String deleteBranch(String branchName)
	{
		String result;
		String activeBranchName = this.getRepository().getHeadBranch().getBranchName();
		
		if (!branchName.equals(activeBranchName))
		{
			if (this.getRepository().getBranches().get(branchName) != null)
			{
				this.getRepository().getBranches().remove(branchName);
				Utilz.fileErasure(this.getRepository().getPath() + "\\.magit\\branches\\" + branchName + ".txt");
				result = "Branch '" + branchName + "' has been removed successfully";
			}
			else
			{
				result = "Branch '" + branchName + "' could not be found in the repository";
			}
		}
		else
		{
			result = "Branch '" + branchName + "' is currently the active branch, and therefore can not be removed";
		}
		
		return result;
	}
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////// CMD number 10
	
	public String changeHeadBranch(String nameOfHeadBranch) throws IOException
	{
		File rootFolder = new File(m_Repository.getPath());
		Utilz.deleteMagitDirectory(rootFolder);
		this.getRepository().getHeadBranch().setBranchName(nameOfHeadBranch);
		this.getRepository().createMagitFile("Head", nameOfHeadBranch, "branch");
		Node node = this.getRepository().getRootFolderByBranchName(nameOfHeadBranch);
		if (node != null)
		{
			deployCommitRec(rootFolder, node);
			return "Successfully changed and fetched the head branch!";
		}
		
		return "Successfully changed the head branch!";
	}
	
	private void deployCommitRec(File fileToDeploy, Node node) throws IOException
	{
		if (node instanceof Folder)
		{
			Map<String, NodeInformation> nodeInformationMap = ((Folder) node).getInformationMap();
			for (NodeInformation nodeInformation : nodeInformationMap.values())
			{
				File newFile = new File(fileToDeploy.getAbsolutePath() + "\\" + nodeInformation.getName());
				createWorkingCopyFile(newFile, nodeInformation.getEncryption());
				deployCommitRec(newFile, getRepository().getFiles().get(nodeInformation.getEncryption()));
			}
		}
		else if (node instanceof Blob)
		{
			Utilz.writeFileAsString(fileToDeploy.getAbsolutePath(), ((Blob) node).getContent());
		}
	}
	
	private void createWorkingCopyFile(File file, String encryption) throws IOException
	{
		Node node = getRepository().getFiles().get(encryption);
		node.createWorkingCopyFile(file);
	}
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////// CMD number 11
	
	public String branchHistory()
	{
		String nameOfLastBranch = getRepository().getHeadBranch().getBranchName();
		Branch lastBranch = getRepository().getBranches().get(nameOfLastBranch);
		String nameOfLastCommit = lastBranch.getKeyOfCommit();
		String result = "";
		Commit commit;
		while (!nameOfLastCommit.equals("null"))
		{
			commit = this.getRepository().getCommits().get(nameOfLastCommit);
			result += "================================================================================================";
			result += System.lineSeparator();
			result += "The commit's SHA1 is: " + nameOfLastCommit + System.lineSeparator();
			result += "The commit's message is: " + commit.getMessage() + System.lineSeparator();
			result += "The commit was created in: " + commit.getCreationDate() + System.lineSeparator();
			result += "The commit was created by: " + commit.getAuthor() + System.lineSeparator();
			result += "================================================================================================";
			result += System.lineSeparator();
			nameOfLastCommit = commit.getPreviousCommitKey();
		}
		
		if (result.equals(""))
		{
			result = "The active branch does not contain any commit history";
		}
		
		return result;
	}
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////// CMD number 12
	
	public boolean checkForExistanceOfCommit(String commitKey)
	{
		return this.m_Repository.getCommits().get(commitKey) != null;
	}
	
	public boolean commitIsActive(String commitKey)
	{
		return this.m_Repository.getActiveBranch().getKeyOfCommit().equals(commitKey);
	}
	
	public void changeActiveCommit(String commitKey) throws IOException
	{
		this.m_Repository.getActiveBranch().setKeyOfCommit(commitKey);
		changeCommit();
	}
	
	private String changeCommit() throws IOException
	{
		File rootFolder = new File(m_Repository.getPath());
		Utilz.deleteMagitDirectory(rootFolder);
		Node node = this.getRepository().getRootFolderByBranchName(this.getRepository().getHeadBranch().getBranchName());
		if (node != null)
		{
			deployCommitRec(rootFolder, node);
			return "Successfully changed and fetched the active commit!";
		}
		
		return "Successfully changed the active commit!";
	}
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////// CMD number 13
	
	public void localRepositoryInitialization(String repositoryPath, String repositoryName) throws IOException
	{
		this.setRepository(new Repository(new Head("Master"), repositoryPath, repositoryName));
		this.getRepository().addBranch("Master", new Branch("Master", "null"));
		Branch branch;
		String branchName = this.getRepository().getHeadBranch().getBranchName();
		branch = this.getRepository().getBranches().get(branchName);
		this.getRepository().createMagit(repositoryPath);
		this.getRepository().createMagitFile(branchName, "null", "branch");
		this.getRepository().createMagitFile("Head", branch.getName(), "branch");
		this.getRepository().createMagitFile("Repository", this.getRepository().getRepositoryName(), "branch");
	}
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////// utilz
	
	public boolean checkIfExistUnsavedChanges() throws IOException
	{
		boolean isExistChanges = false;
		Map<String, String> currentStatus = showStatus();
		
		isExistChanges = !currentStatus.isEmpty();
		
		return isExistChanges;
	}
	
}
