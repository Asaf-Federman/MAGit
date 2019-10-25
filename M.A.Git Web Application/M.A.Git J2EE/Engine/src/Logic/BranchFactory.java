package Logic;

import java.io.File;
import java.io.IOException;

//Factory Method
public class BranchFactory
{
	enum eBranchType{
		localBranch,
		remoteBranch,
		remoteTrackingBranch
	}
	public static Branch createBranch(String i_BranchName, String i_KeyOfCommit)
	{
		return new LocalBranch(i_BranchName,i_KeyOfCommit);
	}
	
	public static Branch createBranch(String i_BranchName, String i_KeyOfCommit, String followingAfter)
	{
		return new RemoteTrackingBranch(i_BranchName,i_KeyOfCommit,followingAfter);
	}
	
	public static Branch createBranch(String i_RemoteRepositoryName,String i_BranchName,String i_RemoteRepositoryLocation,String i_KeyOfCommit)
	{
		String branchLocation=i_RemoteRepositoryLocation+"\\.magit\\branches\\"+i_BranchName;
		return new RemoteBranch(i_RemoteRepositoryName,i_BranchName,branchLocation,i_KeyOfCommit);
	}
	
	public static Branch createBranchFromFile(File branchFile) throws IOException
	{
		String branchName = branchFile.getName().substring(0, branchFile.getName().length() - 4);
		String branchFileContext = Utilz.readFileAsString(branchFile.getAbsolutePath());
		String[] splittedString = branchFileContext.split(",");
		if (splittedString[1].equals("Local Branch"))
		{
			return new LocalBranch(branchName, splittedString[0]);
		}
		else if (splittedString[1].equals("Remote Branch"))
		{
			String parentName= branchFile.getParentFile().getName();
			return new RemoteBranch(parentName,branchName,branchFile.getAbsolutePath(),splittedString[0]);
		}
		else
		{
			return new RemoteTrackingBranch(branchName,splittedString[0],splittedString[2]);
		}
	}
}
