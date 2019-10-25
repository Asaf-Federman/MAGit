package Logic;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class LocalBranch extends Branch
{
	public LocalBranch(String i_BranchName, String i_KeyOfCommit)
	{
		super(i_BranchName, i_KeyOfCommit,
				BranchFactory.eBranchType.localBranch);
	}
	
	@Override
	public void createMagitFile(String pathOfRepository) throws IOException
	{
		String newPath = pathOfRepository+"\\.magit\\branches\\"+ getName() + ".txt";
		File file = new File(newPath);
		file.createNewFile();
		String context=getKeyOfCommit()+",Local Branch";
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
		
		return new LocalBranch(branchFile.getName(),splittedString[0]);
	}
	
	@Override
	public String toString()
	{
		int count = 0;
		String result = "";
		result += "================================================================================================";
		result +=System.lineSeparator();
		result += "The branch information is:" + System.lineSeparator();
		result += ++count + ". The branch name is: " + this.getName() + System.lineSeparator();
		result += ++count + ". The commit encryption key is: " + this.getKeyOfCommit() + System.lineSeparator();
		result += "================================================================================================";
		result += System.lineSeparator();
		return result;
	}
}
