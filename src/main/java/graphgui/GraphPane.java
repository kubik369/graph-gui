package graphgui;

import graphgui.enums.GraphMode;
import graphgui.utils.GraphicsHelpers;
import graphics.Controller;
import java.util.HashMap;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import javafx.util.Pair;

/** Trieda pre grafický panel, na ktorom umiestňujeme nakreslenie grafu. */
public class GraphPane extends Pane implements ExtendedGraph.GraphObserver {
  private static final int EDGE_WIDTH = 4;
  
  private Controller controller;
  private ExtendedGraph graph;
  private final Line edgeLine;
  private HashMap<Vertex, Text> vertexTexts;
  private double nextVertexX = 20;
  private double nextVertexY = 20;

  /**
   * Inicializuje grafový panel.
   */
  public GraphPane() {
    this.edgeLine = new Line();
    vertexTexts = new HashMap<>();
    this.setOnMouseMoved((MouseEvent e) -> {
      Vertex v = State.getState().getSelectedVertex();
      if (State.getState().getMode() == GraphMode.EDIT_GRAPH && v != null) {
        State.getState().setAddingEdge(true);
        this.edgeLine.setStartX(v.getX() + v.getSize() / 2);
        this.edgeLine.setStartY(v.getY() + v.getSize() / 2);
        this.edgeLine.setEndX(e.getX());
        this.edgeLine.setEndY(e.getY());
      }
    });
    this.getChildren().add(this.edgeLine);

    this.setMinSize(400, 400);
    this.setBackground(new Background(new BackgroundFill(
        Color.LIGHTGRAY, CornerRadii.EMPTY, Insets.EMPTY
    )));
  }

  /**
   * Inicializácia po naloadovaní FXML.
   * @param controller Controller okna aplikácie
   * @param graph Objekt držiaci informácie o grafe
   */
  public void init(Controller controller, ExtendedGraph graph) {
    this.controller = controller;
    this.graph = graph;
    this.graph.addObserver(this);

    this.setOnMouseClicked((MouseEvent event) -> {
      if (State.getState().getMode() == GraphMode.EDIT_GRAPH
          && !State.getState().isAddingEdge()) {
        this.graph.addVertex(event.getX(), event.getY());
      } else if (State.getState().getMode() == GraphMode.EDIT_VALUES) {
        this.controller.setDisableVertexValueFields(true);
        this.graph.deselectEdge();
        this.graph.deselectVertex();
      }
    });
  }

  /**
   * Pridá vrchol do panelu.
   * @param vertex Pridaný vrchol
   * @throws IllegalArgumentException ak vrchol nebol korektný
   */
  @Override
  public void vertexAdded(Vertex vertex) throws IllegalArgumentException {
    Circle circle = (Circle) graph.getVertexShape(vertex);

    circle.setOnMouseClicked((MouseEvent event) -> {
      event.consume();
      State state = State.getState();

      if (state.getMode() == GraphMode.DELETE) {
        System.out.println("Deleting vertex");
        this.graph.removeVertex(vertex);
      } else if (state.getMode() == GraphMode.EDIT_GRAPH) {
        System.out.println("Clicked on Vertex\n" + vertex + "\n" + state.getSelectedVertex());
        if (state.getSelectedVertex() != null && state.getSelectedVertex() != vertex) {
          this.graph.addEdgeFromSelected(vertex);
          this.graph.deselectVertex();
        } else {
          this.graph.toggleVertexSelection(vertex);
        }
      } else if (state.getMode() == GraphMode.EDIT_VALUES || state.getMode() == GraphMode.VIEW) {
        this.graph.toggleVertexSelection(vertex);
      }
    });
    circle.setCursor(Cursor.HAND);
    circle.setOnMouseMoved((MouseEvent event) -> {
      // FIXME Find out what needs to be done here
      //gui.setInfoLabelText("Vertex " + vertex.toString());
      event.consume();
    });
    this.getChildren().add(circle);
    Text text = GraphicsHelpers.createBoundedText(
        String.valueOf(State.getState().nextVertexId()),
        new Rectangle(
            circle.getCenterX() - circle.getRadius() / 2,
            circle.getCenterY() - circle.getRadius() / 2,
            circle.getRadius(),
            circle.getRadius()),
        Color.BLACK);
    vertexTexts.put(vertex, text);
    text.setPickOnBounds(true);
    text.setMouseTransparent(true);
    this.getChildren().add(text);
  }

