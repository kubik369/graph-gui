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

/** Trieda pre grafický panel, na ktorom umiestňujeme nakreslenie grafu. */
public class GraphPane extends Pane implements ExtendedGraph.GraphObserver {
  private Controller controller;
  private ExtendedGraph graph;
  private final Line edgeLine;
  private static final int EDGE_WIDTH = 4;
  private HashMap<Vertex, Text> vertexTexts;

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
}
