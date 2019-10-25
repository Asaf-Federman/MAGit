package Components.ConflictComponent;

import Components.CommitChangeComponent.CommitEntry;
import Components.ConflictComponent.SingleConflictComponent.SingleConflictController;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiNilLoader;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import logic.ConflictItem;
import logic.RepositoryManager;
import main.UIUtilz;

import java.util.Map;

public class ConflictController
{
	@FXML
	private Button solveCommitButton;
	
	@FXML
	private Button cancelButton;
	
	@FXML
	private ListView<ConflictItem> conflictsListBox;
	
	private Stage stage;
	private RepositoryManager magitLogic;
	private Map<String,ConflictItem> conflictMap;
	private Stage secondaryStage;
	
	public ConflictController()
	{
	}
	
	@FXML
	private void initialize()
	{
		solveCommitButton.setDisable(true);
	}
	
	public void setLogic(RepositoryManager magitLogic)
	{
		this.magitLogic = magitLogic;
	}
	
	public void setConflictMap(Map<String,ConflictItem> conflictMap)
	{
		this.conflictMap = conflictMap;
		populateListBox();
	}
	
	private void populateListBox()
	{
		ObservableList<ConflictItem> conflictObservarbleList = FXCollections.observableArrayList(conflictMap.values());
		conflictsListBox.setItems(conflictObservarbleList);
		conflictsListBox.getSelectionModel().selectedIndexProperty().addListener(event->solveCommitButton.setDisable(false));
	}
	
	public void setStage(Stage stage)
	{
		this.stage = stage;
	}
	@FXML
	void onCancel(ActionEvent event) {
		UIUtilz.closeWindow(stage);
		Alert();
	}
	
	@FXML
	void onSubmit(ActionEvent event) {
		SimpleBooleanProperty isSolve = new SimpleBooleanProperty(false);
		isSolve.addListener(even->removeSelectedItem());
		ConflictItem conflictItem = conflictsListBox.getSelectionModel().getSelectedItem();
		SingleConflictController singleConflictController = secondaryStage("/Components/ConflictComponent/SingleConflictComponent/SingleConflictFXML.fxml", conflictItem.getPath(),250,500);
		singleConflictController.setLogic(magitLogic);
		singleConflictController.setStage(secondaryStage);
		singleConflictController.setIsSolved(isSolve);
		singleConflictController.setConflict(conflictItem);
	}
	
	private void removeSelectedItem()
	{
		conflictsListBox.getItems().remove(conflictsListBox.getSelectionModel().getSelectedItem());
		if(conflictsListBox.getItems().isEmpty())
		{
			UIUtilz.closeWindow(stage);
			Alert();
		}
	}
	
	private SingleConflictController secondaryStage(String path, String title, int minHeight, int minWidth, String... style)
	{
		final Stage dialog = new Stage();
		dialog.initModality(Modality.APPLICATION_MODAL);
		dialog.initOwner(stage);
		SingleConflictController controller = UIUtilz.setStage(path, dialog, style);
		dialog.setMinHeight(minHeight);
		dialog.setMinWidth(minWidth);
		dialog.setTitle(title);
		secondaryStage = dialog;
		secondaryStage.show();
		
		return controller;
	}
	
	private void Alert()
	{
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("Commit");
		alert.setHeaderText("commit the changes");
		alert.setContentText("Please, make sure to commit the changes before continuing using the system");
		alert.showAndWait();
	}
}
