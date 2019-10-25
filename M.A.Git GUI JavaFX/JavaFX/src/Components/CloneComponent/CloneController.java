package Components.CloneComponent;

import com.sun.deploy.security.SelectableSecurityManager;
import javafx.beans.property.SimpleBooleanProperty;
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

public class CloneController
{
	@FXML
	private TextField nameTextField;
	
	@FXML
	private TextField fetchPathTextField;
	
	@FXML
	private Button browseFetchButton;
	
	@FXML
	private Button cloneRepositoryButton;
	
	@FXML
	private Button cancelButton;
	
	@FXML
	private Label warningLabel;
	
	@FXML
	private TextField deploymentPathTextField;
	
	@FXML
	private Button browseDeploymentButton;
	
	
	
	private RepositoryManager magitLogic;
	private Stage stage;
	private SimpleBooleanProperty isSucceed;
	private boolean isFetchSelected=false;
	private boolean isDeploymentSelected=false;
	private String fetchPath;
	private String deploymentPath;
	
	public CloneController()
	{
	
	}
	
	private void initialize()
	{
	
	}
	
	@FXML
	void onFetchClick(ActionEvent event) {
		DirectoryChooser directoryChooser = new DirectoryChooser();
		directoryChooser.setTitle("Select a New Fetch Repository Directory");
		File selectedFile = directoryChooser.showDialog(stage);
		if (selectedFile != null)
		{
			fetchPath=selectedFile.getAbsolutePath();
			fetchPathTextField.setText(fetchPath);
			if(Utilz.checkExistenceOfMagit(selectedFile.getAbsolutePath()))
			{
				isFetchSelected=true;
				warningLabel.setText("");
			}
			else
			{
				warningLabel.setText("There's no M.A.Git repository in the fetch path");
			}

		}
	}
	
	@FXML
	void onDeploymentClick(ActionEvent event) {
		DirectoryChooser directoryChooser = new DirectoryChooser();
		directoryChooser.setTitle("Select a New Deployment Repository Directory");
		File selectedFile = directoryChooser.showDialog(stage);
		if (selectedFile != null)
		{
			deploymentPath=selectedFile.getAbsolutePath();
			deploymentPathTextField.setText(deploymentPath);
			if(Utilz.isEmpty(selectedFile.getAbsolutePath()))
			{
				isDeploymentSelected=true;
				warningLabel.setText("");
			}
			else
			{
				warningLabel.setText("The deployment path is not empty");
			}
		}
	}
	
	@FXML
	void onCancel(ActionEvent event) {
		UIUtilz.closeWindow(stage);
	}
	
	@FXML
	void onSubmit(ActionEvent event)
	{
		if(validation())
		{
			try
			{
				Utilz.TwoParametersResult res;
				res=magitLogic.cloneRepository(fetchPath,deploymentPath,nameTextField.getText());
				if(!res.m_IsValid)
				{
					warningLabel.setText(res.m_String);
				}
				else
				{
					isSucceed.setValue(true);
					UIUtilz.closeWindow(stage);
				}
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		
	}
	
	private boolean validation()
	{
		if(validateText(nameTextField.getText()))
		{
			if(isDeploymentSelected && isFetchSelected)
			{
				warningLabel.setText("");
				return true;
			}
		}
		
		warningLabel.setText("Please make sure to fill all the fields");
		return false;
	}
	
	public void setLogic(RepositoryManager magitLogic)
	{
		this.magitLogic=magitLogic;
	}
	
	public void setStage(Stage secondaryStage)
	{
		stage=secondaryStage;
	}
	
	public void setLoadedBoolean(SimpleBooleanProperty isSucceed)
	{
		this.isSucceed=isSucceed;
	}
	
	private boolean validateText(String text)
	{
		return text != null && !text.equals("");
	}
}
