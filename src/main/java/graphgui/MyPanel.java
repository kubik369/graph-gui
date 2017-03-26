package graphgui;

import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Shape;

/** Trieda pre grafický panel, na ktorom umiestňujeme nakreslenie grafu. */
public class MyPanel extends Pane implements ExtendedGraph.GraphObserver {
  private MyPanel mp;
  private GraphGUI gui;
  private ExtendedGraph graph;

  public MyPanel() {

  }

  /**
   * Metóda, ktorou sa panelu oznamuje, že bol pridaný vrchol.
   * Nastaví vrcholu eventy a pridá jeho vykreslenie.
   *
   * @param vertex pridaný vrchol
   * @throws IllegalArgumentException ak vrchol nebol korektný
   */
  @Override
  public void vertexAdded(Vertex vertex) throws IllegalArgumentException {
    Shape shape = graph.getVertexShape(vertex);
    shape.setOnMouseClicked((MouseEvent event) -> {
      if (event.isShiftDown()) {
        graph.addEdgeFromSelected(vertex);
      } else {
        graph.toggleVertexSelection(vertex);
      }
      event.consume();
    });
    shape.setOnMouseMoved((MouseEvent event) -> {
      gui.setInfoLabelText("Vertex " + vertex.toString());
      event.consume();
    });
    mp.getChildren().add(shape);
  }

  /**
   * Metóda, ktorou sa panelu oznamuje, že bola pridaná hrana.
   * Pridá jej vykreslenie na plochu.
   *
   * @param edge pridaná hrana
   * @throws IllegalArgumentException ak hrana nebola korektná
   */
  @Override
  public void edgeAdded(Edge edge) throws IllegalArgumentException {
    try {
      mp.getChildren().add(graph.getEdgeShape(edge));
    } catch (Exception ex) {
      throw new IllegalArgumentException("edgeAdded");
    }
  }

  /**
   * Metóda, ktorou sa panelu oznamuje, že bude zmazaný vrchol vertex.
   * Zruší príslúchajúce nakreslenie vrcholu
   *
   * @param vertex o chvíľu zmazaný vrchol
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
   * Metóda, ktorou sa panelu oznamuje, že bude zmazaná hrana edge.
   * Zruší príslúchajúce nakreslenie hrane
   *
   * @param edge o chvíľu zmazaná hrana
   * @throws IllegalArgumentException ak hrana nebola korektná alebo graf je nekonzistentný
   */
  @Override
  public void edgeRemoved(Edge edge) throws IllegalArgumentException {
    try {
      mp.getChildren().remove(graph.getEdgeShape(edge));
    } catch (Exception ex) {
      throw new IllegalArgumentException("edgeRemoved");
    }
  }

  @Override
  public void edgeSelected(Edge edge) {
  }

  @Override
  public void edgeDeselected(Edge edge) {
  }

  @Override
  public void vertexSelected(Vertex vertex) {
  }

  @Override
  public void vertexDeselected(Vertex vertex) {
  }

  @Override
  public void edgeChanged(Edge edge) {
  }

  @Override
  public void vertexChanged(Vertex vertex) {
  }

  /**
   Initialize method.
   */
  public void init(GraphGUI gui) {
    this.gui = gui;
    this.graph = gui.getGraph();

    setOnMouseClicked(event -> {
      // TODO handle user clicks here
    });
  }
}
