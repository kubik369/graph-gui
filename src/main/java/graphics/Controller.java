package graphics;

import graphgui.Edge;
import graphgui.GraphAlgorithm;
import graphgui.GraphPane;
import graphgui.State;
import graphgui.Vertex;
import graphgui.enums.GraphMode;
import graphgui.utils.GraphLoader;
import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.AccessibleRole;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
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
  public TextField tfVertexNumber;
  public TextField tfVertexValue;
  public ComboBox cbVertexColor;
  public ComboBox cbEdgeColor;
  @FXML
  public TextField tfFromVertex;
  @FXML
  public TextField tfToVertex;
  public TextField tfEdgeValue;
  public MenuBar menuBar;
  public Menu menuFile;
  public Menu menuEdit;
  public Menu menuHelp;
  public MenuItem menuItemCloseProgram;
  public MenuItem menuItemLoad;
  public MenuItem menuItemSave;
  @FXML
  public TextArea taConsole;

  public static final ArrayList<String> VERTEX_COLORS
      = new ArrayList<>(Arrays.asList("black", "white", "green", "orange", "blue", "yellow"));
  public static final ArrayList<String> EDGE_COLORS
      = new ArrayList<>(Arrays.asList("black", "white", "green", "orange", "blue", "yellow"));

  @FXML
  private void initialize() {
    this.btnView.setStyle("-fx-background-color: yellow;");
    this.btnChangeVertexValues.setDisable(true);
    this.btnChangeEdgeValues.setDisable(true);
    this.tfVertexValue.setDisable(true);
    this.tfEdgeValue.setDisable(true);
    this.cbVertexColor.setDisable(true);
    this.cbVertexColor.getItems().addAll(VERTEX_COLORS);

    this.cbEdgeColor.setDisable(true);
    this.cbEdgeColor.getItems().addAll(EDGE_COLORS);
    this.menuItemSave.setAccelerator(new KeyCodeCombination(
        KeyCode.S,
        KeyCombination.CONTROL_DOWN
    ));
    this.menuItemLoad.setAccelerator(new KeyCodeCombination(
        KeyCode.O,
        KeyCombination.CONTROL_DOWN
    ));
    this.menuItemCloseProgram.setAccelerator(new KeyCodeCombination(
        KeyCode.Q,
        KeyCombination.CONTROL_DOWN
    ));
    Platform.runLater(() -> {
      this.tfCommandLine.requestFocus();
    });
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

  private Text stringToBoldText(String s) {
    Text t = new Text(s);
    t.setStyle("-fx-font-weight: bold;");
    return t;
  }

  /**
   * Displays an alert listing all supported command line commands.
   */
  @SuppressWarnings("checkstyle:LineLength")
  public void displayHelpAlert() {
    /* Takto by sa to robilo pre bold, unhighlightable
    (odkomentovat toto, zakomentovat zvysok, then you can look at it)
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle("Supported Commands");
    alert.setHeaderText(null);
    alert.setResizable(true);
    alert.getDialogPane().setMinWidth(750);
    DialogPane dialogPane = alert.getDialogPane();

    TextFlow flow = new TextFlow();

    flow.getChildren().add(stringToBoldText("Save (String) filename :\n"));
    flow.getChildren().add(new Text("Saves current graph to the file named filename, or filename.graph if filename does not have said extension.\n"));
    flow.getChildren().add(stringToBoldText("Load (String) filename :\n"));
    flow.getChildren().add(new Text("Loads graph from file named filename, or filename.graph if filename does not have said extension.\n"));
    dialogPane.setContent(flow);
    alert.showAndWait();
    */

    Alert alert = new Alert(AlertType.INFORMATION);
    alert.setTitle("Supported Commands");
    alert.setHeaderText(null);
    alert.setContentText(null);

    TextArea textArea = new TextArea();
    textArea.setEditable(false);
    textArea.setWrapText(true);
    textArea.setMaxWidth(Double.MAX_VALUE);
    textArea.setMaxHeight(Double.MAX_VALUE);
    alert.getDialogPane().setContent(textArea);
    alert.getDialogPane().setMinHeight(750);
    alert.getDialogPane().setMinWidth(750);

    textArea.appendText("Save (String) filename :\n");
    textArea.appendText("Saves current graph to the file named filename, or filename.graph if filename does not have said extension.\n");
    textArea.appendText("Load (String) filename :\n");
    textArea.appendText("Loads graph from file named filename, or filename.graph if filename does not have said extension.\n");
    textArea.appendText("Add Vertex (optional (double) x (double) y) :\n");
    textArea.appendText("Adds a new vertex to the graph. If no coordinates are given, vertices are added in a grid-like fashion spaced out on the panel.\n");
    textArea.appendText("Otherwise it adds a new vertex to the graph with the given coordinates.\n");
    textArea.appendText("Add Edge (int) from (int) to :\n");
    textArea.appendText("Adds an edge from the vertex with id from to vertex with id to.\n");
    textArea.appendText("Select Vertex (int) vertexId :\n");
    textArea.appendText("Selects the vertex with id vertexId.\n");
    textArea.appendText("Select Edge (int) from (int) to:\n");
    textArea.appendText("Selects edge with originId from and destinationId to.\n");
    textArea.appendText("Edit Vertex (int) vertexId (int) value :\n");
    textArea.appendText("Sets the value of vertex with id vertexId to value.\n");
    textArea.appendText("Edit Vertex (int) vertexId (String) color :\n");
    textArea.appendText("Sets the color of vertex with id vertexId to color.\n");
    textArea.appendText("Edit Edge (int) from (int) to (int) value :\n");
    textArea.appendText("Sets the value of edge with oridingId from and destinationId to to value.\n");
    textArea.appendText("Edit Edge (int) from (int) to (String) color :\n");
    textArea.appendText("Sets the color of edge with oridingId from and destinationId to to color.\n");
    textArea.appendText("Remove Vertex (int) vertexId :\n");
    textArea.appendText("Removes the vertex with id vertexId.\n");
    textArea.appendText("Remove Edge (int) from (int) to :\n");
    textArea.appendText("Removes edge with originId from and destinationId to.\n");
    textArea.appendText("Move (int) vertexId (double) x (double) y) :\n");
    textArea.appendText("Moves vertex with id vertexId vertexId to new coordinates (x, y).\n");
    textArea.appendText("Deselect Edge :\n");
    textArea.appendText("Deselects currently selected edge\n");
    textArea.appendText("Deselect Vertex : Deselects currently selected vertex\n");
    textArea.appendText("Run :\n");
    textArea.appendText("Runs controller.runGraphAlgorithm().\n");
    textArea.appendText("Dialog :\n");
    textArea.appendText("Runs controller.runDialog().\n");
    textArea.appendText("Exit :\n");
    textArea.appendText("Exits the application.\n");
    textArea.appendText("Help :\n");
    textArea.appendText("Displays this alert.");
    alert.showAndWait();
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
      this.tfVertexNumber.setText("N/A");
      this.tfVertexValue.setText("N/A");
      this.setDisableVertexValueFields(true);
      return;
    }
    Vertex v = State.getState().getSelectedVertex();
    this.tfVertexNumber.setText(Integer.toString(v.getIndex()));
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
    this.tfFromVertex.setText(Integer.toString(e.getOriginId()));
    this.tfToVertex.setText(Integer.toString(e.getDestinationId()));
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
      this.taConsole.appendText("Neplatný vstup!\n");
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
      this.taConsole.appendText("Neplatný vstup!\n");
    }
  }

  @FXML
  protected void commandLineTyped(KeyEvent event) {
    if (event.getCode() == KeyCode.ENTER) {
      String command = tfCommandLine.getText();
      this.tfCommandLine.clear();
      String[] tempTokens = command.split("\\s");
      ArrayList<String> tokens = new ArrayList<>();
      for (String token : tempTokens) {
        token.trim();
        if (token.length() != 0) {
          tokens.add(token);
        }
      }
      String result = this.gpGraph.executeCommand(tokens.toArray(new String[ tokens.size() ]));
      if (result != "ignore me") {
        this.appendTextArea(result + "\n");
      }
    }
  }

  /**
   * Spustí GraphAlgorithm() na graph od selectedVertex() a selectedEdge().
   * Vráti jeho výsledok.
   * @return String výsledok behu GraphAlgorithm()
   */
  public String runGraphAlgorithm() {
    try {
      GraphAlgorithm ga = new GraphAlgorithm(State.getState().getExtendedGraph(),
                                               State.getState().getSelectedVertex(),
                                               State.getState().getSelectedEdge());
      String result = ga.performAlgorithm();
      if (result != null) {
        return result;
      }
    } catch (Exception e) {
      return String.format("Vo funkcii v GraphAlgorithm bola chyba: %s", e.toString());
    }

    return "Result bol null";
  }

  public void runDialog() {
    //TODO: spustit druhy skuskovy algoritmus!
  }

  /**
  * Spusti GraphAlgorithm() a jeho vysledok zobrazi alertom.
  */
  public void btnGraphAlgorithm() {
    String result = runGraphAlgorithm();
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle("Graph Algorithm");
    alert.setHeaderText(null);
    alert.setResizable(false);
    alert.setContentText(result);
    alert.showAndWait();
  }

  public boolean validVertexColor(String color) {
    return this.VERTEX_COLORS.contains(color);
  }

  public boolean validEdgeColor(String color) {
    return this.EDGE_COLORS.contains(color);
  }

  public void appendTextArea(String text) {
    this.taConsole.appendText(text);
  }
}
