package Components.UsernameComponent;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import logic.RepositoryManager;

import static main.UIUtilz.*;
import static main.UIUtilz.closeWindow;

public class UsernameController
{
	@FXML
	private TextField newUsernameTextField;
	
	@FXML
	private Label currentUserNameLabel;
	
	@FXML
	private Label warningLabel;
	
	@FXML
	private Button changeNameButton;
	
	@FXML
	private Button cancelButton;
	
	private RepositoryManager magitLogic;
	private SimpleStringProperty warning;
	private Stage stage;
	private SimpleBooleanProperty isSucceed;
	
	public UsernameController()
	{
		warning = new SimpleStringProperty("");
	}
	
	@FXML
	private void initialize()
	{
		warningLabel.textProperty().bind(warning);
	}
	
	public void setLogic(RepositoryManager magitLogic, SimpleBooleanProperty isSucceed)
	{
		this.magitLogic = magitLogic;
		this.isSucceed=isSucceed;
		currentUserNameLabel.setText(magitLogic.getUsername());
		this.isSucceed.addListener((observable, oldValue, newValue)->closeWindow(stage));
	}
	
	public void setStage(Stage stage)
	{
		this.stage = stage;
	}
	
	@FXML
	void changeUserName(ActionEvent event)
	{
		if (validateString(newUsernameTextField.getText()))
		{
			magitLogic.setUsername(newUsernameTextField.getText());
			isSucceed.setValue(true);
		}
		else
		{
			warning.setValue("Invalid name");
		}
	}
	
	@FXML
	void onCancel(ActionEvent event)
	{
		closeWindow(stage);
	}
}
