package Components.AppWindowComponent;

import Components.CloneComponent.CloneController;
import Components.CommitChangeComponent.ChangeCommit;
import Components.CommitChangeComponent.CommitChangeController;
import Components.ConflictComponent.ConflictController;
import Components.FetchLocalRepository.FetchLocalRepositoryController;
import Components.NewBranchComponent.CommitInformation;
import Components.NewBranchComponent.NewBranchController;
import Components.NewBranchComponent.NewBranchLogic;
import Components.XMLRepositoryComponent.XMLRepositoryController;
import Components.NewRepositoryComponent.NewRepositoryController;
import Components.UsernameComponent.UsernameController;
import com.fxgraph.graph.Graph;
import com.fxgraph.graph.PannableCanvas;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import logic.*;
import main.UIUtilz;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class AppWindowController
{
	@FXML
	private TextField commitTextField;
	
	@FXML
	private TextArea contentTextArea;
	
	@FXML
	private TableView<CommitInfo> commitTable;
	
	@FXML
	private TableColumn<CommitInfo, String> branchColumn;
	
	@FXML
	private TableColumn<CommitInfo, String> messageColumn;
	
	@FXML
	private TableColumn<CommitInfo, String> creationDateColumn;
	
	@FXML
	private TableColumn<CommitInfo, String> authorColumn;
	
	@FXML
	private TableColumn<CommitInfo, String> encrryptionKeyColumn;
	
	@FXML
	private TableColumn<CommitInfo, String> firstCommitKeyColumn;
	
	@FXML
	private TableColumn<CommitInfo, String> secondCommitKeyColumn;
	
	@FXML
	private TableView<FileInfo> fileTable;
	
	@FXML
	private TableColumn<FileInfo, String> fileColumn;
	
	@FXML
	private Label creationDateLabel;
	
	@FXML
	private Label authorLabel;
	
	@FXML
	private MenuItem changeUsernameMenuItem;
	
	@FXML
	private MenuItem newRepositoryMenuItem;
	
	@FXML
	private MenuItem switchRepositoryMenuItem;
	
	@FXML
	private MenuItem loadXMLMenuItem;
	
	@FXML
	private MenuItem cloneMenuItem;
	
	@FXML
	private MenuItem fetchMenuItem;
	
	@FXML
	private MenuItem pullMenuItem;
	
	@FXML
	private MenuItem pushMenuItem;
	
	@FXML
	private MenuItem newBranchMenuItem;
	
	@FXML
	private MenuItem changeBranchLocMenuItem;
	
	@FXML
	private TextField repositoryTextField;
	
	@FXML
	private TextField activeBranchTextField;
	
	@FXML
	private Button mergeButton;
	
	@FXML
	private Button deltaButton;
	
	@FXML
	private Button commitButton;
	
	@FXML
	private TextField pathTextField;
	
	@FXML
	private TextField usernameTextField;
	
	@FXML
	private Menu showBranchesMenu;
	
	@FXML
	private ScrollPane scrollpaneContainer;
	
	@FXML
	private VBox firstUpdatedVBox;
	
	@FXML
	private VBox firstAddedVbox;
	
	@FXML
	private VBox firstRemovedVbox;
	
	@FXML
	private VBox secondUpdatedVbox;
	
	@FXML
	private VBox secondAddedVbox;
	
	@FXML
	private VBox secondRemovedVbox;
	
	@FXML
	private Tab secondCommitTab;
	
	@FXML
	private TextField branchTextField;
	
	@FXML
	private Button removeButton;
	
	@FXML
	private Button resetButton;
	
	@FXML
	private TextField newBranchTextField;
	
	@FXML
	private Button newBranchButton;
	
	@FXML
	private ChoiceBox<eStyle> styleChoiceBox;
	private SimpleBooleanProperty isRemote;
	private SimpleStringProperty remotePath;
	
	public void setIsRemote(SimpleBooleanProperty isRemote)
	{
		this.isRemote=isRemote;
		fetchMenuItem.disableProperty().bind(isRemote.not());
		pullMenuItem.disableProperty().bind(isRemote.not());
		pushMenuItem.disableProperty().bind(isRemote.not());
	}
	
	public void setRemotePath(SimpleStringProperty remotePath)
	{
		this.remotePath=remotePath;
	}
	
	enum eStyle{Default,Silver,Dark};
	private SimpleBooleanProperty isRepositoryLoaded;
	private RepositoryManager magitLogic;
	private Stage stage;
	private Stage secondaryStage;
	private Graph tree;
	private SimpleStringProperty isNodeClicked;
	private SimpleBooleanProperty isMerge;
	private String othersBranchName;
	
	public AppWindowController()
	{
		isRepositoryLoaded = new SimpleBooleanProperty(false);
		isNodeClicked = new SimpleStringProperty("");
		isMerge = new SimpleBooleanProperty(false);
	}
	
	@FXML
	private void initialize()
	{
		initializeStyleBox();
		newBranchTextField.disableProperty().bind(commitTable.getSelectionModel().selectedItemProperty().isNull());
		newBranchButton.disableProperty().bind(newBranchTextField.textProperty().isEmpty());
		changeBranchLocMenuItem.disableProperty().bind(isRepositoryLoaded.not());
		newBranchMenuItem.disableProperty().bind(isRepositoryLoaded.not());
		deltaButton.disableProperty().bind(isRepositoryLoaded.not());
		commitTextField.textProperty().addListener(event -> validateMessage());
		isNodeClicked.addListener((event) -> tableSelection());
		resetButton.disableProperty().bind(commitTable.getSelectionModel().selectedItemProperty().isNull());
		deltaButton.addEventHandler(ActionEvent.ACTION, even ->
		{
			fileTable.getItems().clear();
			clearContentFile();
			commitTable.getSelectionModel().clearSelection();
		});
		initializeCommitList();
		initializeFileList();
	}
	
	private void initializeStyleBox()
	{
		styleChoiceBox.getItems().addAll(eStyle.Default,eStyle.Silver,eStyle.Dark);
		styleChoiceBox.setValue(eStyle.Default);
		styleChoiceBox.getSelectionModel().selectedIndexProperty().addListener((observable,oldValue,newValue)->onStyleSelection(newValue));
	}
	
	private void onStyleSelection(java.lang.Number index)
	{
		eStyle newValue=styleChoiceBox.getItems().get(index.intValue());
		
		switch (newValue)
		{
			case Default:
				stage.getScene().getStylesheets().removeAll("/CSS/dark.css", "/CSS/silver.css");
				break;
			case Silver:
				stage.getScene().getStylesheets().removeAll("/CSS/dark.css");
				stage.getScene().getStylesheets().addAll("/CSS/silver.css");
				break;
			case Dark:
				stage.getScene().getStylesheets().removeAll("/CSS/silver.css");
				stage.getScene().getStylesheets().addAll("/CSS/dark.css");
				break;
		}
	}
	
	private void tableSelection()
	{
		for (int i = 0; i < commitTable.getItems().size(); ++i)
		{
			if (commitTable.getItems().get(i).getEncryptionKey().equals(isNodeClicked.getValue()))
			{
				commitTable.getSelectionModel().select(i);
			}
		}
	}
	
	private void validateMessage()
	{
		if (commitTextField.getText().equals("") || commitTextField.getText().isEmpty())
		{
			commitButton.setDisable(true);
		}
		else
		{
			commitButton.setDisable(false);
		}
	}
	
	private void onChange()
	{
		fetchCommitTable();
		fetchInfoLabels();
		fetchBranchData();
		initTree();
	}
	
	private void fetchInfoLabels()
	{
		repositoryTextField.setText(magitLogic.getRepositoryName());
		activeBranchTextField.setText(magitLogic.getRepository().getHeadBranch().getBranchName());
		pathTextField.setText(magitLogic.getRepository().getPath());
		usernameTextField.setText(magitLogic.getUsername());
	}
	
	private void initializeFileList()
	{
		fileColumn.setCellValueFactory(new PropertyValueFactory<>("FileName"));
		fileTable.getSelectionModel().selectedIndexProperty().addListener(event ->
		{
			contentTextArea.setText("");
			getContectFile(fileTable.getSelectionModel().getSelectedItem());
		});
	}
	
	private void clearContentFile()
	{
		newBranchTextField.setText("");
		branchTextField.setText("");
		branchTextField.setDisable(true);
		secondCommitTab.setDisable(true);
		mergeButton.setDisable(true);
		removeButton.setDisable(true);
		creationDateLabel.setText("");
		authorLabel.setText("");
		contentTextArea.setText("");
		contentTextArea.setDisable(true);
	}
	
	private void getContectFile(FileInfo selectedItem)
	{
		if (selectedItem != null)
		{
			Node file = magitLogic.getRepository().getFiles().get(selectedItem.getEncryptionKey());
			creationDateLabel.setText(selectedItem.getCreationDate());
			authorLabel.setText(selectedItem.getAuthor());
			contentTextArea.setDisable(false);
			contentTextArea.setText(((Blob) file).getContent());
		}
	}
	
	
	private void initializeCommitList()
	{
		branchColumn.setCellValueFactory(new PropertyValueFactory<>("BranchName"));
		messageColumn.setCellValueFactory(new PropertyValueFactory<>("Message"));
		creationDateColumn.setCellValueFactory(new PropertyValueFactory<>("CreationDate"));
		creationDateColumn.setComparator(Utilz::DateTimeComparator);
		authorColumn.setCellValueFactory(new PropertyValueFactory<>("Author"));
		encrryptionKeyColumn.setCellValueFactory(new PropertyValueFactory<>("EncryptionKey"));
		firstCommitKeyColumn.setCellValueFactory(new PropertyValueFactory<>("FirstPreviousCommitKey"));
		secondCommitKeyColumn.setCellValueFactory(new PropertyValueFactory<>("SecondPreviousCommitKey"));
		commitTable.getSelectionModel().selectedIndexProperty().addListener(event ->
		{
			fileTable.getItems().clear();
			clearContentFile();
			if (commitTable.getSelectionModel().getSelectedItem() != null)
			{
				CommitInfo commitInfo = commitTable.getSelectionModel().getSelectedItem();
				fetchFileTable(commitInfo.getEncryptionKey());
				fetchCommitInfo(commitInfo.getEncryptionKey());
				commitTableMergeInfo(commitInfo);
			}
		});
	}
	
	private void commitTableMergeInfo(CommitInfo commitInfo)
	{
		if (commitInfo.getBranchName() != null && !commitInfo.getBranchName().equals(""))
		{
			if (!commitInfo.getBranchName().equals(magitLogic.getRepository().getActiveBranch().getName()))
			{
				branchTextField.setText(commitInfo.getBranchName());
				branchTextField.setDisable(true);
				mergeButton.setDisable(false);
				removeButton.setDisable(false);
			}
		}
	}
	
	private void updateFirstVbox(Map<String, String> commitInfo)
	{
		firstAddedVbox.getChildren().clear();
		firstUpdatedVBox.getChildren().clear();
		firstRemovedVbox.getChildren().clear();
		String fileName;
		Path path;
		
		if (commitInfo != null)
		{
			for (Map.Entry<String, String> commitEntry : commitInfo.entrySet())
			{
				path = Paths.get(commitEntry.getKey());
				fileName = path.getFileName().toString();
				if (commitEntry.getValue().equals("Added File"))
				{
					Text text = new Text(fileName);
					firstAddedVbox.getChildren().add(text);
				}
				else if (commitEntry.getValue().equals("Updated File"))
				{
					Text text = new Text(fileName);
					firstUpdatedVBox.getChildren().add(text);
				}
				else
				{
					Text text = new Text(fileName);
					firstRemovedVbox.getChildren().add(text);
				}
			}
		}
	}
	
	private void updateSecondVbox(Map<String, String> commitInfo)
	{
		secondAddedVbox.getChildren().clear();
		secondUpdatedVbox.getChildren().clear();
		secondRemovedVbox.getChildren().clear();
		secondCommitTab.setDisable(false);
		String fileName;
		Path path;
		
		if (commitInfo != null && !commitInfo.isEmpty())
		{
			for (Map.Entry<String, String> commitEntry : commitInfo.entrySet())
			{
				path = Paths.get(commitEntry.getKey());
				fileName = path.getFileName().toString();
				if (commitEntry.getValue().equals("Added File"))
				{
					Text text = new Text(fileName);
					secondAddedVbox.getChildren().add(text);
				}
				else if (commitEntry.getValue().equals("Updated File"))
				{
					Text text = new Text(fileName);
					secondUpdatedVbox.getChildren().add(text);
				}
				else
				{
					Text text = new Text(fileName);
					secondRemovedVbox.getChildren().add(text);
				}
			}
		}
	}
	
	private void fetchCommitInfo(String encryptionKey)
	{
		List<Map<String, String>> commitInfo;
		
		commitInfo = magitLogic.showStatusOfCommit(encryptionKey);
		if (commitInfo != null)
		{
			if (commitInfo.size() == 2)
			{
				updateSecondVbox(commitInfo.get(1));
			}
			
			if (commitInfo.size() >= 1)
			{
				updateFirstVbox(commitInfo.get(0));
			}
		}
	}
	
	private void fetchFileTable(String encryptionKey)
	{
		Commit commitToShow = magitLogic.getRepository().getCommits().get(encryptionKey);
		ObservableList<FileInfo> fileList = FXCollections.observableArrayList();
		String currentNodeEncryptKey = commitToShow.getRootFolderEncryptionKey();
		Node currentNode = magitLogic.getRepository().getFiles().get(currentNodeEncryptKey);
		makeFileListRec(currentNode, fileList);
		fileTable.setItems(fileList);
	}
	
	private void makeFileListRec(Node currentNode, ObservableList<FileInfo> fileList)
	{
		if (currentNode != null)
		{
			if (currentNode instanceof Folder)
			{
				for (Map.Entry<String, NodeInformation> nodeEntry : ((Folder) currentNode).getInformationMap().entrySet())
				{
					if (nodeEntry.getValue().getType() != NodeInformation.ItemType.Folder)
					{
						fileList.add(new FileInfo(nodeEntry.getValue().getName(), nodeEntry.getValue().getLastChangeDate(), nodeEntry.getValue().getLastChangeName(), nodeEntry.getValue().getEncryption()));
					}
					
					currentNode = magitLogic.getRepository().getFiles().get(nodeEntry.getValue().getEncryption());
					makeFileListRec(currentNode, fileList);
				}
			}
		}
	}
	
	private void fetchCommitTable()
	{
		String branchName, encryptKey;
		ObservableMap<String, CommitInfo> commitMap = FXCollections.observableHashMap();
		
		commitTable.getItems().clear();
		for (Map.Entry<String, Branch> branchEntry : magitLogic.getRepository().getBranches().entrySet())
		{
			branchName = branchEntry.getKey();
			encryptKey = branchEntry.getValue().getKeyOfCommit();
			Commit currentCommit = magitLogic.getRepository().getCommits().get(encryptKey);
			makeCommitListRec(currentCommit, commitMap, branchName, encryptKey);
		}
		
		ObservableList<CommitInfo> commitList = FXCollections.observableArrayList(commitMap.values());
		commitList.sort((commit1, commit2) -> Utilz.DateTimeComparator(commit1.getCreationDate(), commit2.getCreationDate()));
		commitTable.setItems(commitList);
	}
	
	private void makeCommitListRec(Commit currentCommit, Map<String, CommitInfo> commitMap, String branchName, String encryptKey)
	{
		String message, creationDate, author;
		List<String> previousCommitList;
		
		if (currentCommit != null)
		{
			previousCommitList = currentCommit.getPreviousCommitKeys();
			message = currentCommit.getMessage();
			creationDate = currentCommit.getCreationDate();
			author = currentCommit.getAuthor();
			if (commitMap.get(encryptKey) != null)
			{
				commitMap.get(encryptKey).addBranchName(branchName);
			}
			else
			{
				commitMap.put(encryptKey, new CommitInfo(branchName, message, creationDate, author, encryptKey, previousCommitList));
			}
			
			for (String previousCommitKey : previousCommitList)
			{
				currentCommit = magitLogic.getRepository().getCommits().get(previousCommitKey);
				branchName = "";
				makeCommitListRec(currentCommit, commitMap, branchName, previousCommitKey);
			}
		}
	}
	
	@FXML
	void changeBranchLocation(ActionEvent event)
	{
		SimpleBooleanProperty isSucceed = new SimpleBooleanProperty(false);
		isSucceed.addListener(even -> onChange());
		CommitChangeController commitChangeController = secondaryStage("/Components/CommitChangeComponent/CommitChangeFXML.fxml", "Change Branch Location", 250, 500);
		commitChangeController.setLogic(magitLogic, isSucceed);
		commitChangeController.setStage(secondaryStage);
		commitChangeController.populateList();
	}
	
	@FXML
	void switchRepository(ActionEvent event)
	{
		SimpleBooleanProperty isSucceed = new SimpleBooleanProperty(false);
		isSucceed.addListener(even -> isRepositoryLoaded.setValue(true));
		isSucceed.addListener(even -> onChange());
		FetchLocalRepositoryController fetchLocalRepositoryController = secondaryStage("/Components/FetchLocalRepository/FetchLocalRepository.fxml", "Fetch Local Repository", 250, 500);
		fetchLocalRepositoryController.setLogic(magitLogic);
		fetchLocalRepositoryController.setStage(secondaryStage);
		fetchLocalRepositoryController.setLoadedBoolean(isSucceed);
	}
	
	@FXML
	void changeUserName(ActionEvent event)
	{
		SimpleBooleanProperty isSucceed = new SimpleBooleanProperty(false);
		UsernameController usernameController = secondaryStage("/Components/UsernameComponent/UsernameFXML.fxml", "Change User Name Window", 250, 500);
		usernameController.setLogic(magitLogic, isSucceed);
		usernameController.setStage(secondaryStage);
		isSucceed.addListener(even -> usernameTextField.setText(magitLogic.getUsername()));
		
	}
	
	@FXML
	void clone(ActionEvent event)
	{
		SimpleBooleanProperty isSucceed = new SimpleBooleanProperty(false);
		isSucceed.addListener(even -> isRepositoryLoaded.setValue(true));
		isSucceed.addListener(even -> onChange());
		CloneController cloneController = secondaryStage("/Components/CloneComponent/CloneFXML.fxml", "Clone Repository", 250, 500);
		cloneController.setLogic(magitLogic);
		cloneController.setStage(secondaryStage);
		cloneController.setLoadedBoolean(isSucceed);
	}
	
	@FXML
	void createNewBranch(ActionEvent event)
	{
		SimpleBooleanProperty isSucceed = new SimpleBooleanProperty(false);
		isSucceed.addListener(even -> onChange());
		NewBranchController branchController = secondaryStage("/Components/NewBranchComponent/NewBranchFXML.fxml", "Create New Branch", 250, 500);
		branchController.setLogic(magitLogic, isSucceed);
		branchController.setStage(secondaryStage);
	}
	
	@FXML
	void fetch(ActionEvent event)
	{
		try
		{
			magitLogic.fetch();
			onChange();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	@FXML
	void fileTab(ActionEvent event)
	{
	
	}
	
	@FXML
	void loadXML(ActionEvent event)
	{
		SimpleBooleanProperty isSucceed = new SimpleBooleanProperty(false);
		isSucceed.addListener(even -> isRepositoryLoaded.setValue(true));
		isSucceed.addListener(even -> onChange());
		XMLRepositoryController xmlRepositoryController = secondaryStage("/Components/XMLRepositoryComponent/XMLRepositoryFXML.fxml", "Fetch XML Repository", 250, 500);
		xmlRepositoryController.setLogic(magitLogic);
		xmlRepositoryController.setStage(secondaryStage);
		xmlRepositoryController.setRemotePath(remotePath);
		secondaryStage.getScene().getStylesheets().addAll(stage.getScene().getStylesheets());
		xmlRepositoryController.setLoadedBoolean(isSucceed);
	}
	
	@FXML
	void newRepository(ActionEvent event)
	{
		SimpleBooleanProperty isSucceed = new SimpleBooleanProperty(false);
		isSucceed.addListener(even -> isRepositoryLoaded.setValue(true));
		isSucceed.addListener(even -> onChange());
		NewRepositoryController newRepositoryController = secondaryStage("/Components/NewRepositoryComponent/NewRepositoryFXML.fxml", "Create New Repository", 250, 500);
		secondaryStage.setMinHeight(250);
		secondaryStage.setMinWidth(500);
		newRepositoryController.setLogic(magitLogic);
		newRepositoryController.setStage(secondaryStage);
		newRepositoryController.setLoadedBoolean(isSucceed);
	}
	
	@FXML
	void push(ActionEvent event) throws IOException
	{
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("Information Dialog");
		alert.setHeaderText("The action 'push' is not possible ");
		if (!magitLogic.headBranchIsRemoteTrackingBranch())
		{
			alert.setContentText("The head branch is not an RTB");
			alert.showAndWait();
		}
		else if (!magitLogic.isRemotedPositionUnrendered())
		{
			alert.setContentText("The remote branch does not point to the same location");
			alert.showAndWait();
		}
		else if(magitLogic.isRemoteOpenChanges())
		{
			alert.setContentText("The remote repository has open changes, and therefore the action has been terminated");
			alert.showAndWait();
		}
		else
		{
			magitLogic.push();
			onChange();
		}
	}
	
	@FXML
	void pull(ActionEvent event) throws IOException
	{
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("Information Dialog");
		alert.setHeaderText("The action 'pull' is not possible ");
		if (!magitLogic.headBranchIsRemoteTrackingBranch())
		{
			alert.setContentText("The head branch is not an RTB");
			alert.showAndWait();
		}
		else if (magitLogic.showCurrentStatus(magitLogic.getRepository()).size() != 0)
		{
			alert.setContentText("There are open changes");
			alert.showAndWait();
		}
		else if (!magitLogic.isPointingToRemote())
		{
			alert.setContentText("The head branch does not point to the same commit of the remote branch");
			alert.showAndWait();
		}
		else
		{
			magitLogic.pull();
			onChange();
		}
		
	}
	
	private void fetchBranchData()
	{
		showBranchesMenu.getItems().clear();
		List<Branch> branches = new ArrayList<>(magitLogic.getRepository().getBranches().values());
		showBranchesMenu.disableProperty().bind(Bindings.createBooleanBinding(branches::isEmpty));
		branches.forEach(this::createMenu);
	}
	
	private void createMenu(Branch branch)
	{
		if (branch.getName().equals(magitLogic.getRepository().getHeadBranch().getBranchName()))
		{
			ImageView headBranchView = UIUtilz.Icon(15, "/Resources/main.png");
			MenuItem menu = new MenuItem(branch.getName());
			menu.setGraphic(headBranchView);
			showBranchesMenu.getItems().add(menu);
		}
		else if (branch instanceof LocalBranch || branch instanceof RemoteTrackingBranch)
		{
			Menu menu = new Menu(branch.getName());
			MenuItem removeMenuItem = new MenuItem("Remove Branch");
			removeMenuItem.addEventHandler(ActionEvent.ACTION, event ->
			{
				removeBranch(menu.getText());
				fetchBranchData();
			});
			MenuItem setHeadBranchMenuItem = new MenuItem("Set Head Branch");
			setHeadBranchMenuItem.addEventHandler(ActionEvent.ACTION, event ->
			{
				setMainBranch(menu.getText());
				fetchBranchData();
			});
			menu.getItems().add(setHeadBranchMenuItem);
			menu.getItems().add(removeMenuItem);
			showBranchesMenu.getItems().add(menu);
		}
		else
		{
			MenuItem menu = new MenuItem(branch.getName());
			showBranchesMenu.getItems().add(menu);
		}
	}
	
	private void setMainBranch(String branchName)
	{
		Alert alert = new Alert(Alert.AlertType.WARNING);
		boolean isValid = true;
		try
		{
			if (magitLogic.checkIfExistUnsavedChanges(magitLogic.getRepository()))
			{
				isValid = false;
				alert.setTitle("Open Changes");
				alert.setHeaderText("There are open changes in the current head branch");
				alert.setContentText("Please, make sure to commit before choosing this option again");
				alert.show();
			}
		}
		catch (IOException e)
		{
			alert.setAlertType(Alert.AlertType.ERROR);
			alert.setTitle("Error");
			alert.setHeaderText("Could not check if there are option changes");
			alert.setContentText("Please, try again later");
			alert.show();
		}
		
		try
		{
			if (isValid)
			{
				magitLogic.changeHeadBranch(branchName);
				onChange();
			}
		}
		catch (IOException e)
		{
			alert.setTitle("Error");
			alert.setHeaderText("Could not change head branch");
			alert.setContentText("Please, try again later");
			alert.show();
		}
	}
	
	private void removeBranch(String branchName)
	{
		magitLogic.deleteBranch(branchName);
		onChange();
	}
	
	public void setLogic(RepositoryManager magitLogic)
	{
		this.magitLogic = magitLogic;
		usernameTextField.setText(magitLogic.getUsername());
	}
	
	public void setStage(Stage stage)
	{
		this.stage = stage;
	}
	
	private <T> T secondaryStage(String path, String title, int minHeight, int minWidth, String... style)
	{
		final Stage dialog = new Stage();
		dialog.initModality(Modality.APPLICATION_MODAL);
		dialog.initOwner(stage);
		T controller = UIUtilz.setStage(path, dialog, style);
		dialog.setMinHeight(minHeight);
		dialog.setMinWidth(minWidth);
		dialog.setTitle(title);
		secondaryStage = dialog;
		secondaryStage.show();
		secondaryStage.getScene().getStylesheets().addAll(stage.getScene().getStylesheets());
		
		return controller;
	}
	
	@FXML
	void onCommit(ActionEvent event)
	{
		try
		{
			if (isMerge.getValue())
			{
				magitLogic.commit(commitTextField.getText(), othersBranchName);
				isMerge.setValue(false);
			}
			else
			{
				magitLogic.commit(commitTextField.getText());
			}
			
			onChange();
		}
		catch (IOException e)
		{
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setHeaderText("Failed to commit updated working copy");
			alert.setContentText(e.getMessage());
			alert.show();
		}
		finally
		{
			commitButton.setDisable(true);
			commitTextField.setDisable(true);
			commitTextField.setText("");
			isMerge.setValue(false);
		}
	}
	
	@FXML
	void onDelta(ActionEvent event)
	{
		// Show Delta
		Map<String, String> delta;
		try
		{
			delta = magitLogic.showCurrentStatus(magitLogic.getRepository());
			updateFirstVbox(delta);
			if (!delta.isEmpty())
			{
				commitTextField.disableProperty().setValue(false);
			}
			else
			{
				commitTextField.disableProperty().setValue(true);
				Alert alert = new Alert(Alert.AlertType.ERROR);
				alert.setHeaderText("Failed to show open changes");
				alert.setContentText("There are no open changes as for right now");
				alert.show();
			}
		}
		catch (IOException e)
		{
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setHeaderText("Failed to show delta status");
			alert.setContentText(e.getMessage());
			alert.show();
		}
	}
	
	private void initTree()
	{
		tree = new Graph();
		BuildTree buildTree= new BuildTree(magitLogic,tree,isNodeClicked);
		ScrollPane scrollPane = scrollpaneContainer;
		PannableCanvas canvas = tree.getCanvas();
		scrollPane.setContent(canvas);

		Platform.runLater(() ->
		{
			tree.getUseViewportGestures().set(false);
			tree.getUseNodeGestures().set(false);
		});
	}
	
	@FXML
	void onMerge(ActionEvent event) throws IOException
	{
		othersBranchName = branchTextField.getText().split(",")[0];
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		if (magitLogic.checkIfExistUnsavedChanges(magitLogic.getRepository()))
		{
			alert.setTitle("Open Changes");
			alert.setHeaderText("There are open changes in the current head branch");
			alert.setContentText("Please, make sure to commit before choosing this option again");
			alert.showAndWait();
		}
		else if (magitLogic.isContained(othersBranchName))
		{
			alert.setTitle("Active branch contained");
			alert.setHeaderText("Active Branch's commit is contained inside other's branch commit tree");
			alert.setContentText("Merge is not valid at that point");
			alert.showAndWait();
			onChange();
		}
		else if (magitLogic.isContains(othersBranchName))
		{
			alert.setTitle("Active branch contains");
			alert.setHeaderText("Active Branch's commit tree contains other's branch commit");
			alert.setContentText("Merge is not valid at that point");
			alert.showAndWait();
		}
		else
		{
			Map<String, ConflictItem> conflictMap = magitLogic.merge(othersBranchName);
			if (conflictMap.size() > 0)
			{
				ConflictController conflictController = secondaryStage("/Components/ConflictComponent/ConflictFXML.fxml", "Conflicts Window", 250, 500);
				conflictController.setLogic(magitLogic);
				conflictController.setStage(secondaryStage);
				conflictController.setConflictMap(conflictMap);
			}
			else
			{
				alert.setTitle("Commit");
				alert.setHeaderText("commit the changes");
				alert.setContentText("Please, make sure to commit the changes before continuing using the system");
				alert.showAndWait();
			}
			
			isMerge.setValue(true);
		}
	}
	
	@FXML
	void onNewBranch(ActionEvent event) {
		String branchName=newBranchTextField.getText();
		String branchesNames=branchTextField.getText();
		CommitInfo commitInfo=commitTable.getSelectionModel().getSelectedItem();
		CommitInformation commitInformation=
				new CommitInformation(commitInfo.getBranchName(),commitInfo.getEncryptionKey(),
						commitInfo.getMessage(),commitInfo.getCreationDate(),commitInfo.getAuthor(),false);
		NewBranchLogic newBranchLogic=new NewBranchLogic(magitLogic,stage,commitInformation);
		boolean isRemote=newBranchLogic.isRemote(branchesNames);
		commitInformation.setRemote(isRemote);
		newBranchLogic.createNewBranch(branchName);
		onChange();
	}
	
	@FXML
	void onRemove(ActionEvent event) {
		Branch branch;
		String branchesNames=branchTextField.getText();
		String[] branchesArray=branchesNames.split(", ");
		String activeBranchName=magitLogic.getRepository().getActiveBranch().getName();
		for(String branchName: branchesArray)
		{
			if(!branchName.equals(activeBranchName))
			{
				branch = magitLogic.getRepository().getBranches().get(branchName);
				if (branch != null)
				{
					if (!(branch instanceof RemoteBranch))
					{
						removeBranch(branchName);
						return;
					}
				}
			}
		}
		
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("Remove Branch");
		alert.setHeaderText("Selection of branch");
		alert.setContentText("Please, make sure to select a valid branch to delete");
		alert.showAndWait();
	}
	
	@FXML
	void onReset(ActionEvent event) {
		ChangeCommit changeCommit = new ChangeCommit(stage,magitLogic);
		boolean isValid;
		isValid=changeCommit.checkForOpenChanges();
		if(isValid)
		{
			String commitEncryptionKey = commitTable.getSelectionModel().getSelectedItem().getEncryptionKey();
			changeCommit.changeActiveBranchCommit(commitEncryptionKey);
			onChange();
		}
		
	}
}
