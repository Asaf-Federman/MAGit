package main;

import Components.AppWindowComponent.AppWindowController;
import javafx.application.Application;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.stage.Stage;
import logic.RepositoryManager;

public class Main extends Application
{
	@Override
	public void start(Stage primaryStage) throws Exception {
		SimpleBooleanProperty isRemote = new SimpleBooleanProperty(false);
		SimpleStringProperty remotePath=new SimpleStringProperty("");
		RepositoryManager magitLogic = new RepositoryManager(isRemote,remotePath);
		AppWindowController appWindowController= UIUtilz.setStage("/Components/AppWindowComponent/AppWindowFXML.fxml",primaryStage, "/CSS/table.css", "/CSS/menu1.css");
		primaryStage.setTitle("M.A.Git");
		primaryStage.getScene().getStylesheets().remove("/CSS/silver.css");
		primaryStage.setMinWidth(750);
		primaryStage.setMinHeight(500);
		appWindowController.setStage(primaryStage);
		appWindowController.setLogic(magitLogic);
		appWindowController.setIsRemote(isRemote);
		appWindowController.setRemotePath(remotePath);
		primaryStage.show();
	}
	
	public static void main(String[] args) {

		launch(args);
	}
}
