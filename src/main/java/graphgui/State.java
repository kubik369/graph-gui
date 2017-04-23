package graphgui;

import graphgui.enums.GraphMode;

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
}
