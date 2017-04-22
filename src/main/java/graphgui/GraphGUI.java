package graphgui;

import graphics.Controller;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Scanner;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

/**
 * Hlavná trieda aplikácie - obsahuje dizajn a funkcionalitu ovládacích
 * prvkov hlavného okna aplikácie. Obsahuje nasledovné ovládacie prvky
 - Label infoLabel: aktuálna pozícia myši (súradnice alebo vrchol, na ktorom je myš)
 - Label selectedLabel: informácie o vybranom vrchole
 - GraphPane gpanel: grafický panel
 - ListView listView: zoznam hrán incidentných s vybraným vrcholom
 - Button del: gombík na mazanie vybratého vrchola
 - Button edel: gombík na mazanie vybratej hrany
 - Button ebtn: gombík na úlohu A
 - Button abtn: gombík na úlohu B
 - Menu menu1: menu File s prvkami Open, Save a Exit
 - BorderPane rpanel, GridPane panel, BorderPane root: panely na uloženie ovládacích prvkov
 */

@SuppressWarnings("checkstyle:AbbreviationAsWordInName")
public class GraphGUI extends Application {

  private final ExtendedGraph graph = new ExtendedGraph();
  private GraphPane graphPanel;

  /**
   * @param args the command line arguments.
   */
  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage primaryStage) {
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource("/graphics/layout.fxml"));
      Parent root = loader.load();
      Scene scene = new Scene(root, 850, 620);

      primaryStage.setMinWidth(400);
      primaryStage.setMinHeight(400);
      primaryStage.setTitle("Graph GUI");
      primaryStage.setScene(scene);
      primaryStage.show();
      primaryStage.setOnCloseRequest((e) -> System.exit(0));

      Controller controller = loader.getController();
      this.graphPanel = controller.gpGraph;
      this.graphPanel.init(controller, this.graph);

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /** Vykoná potrebné akcie po zmene vybranej hrany alebo vrchola alebo ich vlastností. */
  private void selectionUpdated() {
    /*Vertex selected = graph.getSelectedVertex();
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
    }*/
  }

  /**
   * Otvorenie súboru na načítanie a posunutie scannera grafu, aby sa načítal.
   * @param sf názov vstupného súboru
   * @throws java.io.IOException hádže pokiaľ nenájde súbor
   */
  private void openFile(String sf) throws java.io.IOException {
    Scanner s = new Scanner(new File(sf));
    try {
      this.graph.read(s);
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
    this.graph.print(out, true);
    out.close();
  }

  public ExtendedGraph getGraph() {
    return this.graph;
  }

  public GraphPane getGraphPanel() {
    return this.graphPanel;
  }
}
