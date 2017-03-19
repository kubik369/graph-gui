package graphgui;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Trieda, ktorá má obsahovať implementáciu JavaFXovej úlohy A. V
 * tejto triede používajte iba metódy rozhraní Graph, Vertex a Edge,
 * nevolajte priamo žiadne metódy z iných tried balíčka
 * graphgui. Nemeňte hlavičku konštruktora ani public metód edit,
 * editEdge a editVertex, môžete však samozrejme zmeniť ich telo a
 * pridávať do triedy vlastné metódy, premenné, prípadne pomocné
 * triedy.
 */
public class Editor {

  // POMOCNÁ TRIEDA PRE UKÁŽKOVÝ PRÍKLAD, MEŇTE PODĽA POTREBY
  class MyStage extends Stage {
    Vertex vertex;

    MyStage(Vertex vertex) {
      this.vertex = vertex;
      MyStage dialog = this;
      GridPane pan = new GridPane();

      Color c = Color.web(vertex.getColorName());

      final TextField rText = new TextField((int)(c.getRed() * 255) + "");
      final TextField gText = new TextField((int)(c.getGreen() * 255) + "");
      final TextField bText = new TextField((int)(c.getBlue() * 255) + "");
      final Label rLabel = new Label("Red (0-255): ");
      final Label gLabel = new Label("Green (0-255): ");
      final Label bLabel = new Label("Blue (0-255): ");
      Button ok = new Button("OK");

      ok.setOnAction(new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
          try {
            double r = (double)Integer.parseInt(rText.getText());
            double g = (double)Integer.parseInt(gText.getText());
            double b = (double)Integer.parseInt(bText.getText());
            if (r >= 0 && g >= 0 && b >= 0 && r < 256 && g < 256 && b < 256) {
              vertex.setColorName(new Color(r / 255, g / 255, b / 255, 1).toString());
              dialog.close();
            }
          } catch (Exception e) {
            System.err.println("Problem s nastavenim farby");
          }
        }
      });

      pan.getColumnConstraints().add(new ColumnConstraints(100));
      pan.setRowIndex(rLabel, 0);
      pan.setColumnIndex(rLabel, 0);
      pan.setRowIndex(rText, 0);
      pan.setColumnIndex(rText, 1);
      pan.setRowIndex(gLabel, 1);
      pan.setColumnIndex(gLabel, 0);
      pan.setRowIndex(gText, 1);
      pan.setColumnIndex(gText, 1);
      pan.setRowIndex(bLabel, 2);
      pan.setColumnIndex(bLabel, 0);
      pan.setRowIndex(bText, 2);
      pan.setColumnIndex(bText, 1);
      pan.setRowIndex(ok, 3);
      pan.setColumnIndex(ok, 1);

      pan.getChildren().addAll(rLabel, rText, gLabel, gText, bLabel, bText, ok);

      Scene sc = new Scene(pan);
      this.setScene(sc);
    }
  }

  // PREMENNÉ TRIEDY, UPRAVTE SI PODĽA POTREBY
  private Graph graph;

  // KONŠTRUKTOR: NEMEŇTE HLAVIČKU, TELO UPRAVTE PODĽA POTREBY
  /** Konštruktor triedy, ktorý dostane graf. */
  public Editor(Graph graph) {
    this.graph = graph;
  }

  // METÓDA editVertex: NEMEŇTE HLAVIČKU, TELO UPRAVTE PODĽA POTREBY
  /**
   * Akcia, ktorá sa má vykonať v prípade, že je vybratý nejaký vrchol.
   * @param vertex vybraný vrchol
   */
  public void editVertex(Vertex vertex) {
    MyStage dialog = new MyStage(vertex);
    dialog.initStyle(StageStyle.UTILITY);
    dialog.initModality(Modality.APPLICATION_MODAL);
    dialog.show();
  }

  // METÓDA editEdge: NEMEŇTE HLAVIČKU, TELO UPRAVTE PODĽA POTREBY
  /**
   * Akcia, ktorá sa má vykonať v prípade, že je vybratá nejaká hrana.
   * @param edge vybraná hrana
   */
  public void editEdge(Edge edge) {
  }

  // METÓDA edit: NEMEŇTE HLAVIČKU, TELO UPRAVTE PODĽA POTREBY
  /** Akcia, ktorá sa má vykonať v prípade, že nie je vybratý žiadny vrchol. */
  public void edit() {
  }

}
