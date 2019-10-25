package Logic;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class RemoteBranch extends Branch
{
	private String remoteRepositoryName;
	private String remoteBranchLocation;
	
	public RemoteBranch(String i_RemoteRepositoryName,String i_BranchName,String i_RemoteBranchLocation, String i_KeyOfCommit)
	{
		super(i_RemoteRepositoryName+"/"+i_BranchName, i_KeyOfCommit,
				BranchFactory.eBranchType.remoteBranch);
		remoteRepositoryName=i_RemoteRepositoryName;
		remoteBranchLocation=i_RemoteBranchLocation;
	}
	
	public String getRemoteRepositoryName()
	{
		return remoteRepositoryName;
	}
	
	public void setRemoteRepositoryName(String remoteRepositoryName)
	{
		this.remoteRepositoryName = remoteRepositoryName;
	}
	
	public String getRemoteBranchLocation()
	{
		return remoteBranchLocation;
	}
	
	public void setRemoteBranchLocation(String remoteBranchLocation)
	{
		this.remoteBranchLocation = remoteBranchLocation;
	}
	
	@Override
	public void createMagitFile(String pathOfRepository) throws IOException
	{
		String newPath=getPath(pathOfRepository);
		File file = new File(newPath);
		file.createNewFile();
		String context=getKeyOfCommit()+",Remote Branch,"+getRemoteBranchLocation();
		Utilz.writeFileAsString(newPath, context);
	}
	
	@Override
	public void removeMagitFile(String pathOfRepository) throws IOException
	{
		String newPath=getPath(pathOfRepository);
		Files.deleteIfExists(Paths.get(newPath));
	}
	
	public String getPath(String pathOfRepository)
	{
		String[] splitString=getName().split("/");
		String pathOfFolder=pathOfRepository+"\\.magit\\branches\\"+getRemoteRepositoryName()+"\\";
		new File(pathOfFolder).mkdirs();
		return pathOfFolder+ splitString[1] + ".txt";
	}
	
	@Override
	public Branch createBranchFromFile(File branchFile) throws IOException
	{
		String branchContext=Utilz.readFileAsString(branchFile.getAbsolutePath());
		String[] splittedString=branchContext.split(",");
		File parentFile=branchFile.getParentFile();
		return new RemoteBranch(parentFile.getName(),branchFile.getName(),splittedString[0],splittedString[2]);
	}
}