  /**
   * Pridá hranu do panelu.
   * @param edge Pridaná hrana
   * @throws IllegalArgumentException ak hrana nebola korektná
   */
  @Override
  public void edgeAdded(Edge edge) throws IllegalArgumentException {
    try {
      Shape s = graph.getEdgeShape(edge);
      s.setStyle(String.format("-fx-stroke: %s;", edge.getColorName()));
      s.setStrokeWidth(this.EDGE_WIDTH);
      s.setOnMouseClicked((MouseEvent event) -> {
        event.consume();
        GraphMode mode = State.getState().getMode();
        if (mode == GraphMode.EDIT_VALUES) {
          this.graph.toggleEdgeSelection(edge);
        } else if (mode == GraphMode.DELETE) {
          this.graph.removeEdge(edge);
        }
      });
      s.setCursor(Cursor.HAND);
      this.getChildren().add(s);
      s.toBack();
    } catch (Exception ex) {
      throw new IllegalArgumentException("edgeAdded");
    }
  }

  /**
   * Zmaže vrchol z panelu.
   * @param vertex Zmazaný vrchol
   * @throws IllegalArgumentException ak vrchol nebol korektný
   */
  @Override
  public void vertexRemoved(Vertex vertex) throws IllegalArgumentException {
    try {
      this.getChildren().remove(graph.getVertexShape(vertex));
      this.getChildren().remove(vertexTexts.get(vertex));
    } catch (Exception ex) {
      System.err.println("vertexRemoved");
      throw new IllegalArgumentException();
    }
  }

  /**
   * Zmaže hranu z panelu.
   * @param edge Zmazaná hrana
   * @throws IllegalArgumentException ak hrana nebola korektná alebo graf je nekonzistentný
   */
  @Override
  public void edgeRemoved(Edge edge) throws IllegalArgumentException {
    try {
      this.getChildren().remove(graph.getEdgeShape(edge));
    } catch (Exception ex) {
      throw new IllegalArgumentException("edgeRemoved");
    }
  }

  /**
   * Akcia po zvolení hrany.
   * @param edge Zvolená hrana
   */
  @Override
  public void edgeSelected(Edge edge) {
    System.out.println("Edge selected " + edge);
    this.controller.setDisableEdgeValueFields(false);
    this.controller.fillEdgeFields();
  }

  /**
   * Akcia po zrušení voľby hrany.
   * @param edge Odvolená hrana
   */
  @Override
  public void edgeDeselected(Edge edge) {
    this.controller.fillEdgeFields();
  }

  /**
   * Akcia po zvolení vrcholu.
   * @param vertex Zvolený vrchol
   */
  @Override
  public void vertexSelected(Vertex vertex) {
    System.out.println("Vertex selected");
    if (State.getState().getMode() == GraphMode.EDIT_VALUES) {
      this.controller.setDisableVertexValueFields(false);
      this.controller.fillVertexFields();
    } else if (State.getState().getMode() == GraphMode.VIEW) {
      this.controller.fillVertexFields();
    }
  }

  /**
   * Akcia po zrušení voľby vrcholu.
   * @param vertex Odvolený vrchol
   */
  @Override
  public void vertexDeselected(Vertex vertex) {
    this.resetEdgeLine();
    this.controller.fillVertexFields();
  }

  /**
   * Akcia po zmene vlastností hrany.
   * @param edge Zmenená hrana
   */
  @Override
  public void edgeChanged(Edge edge) {
  }

  /**
   * Akcia po zmene vlastností vrcholu.
   * @param vertex Zmenený vrchol
   */
  @Override
  public void vertexChanged(Vertex vertex) {
  }
  /**
   * Vráti Shape čiary reprezentujúcej novú hranu.
   * @return Shape editovacej hrany
   */
  public Line getEdgeLine() {
    return edgeLine;
  }

  /**
   * "Zmaže" Shape novej editovacej hrany posunutím do rohu
   * a nastavením dĺžky na 0.
   */
  public void resetEdgeLine() {
    this.edgeLine.setStartX(0);
    this.edgeLine.setStartY(0);
    this.edgeLine.setEndX(0);
    this.edgeLine.setEndY(0);
  }

  public ExtendedGraph getGraph() {
    return graph;
  }
  
  /** Vráti súradnice na zobrazenie ďalšieho vrcholu pre konzolový príkaz
   * 'add vertex' bez zadaných súradníc.
   * @return Pair vhodné súradnice na zobrazenie vrcholu
   */
  public Pair nextVertexCoordinates() {
    
    if (this.nextVertexX == -1) {
      return new Pair(10.0, 10.0);
    }
      
    final Pair result = new Pair(this.nextVertexX, this.nextVertexY);
      
    this.nextVertexX += 50;
      
    if (this.nextVertexX >= this.getWidth() - 20) {
      this.nextVertexX = 20;
      this.nextVertexY += 50;
    }
      
    if (this.nextVertexY >= this.getHeight() - 20) {
      this.nextVertexX = -1;
    }
    
    return result;
  }
  
