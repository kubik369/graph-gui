package graphgui;

import graphgui.enums.GraphMode;
import graphgui.utils.GraphLoader;
import graphgui.utils.GraphicsHelpers;
import graphics.Controller;
import java.io.File;
import java.util.HashMap;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.scene.Cursor;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
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
        moveLine(e, v);
      }
    });
    this.getChildren().add(this.edgeLine);

    this.setMinSize(400, 400);
    this.setBackground(new Background(new BackgroundFill(
        Color.LIGHTGRAY, CornerRadii.EMPTY, Insets.EMPTY
    )));
    this.toBack();
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
   * Prekresli ciaru aby viedla od suradnic vrchola po poziciu mysky.
   * @param e event obsahujuci pozicie mysky
   * @param v ak je null, tak zmenime iba koncove body, inak aj zaciatocne
   */
  public void moveLine(MouseEvent e, Vertex v) {
    if (v != null) {
      this.edgeLine.setStartX(v.getX() + v.getSize() / 2);
      this.edgeLine.setStartY(v.getY() + v.getSize() / 2);
    }
    this.edgeLine.setEndX(e.getX());
    this.edgeLine.setEndY(e.getY());
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
      if (State.getState().isAddingEdge()) {
        moveLine(event, null);
      }
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
        if (mode == GraphMode.EDIT_VALUES || mode == GraphMode.VIEW) {
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
    if (State.getState().getMode() == GraphMode.EDIT_VALUES) {
      this.controller.setDisableEdgeValueFields(false);
      this.controller.fillEdgeFields();
    } else if (State.getState().getMode() == GraphMode.VIEW) {
      this.controller.fillEdgeFields();
    }
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
   * a vráti jeho výsledok.
   * @param tokens jeden príkaz reprezentovaný poľom Stringov.
   * @return výsledok pokusu vykonania príkazu tokens.
   */
  public String executeCommand(String[] tokens) {
    // Prázdny príkaz je pravdepodobne len omylom stlačený enter - ignorujeme takýto pokus.
    if (tokens.length == 0) {
      return "ignore me";
    }

    //Skonvertujeme príkazy na malé písmená kvôli zjednoteniu
    for (int i = 0;i < tokens.length;i++) {
      tokens[i] = tokens[i].toLowerCase();
    }

    switch (tokens[0]) {
      case "save": {
        if (tokens.length != 2) {
          return "Nespravny pocet parametrov pre save";
        }

        String filename = new String(tokens[1]);
        if (filename.endsWith(".graph") == false) {
          filename = new String(filename + ".graph");
        }

        File file = new File(filename);
        if (GraphLoader.saveGraph(this.graph, file)) {
          return String.format("Graf ulozeny v subore %s", tokens[1]);
        } else {
          return String.format("Graf sa nepodarilo ulozit v subore %s", tokens[1]);
        }
      }
      case "load": {
        if (tokens.length != 2) {
          return "Nespravny pocet parametrov pre load";
        }

        String filename = new String(tokens[1]);
        if (filename.endsWith(".graph") == false) {
          filename = new String(filename + ".graph");
        }

        File file = new File(filename);
        if (GraphLoader.loadGraph(file)) {
          return String.format("Graf nacitany zo suboru %s", tokens[1]);
        } else {
          return String.format("Graf sa nepodarilo nacitat zo suboru %s", tokens[1]);
        }
      }
      case "add": {
        if (tokens.length < 2) {
          return "Nespravny pocet parametrov pre add";
        }

        switch (tokens[1]) {
          case "edge": {
            if (tokens.length != 4) {
              return "Nespravny pocet parametrov pre add edge";
            }
            int from;
            int to;
            try {
              from = Integer.parseInt(tokens[2]);
              to = Integer.parseInt(tokens[3]);
              this.graph.addEdge(from, to);
            } catch (NumberFormatException e) {
              return "Parametre pre add edge maju nespravny format - ocakavaju sa dve cele cisla";
            } catch (IllegalArgumentException e) {
              return e.toString();
            } catch (IndexOutOfBoundsException e) {
              return "Neplatny index vrcholu From alebo To";
            }

            return String.format("Hrana from %d to %d uspesne pridana", from, to);
          }
          case "vertex": {
            if (tokens.length == 2) {
              //niesu pritomne suradnice, vygeneruje sa podla nextVertexCoordinates()
              Pair coordinates = nextVertexCoordinates();
              double x = (double) coordinates.getKey();
              double y = (double) coordinates.getValue();
              this.graph.addVertex(x, y);
              return String.format(
                        "Vrchol %d uspesne pridany na suradnice (%f,%f)",
                        this.graph.getNumberOfVertices() - 1 , x, y
                    );
            }

            if (tokens.length != 4) {
              return "Add vertex pozaduje bud 0 alebo 2 argumenty typu double";
            }

            double x;
            double y;
            try {
              x = Double.parseDouble(tokens[2]);
              y = Double.parseDouble(tokens[3]);
            } catch (NumberFormatException e) {
              return "Parametre add vertex neboli v spravnom formate double";
            }

            if (x > this.getWidth() - 20 || x < 0 || y > this.getHeight() || y < 0) {
              return "Add vertex suradnice boli mimo grafickeho panelu";
            }

            this.graph.addVertex(x, y);
            return  String.format(
                      "Vrchol %d uspesne pridany na suradnice (%f,%f)",
                      this.graph.getNumberOfVertices() - 1 , x, y
                    );
          }
          default: {
            return  String.format(
                     "Druhy parameter add moze byt 'vertex' alebo 'edge', nie '%s'",
                      tokens[1]
                    );
          }
        }
      }
      case "edit": {
        // TODO: selected edge/vertex, when edited, change values in right menu
        if (tokens.length < 4) {
          return "edit musi mat aspon styri parametre";
        }

        switch (tokens[1]) {
          case "vertex": {
            if (tokens.length != 4) {
              return  "edit vertex pozaduje presne dva parametre";
            }

            int index;
            Vertex v;
            try {
              index = Integer.parseInt(tokens[2]);
              v = this.graph.getVertex(index);
            } catch (NumberFormatException e) {
              return  String.format("Index vrcholu '%s' v nespravnom formate", tokens[1]);
            } catch (IndexOutOfBoundsException e) {
              return  "Neplatny index vrcholu";
            }

            try {
              int value = Integer.parseInt(tokens[3]);
              v.setValue(value);
              this.graph.deselectVertex();
              this.graph.selectVertex(v);
              return  String.format("Hodnota vrcholu %d uspesne zmenena na %d", index, value);
            } catch (NumberFormatException e) {
              if (this.controller.validVertexColor(tokens[3])) {
                v.setColorName(tokens[3]);
                this.graph.deselectVertex();
                this.graph.selectVertex(v);
                return String.format("Farba vrcholu %d uspesne zmenena na %s", index, tokens[3]);
              } else {
                return String.format(
                            "Stvrty parameter edit vertex ma byt cele cislo"
                            + " alebo platny nazov farby, nie %s",
                            tokens[3]
                        );
              }
            }
          }
          case "edge": {
            if (tokens.length != 5) {
              return "edit edge pozaduje presne 5 parametrov";
            }

            int from;
            int to;
            Edge chosenEdge = null;
            try {
              from = Integer.parseInt(tokens[2]);
              to = Integer.parseInt(tokens[3]);
              Vertex originVertex = this.graph.getVertex(from);
              for (Edge adjEdge : originVertex.adjEdges()) {
                if (adjEdge.getDestinationId() == to) {
                  chosenEdge = adjEdge;
                }
              }

              if (chosenEdge == null) {
                throw new IllegalArgumentException(
                  String.format("Hrana %d %d neexistuje", from, to)
                );
              }

            } catch (NumberFormatException e) {
              return  "Indexy pre edit edge maju nespravny format - ocakavaju sa dve cele cisla";

            } catch (IllegalArgumentException e) {
              return  e.toString();
            } catch (IndexOutOfBoundsException e) {
              return  "Neplatny index vrcholu From alebo To";
            }

            chosenEdge = chosenEdge.getPrimary();

            try {
              int value = Integer.parseInt(tokens[4]);
              chosenEdge.setValue(value);
              this.graph.deselectEdge();
              this.graph.selectEdge(chosenEdge);
              return  String.format("Hodnota hrany %d %d uspesne zmenena na %d", from, to, value);
            } catch (NumberFormatException e) {
              if (this.controller.validVertexColor(tokens[4])) {
                chosenEdge.setColorName(tokens[4]);
                this.graph.deselectEdge();
                this.graph.selectEdge(chosenEdge);
                return  String.format(
                            "Farba hrany %d %d uspesne zmenena na %s", from, to, tokens[4]
                        );
              } else {
                return String.format(
                          "Piaty parameter edit edge ma byt cele cislo alebo platny nazov farby,"
                          + " nie %s",
                          tokens[4]
                        );
              }
            }
          }
          default: {
            return "Druhy parameter edit moze byt 'vertex' alebo 'edge'";
          }
        }
      }
      case "select": {
        if (tokens.length < 2) {
          return "Nespravny pocet parametrov pre select";
        }

        switch (tokens[1]) {
          case "vertex": {
            if (tokens.length != 3) {
              return
                  "select vertex ocakava jediny parameter (int)vertexId";
            }
            int vertexId = -1;
            try {
              vertexId = Integer.parseInt(tokens[2]);
              Vertex chosenVertex = this.graph.getVertex(vertexId);
              this.graph.selectVertex(chosenVertex);
              return  String.format(
                          "Vrchol %d (%f, %f) uspesne vybrany.\n"
                          + "Jeho hodnota je %d a jeho farba je %s",
                          vertexId, chosenVertex.getX(), chosenVertex.getY(),
                          chosenVertex.getValue(), chosenVertex.getColorName()
                      );

            } catch (NumberFormatException e) {
              return   String.format("select vertex ocakaval (int)vertexId, nasiel %s", tokens[2]);
            } catch (IndexOutOfBoundsException e) {
              return  String.format("Neplatne vertexId %d", vertexId);
            }
          }
          case "edge": {
            if (tokens.length != 4) {
              return  "select edge vyzaduje presne dva parametre: (int)from (int)to";
            }

            int from = -1;
            int to = -1;
            Edge chosenEdge = null;
            try {
              from = Integer.parseInt(tokens[2]);
              to = Integer.parseInt(tokens[3]);
              Vertex originVertex = this.graph.getVertex(from);
              for (Edge adjEdge : originVertex.adjEdges()) {
                if (adjEdge.getDestinationId() == to) {
                  chosenEdge = adjEdge;
                }
              }

              if (chosenEdge == null) {
                throw new IllegalArgumentException(
                    String.format("Hrana %d %d neexistuje", from, to)
                );
              }

              this.graph.selectEdge(chosenEdge);
              return  String.format(
                          "Hrana %d %d uspesne vybrana\nJej value je %d a jej farba je %s",
                          from, to, chosenEdge.getValue(), chosenEdge.getColorName()
                      );


            } catch (NumberFormatException e) {
              return "Parametre pre remove edge maju nespravny format, ocakavaju sa dve cele cisla";
            } catch (IllegalArgumentException e) {
              return e.toString();
            } catch (IndexOutOfBoundsException e) {
              return "Neplatny index vrcholu From alebo To";
            }
          }
          default: {
            return  String.format(
                        "Druhy parameter select ma byt bud 'vertex' alebo 'edge', nie %s",
                        tokens[1]
                     );
          }
        }
      }
      case "remove": {
        if (tokens.length < 2) {
          return "Nespravny pocet parametrov pre delete";
        }

        switch (tokens[1]) {
          case "edge": {
            if (tokens.length < 4) {
              return "Nespravny pocet parametrov pre remove edge";
            }

            int from;
            int to;
            try {
              from = Integer.parseInt(tokens[2]);
              to = Integer.parseInt(tokens[3]);
              this.graph.removeEdge(from, to);
            } catch (NumberFormatException e) {
              return "Parametre pre remove edge maju nespravny format, ocakavaju sa dve cele cisla";
            } catch (IllegalArgumentException e) {
              return e.toString();
            } catch (IndexOutOfBoundsException e) {
              return "Neplatny index vrcholu From alebo To";
            }

            return  String.format("Hrana from %d to %d uspesne odstranena", from, to);
          }
          case "vertex": {
            if (tokens.length != 3) {
              return  String.format(
                          "delete vertex ocakava 1 parameter, nasiel %d",
                          tokens.length - 2
                      );
            }

            int vertexId;
            try {
              vertexId = Integer.parseInt(tokens[2]);
            } catch (NumberFormatException e) {
              return  String.format("delete vertex ocakava int vertexId, nasiel %s", tokens[2]);
            }

            try {
              this.graph.removeVertex(this.graph.getVertex(vertexId));
              return  String.format("Vrchol %d uspesne zmazany", vertexId);
            } catch (IndexOutOfBoundsException e) {
              return  String.format("vertexId %d je neplatne", vertexId);
            }
          }
          default: {
            return   String.format(
                        "Druhy parameter delete moze byt 'vertex' alebo 'edge', nie '%s'",
                        tokens[1]
                      );
          }
        }
      }
      case "move": {
        if (tokens.length != 4) {
          return  "move vyzaduje parametre (int)vertexId (double)newX (double)newY";
        }

        int vertexId;
        double newX;
        double newY;
        try {
          vertexId = Integer.parseInt(tokens[1]);
          newX = Double.parseDouble(tokens[2]);
          newY = Double.parseDouble(tokens[3]);
        } catch (NumberFormatException e) {
          return  "move parametre niesu v spravnom formate";
        }

        if (newX < 0 || newY < 0 || newX > this.getWidth() - 20 || newY > this.getHeight() - 20) {
          return  "move suradnice mimo grafickeho panelu";
        }

        try {
          Vertex v = this.graph.getVertex(vertexId);
          // text vo vrchole ma divne nastavene [x,y], preto pouzijeme diff pozicii
          double dx = newX - v.getX();
          double dy = newY - v.getY();
          v.setX(v.getX() + dx);
          v.setY(v.getY() + dy);
          this.vertexTexts.get(v).setX(this.vertexTexts.get(v).getX() + dx);
          this.vertexTexts.get(v).setY(this.vertexTexts.get(v).getY() + dy);
        } catch (IndexOutOfBoundsException e) {
          return  String.format("Neplatny vertexId %d", vertexId);
        }
        return  String.format(
                "Vrchol %d uspesne presunuty na suradnice (%f, %f)",
                vertexId, newX, newY
                );
      }
      case "deselect": {
        if (tokens.length != 2) {
          return  "Nespravny pocet parametrov pre deselect";
        }

        switch (tokens[1]) {
          case "edge": {
            State.getState().getExtendedGraph().deselectEdge();
            return  "Vyber hrany bol zruseny";
          }
          case "vertex": {
            State.getState().getExtendedGraph().deselectVertex();
            return  "Vyber vrchola bol zruseny";
          }
          default: {
            return  String.format(
                        "Druhy parameter pre delete moze byt bud 'edge' alebo 'vertex', nie %s",
                        tokens[1]
                    );
          }
        }
      }
      case "run": {
        return  String.format("%s", this.controller.runGraphAlgorithm());
      }
      case "dialog": {
        this.controller.runDialog();
        return "runDialog() executed";
      }
      case "help": {
        this.controller.displayHelpAlert();
        return "Help alert displayed";
      }
      case "exit": {
        this.controller.closeProgramHandler(new ActionEvent());
        return  "Exit failed";
      }
      default: {
        return  String.format("Neznamy prikaz '%s'; skus prikaz 'help'", tokens[0]);
      }
    }
  }


}
