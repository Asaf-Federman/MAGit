package main;

import Components.AppWindowComponent.AppWindowController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.net.URL;

public class UIUtilz
{
	public static  <T> T setStage(String path, Stage stage, String... style)
	{
		Parent root;
		FXMLLoader loader = new FXMLLoader();
		Integer temp = 0;
		// load main fxml
		URL mainFXML = temp.getClass().getResource(path);
		loader.setLocation(mainFXML);
		try
		{
			root = loader.load();
		}
		catch (Exception e)
		{
			return null;
		}
		// wire up controller
		T controller = loader.getController();
		
		// set stage
		Scene scene = new Scene(root);
		for (String styleCSS : style)
			scene.getStylesheets().add(styleCSS);
		stage.setScene(scene);
		return controller;
	}
	
	public static void closeWindow(Stage stage)
	{
		stage.close();
	}
	
	public static boolean validateString(String stringInput)
	{
		return stringInput != null && stringInput.length() >= 2;
	}
	
	public static ImageView Icon(int size, String path)
	{
		Integer temp=0;
		javafx.scene.image.Image headBranchIcon = new javafx.scene.image.Image(temp.getClass().getResourceAsStream("/Resources/main.png"));
		ImageView headBranchView = new ImageView(headBranchIcon);
		headBranchView.setFitWidth(size);
		headBranchView.setFitHeight(size);
		
		return headBranchView;
	}
	
//	public static <T> T secondaryStage(Stage stage, Stage secondaryStage, String path, String title, int minHeight, int minWidth, String[] style)
//	{
//		final Stage dialog = new Stage();
//		dialog.initModality(Modality.APPLICATION_MODAL);
//		dialog.initOwner(stage);
//		T controller = UIUtilz.setStage(path, dialog, style);
//		dialog.setMinHeight(minHeight);
//		dialog.setMinWidth(minWidth);
//		dialog.setTitle(title);
//		secondaryStage = dialog;
//		secondaryStage.show();
//
//		return controller;
//	}
}
