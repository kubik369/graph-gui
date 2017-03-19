package graphgui;

import java.util.ArrayList;
import java.util.HashMap;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

/**
 * Trieda pre graf rozšírený o niektoré grafické prvky. Pre každú
 * hranu a vrchol si pamätá ich Shape (Rectangle, Line). Okrem toho si
 * pamätá, ktorý vrchol a hrana boli zvoleneé (selected). Pri zmene
 * grafu alebo zvolených prvkov upozorní zaregistrovaných
 * pozorovateľov, aby si aktualizovali zobrazenie grafu.  Túto triedu
 * priamo nepoužívajte vo vašej časti programu, ku grafom pristupujte
 * len pomocou rozhraní Graph, Vertex, Edge.
 */
public class ExtendedGraph extends GraphImplementation {

  private static final Color defaultBorderCol = Color.BLACK;
  private static final Color highlightBorderCol = Color.RED;
  private static final Color defaultEdgeColor = Color.BLACK;
  private static final Color highlightEdgeColor = Color.RED;

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
  private HashMap<Vertex, Rectangle> vertexShapes;
  private HashMap<Edge, Line> edgeShapes;

  private Edge selectedEdge = null;  // nemusi byt primary
  private Vertex selectedVertex = null;

  /**
   * Konštruktor.
   */
  public ExtendedGraph() {
    observers = new ArrayList<GraphObserver>();
    vertexShapes = new HashMap<Vertex, Rectangle>();
    edgeShapes = new HashMap<Edge, Line>();
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
    vertexShapes.put(v, new Rectangle());
    updateVertexShape(v);
    for (GraphObserver o : observers) {
      o.vertexAdded(v);
    }
    return v;
  }

  @Override
  public void removeVertex(Vertex v) {
    if (selectedVertex == v) {
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
    for (GraphObserver o : observers) {
      o.edgeAdded(e);
    }
    return e;
  }

  @Override
  public void removeEdge(Edge e) throws IllegalArgumentException {
    e = e.getPrimary();
    if (e.isEquivalent(selectedEdge)) {
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
    Rectangle r = vertexShapes.get(v);
    r.setX(v.getX());
    r.setY(v.getY());
    r.setWidth(v.getSize());
    r.setHeight(v.getSize());
    r.setFill(Color.web(v.getColorName()));
    if (v == selectedVertex) {
      r.setStroke(highlightBorderCol);
    } else {
      r.setStroke(defaultBorderCol);
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
    if (e.isEquivalent(selectedEdge)) {
      l.setStroke(highlightEdgeColor);
    } else {
      l.setStroke(defaultEdgeColor);
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
    if (selectedEdge != null) {
      Edge old = selectedEdge;
      selectedEdge = null;
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
    if (selectedVertex != null) {
      deselectEdge();
      Vertex old = selectedVertex;
      selectedVertex = null;
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
    if (v != selectedVertex) {
      deselectVertex();
      selectedVertex = v;
      updateVertexShape(v);
      for (GraphObserver o : observers) {
        o.vertexSelected(selectedVertex);
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
    if (e != selectedEdge) {
      deselectEdge();
      selectedEdge = e;
      updateEdgeShape(e);
      for (GraphObserver o : observers) {
        o.edgeSelected(selectedEdge);
      }
    }
  }

  /**
   * Metóda, ktorá zmaže z grafu vybraný vrchol, ak taký existuje.
   */
  public void removeSelectedVertex() {
    if (selectedVertex != null) {
      removeVertex(selectedVertex);
    }
  }

  /**
   * Metóda, ktorá zmaže z grafu vybranú hranu, ak taká existuje.
   */
  public void  removeSelectedEdge() {
    if (selectedEdge != null) {
      removeEdge(selectedEdge);
    }
  }

  /**
   * Pridať hranu z vrcholu.
   * @param vertex Vrchol z ktorého viesť hranu.
   */
  public void addEdgeFromSelected(Vertex vertex) {
    if (selectedVertex != null && selectedVertex != vertex
            && findEdge(selectedVertex, vertex) == null) {
      Edge newEdge = addEdge(selectedVertex, vertex);
    }
  }

  /**
   * Označí vrchol pokiaľ nie je vybratý, odznačí ho pokiaľ je.
   * @param vertex Vrchol na ktorom vykonať operáciu.
   */
  public void toggleVertexSelection(Vertex vertex) {
    if (selectedVertex == vertex) {
      deselectVertex();
    } else {
      selectVertex(vertex);
    }
  }

  public Vertex getSelectedVertex() {
    return selectedVertex;
  }

  public Edge getSelectedEdge() {
    return selectedEdge;
  }
}
