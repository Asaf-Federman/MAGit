package Components.NewBranchComponent;

import Components.CommitChangeComponent.CommitEntry;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import logic.*;
import main.UIUtilz;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class NewBranchController
{
	@FXML
	private TextField nameTextField;
	
	@FXML
	private Button createBranchButton;
	
	@FXML
	private Button cancelButton;
	
	@FXML
	private Label warningLabel;
	
	@FXML
	private TableView<CommitInformation> commitTableView;
	
	@FXML
	private TableColumn<CommitInformation, String> branchNameColumn;
	
	@FXML
	private TableColumn<CommitInformation, String> encryptionKeyColumn;
	
	@FXML
	private TableColumn<CommitInformation, String> messageColumn;
	
	@FXML
	private TableColumn<CommitInformation, String> creationDateColumn;
	
	@FXML
	private TableColumn<CommitInformation, String> authorColumn;
	
	@FXML
	private TableColumn<CommitInformation, Boolean> isRemoteColumn;
	
	private SimpleBooleanProperty isValid;
	private SimpleBooleanProperty isSuccess;
	private RepositoryManager magitLogic;
	private Stage stage;
	private NewBranchLogic newBranchLogic;
	
	public NewBranchController()
	{
		isValid = new SimpleBooleanProperty(false);
	}
	
	@FXML
	private void initialize()
	{
		createBranchButton.disableProperty().bind(isValid.not());
		nameTextField.textProperty().addListener(event -> warningLabel.setText(""));
		nameTextField.textProperty().addListener(event -> checkValidation());
		branchNameColumn.setCellValueFactory(new PropertyValueFactory<CommitInformation, String>("BranchName"));
		encryptionKeyColumn.setCellValueFactory(new PropertyValueFactory<CommitInformation, String>("Key"));
		creationDateColumn.setCellValueFactory(new PropertyValueFactory<CommitInformation,String>("CreationDate"));
		authorColumn.setCellValueFactory(new PropertyValueFactory<CommitInformation, String>("Author"));
		isRemoteColumn.setCellValueFactory(new PropertyValueFactory<CommitInformation, Boolean>("Remote"));
		messageColumn.setCellValueFactory(new PropertyValueFactory<CommitInformation, String>("Message"));
		commitTableView.getSelectionModel().selectedItemProperty().addListener(event->checkValidation());
		creationDateColumn.setComparator(Utilz::DateTimeComparator);
	}
	
	private void checkValidation()
	{
		String branchName = nameTextField.getText();
		
		if((branchName.isEmpty() || branchName.equals("")))
		{
			isValid.setValue(false);
			warningLabel.setText("You can not enter an empty branch name");
		}
		else if(commitTableView.getSelectionModel().isEmpty())
		{
			isValid.setValue(false);
			warningLabel.setText("You need to choose a commit");
		}
		else if (magitLogic.getRepository().getBranches().get(branchName) == null)
		{
			isValid.setValue(true);
			warningLabel.setText("");
		}
		else
		{
			isValid.setValue(false);
			warningLabel.setText("This branch name is already taken");
		}
	}
	
	@FXML
	void onCancel(ActionEvent event)
	{
		UIUtilz.closeWindow(stage);
	}
	
	@FXML
	void onSubmit(ActionEvent event)
	{
		boolean isSuccessful;
		newBranchLogic = new NewBranchLogic(magitLogic,stage,commitTableView.getSelectionModel().getSelectedItem());
		isSuccessful=newBranchLogic.createNewBranch(nameTextField.getText());
		if(isSuccessful)
		{
			isSuccess.setValue(true);
			UIUtilz.closeWindow(stage);
		}
		else
		{
			warningLabel.setText("Unable to create the branch");
		}
	}
	
	public void setLogic(RepositoryManager magitLogic, SimpleBooleanProperty isSuccess)
	{
		this.magitLogic = magitLogic;
		this.isSuccess=isSuccess;
		fetchTableInformation();
	}
	
	private void fetchTableInformation()
	{
		String branchesNames;
		NewBranchLogic branchLogic;
		ObservableList<CommitInformation> commitList = FXCollections.observableArrayList();
		Map<String,String> branchPointers=magitLogic.branchPointers();
		CommitInformation commitInformation;
		
		for (Map.Entry<String, Commit> commitEntry : magitLogic.getRepository().getCommits().entrySet())
		{
			branchesNames= branchPointers.get(commitEntry.getKey());
			commitInformation=new CommitInformation(branchesNames,commitEntry.getKey(), commitEntry.getValue().getMessage(), commitEntry.getValue().getCreationDate(), commitEntry.getValue().getAuthor(),false);
			branchLogic=new NewBranchLogic(magitLogic,stage,commitInformation);
			if(branchLogic.isRemote(branchesNames))
			{
				commitInformation.setRemote(true);
			}
			
			commitList.add(commitInformation);
		}
		
		commitList.sort(CommitInformation::compareTo);
		commitTableView.setItems(commitList);
	}
	
	public void setStage(Stage stage)
	{
		this.stage = stage;
	}
}

