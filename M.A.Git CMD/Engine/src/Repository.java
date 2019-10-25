import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Repository
{
	private Map<String, Node> m_Files;
	private Map<String, Commit> m_Commits;
	private Head m_HeadBranch;
	private final String m_Path;
	private Map<String, Branch> m_Branches;
	private final String m_RepositoryName;
	
	public Repository(Head headBranch, String path, String repositoryName)
	{
		m_Files = new HashMap<>();
		m_Commits = new HashMap<>();
		m_Path = path;
		m_HeadBranch = headBranch;
		m_Branches = new HashMap<>();
		m_RepositoryName = repositoryName;
	}
	
	public String getPath()
	{
		return m_Path;
	}
	
	public Map<String, Node> getFiles()
	{
		return m_Files;
	}
	
	public void setFiles(Map<String, Node> files)
	{
		this.m_Files = files;
	}
	
	public Map<String, Commit> getCommits()
	{
		return m_Commits;
	}
	
	public void setCommits(Map<String, Commit> commits)
	{
		this.m_Commits = commits;
	}
	
	public Head getHeadBranch()
	{
		return m_HeadBranch;
	}
	
	public void setHeadBranch(Head headBranch)
	{
		this.m_HeadBranch = headBranch;
	}
	
	public Map<String, Branch> getBranches()
	{
		return m_Branches;
	}
	
	public void setBranches(Map<String, Branch> branches)
	{
		this.m_Branches = branches;
	}
	
	public void addFile(String key, Node node)
	{
		this.getFiles().putIfAbsent(key, node);
	}
	
	public void removeFile(String key)
	{
		this.getFiles().remove(key);
	}
	
	public void addCommit(String key, Commit commit)
	{
		this.getCommits().putIfAbsent(key, commit);
	}
	
	public void removeCommit(String key)
	{
		this.getCommits().remove(key);
	}
	
	public void addBranch(String branchName, Branch branch)
	{
		this.getBranches().put(branchName, branch);
	}
	
	public void removeBranch(String branchName)
	{
		this.getBranches().remove(branchName);
	}
	
	public Branch getActiveBranch()
	{
		return this.getBranches().get(this.getHeadBranch().getBranchName());
	}
	
	public Commit getActiveCommit()
	{
		Branch activeBranch = getActiveBranch();
		Commit commitToReturn = this.getCommits().get(activeBranch.getKeyOfCommit());
		if (commitToReturn == null)
		{
			return null;
		}
		
		return this.getCommits().get(activeBranch.getKeyOfCommit());
	}
	
	public Commit getCommitByBranchName(String branchName)
	{
		Branch branch = this.getBranches().get(branchName);
		if (branch == null)
		{
			return null;
		}
		
		Commit commit = this.getCommits().get(branch.getKeyOfCommit());
		if (commit == null)
		{
			return null;
		}
		
		return commit;
	}
	
	public Folder getRootFolderByBranchName(String branchName)
	{
		Commit commit = getCommitByBranchName(branchName);
		if (commit == null)
		{
			return null;
		}
		
		Folder rootFolder = (Folder) this.getFiles().get(commit.getRootFolderEncryptionKey());
		
		return rootFolder;
	}
	
	public String getRepositoryName()
	{
		return m_RepositoryName;
	}
	
	public boolean createMagitFile(String nameOfFile, String contentOfFile, String typeOfFile) throws IOException
	{
		String Path = this.getPath() + "\\.magit";
		if (typeOfFile.equals("branch"))
		{
			Path += "\\branches\\";
		}
		else if (typeOfFile.equals("object"))
		{
			Path += "\\objects\\";
			
			if ((new File(Path + nameOfFile + ".zip").exists()))
			{
				return true;
			}
		}
		
		String newPath = Path + nameOfFile + ".txt";
		File file = new File(newPath);
		file.createNewFile();
		
		Utilz.writeFileAsString(newPath, contentOfFile);
		if (typeOfFile.equals("object"))
		{
			Utilz.zipFile(newPath, Path + nameOfFile + ".zip");
			return file.delete();
		}
		
		return true;
	}
	
	public boolean createMagit(String repositoryPath)
	{
		File objectLoc = new File(repositoryPath + "\\.magit\\objects");
		File branchLoc = new File(repositoryPath + "\\.magit\\branches");
		if (!objectLoc.mkdirs() || !branchLoc.mkdirs())
		{
			return false;
		}
		
		return true;
	}
	
	public NodeInformation searchForNodeInfo(String sha1)
	{
		for(Node node : getFiles().values())
		{
			if(node instanceof Folder)
			{
				if(((Folder) node).getInformationMap().get(sha1)!=null)
					return ((Folder) node).getInformationMap().get(sha1);
			}
		}
		
		return null;
	}
}
