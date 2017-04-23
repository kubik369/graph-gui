package main.graphgui;

import java.io.File;
import java.io.PrintStream;
import java.util.Scanner;
import javafx.application.Application;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

/**
 * Hlavná trieda aplikácie - obsahuje dizajn a funkcionalitu ovládacích
 * prvkov hlavného oknaaplikácie. Obsahuje nasledovné ovládacie prvky
 * - Label infoLabel: aktuálna pozícia myši (súradnice alebo vrchol, na ktorom je myš)
 * - Label selectedLabel: informácie o vybranom vrchole
 * - MyPanel gpanel: grafický panel
 * - ListView listView: zoznam hrán incidentných s vybraným vrcholom
 * - Button btnDeleteVertex: gombík na mazanie vybratého vrchola
 * - Button btnDeleteEdge: gombík na mazanie vybratej hrany
 * - Button btnEdit: gombík na úlohu A
 * - Button btnAction: gombík na úlohu B
 * - Menu menuFile: menu File s prvkami Open, Save a Exit
 * - BorderPane rpanel, GridPane panel, BorderPane root: panely na uloženie ovládacích prvkov
 */

@SuppressWarnings("checkstyle:AbbreviationAsWordInName")
public class GraphGUI extends Application implements ExtendedGraph.GraphObserver {

  private static final ObservableList<Edge> data = FXCollections.observableArrayList();

  private static final int BTNSIZE = 100;
  private static final int WINDOW_WIDTH = 700;
  private static final int WINDOW_HEIGHT = 700;

  private final Label infoLabel = new Label("");
  private final Label selectedLabel = new Label("No selected vertex");

  private final Button btnDeleteVertex = new Button("Delete Vertex");
  private final Button btnDeleteEdge = new Button("Delete Edge");
  private final Button btnEdit = new Button("Edit");
  private final Button btnAction = new Button("Action");

  private final ExtendedGraph graph = new ExtendedGraph();
  private final ListView<Edge> listView = new ListView<Edge>(data);
  private final MyPanel gpanel = new MyPanel(this, graph);
  private final BorderPane rpanel = new BorderPane();
  private final GridPane panel = new GridPane();
  private final BorderPane root = new BorderPane();

  private final Menu menuFile = new Menu("File");

  @Override
  public void start(Stage primaryStage) {
    graph.addObserver(this);

    this.initMenu();

    listView.setEditable(false);
    listView.setMaxWidth(BTNSIZE);
    listView.setItems(data);
    listView.getSelectionModel().selectedItemProperty().addListener(
      (ObservableValue<? extends Edge> observable, Edge oldValue, Edge newValue) -> {
        if (newValue != null) {
          graph.selectEdge(newValue);
        } else {
          graph.deselectEdge();
        }
      }
    );

    panel.getColumnConstraints().add(new ColumnConstraints(BTNSIZE));
    GridPane.setRowIndex(btnDeleteVertex, 0);
    GridPane.setColumnIndex(btnDeleteVertex, 0);
    btnDeleteVertex.setMinWidth(BTNSIZE);
    GridPane.setRowIndex(btnDeleteEdge, 1);
    GridPane.setColumnIndex(btnDeleteEdge, 0);
    btnDeleteEdge.setMinWidth(BTNSIZE);
    GridPane.setRowIndex(btnEdit, 2);
    GridPane.setColumnIndex(btnEdit, 0);
    btnEdit.setMinWidth(BTNSIZE);
    GridPane.setRowIndex(btnAction, 3);
    GridPane.setColumnIndex(btnAction, 0);
    btnAction.setMinWidth(BTNSIZE);

    panel.getChildren().addAll(btnDeleteVertex, btnDeleteEdge, btnEdit,
                               btnAction);
    rpanel.setCenter(listView);
    rpanel.setTop(selectedLabel);
    rpanel.setBottom(panel);

    btnDeleteVertex.setOnAction((ActionEvent event) -> {
      try {
        graph.removeSelectedVertex();
      } catch (Exception e) {
        System.err.println("Nedá sa vymazať vrchol!");
      }
    });

    btnDeleteEdge.setOnAction((ActionEvent event) -> {
      try {
        graph.removeSelectedEdge();
      } catch (Exception e) {
        System.err.println("Nedá sa vymazať hrana!");
      }
    });

    btnEdit.setOnAction((ActionEvent event) -> {
      try {
        ExamSpecifics.edit(graph);
      } catch (Exception e) {
        System.err.println("Vo funkcii v Editor bola chyba: " + e.toString());
      }
    });

    btnAction.setOnAction((ActionEvent event) -> {
      try {
        GraphAlgorithm ga = new GraphAlgorithm((Graph)graph, graph.getSelectedVertex(),
                graph.getSelectedEdge());
        String result = ga.performAlgorithm();
        if (result != null) {
          Alert alert = new Alert(AlertType.INFORMATION);
          alert.setTitle("Information Dialog");
          alert.setHeaderText(null);
          alert.setContentText(result);
          alert.showAndWait();
        }
      } catch (Exception e) {
        System.err.println("Vo funkcii v GraphAlgorithm bola chyba: " + e.toString());
      }
    });

    root.setBottom(infoLabel);
    root.setCenter(gpanel);
    root.setRight(rpanel);

    Scene scene = new Scene(root);

    primaryStage.setTitle("GraphGUI");
    primaryStage.setWidth(this.WINDOW_WIDTH);
    primaryStage.setHeight(this.WINDOW_HEIGHT);
    primaryStage.setResizable(false);
    primaryStage.setScene(scene);
    primaryStage.show();
  }

