package graphgui;

import graphgui.enums.GraphMode;
import java.util.ArrayList;
import java.util.HashMap;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;


/**
 * Trieda pre graf rozšírený o niektoré grafické prvky. Pre každú
 * hranu a vrchol si pamätá ich Shape (Circle, Line). Okrem toho si
 * pamätá, ktorý vrchol a hrana boli zvoleneé (selected). Pri zmene
 * grafu alebo zvolených prvkov upozorní zaregistrovaných
 * pozorovateľov, aby si aktualizovali zobrazenie grafu.  Túto triedu
 * priamo nepoužívajte vo vašej časti programu, ku grafom pristupujte
 * len pomocou rozhraní Graph, Vertex, Edge.
 */
public class ExtendedGraph extends GraphImplementation {

  private static final String DEFAULT_BORDER_COLOR = "black";
  private static final String HIGHLIGHT_BORDER_COLOR = "red";
  private static final String DEFAULT_EDGE_COLOR = "black";
  private static final String HIGHLIGHT_EDGE_COLOR = "red";

  /**
   * Pomocné rozhranie, určené pre triedy, ktoré chcú byť upovedomené o
   * zmenách v grafe, aby ich mohli zobraziť.
   */
  public interface GraphObserver {
    public void vertexAdded(Vertex vertex);

    public void edgeAdded(Edge edge);

    public void vertexRemoved(Vertex vertex);

    public void edgeRemoved(Edge edge);

    public void vertexChanged(Vertex vertex);

    public void edgeChanged(Edge edge);

    public void vertexDeselected(Vertex vertex);

    public void vertexSelected(Vertex vertex);

    public void edgeDeselected(Edge edge);

    public void edgeSelected(Edge edge);
  }

  private ArrayList<GraphObserver> observers;
  private HashMap<Vertex, Circle> vertexShapes;
  private HashMap<Edge, Line> edgeShapes;


  /**
   * Konštruktor.
   */
  public ExtendedGraph() {
    observers = new ArrayList<>();
    vertexShapes = new HashMap<>();
    edgeShapes = new HashMap<>();

    State.getState().setExtendedGraph(this);
  }

  public void addObserver(GraphObserver o) {
    observers.add(o);
  }

  public void removeObserver(GraphObserver o) {
    observers.remove(o);
  }

  @Override
  public VertexImplementation addVertex(double x, double y) {
    VertexImplementation v = super.addVertex(x, y);
    vertexShapes.put(v, new Circle());
    updateVertexShape(v);
    for (GraphObserver o : observers) {
      o.vertexAdded(v);
    }
    return v;
  }

  @Override
  public void removeVertex(Vertex v) {
    if (v == State.getState().getSelectedVertex()) {
      deselectVertex();
    }
    super.removeVertex(v);
    for (GraphObserver o : observers) {
      o.vertexRemoved(v);
    }
    vertexShapes.remove(v);
  }

  @Override
  public EdgeImplementation addEdge(Vertex v1, Vertex v2) throws IllegalArgumentException {
    EdgeImplementation e = super.addEdge(v1, v2);
    edgeShapes.put(e, new Line());
    updateEdgeShape(e);
    State.getState().setAddingEdge(false);
    for (GraphObserver o : observers) {
      o.edgeAdded(e);
    }
    return e;
  }

  @Override
  public void removeEdge(Edge e) throws IllegalArgumentException {
    e = e.getPrimary();
    if (e.isEquivalent(State.getState().getSelectedEdge())) {
      deselectEdge();
    }
    super.removeEdge(e);
    for (GraphObserver o : observers) {
      o.edgeRemoved(e);
    }
    edgeShapes.remove(e);
  }

  @Override
  public void vertexChanged(Vertex v) {
    updateVertexShape(v);
    for (GraphObserver o : observers) {
      o.vertexChanged(v);
    }
    for (Edge e: v.adjEdges()) {
      updateEdgeShape(e);
    }
  }

  @Override
  public void edgeChanged(Edge e) {
    e  = e.getPrimary();
    updateEdgeShape(e);
    for (GraphObserver o : observers) {
      o.edgeChanged(e);
    }
  }

  private void updateVertexShape(Vertex v) {
    Circle r = vertexShapes.get(v);
    r.setCenterX(v.getX() + v.getSize() / 2);
    r.setCenterY(v.getY() + v.getSize() / 2);
    r.setRadius(v.getSize());
    r.setFill(Color.web(v.getColorName()));
    if (v == State.getState().getSelectedVertex()) {
      r.setStyle("-fx-stroke:" + HIGHLIGHT_BORDER_COLOR);
    } else {
      r.setStyle("-fx-stroke:" + DEFAULT_BORDER_COLOR);
    }
  }

