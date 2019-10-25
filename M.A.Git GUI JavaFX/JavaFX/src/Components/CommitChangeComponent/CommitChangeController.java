package Components.CommitChangeComponent;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import logic.Commit;
import logic.RepositoryManager;
import logic.Utilz;
import main.UIUtilz;

import java.util.Map;

public class CommitChangeController
{
	@FXML
	private TextField activeBranchNameTextField;
	
	@FXML
	private Button changeCommitButton;
	
	@FXML
	private Button cancelButton;
	
	@FXML
	private TableView<CommitEntry> commitTable;
	
	@FXML
	private TableColumn<CommitEntry, String> messageColumn;
	
	@FXML
	private TableColumn<CommitEntry, String> creationDateColumn;
	
	@FXML
	private TableColumn<CommitEntry, String> authorColumn;
	
	@FXML
	private TableColumn<CommitEntry, Boolean> isActiveColumn;
	
	private RepositoryManager magitLogic;
	private Stage stage;
	private SimpleBooleanProperty isSelected;
	private SimpleBooleanProperty isValid;
	private SimpleBooleanProperty isSucceed;
	private ChangeCommit changeCommit;
	
	public CommitChangeController()
	{
		isSelected = new SimpleBooleanProperty(false);
		isValid = new SimpleBooleanProperty(false);
	}
	
	@FXML
	private void initialize()
	{
		messageColumn.setCellValueFactory(new PropertyValueFactory<CommitEntry, String>("Message"));
		creationDateColumn.setCellValueFactory(new PropertyValueFactory<CommitEntry, String>("CreationDate"));
		authorColumn.setCellValueFactory(new PropertyValueFactory<CommitEntry, String>("Author"));
		isActiveColumn.setCellValueFactory(new PropertyValueFactory<CommitEntry, Boolean>("Status"));
		commitTable.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) ->
		{
			isSelected.setValue(commitTable.getSelectionModel().getSelectedItem() != null);
			validate();
		});
		changeCommitButton.disableProperty().bind(isValid.not());
		creationDateColumn.setComparator(Utilz::DateTimeComparator);
	}
	
	private void validate()
	{
		changeCommit =new ChangeCommit(stage,magitLogic);
		
		if (isSelected.getValue())
		{
			CommitEntry commitEntry = commitTable.getSelectionModel().getSelectedItem();
			if (!checkForOpenChanges())
			{
				isValid.setValue(false);
			}
			else if (!commitEntry.getStatus())
			{
				isValid.setValue(true);
			}
			else
			{
				isValid.setValue(false);
			}
		}
		else
		{
			isValid.setValue(false);
		}
	}
	
	private boolean checkForOpenChanges()
	{
		return changeCommit.checkForOpenChanges();
	}
	
	@FXML
	void onCancel(ActionEvent event)
	{
		UIUtilz.closeWindow(stage);
	}
	
	@FXML
	void onSubmit(ActionEvent event)
	{
		CommitEntry commit = commitTable.getSelectionModel().getSelectedItem();
		String commitKey=commit.getKey();
		boolean isSuccessful= changeCommit.changeActiveBranchCommit(commitKey);
		if(isSuccessful)
		{
			isSucceed.setValue(true);
			UIUtilz.closeWindow(stage);
		}
	}
	
	public void setLogic(RepositoryManager magitLogic,SimpleBooleanProperty isChanged)
	{
		this.magitLogic = magitLogic;
		this.isSucceed=isChanged;
		activeBranchNameTextField.setText(magitLogic.getRepository().getHeadBranch().getBranchName());
	}
	
	public void setStage(Stage stage)
	{
		this.stage = stage;
	}
	
	public void populateList()
	{
		boolean isActive;
		ObservableList<CommitEntry> commitList = FXCollections.observableArrayList();
		for (Map.Entry<String, Commit> commitEntry : magitLogic.getRepository().getCommits().entrySet())
		{
			isActive = commitEntry.getKey().equals(magitLogic.getRepository().getActiveBranch().getKeyOfCommit());
			commitList.add(new CommitEntry(commitEntry.getKey(), commitEntry.getValue().getMessage(), commitEntry.getValue().getCreationDate(), commitEntry.getValue().getAuthor(), isActive));
		}
		commitList.sort(CommitEntry::compareTo);
		commitTable.setItems(commitList);
	}
}
