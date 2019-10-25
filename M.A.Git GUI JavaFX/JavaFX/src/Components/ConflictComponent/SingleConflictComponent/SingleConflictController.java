package Components.ConflictComponent.SingleConflictComponent;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import logic.Blob;
import logic.ConflictItem;
import logic.RepositoryManager;
import logic.Utilz;
import main.UIUtilz;


public class SingleConflictController
{
	@FXML
	private TextArea oursTextArea;
	
	@FXML
	private TextArea commonTextArea;
	
	@FXML
	private TextArea theirsTextArea;
	
	@FXML
	private TextArea resultTextArea;
	
	@FXML
	private Button cancel;
	
	@FXML
	private Button submit;
	
	@FXML
	private Button remove;
	
	private Stage stage;
	private RepositoryManager magitLogic;
	private ConflictItem conflict;
	private SimpleBooleanProperty isSolved;
	
	public SingleConflictController()
	{
	}
	
	@FXML
	private void initialize()
	{

	}
	
	public void setLogic(RepositoryManager magitLogic)
	{
		this.magitLogic = magitLogic;
	}
	
	public void setConflict(ConflictItem conflict)
	{
		String encryptionKey;
		ConflictItem.eFileOrigin fileOrigin;
		
		this.conflict = conflict;
		
		for(ConflictItem.Encryption encryptionItem: conflict.getEncryptionList())
		{
			encryptionKey=encryptionItem.getEncryptionKey();
			fileOrigin=encryptionItem.getFileOrigin();
			Blob blob = (Blob) magitLogic.getRepository().getFiles().get(encryptionKey);
			if(fileOrigin.equals(ConflictItem.eFileOrigin.Ours))
			{
				oursTextArea.setDisable(false);
				oursTextArea.setText(blob.getContent());
			}
			else if(fileOrigin.equals(ConflictItem.eFileOrigin.Theirs))
			{
				theirsTextArea.setDisable(false);
				theirsTextArea.setText(blob.getContent());
			}
		}
		
		encryptionKey=conflict.getFatherKey();
		if(encryptionKey!=null)
		{
			commonTextArea.setDisable(false);
			Blob blob = (Blob) magitLogic.getRepository().getFiles().get(encryptionKey);
			commonTextArea.setText(blob.getContent());
		}
	}
	
	@FXML
	void onCancel(ActionEvent event) {
		UIUtilz.closeWindow(stage);
	}
	
	@FXML
	void onRemove(ActionEvent event) {
		Utilz.deleteFile(conflict.getPath());
		UIUtilz.closeWindow(stage);
		isSolved.setValue(true);
	}
	
	@FXML
	void onSubmit(ActionEvent event) {
		Utilz.createNewFile(conflict.getPath(),resultTextArea.getText());
		UIUtilz.closeWindow(stage);
		isSolved.setValue(true);
	}
	
	public void setStage(Stage secondaryStage)
	{
		this.stage=secondaryStage;
	}
	
	public void setIsSolved(SimpleBooleanProperty isSolved)
	{
		this.isSolved=isSolved;
	}
}
