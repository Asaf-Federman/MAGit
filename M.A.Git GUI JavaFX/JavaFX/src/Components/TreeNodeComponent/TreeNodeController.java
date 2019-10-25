package Components.TreeNodeComponent;

import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;



public class TreeNodeController
{
	
	@FXML
	private Button commitButton;
	
	@FXML
	private Circle CommitCircle;
	
	@FXML
	private Label commitTimeStampLabel;
	
	@FXML
	private Label messageLabel;
	
	@FXML
	private Label committerLabel;
	
	@FXML
	private Label branchLabel;
	
	@FXML
	private Separator branchSeperator;
	
	@FXML
	private GridPane buttonGridPane;
	
	
	private String encryptionKey;
	private SimpleStringProperty clickedValue;
	
	@FXML
	private void initialize()
	{
		GridPane.setMargin(CommitCircle, new Insets(0, 0, 0, -5));
		GridPane.setMargin(branchLabel, new Insets(0, 10, 0, 10));
	}
	
	
	public void setEncryptionKey(String encryptionKey)
	{
		this.encryptionKey=encryptionKey;
	}
	
	public void setCommitTimeStamp(String timeStamp) {
		commitTimeStampLabel.setText(timeStamp);
		commitTimeStampLabel.setTooltip(new Tooltip(timeStamp));
	}
	
	public void setCommitter(String committerName) {
		committerLabel.setText(committerName);
		committerLabel.setTooltip(new Tooltip(committerName));
	}
	
	public void setCommitMessage(String commitMessage) {
		messageLabel.setText(commitMessage);
		messageLabel.setTooltip(new Tooltip(commitMessage));
	}
	
	public int getCircleRadius() {
		return (int)CommitCircle.getRadius();
	}
	
	public void setProperty(SimpleStringProperty encryptionCodeOfClicked)
	{
		clickedValue=encryptionCodeOfClicked;
		commitButton.addEventHandler(ActionEvent.ACTION, event -> clickedValue.setValue(encryptionKey));
	}
	
	public String getEncryptionKey()
	{
		return encryptionKey;
	}
	
	
	public void setCSS(String styleName)
	{
		commitButton.getStyleClass().add(styleName);
	}
	
	public String getBranch()
	{
		return branchLabel.getText();
	}
	
	public void setBranch(String branchName)
	{
		branchLabel.setText(branchName);
		if(branchName.equals("No Branch"))
		{
			buttonGridPane.getChildren().removeAll(branchLabel,branchSeperator);
			ColumnConstraints col1 = new ColumnConstraints();
			col1.setPrefWidth(0);
			buttonGridPane.getColumnConstraints().add(1,col1);
		}
		branchLabel.setTooltip(new Tooltip(branchName));
	}
	
	public void setBranchColor(javafx.scene.paint.Color color)
	{
		branchLabel.setTextFill(color);
	}
}
