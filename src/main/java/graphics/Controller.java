package graphics;

import graphgui.GraphPane;
import graphgui.State;
import graphgui.enums.GraphMode;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;

/**
 * Created by siegrift on 3/25/17.
 */
public class Controller {

  public GraphPane gpGraph;
  public AnchorPane apRight;
  public AnchorPane apLeft;
  @FXML
  public Button btnView;
  public Button btnEditGraph;
  public Button btnEditValues;
  public Button btnDelete;
  @FXML
  public Button btnChangeVertexValues;
  @FXML
  public Button btnChangeEdgeValues;
  public TextField tfCommandLine;
  public Label labelStatus;
  public BorderPane root;
  public AnchorPane apMain;
  public Label labelVertex;
  public Label labelVertexNumber;
  public TextField tfVertexValue;
  public Label labelVertexColorClickable;
  public ComboBox cbFromVertex;
  public ComboBox cbToVertex;
  public TextField tfEdgeValue;
  public Label labelEdgeColorClickable;
  public MenuBar menuBar;
  public Menu menuFile;
  public Menu menuEdit;
  public Menu menuHelp;
  public MenuItem menuItemCloseProgram;

  @FXML
  private void initialize() {
    this.btnView.setStyle("-fx-background-color: yellow;");
    this.btnChangeVertexValues.setDisable(true);
    this.btnChangeEdgeValues.setDisable(true);
    this.tfVertexValue.setDisable(true);
    this.tfEdgeValue.setDisable(true);
    this.cbFromVertex.setDisable(true);
    this.cbToVertex.setDisable(true);
  }

  @FXML
  protected void changeMode(ActionEvent event) {
    Object source = event.getSource();

    this.btnView.setStyle("-fx-background-color: darkkhaki;");
    this.btnEditGraph.setStyle("-fx-background-color: darkkhaki;");
    this.btnEditValues.setStyle("-fx-background-color: darkkhaki;");
    this.btnDelete.setStyle("-fx-background-color: darkkhaki;");
    ((Button)source).setStyle("-fx-background-color: yellow;");

    State state = State.getState();
    state.setSelectedEdge(null);
    state.setSelectedVertex(null);
    if (source == this.btnView) {
      state.setMode(GraphMode.VIEW);
    } else if (source == this.btnEditGraph) {
      state.setMode(GraphMode.EDIT_GRAPH);
    } else if (source == this.btnEditValues) {
      state.setMode(GraphMode.EDIT_VALUES);
    } else if (source == this.btnDelete) {
      state.setMode(GraphMode.DELETE);
    }
  }
}