  private void updateEdgeShape(Edge e) {
    e = e.getPrimary();
    Line l = edgeShapes.get(e);
    double offset1 = e.getOrigin().getSize() / 2;
    double offset2 = e.getDestination().getSize() / 2;
    l.setStartX(e.getOrigin().getX() + offset1);
    l.setStartY(e.getOrigin().getY() + offset1);
    l.setEndX(e.getDestination().getX() + offset2);
    l.setEndY(e.getDestination().getY() + offset2);

    if (e.isEquivalent(State.getState().getSelectedEdge())) {
      l.setStyle("-fx-stroke:" + HIGHLIGHT_EDGE_COLOR);
    } else {
      l.setStyle("-fx-stroke:" + e.getColorName());
    }
  }


  public Shape getVertexShape(Vertex v) {
    return vertexShapes.get(v);
  }

  public Shape getEdgeShape(Edge e) {
    return edgeShapes.get(e);
  }

  /**
   * Zruší výber hrany.
   */
  public void deselectEdge() {
    if (State.getState().getSelectedEdge() != null) {
      Edge old = State.getState().getSelectedEdge();
      State.getState().setSelectedEdge(null);
      updateEdgeShape(old);
      for (GraphObserver o : observers) {
        o.edgeDeselected(old);
      }
    }
  }

  /**
   * Zruší výber vrcholu.
   */
  public void deselectVertex() {
    if (State.getState().getSelectedVertex() != null) {
      deselectEdge();
      Vertex old = State.getState().getSelectedVertex();
      State.getState().setSelectedVertex(null);
      State.getState().setAddingEdge(false);
      updateVertexShape(old);
      for (GraphObserver o : observers) {
        o.vertexDeselected(old);
      }
    }
  }

  /**
   * Vyberie vrchol.
   * @param v Vrchol, ktorý uživateľ vybrať.
   */
  public void selectVertex(Vertex v) {
    if (v == null) {
      throw new NullPointerException("Selecting null vertex");
    }
    if (State.getState().getMode() == GraphMode.DELETE) {
      return;
    }

    if (v != State.getState().getSelectedVertex()) {
      deselectVertex();
      State.getState().setSelectedVertex(v);
      updateVertexShape(v);
      for (GraphObserver o : observers) {
        o.vertexSelected(v);
      }
    }
  }

  /**
   * Vyberie hranu.
   * @param e Hrana, ktorú chce uživateľ vybrať.
   */
  public void selectEdge(Edge e) {
    if (e == null) {
      throw new NullPointerException("Selecting null edge");
    }
    if (e != State.getState().getSelectedEdge()) {
      deselectEdge();
      State.getState().setSelectedEdge(e);
      updateEdgeShape(e);
      for (GraphObserver o : observers) {
        o.edgeSelected(e);
      }
    }
  }

  /**
   * Metóda, ktorá zmaže z grafu vybraný vrchol, ak taký existuje.
   */
  public void removeSelectedVertex() {
    if (State.getState().getSelectedVertex() != null) {
      removeVertex(State.getState().getSelectedVertex());
    }
  }

  /**
   * Metóda, ktorá zmaže z grafu vybranú hranu, ak taká existuje.
   */
  public void  removeSelectedEdge() {
    if (State.getState().getSelectedEdge() != null) {
      removeEdge(State.getState().getSelectedEdge());
    }
  }

  /**
   * Pridať hranu z vrcholu.
   * @param vertex Vrchol z ktorého viesť hranu.
   */
  public void addEdgeFromSelected(Vertex vertex) {
    Vertex selectedVertex = State.getState().getSelectedVertex();
    if (selectedVertex != null && selectedVertex != vertex
            && findEdge(selectedVertex, vertex) == null) {
      Edge newEdge = addEdge(selectedVertex, vertex);
    }
  }

  /**
   * Označí vrchol pokiaľ nie je vybratý, odznačí ho pokiaľ je.
   * @param vertex Vrchol na ktorom vykonať operáciu.
   */
  public boolean toggleVertexSelection(Vertex vertex) {
    if (vertex == State.getState().getSelectedVertex()) {
      deselectVertex();
      return false;
    } else {
      selectVertex(vertex);
      return true;
    }
  }

  /**
   * Označí hranu pokiaľ nie je vybratá, odznačí ju pokiaľ je.
   * @param edge Hrana na ktorej vykonať operáciu.
   */
  public boolean toggleEdgeSelection(Edge edge) {
    if (edge == State.getState().getSelectedEdge()) {
      deselectEdge();
      return false;
    } else {
      selectEdge(edge);
      return true;
    }
  }

 
  
}
