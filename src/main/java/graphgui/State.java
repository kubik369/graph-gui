package graphgui;

import graphgui.enums.GraphMode;
import javafx.util.Pair;

/**
 * State of the application.
 * Holds chosen modes and other necessary settings.
 */
public final class State {
  private static State state = null;

  protected State() {}

  /**
   * Singleton of the state.
   */
  public static State getState() {
    if (state == null) {
      state = new State();
    }
    return state;
  }

  private GraphMode mode = GraphMode.VIEW;
  private Vertex selectedVertex;
  private Edge selectedEdge;
  private ExtendedGraph extendedGraph;
  private int vertexId;
  private boolean addingEdge = false;

  public ExtendedGraph getExtendedGraph() {
    return extendedGraph;
  }

  public void setExtendedGraph(ExtendedGraph extendedGraph) {
    this.extendedGraph = extendedGraph;
  }

  public Vertex getSelectedVertex() {
    return selectedVertex;
  }

  public void setSelectedVertex(Vertex selectedVertex) {
    this.selectedVertex = selectedVertex;
  }
  

  public Edge getSelectedEdge() {
    return selectedEdge;
  }

  public void setSelectedEdge(Edge selectedEdge) {
    this.selectedEdge = selectedEdge;
  }

  public GraphMode getMode() {
    return this.mode;
  }

  public void setMode(GraphMode m) {
    this.mode = m;
  }

  public int nextVertexId() {
    return this.vertexId++;
  }

  public boolean isAddingEdge() {
    return addingEdge;
  }

  public void setAddingEdge(boolean addingEdge) {
    this.addingEdge = addingEdge;
  }
}
