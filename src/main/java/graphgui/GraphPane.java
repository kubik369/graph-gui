package graphgui;

import graphgui.enums.GraphMode;
import graphics.Controller;
import javafx.geometry.Insets;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;

/** Trieda pre grafický panel, na ktorom umiestňujeme nakreslenie grafu. */
public class GraphPane extends Pane implements ExtendedGraph.GraphObserver {
  private Controller controller;
  private ExtendedGraph graph;
  private final Line edgeLine;

  public GraphPane() {
    this.edgeLine = new Line();
    this.setOnMouseMoved((MouseEvent e) -> {
      Vertex v = State.getState().getSelectedVertex();
      if (State.getState().getMode() == GraphMode.EDIT_GRAPH && v != null) {
        this.edgeLine.setStartX(v.getX());
        this.edgeLine.setStartY(v.getY());
        this.edgeLine.setEndX(e.getX());
        this.edgeLine.setEndY(e.getY());
      }
    });
    this.getChildren().add(this.edgeLine);

    this.setMinSize(400, 400);
    this.setBackground(new Background(
      new BackgroundFill(Color.LIGHTGRAY, CornerRadii.EMPTY, Insets.EMPTY))
    );
  }

  /**
   * Inicializácia.
   * @param g Hlavná objekt okna aplikácie
   */
  public void init(Controller controller, ExtendedGraph graph) {
    this.controller = controller;
    this.graph = graph;
    this.graph.addObserver(this);

    this.setOnMouseClicked((MouseEvent event) -> {
      if (State.getState().getMode() == GraphMode.EDIT_GRAPH) {
        this.graph.addVertex(event.getX(), event.getY());
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
    Shape shape = graph.getVertexShape(vertex);

    shape.setOnMouseClicked((MouseEvent event) -> {
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
      } else if (state.getMode() == GraphMode.EDIT_VALUES) {

      }

      event.consume();
    });
    shape.setOnMouseMoved((MouseEvent event) -> {
      // FIXME Find out what needs to be done here
      //gui.setInfoLabelText("Vertex " + vertex.toString());
      event.consume();
    });
    this.getChildren().add(shape);
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
      s.setStrokeWidth(4);
      s.setOnMouseClicked((MouseEvent event) -> {
        GraphMode mode = State.getState().getMode();
        if (mode == GraphMode.EDIT_VALUES) {
          this.graph.toggleEdgeSelection(edge);
        } else if (mode == GraphMode.DELETE) {
          this.graph.removeEdge(edge);
        }
      });
      this.getChildren().add(graph.getEdgeShape(edge));
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
  }

  /**
   * Akcia po zrušení voľby hrany.
   * @param edge Odvolená hrana
   */
  @Override
  public void edgeDeselected(Edge edge) {
  }

  /**
   * Akcia po zvolení vrcholu.
   * @param vertex Zvolený vrchol
   */
  @Override
  public void vertexSelected(Vertex vertex) {
    System.out.println("Vertex selected");
  }

  /**
   * Akcia po zrušení voľby vrcholu.
   * @param vertex Odvolený vrchol
   */
  @Override
  public void vertexDeselected(Vertex vertex) {
    this.edgeLine.setStartX(0);
    this.edgeLine.setStartY(0);
    this.edgeLine.setEndX(0);
    this.edgeLine.setEndY(0);
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
}