  /**
   * Zoberie popis jedneho prikazu ako pole Stringov a vykoná daný príkaz,
   * a jeho výsledok vypíše na controller.taTextArea.
   * @param tokens jeden príkaz reprezentovaný poľom Stringov.
   */
  public void executeCommand(String[] tokens) {
    //Prázdny príkaz je pravdepodobne len omylom stlačený enter - ignorujeme takýto pokus.
    if (tokens.length == 0) {
      return;
    }
      
    //Skonvertujeme príkazy na malé písmená kvôli zjedno
    for (int i = 0;i < tokens.length;i++) {
      tokens[i] = tokens[i].toLowerCase();
    }

    switch (tokens[0]) {
      case "save": {

        if (tokens.length != 2) {
          this.controller.appendTextArea("Nespravny pocet parametrov pre save\n");
          break;
        }

        //TODO: zavolat funkciu 'Save' ktora je v menu, nazov suboru = tokens[1]
        if (true) { // ak sa podarilo
          this.controller.appendTextArea(String.format("Graf ulozeny v subore %s\n", tokens[1]));
        } else {
          this.controller.appendTextArea(
              String.format("Graf sa nepodarilo ulozit v subore %s\n", tokens[1])
          );
        }

        break;
      }
      case "load": {

        if (tokens.length != 2) {
          this.controller.appendTextArea("Nespravny pocet parametrov pre load\n");
          break;
        }

        //TODO: zavolat funkciu 'Load' ktora je v menu, nazov suboru = tokens[1]
        if (true) { // ak sa podarilo
          this.controller.appendTextArea(String.format("Graf nacitany zo suboru %s\n", tokens[1]));
        } else {
          this.controller.appendTextArea(
              String.format("Graf sa nepodarilo nacitat zo suboru %s\n", tokens[1])
          );
        }

        break;
      }
      case "add": {

        if (tokens.length < 2) {
          this.controller.appendTextArea("Nespravny pocet parametrov pre add\n");
          break;
        }

        switch (tokens[1]) {
          case "edge": {

            if (tokens.length < 4) {
              this.controller.appendTextArea("Nespravny pocet parametrov pre add edge\n");
              break;
            }
            int from;
            int to;
            try {
              from = Integer.parseInt(tokens[2]);
              to = Integer.parseInt(tokens[3]);
            } catch (NumberFormatException e) {
              this.controller.appendTextArea(
                  "Parametre pre add edge maju nespravny format - ocakavaju sa dve cele cisla\n"
              );
              break;
            }

            try {
              this.graph.addEdge(from, to);
            } catch (IllegalArgumentException e) {
              this.controller.appendTextArea(String.format("%s\n", e.toString()));
              break;
            } catch (IndexOutOfBoundsException e) {
              this.controller.appendTextArea("Neplatny index vrcholu From alebo To\n");
              break;
            }
            this.controller.appendTextArea(
                String.format("Hrana from %d to %d uspesne pridana\n", from, to)
            );
            break;
          }
          case "vertex": {

            if (tokens.length == 2) { 
              //niesu pritomne suradnice, vygeneruje sa podla nextVertexCoordinates()
              Pair coordinates = nextVertexCoordinates();
              double x = (double) coordinates.getKey();
              double y = (double) coordinates.getValue();
              this.graph.addVertex(x, y);
              this.controller.appendTextArea(
                  String.format(
                      "Vrchol %d uspesne pridany na suradnice (%f,%f)\n", 
                      this.graph.getNumberOfVertices() - 1 , x, y
                  )
              );
              break;
            }

            if (tokens.length != 4) {
              this.controller.appendTextArea(
                  "Add vertex pozaduje bud 0 alebo 2 argumenty typu double\n"
              );
              break;
            }

            double x;
            double y;
            try {
              x = Double.parseDouble(tokens[2]);
              y = Double.parseDouble(tokens[3]);
            } catch (NumberFormatException e) {
              this.controller.appendTextArea(
                  "Parametre add vertex neboli v spravnom formate double\n"
              );
              break;
            }

            if (x > this.getWidth() - 20 || x < 0 || y > this.getHeight() || y < 0) {
              this.controller.appendTextArea(
                  "Add vertex suradnice boli mimo grafickeho panelu\n"
              );
              break;
            }

            this.graph.addVertex(x, y);
            this.controller.appendTextArea(
                String.format(
                    "Vrchol %d uspesne pridany na suradnice (%f,%f)\n", 
                    this.graph.getNumberOfVertices() - 1 , x, y
                )
            );

            break;
          }
          default: {
            this.controller.appendTextArea(
                String.format(
                    "Druhy parameter add moze byt 'vertex' alebo 'edge', nie '%s'\n", 
                    tokens[1]
                )
            );
            break;
          }
        }

        break;
      }
      case "edit": {
        //TODO po mergi s edit vecami
        break;
      }
      case "select": {
        //TODO po mergi s edit vecami
        break;
      }
      case "remove": {
        if (tokens.length < 2) {
          this.controller.appendTextArea("Nespravny pocet parametrov pre delete\n");
          break;
        }

        switch (tokens[1]) {
          case "edge": {
            if (tokens.length < 4) {
              this.controller.appendTextArea("Nespravny pocet parametrov pre remove edge\n");
              break;
            }
              
            int from;
            int to;
            try {
              from = Integer.parseInt(tokens[2]);
              to = Integer.parseInt(tokens[3]);
            } catch (NumberFormatException e) {
              this.controller.appendTextArea(
                  "Parametre pre remove edge maju nespravny format - ocakavaju sa dve cele cisla\n"
              );
              break;
            }

            try {
              this.graph.removeEdge(from, to);
            } catch (IllegalArgumentException e) {
              this.controller.appendTextArea(String.format("%s\n", e.toString()));
              break;
            } catch (IndexOutOfBoundsException e) {
              this.controller.appendTextArea("Neplatny index vrcholu From alebo To\n");
              break;
            }
              
            this.controller.appendTextArea(
                String.format("Hrana from %d to %d uspesne odstranena\n", from, to)
            );                      
            break;
          }
          case "vertex": {

            if (tokens.length != 3) {
              this.controller.appendTextArea(
                  String.format("delete vertex ocakava 1 parameter, nasiel %d\n", tokens.length - 2)
              );
              break;
            }

            int vertexId;

            try {
              vertexId = Integer.parseInt(tokens[2]);
            } catch (NumberFormatException e) {
              this.controller.appendTextArea(
                  String.format("delete vertex ocakava int vertexId, nasiel %s\n", tokens[2])
              );
              break;
            }

            try {
              this.graph.removeVertex(this.graph.getVertex(vertexId));
              this.controller.appendTextArea(
                  String.format("Vrchol %d uspesne zmazany\n", vertexId)
              );
            } catch (IndexOutOfBoundsException e) {
              this.controller.appendTextArea(String.format("vertexId %d je neplatne\n", vertexId));
            }
            break;
          }
          default: {
            this.controller.appendTextArea(
                String.format(
                    "Druhy parameter delete moze byt 'vertex' alebo 'edge', nie '%s'\n",
                    tokens[1]
                )
            );
            break;
          }
        }

        break;
      }
      case "move": {
        if (tokens.length != 4) {
          this.controller.appendTextArea(
              "move vyzaduje parametre int vertexId double newX double newY\n"
          );
          break;
        }

        int vertexId;
        double newX;
        double newY;
        try {
          vertexId = Integer.parseInt(tokens[1]);
          newX = Double.parseDouble(tokens[2]);
          newY = Double.parseDouble(tokens[3]);
        } catch (NumberFormatException e) {
          this.controller.appendTextArea("move parametre niesu v spravnom formate\n");
          break;
        }

        if (newX < 0 || newY < 0 || newX > this.getWidth() - 20 || newY > this.getHeight() - 20) {
          this.controller.appendTextArea("move suradnice mimo grafickeho panelu\n");
          break;
        }

        try {
          this.graph.getVertex(vertexId).setX(newX);
          this.graph.getVertex(vertexId).setY(newY);
        } catch (IndexOutOfBoundsException e) {
          this.controller.appendTextArea(String.format("Neplatny vertexId %d\n", vertexId));
          break;
        }
        this.controller.appendTextArea(
            String.format(
                "Vrchol %d uspesne presunuty na suradnice (%f, %f)\n", 
                vertexId, newX, newY
            )
        );
        break;
      }
      case "deselect": {
        break;
      }
      case "run": {
        this.controller.appendTextArea(String.format("%s\n", this.controller.runGraphAlgorithm()));
        break;
      }
      
      case "help": {
        //TODO: po dokonceni vsetkych prikazov pridat manual
        break;
      }
      case "exit": {
        //TODO: zavolat Close z menu (v mojom branchi este nieje ten event handler)
        break;
      }
      default: {
        this.controller.appendTextArea(
            String.format("Neznamy prikaz '%s'; skus prikaz 'help'\n", tokens[0])
        );
      }
    }  
  }
}
