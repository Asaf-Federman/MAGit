package Components.NewBranchComponent;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import logic.Branch;
import logic.BranchFactory;
import logic.RemoteBranch;
import logic.RepositoryManager;
import main.UIUtilz;

import java.io.IOException;
import java.util.Optional;

public class NewBranchLogic
{
	private RepositoryManager magitLogic;
	private Stage stage;
	private CommitInformation commitInformation;
	
	public NewBranchLogic(RepositoryManager magitLogic,Stage stage, CommitInformation commitInformation)
	{
		this.magitLogic = magitLogic;
		this.stage = stage;
		this.commitInformation = commitInformation;
	}
	
	public boolean createNewBranch(String branchName)
	{
		boolean isRemote=false;
		Branch branch;
		if(commitInformation.getRemote())
		{
			isRemote=alertWindow();
		}
		try
		{
			String commitEncryptionKey=commitInformation.getKey();
			if(isRemote)
			{
				String followingAfter="";
				String selectedBranches=commitInformation.getBranchName();
				for(String string:selectedBranches.split(", "))
				{
					branch=magitLogic.getRepository().getBranches().get(string);
					if(branch instanceof RemoteBranch)
					{
						followingAfter=branch.getName();
						break;
					}
				}
				
				String name=followingAfter.split("/")[1];
				branch=BranchFactory.createBranch(name, commitEncryptionKey, followingAfter);
			}
			else
			{
				branch=BranchFactory.createBranch(branchName,commitEncryptionKey);
			}
			
			magitLogic.createBranch(branch);
			return true;
		}
		catch (IOException e)
		{
			return false;
		}
	}
	
	private boolean alertWindow()
	{
		Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
		alert.setTitle("Confirmation Dialog");
		alert.setHeaderText("Remote Branch");
		alert.setContentText("Would you like to create a Remote Tracking Branch?");
		
		ButtonType yesButton = new ButtonType("Yes");
		ButtonType noButton = new ButtonType("No");
		
		alert.getButtonTypes().setAll(yesButton, noButton);
		Optional<ButtonType> result = alert.showAndWait();
		return result.get() == yesButton;
	}
	
	public boolean isRemote(String branchesNames)
	{
		Branch branch;
		if(branchesNames!=null)
		{
			String[] splittedString=branchesNames.split(", ");
			for(String string: splittedString)
			{
				branch=magitLogic.getRepository().getBranches().get(string);
				if(branch instanceof RemoteBranch)
				{
					return true;
				}
			}
		}
		
		return false;
	}
}
