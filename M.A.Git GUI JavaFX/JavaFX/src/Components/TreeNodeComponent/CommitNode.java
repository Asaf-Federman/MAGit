package Components.TreeNodeComponent;

import com.fxgraph.cells.AbstractCell;
import com.fxgraph.graph.Graph;
import com.fxgraph.graph.IEdge;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;


public class CommitNode extends AbstractCell {

    private String encryptionKey;
    private String timestamp;
    private String committer;
    private String message;
    private String branchName;
    private TreeNodeController commitNodeController;
    private SimpleStringProperty clickEncrypt;
    private javafx.scene.paint.Color color;

    public CommitNode(String encryptionKey, String timestamp, String committer, String message, String branchName, SimpleStringProperty clickedEncrypt) {
        this.encryptionKey = encryptionKey;
        this.timestamp = timestamp;
        this.committer = committer;
        this.message = message;
        this.branchName = branchName;
        this.clickEncrypt=clickedEncrypt;
        color=null;
    }
    
    public void setColor(javafx.scene.paint.Color color)
    {
        this.color=color;
    }
    
    @Override
    public Region getGraphic(Graph graph) {
        try {

            FXMLLoader fxmlLoader = new FXMLLoader();
            URL url = getClass().getResource("commitNode.fxml");
            fxmlLoader.setLocation(url);
            GridPane root = fxmlLoader.load(url.openStream());

            commitNodeController = fxmlLoader.getController();
            commitNodeController.setCommitMessage(message);
            commitNodeController.setCommitter(committer);
            commitNodeController.setCommitTimeStamp(timestamp);
            commitNodeController.setBranch(branchName);
            commitNodeController.setEncryptionKey(encryptionKey);
            commitNodeController.setProperty(clickEncrypt);
            if(color!=null)
            {
                commitNodeController.setBranchColor(color);
            }

            return root;
        } catch (IOException e) {
            return new Label("Error when tried to create graphic node !");
        }
    }

    @Override
    public DoubleBinding getXAnchor(Graph graph, IEdge edge) {
        final Region graphic = graph.getGraphic(this);
        return graphic.layoutXProperty().add(commitNodeController.getCircleRadius());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CommitNode that = (CommitNode) o;

        return Objects.equals(timestamp, that.timestamp);
    }

    @Override
    public int hashCode() {
        return timestamp != null ? timestamp.hashCode() : 0;
    }
    
}
