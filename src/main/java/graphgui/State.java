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
  private Vertex selectedVertex = null;
  private Edge selectedEdge = null;

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
}
