package Logic;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class RepositoryDetails
{
	private List<RepositoryDetail> repositoryDetailList;
	
	public RepositoryDetails()
	{
		this.repositoryDetailList = new LinkedList<>();
	}
	
	public List<RepositoryDetail> getRepositoryDetailList()
	{
		return repositoryDetailList;
	}
	
	public List<RepositoryDetail> transformer(Collection<RepositoryManager> repositoryManagerCollection){
		RepositoryDetail repositoryDetail;
		String repositoryName;
		String activeBranchName;
		int numberOfBranches;
		Commit commit;
		String lastCommit;
		String lastCommitMessage;
		
		for(RepositoryManager repositoryManager : repositoryManagerCollection){
			repositoryName=repositoryManager.getRepositoryName();
			activeBranchName=repositoryManager.getRepository().getActiveBranch().getName();
			numberOfBranches=repositoryManager.getRepository().getBranches().size();
			commit=repositoryManager.getRepository().searchForLastCommit();
			lastCommit=commit.getCreationDate();
			lastCommitMessage=commit.getMessage();
			repositoryDetail=new RepositoryDetail(repositoryName, activeBranchName, numberOfBranches, lastCommit, lastCommitMessage);
			getRepositoryDetailList().add(repositoryDetail);
		}
		
		return getRepositoryDetailList();
	}
}
