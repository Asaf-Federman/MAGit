package Components.NewRepositoryComponent;


import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import logic.RepositoryManager;
import logic.Utilz;
import main.UIUtilz;

import java.io.File;
import java.io.IOException;

import static main.UIUtilz.closeWindow;
import static main.UIUtilz.validateString;

public class NewRepositoryController
{
	
	@FXML
	private TextField nameTextField;
	
	@FXML
	private TextField pathTextField;
	
	@FXML
	private Button browseButton;
	
	@FXML
	private Button createRepositoryButton;
	
	@FXML
	private Button cancelButton;
	
	@FXML
	private Label warningLabel;
	
	private SimpleStringProperty path;
	private SimpleStringProperty warning;
	private SimpleStringProperty name;
	private SimpleBooleanProperty isValid;
	private SimpleBooleanProperty isNameValid;
	private SimpleBooleanProperty isSelected;
	private Stage stage;
	private RepositoryManager magitLogic;
	
	
	public NewRepositoryController()
	{
		path = new SimpleStringProperty("");
		warning = new SimpleStringProperty("");
		name = new SimpleStringProperty("");
		isNameValid = new SimpleBooleanProperty(false);
		isSelected = new SimpleBooleanProperty(false);
	}
	
	@FXML
	private void initialize()
	{
		warningLabel.textProperty().bind(warning);
		pathTextField.textProperty().bind(path);
		path.addListener(event -> warning.setValue(""));
		nameTextField.textProperty().addListener((observable, oldValue, newValue) ->
		{
			name.setValue(nameTextField.getText());
			isNameValid.setValue(validateString(name.getValue()));
		});
		isNameValid.addListener((observable, oldValue, newValue) -> setMessage());
		createRepositoryButton.disableProperty().bind(Bindings.or(isNameValid.not(), isSelected.not()));
	}
	
	private void setMessage()
	{
		if (!isNameValid.getValue())
		{
			warning.setValue("Invalid name");
		}
		else
		{
			warning.setValue("");
		}
	}
	
	public void setLogic(RepositoryManager magitLogic)
	{
		this.magitLogic = magitLogic;
	}
	
	public void setStage(Stage stage)
	{
		this.stage = stage;
	}
	
	@FXML
	void onBrowse(ActionEvent event)
	{
		DirectoryChooser directoryChooser = new DirectoryChooser();
		directoryChooser.setTitle("Select a New Repository Directory");
		File selectedFile = directoryChooser.showDialog(stage);
		if (selectedFile != null)
		{
			isSelected.setValue(true);
			path.setValue(selectedFile.getAbsolutePath());
		}
	}
	
	@FXML
	void onCancel(ActionEvent event)
	{
		closeWindow(stage);
	}
	
	@FXML
	void onSubmit(ActionEvent event)
	{
		fetchNewRepository();
	}
	
	private void fetchNewRepository()
	{
		try
		{
			Utilz.TwoParametersResult result = magitLogic.localRepositoryInitialization(path.getValue(), nameTextField.getText());
			if (!result.m_IsValid)
			{
				warning.setValue(result.m_String);
			}
			else
			{
				isValid.setValue(true);
				UIUtilz.closeWindow(stage);
			}
		}
		catch (IOException e)
		{
			warning.setValue(e.getMessage());
		}
	}
	
	public void setLoadedBoolean(SimpleBooleanProperty isRepositoryLoaded)
	{
		isValid=isRepositoryLoaded;
	}
}