  private void initMenu() {
    MenuBar menuBar = new MenuBar();
    menuBar.getMenus().add(menuFile);

    MenuItem miOpen = new MenuItem("Open");
    miOpen.setOnAction((ActionEvent event) -> {
      try {
        openFile(ExamSpecifics.getInFileName());
      } catch (java.io.IOException e) {
        System.err.println("Problem so suborom");
      }
    });

    MenuItem miSave = new MenuItem("Save");
    miSave.setOnAction((ActionEvent event) -> {
      try {
        saveFile(ExamSpecifics.getOutFileName());
      } catch (java.io.IOException e) {
        System.err.println("Problem so suborom");
      }
    });

    MenuItem miExit= new MenuItem("Exit");
    miExit.setOnAction((ActionEvent event) -> {
      System.exit(0);
    });

    menuFile.getItems().add(miOpen);
    menuFile.getItems().add(miSave);
    menuFile.getItems().add(miExit);

    root.setTop(menuBar);
  }

  /**
   * @param args the command line arguments.
   */
  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void vertexAdded(Vertex vertex) {
  }

  @Override
  public void edgeAdded(Edge edge) {
    if (edge.getOrigin() == graph.getSelectedVertex()) {
      data.add(edge);
    } else if (edge.getDestination() == graph.getSelectedVertex()) {
      data.add(edge.getReverse());
    }
  }

  @Override
  public void vertexRemoved(Vertex vertex) {
  }

  @Override
  public void edgeRemoved(Edge edge) {
    selectionUpdated();
  }

  @Override
  public void edgeSelected(Edge edge) {
    // ak by hranu zvolil iny mechanizmus, treba ju zvolit aj v listView
    if (edge == null) {
      throw new IllegalArgumentException("null edge cannot be selected");
    }
    int toSelect = data.indexOf(edge);
    if (toSelect < 0) {
      toSelect = data.indexOf(edge.getReverse());
    }
    if (toSelect < 0) {
      throw new IllegalArgumentException("edge cannot be selected");
    }
    if (!listView.getSelectionModel().isSelected(toSelect)) {
      listView.getSelectionModel().clearAndSelect(toSelect);
    }
  }

  @Override
  public void edgeDeselected(Edge edge) {
    // ak by hranu odzvolil iny mechanizmus, treba ju odzvolit aj v listView
    if (!listView.getSelectionModel().isEmpty()) {
      listView.getSelectionModel().clearSelection();
    }
  }

  @Override
  public void vertexSelected(Vertex vertex) {
    selectionUpdated();
  }

  @Override
  public void vertexDeselected(Vertex vertex) {
    selectionUpdated();
  }

  @Override
  public void edgeChanged(Edge edge) {
    selectionUpdated();
  }

  @Override
  public void vertexChanged(Vertex vertex) {
    selectionUpdated();
  }

  /** Vykoná potrebné akcie po zmene vybranej hrany alebo vrchola alebo ich vlastností. */
  private void selectionUpdated() {
    Vertex selected = graph.getSelectedVertex();
    Edge selectedEdge = graph.getSelectedEdge();
    data.clear();

    if (selected == null) {
      selectedLabel.setText("No selected vertex");
    } else {
      selectedLabel.setText("Selected vertex:" + selected.getId());
      int toSelect = -1;
      for (Edge e : selected.adjEdges()) {
        data.add(e);
        //ak je hrana e zvolena, zvol ju
        if (e == selectedEdge) {
          toSelect = data.size() - 1;
        }
      }
      listView.getSelectionModel().selectIndices(toSelect);
    }
  }

  /**
   * Otvorenie súboru na načítanie a posunutie scannera grafu, aby sa načítal.
   * @param sf názov vstupného súboru
   * @throws java.io.IOException hádže pokiaľ nenájde súbor
   */
  private void openFile(String sf) throws java.io.IOException {
    Scanner s = new Scanner(new File(sf));
    try {
      graph.read(s);
    } catch (Exception e) {
      Alert alert = new Alert(AlertType.INFORMATION);
      alert.setTitle("Information Dialog");
      alert.setHeaderText(null);
      alert.setContentText(e.getMessage());
      alert.showAndWait();
    }
    s.close();
    selectionUpdated();
  }

  /**
   * Otvorenie súboru na výpis a posunutie streamu grafu, aby sa vypísal.
   * @param s vstupný súbor
   * @throws java.io.IOException hádže pokiaľ sa nepodarí uložiť súbor.
   */
  private void saveFile(String s) throws java.io.IOException {
    PrintStream out = new PrintStream(s);
    graph.print(out, true);
    out.close();
  }

  /** Nastavenie textu s informáciou o pozícii myši. */
  public void setInfoLabelText(String s) {
    infoLabel.setText(s);
  }

  public void setSelectedLabelText(String s) {
    selectedLabel.setText(s);
  }
}
