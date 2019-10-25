package Components.FetchLocalRepository;

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

public class FetchLocalRepositoryController
{
	@FXML
	private TextField pathTextField;
	
	@FXML
	private Button browseButton;
	
	@FXML
	private Button fetchRepositoryButton;
	
	@FXML
	private Button cancelButton;
	
	@FXML
	private Label warningLabel;
	
	private SimpleStringProperty path;
	private SimpleStringProperty warning;
	private SimpleBooleanProperty isSelected;
	private SimpleBooleanProperty isValid;
	private Stage stage;
	private RepositoryManager magitLogic;
	
	public FetchLocalRepositoryController()
	{
		path = new SimpleStringProperty("");
		isSelected = new SimpleBooleanProperty(false);
		isValid = new SimpleBooleanProperty(false);
		warning = new SimpleStringProperty("");
	}
	
	@FXML
	private void initialize()
	{
		pathTextField.textProperty().bind(path);
		fetchRepositoryButton.disableProperty().bind(isSelected.not());
		isValid.addListener((observable, oldValue, newValue) ->
		{
			if (isValid.getValue())
			{
				UIUtilz.closeWindow(stage);
			}
		});
		warningLabel.textProperty().bind(warning);
		path.addListener(event -> warning.setValue(""));
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
		UIUtilz.closeWindow(stage);
	}
	
	@FXML
	void onSubmit(ActionEvent event)
	{
		Utilz.TwoParametersResult result;
		try
		{
			result = magitLogic.changeRepository(path.getValue());
			if(result.m_IsValid)
			{
				isValid.setValue(true);
			}
			
			warning.setValue(result.m_String);
		}
		catch (IOException e)
		{
			warning.setValue(e.getMessage());
		}
	}
	
	public void setLoadedBoolean(SimpleBooleanProperty isRepositoryLoaded)
	{
		isValid.addListener((observer, oldValue, newValue) ->
		{
			if (newValue)
			{
				isRepositoryLoaded.setValue(true);
			}
		});
	}
}
