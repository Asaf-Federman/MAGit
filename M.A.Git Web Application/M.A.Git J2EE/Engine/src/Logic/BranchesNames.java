package Logic;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

public class BranchesNames
{
	private Collection<String> baseBranchesNames;
	private Collection<String> targetBranchesNames;
	
	public BranchesNames(){
		baseBranchesNames=new LinkedList<>();
		targetBranchesNames=new LinkedList<>();
	}
	
	public Collection<String> getBaseBranchesNames()
	{
		return baseBranchesNames;
	}
	
	public void setBaseBranchesNames(Collection<String> baseBranchesNames)
	{
		this.baseBranchesNames = baseBranchesNames;
	}
	
	public Collection<String> getTargetBranchesNames()
	{
		return targetBranchesNames;
	}
	
	public void setTargetBranchesNames(Collection<String> targetBranchesNames)
	{
		this.targetBranchesNames = targetBranchesNames;
	}
}
