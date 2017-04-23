package graphics;

import graphgui.Edge;
import graphgui.GraphPane;
import graphgui.State;
import graphgui.Vertex;
import graphgui.enums.GraphMode;
import graphgui.utils.GraphLoader;
import java.io.File;
import java.util.ArrayList;
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
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;


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
  public ComboBox cbVertexColor;
  public ComboBox cbEdgeColor;
  @FXML
  public Label labelFromVertex;
  @FXML
  public Label labelToVertex;
  public TextField tfEdgeValue;
  public Label labelEdgeColorClickable;
  public MenuBar menuBar;
  public Menu menuFile;
  public Menu menuEdit;
  public Menu menuHelp;
  public MenuItem menuItemCloseProgram;
  public MenuItem menuItemLoad;
  public MenuItem menuItemSave;

  @FXML
  private void initialize() {
    this.btnView.setStyle("-fx-background-color: yellow;");
    this.btnChangeVertexValues.setDisable(true);
    this.btnChangeEdgeValues.setDisable(true);
    this.tfVertexValue.setDisable(true);
    this.tfEdgeValue.setDisable(true);
    this.cbVertexColor.setDisable(true);
    this.cbVertexColor.getItems().addAll(
        "black", "white", "green", "orange", "blue", "yellow"
    );

    this.cbEdgeColor.setDisable(true);
    this.cbEdgeColor.getItems().addAll(
        "black", "white", "green", "orange", "blue", "yellow"
    );
  }

  @FXML
  protected void changeMode(ActionEvent event) {
    // Zrušiť výber hrany a vrcholu v GraphPane
    this.gpGraph.resetEdgeLine();
    this.gpGraph.getGraph().deselectVertex();
    this.gpGraph.getGraph().deselectEdge();

    // Resetnúť všetky tlačidlá módov na ľavom paneli
    this.btnView.setStyle("-fx-background-color: darkkhaki;");
    this.btnEditGraph.setStyle("-fx-background-color: darkkhaki;");
    this.btnEditValues.setStyle("-fx-background-color: darkkhaki;");
    this.btnDelete.setStyle("-fx-background-color: darkkhaki;");

    // Zvýrazniť tlačidlo novo vybratého módu
    Object source = event.getSource();
    ((Button)source).setStyle("-fx-background-color: yellow;");

    State state = State.getState();
    state.setSelectedEdge(null);
    state.setSelectedVertex(null);
    state.setAddingEdge(false);
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

  /**
   * Ukonci program.
   * @param actionEvent event
   */
  public void closeProgramHandler(ActionEvent actionEvent) {
    System.exit(0);
  }

  /**
   * Otvori dialog na ulozenie grafu.
   * @param actionEvent event
   */
  public void saveGraphHandler(ActionEvent actionEvent) {
    FileChooser chooser = new FileChooser();
    chooser.setTitle("Ulozit graf");
    chooser.getExtensionFilters().clear();
    chooser.getExtensionFilters().add(new ExtensionFilter("Grafovy subor", "*.graph"));
    File file = chooser.showSaveDialog(null);
    if (file != null) {
      if (!file.getName().endsWith(".graph")) {
        file = new File(file.getPath() + ".graph");
      }
      GraphLoader.saveGraph(State.getState().getExtendedGraph(), file);
    }
  }

  /**
   * Otvori dialog na nacitanie grafu.
   * @param actionEvent event
   */
  public void loadGraphHandler(ActionEvent actionEvent) {
    FileChooser chooser = new FileChooser();
    chooser.setTitle("Otvorit graf");
    chooser.getExtensionFilters().clear();
    chooser.getExtensionFilters().add(new ExtensionFilter("Grafovy subor", "*.graph"));
    File file = chooser.showOpenDialog(null);
    if (file != null) {
      GraphLoader.loadGraph(file);
    }
  }

  /**
   * Zmení stav prvkov o vrchole v pravom paneli.
   * @param b nový stav podaní .setDisable
   */
  @FXML
  public void setDisableVertexValueFields(boolean b) {
    this.btnChangeVertexValues.setDisable(b);
    this.tfVertexValue.setDisable(b);
    this.cbVertexColor.setDisable(b);
  }

  /**
   * Zmení stav prvkov o hrane v pravom paneli.
   * @param b nový stav podaní .setDisable
   */
  @FXML
  public void setDisableEdgeValueFields(boolean b) {
    this.btnChangeEdgeValues.setDisable(b);
    this.tfEdgeValue.setDisable(b);
    this.cbEdgeColor.setDisable(b);
  }

  /**
   * Vyplní prvky o vrchole informáciami vybraného vrcholu.
   */
  public void fillVertexFields() {
    if (State.getState().getSelectedVertex() == null) {
      this.labelVertexNumber.setText("N/A");
      this.tfVertexValue.setText("N/A");
      this.setDisableVertexValueFields(true);
      return;
    }
    Vertex v = State.getState().getSelectedVertex();
    this.labelVertexNumber.setText(Integer.toString(v.getIndex()));
    this.tfVertexValue.setText(Integer.toString(v.getValue()));
    this.cbVertexColor.getSelectionModel().select(v.getColorName());
  }

  /**
   * Vyplní prvky o hrane s informáciami o vybranej hrane.
   */
  public void fillEdgeFields() {
    if (State.getState().getSelectedEdge() == null) {
      this.setDisableEdgeValueFields(true);
      return;
    }

    Edge e = State.getState().getSelectedEdge();
    this.labelFromVertex.setText(Integer.toString(e.getOriginId()));
    this.labelToVertex.setText(Integer.toString(e.getDestinationId()));
    this.cbEdgeColor.getSelectionModel().select(e.getColorName());
    this.tfEdgeValue.setText(Integer.toString(e.getValue()));
  }

  /**
   * Zoberie nové informácie o vrchole z pravého panelu a nastaví ich
   * vybratému vrcholu.
   */
  @FXML
  public void updateVertexValues() {
    if (State.getState().getSelectedVertex() == null) {
      return;
    }
    try {
      Vertex v = State.getState().getSelectedVertex();
      v.setValue(Integer.parseInt(this.tfVertexValue.getText().trim()));
      v.setColorName((String)this.cbVertexColor.getSelectionModel().getSelectedItem());
    } catch (NumberFormatException e) {
      // TODO pridať správu o neplatnom vstupe do text area
    }
  }

  /**
   * Zoberie nové informácie o hrane z pravého panelu a nastaví ich
   * vybratej hrane.
   */
  @FXML
  public void updateEdgeValues() {
    if (State.getState().getSelectedEdge() == null) {
      return;
    }
    try {
      Edge e = State.getState().getSelectedEdge();
      e.setValue(Integer.parseInt(this.tfEdgeValue.getText().trim()));
      System.out.println((String)this.cbEdgeColor.getSelectionModel().getSelectedItem());
      e.setColorName((String)this.cbEdgeColor.getSelectionModel().getSelectedItem());
      State.getState().getExtendedGraph().deselectEdge();
    } catch (NumberFormatException e) {
      // TODO pridať správu o neplatnom vstupe do text area
    }
  }
}
