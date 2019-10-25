package Components.XMLRepositoryComponent;

import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import logic.Repository;
import logic.RepositoryManager;
import logic.Utilz;
import logic.XMLFetcher;
import main.UIUtilz;

import javax.xml.bind.JAXBException;

import java.io.File;

import static main.UIUtilz.closeWindow;

public class XMLRepositoryController
{
	@FXML
	private ScrollPane scrollpaneContainer;
	
	@FXML
	private TextField pathTextField;
	
	@FXML
	private Button browseButton;
	
	@FXML
	private Button deleteButton;
	
	@FXML
	private Label informationLabel;
	
	@FXML
	private Button createRepositoryButton;
	
	@FXML
	private Button cancelButton;
	
	@FXML
	private Label warningLabel;
	
	@FXML
	private ProgressBar progressBar;
	
	private SimpleBooleanProperty isPreValid;
	private SimpleBooleanProperty isValid;
	private SimpleBooleanProperty isSubmitValid;
	private SimpleBooleanProperty isRepository;
	private SimpleStringProperty warning;
	private SimpleStringProperty path;
	private Stage stage;
	private RepositoryManager magitLogic;
	private XMLFetcher fetcher;
	private MyTask task;
	private SimpleStringProperty remotePath;
	
	public XMLRepositoryController()
	{
		warning = new SimpleStringProperty("");
		path = new SimpleStringProperty("");
		isValid = new SimpleBooleanProperty(false);
		isSubmitValid = new SimpleBooleanProperty(false);
		isPreValid = new SimpleBooleanProperty(false);
		isRepository = new SimpleBooleanProperty(false);
		fetcher = new XMLFetcher();
		task= new MyTask();
	}
	
	public void setRemotePath(SimpleStringProperty remotePath)
	{
		this.remotePath=remotePath;
	}
	
	public class MyTask extends Task<Repository>
	{
		private IntegerProperty currentProgress;
		private SimpleIntegerProperty maxProgress;
		
		public MyTask()
		{
			currentProgress= new SimpleIntegerProperty(0);
			maxProgress= new SimpleIntegerProperty(0);
			currentProgress.addListener(event->updateProgress());
			maxProgress.addListener(event->updateProgress());
		}
		
		@Override
		protected Repository call()
		{
			Repository repository=null;
			try
			{
				repository = fetcher.fetchXMLRepository(currentProgress, maxProgress,remotePath);
				magitLogic.setRepository(repository);
				magitLogic.fetchWorkingCopyInfo(magitLogic.getRepository());
				Platform.runLater(() -> isValid.set(true));
			}
			catch (Exception e)
			{
				e.printStackTrace();
				updateProgress(0,1);
			}
			
			return repository;
		}
		
		private void updateProgress()
		{
			updateProgress(currentProgress.getValue(), maxProgress.getValue());
		}
	}
	
	
	@FXML
	private void initialize()
	{
		progressBar.setProgress(0);
		warningLabel.textProperty().bind(warning);
		isValid.addListener((observable, oldValue, newValue) ->
		{
			if (newValue)
			{
				closeWindow(stage);
			}
		});
		pathTextField.textProperty().bind(path);
		path.addListener((observable, oldValue, newValue) ->
		{
			preValidate();
			checkPath();
			checkValidity();
		});
		createRepositoryButton.disableProperty().bind(isSubmitValid.not());
		deleteButton.disableProperty().bind(isRepository.not());
		isRepository.addListener((observable, oldValue, newValue) ->
		{
			if (newValue)
			{
				informationLabel.setText("Repository already exists in that location... Do you wish to delete it?");
			}
			else
			{
				checkValidity();
				informationLabel.setText("");
			}
		});
	}
	
	private void preValidate()
	{
		Utilz.TwoParametersResult result;
		try
		{
			result = fetcher.preValidateXML(path.getValue());
			if (!result.m_IsValid)
			{
				warning.set(result.m_String);
			}
			else if (!fetcher.isEmpty() && !fetcher.checkIfRepository().m_IsValid)
			{
				warning.set("The XML path is not empty");
				result.m_IsValid = false;
			}
			
			isPreValid.setValue(result.m_IsValid);
		}
		catch (JAXBException e)
		{
			warning.set(e.getMessage());
			isPreValid.setValue(false);
		}
	}
	
	private void checkPath()
	{
		if (isPreValid.getValue())
		{
			Utilz.TwoParametersResult result = fetcher.checkIfRepository();
			isRepository.setValue(result.m_IsValid);
		}
		else
		{
			isRepository.setValue(false);
		}
	}
	
	private void checkValidity()
	{
		if (isPreValid.getValue() && !isRepository.getValue())
		{
			isSubmitValid.setValue(true);
		}
		else
		{
			isSubmitValid.setValue(false);
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
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Select XML file");
		fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("xml files", "*.xml"));
		File selectedFile = fileChooser.showOpenDialog(stage);
		if (selectedFile == null)
		{
			return;
		}
		
		String absolutePath = selectedFile.getAbsolutePath();
		path.set(absolutePath);
		warning.setValue("");
	}
	
	@FXML
	void onCancel(ActionEvent event)
	{
		UIUtilz.closeWindow(stage);
	}
	
	@FXML
	void onDelete(ActionEvent event)
	{
		isRepository.setValue(false);
		fetcher.deleteDirectory();
	}
	
	@FXML
	void onSubmit(ActionEvent event)
	{
		Utilz.TwoParametersResult result;
		result = fetcher.checkValidationXML();
		if (!result.m_IsValid)
		{
			warning.set(result.m_String);
		}
		else
		{
			progressBar.progressProperty().bind(task.progressProperty());
			new Thread(task).start();
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

