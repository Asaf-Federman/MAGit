package Components.CommitChangeComponent;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextFormatter;
import javafx.stage.Stage;
import logic.Branch;
import logic.Commit;
import logic.RepositoryManager;
import main.UIUtilz;

import java.io.IOException;
import java.util.Optional;

public class ChangeCommit
{
	private Stage stage;
	private RepositoryManager magitLogic;
	
	public ChangeCommit(Stage stage, RepositoryManager magitLogic)
	{
		this.stage=stage;
		this.magitLogic=magitLogic;
	}
	
	public boolean checkForOpenChanges()
	{
		Alert alert;
		try
		{
			if (magitLogic.checkIfExistUnsavedChanges(magitLogic.getRepository()))
			{
				alert = new Alert(Alert.AlertType.WARNING);
				alert.setTitle("Open Changes");
				alert.setHeaderText("There are open changes in the current head branch");
				alert.setContentText("Would you like to save them?");
				ButtonType buttonSubmit = new ButtonType("Yes", ButtonBar.ButtonData.APPLY);
				ButtonType buttonCancel = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);
				alert.getButtonTypes().setAll(buttonSubmit, buttonCancel);
				Optional<ButtonType> result = alert.showAndWait();
				
				if (result.isPresent())
				{
					if (result.get() == buttonCancel)
					{
						return true;
					}
					else
					{
						UIUtilz.closeWindow(stage);
					}
				}
			}
			else
			{
				return true;
			}
		}
		catch (IOException e)
		{
			alert = new Alert(Alert.AlertType.WARNING);
			alert.setTitle("Error");
			alert.setHeaderText("Could not check if there are option changes");
			alert.setContentText("Please, try again later");
			alert.show();
		}
		
		return false;
	}
	
	public boolean changeActiveBranchCommit(String commitKey)
	{
		try
		{
			magitLogic.changeActiveCommit(commitKey);
			return true;
		}
		catch (IOException e)
		{
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setHeaderText("Failed to change active branch's commit");
			alert.setContentText(e.getMessage());
			alert.show();
			return false;
		}
	}
	
	private boolean isCurrentActiveCommit(String commitKey)
	{
		Branch branch= magitLogic.getRepository().getActiveBranch();
		String keyOfActiveCommit=branch.getKeyOfCommit();
		if(keyOfActiveCommit!=null)
		{
			if(keyOfActiveCommit.equals(commitKey))
			{
				return true;
			}
		}
		
		return false;
	}
}
