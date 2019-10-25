package logic;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class RemoteTrackingBranch extends Branch
{
	private String followingAfterName;
	
	public RemoteTrackingBranch(String i_BranchName, String i_KeyOfCommit, String followingAfter)
	{
		super(i_BranchName, i_KeyOfCommit);
		this.followingAfterName = followingAfter;
	}
	
	public String getFollowingAfterName()
	{
		return followingAfterName;
	}
	
	public void setFollowingAfterName(String followingAfterName)
	{
		this.followingAfterName = followingAfterName;
	}
	
	@Override
	public void createMagitFile(String pathOfRepository) throws IOException
	{
		String newPath = getMagitPath(pathOfRepository);
		File file = new File(newPath);
		file.createNewFile();
		String context=getKeyOfCommit()+",Remote Tracking Branch,"+ getFollowingAfterName();
		Utilz.writeFileAsString(newPath, context);
	}
	
	@Override
	public void removeMagitFile(String pathOfRepository) throws IOException
	{
		String newPath = getMagitPath(pathOfRepository);
		Files.deleteIfExists(Paths.get(newPath));
	}
	
	private String getMagitPath(String repositoryPath)
	{
		return repositoryPath+"\\.magit\\branches\\"+ getName() + ".txt";
	}
	
	@Override
	public Branch createBranchFromFile(File branchFile) throws IOException
	{
		String branchContext=Utilz.readFileAsString(branchFile.getAbsolutePath());
		String[] splittedString=branchContext.split(",");
		
		return new RemoteTrackingBranch(branchFile.getName(),splittedString[0],splittedString[2]);
	}
}
