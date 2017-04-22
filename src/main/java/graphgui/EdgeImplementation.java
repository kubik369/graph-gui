package graphgui;


/**
 * Trieda hrán grafu, implementácia rozhrania Edge.
 * V svojej časti programu pristupujte ku grafu len cez
 * rozhrania Graph, Vertex a Edge.
 */
public final class EdgeImplementation implements Edge {

  public static final int defaultValue = 1;
  private static final String defaultColorName = "black";
  
  private final GraphImplementation graph;
  private final VertexImplementation from;
  private final VertexImplementation to;
  private int value;
  private String colorName = defaultColorName;

  // hrana v opacnom smere
  private EdgeImplementation reverse;
  // je tato kopia hrany primarna, t.j. ulozena v zozname hran grafu?
  private boolean isPrimary;

  public boolean isPrimary() {
    return isPrimary;
  }

  @Override
  public VertexImplementation getOrigin() {
    return from;
  }

  @Override
  public int getOriginId() {
    return getOrigin().getId();
  }

  @Override
  public VertexImplementation getDestination() {
    return to;
  }

  @Override
  public int getDestinationId() {
    return getDestination().getId();
  }

  @Override
  public EdgeImplementation getReverse() {
    return reverse;
  }

  @Override
  public EdgeImplementation getPrimary() {
    if (isPrimary) {
      return this;
    } else {
      return reverse;
    }
  }

  @Override
  public boolean isEquivalent(Edge e) {
    return e == this || e == reverse;
  }

  /**
   * Vytvorí hranu v grafe.
   */
  public static EdgeImplementation createEdgePair(GraphImplementation graph,
      VertexImplementation from,
      VertexImplementation to) {
    EdgeImplementation e1 = new EdgeImplementation(graph, from, to, null);
    EdgeImplementation e2 = new EdgeImplementation(graph, to, from, e1);
    e1.reverse = e2;
    e1.isPrimary = true;
    return e1;
  }

  // private konstruktor, treba pouzivat namiesto neho createEdgePair
  private EdgeImplementation(GraphImplementation graph,
          VertexImplementation from, VertexImplementation to,
          EdgeImplementation reverse) {
    this.graph = graph;
    this.from = from;
    this.to = to;
    this.value = defaultValue;
    this.colorName = defaultColorName;
    this.reverse = reverse;
    isPrimary = false;
  }

  @Override
  public String toString() {
    Integer ifrom = getOriginId();
    Integer ito = getDestinationId();
    return String.format("(%s,%s) value %d colorName %s", ifrom, ito, value, colorName);
  }

  @Override
  public void setValue(int value) {
    if (!isPrimary()) {
      reverse.setValue(value);
      return;
    }
    if (value != this.value) {
      this.value = value;
      reverse.value = value;
      graph.edgeChanged(this);
    }
  }
  
  @Override
  public void setColorName(String colorName) {
    if (!this.colorName.equals(colorName)) {
      this.colorName = colorName;
      graph.edgeChanged(this);
    }
  }
  
  @Override
  public String getColorName() {
    return colorName;
  }

  @Override
  public int getValue() {
    return value;
  }
}
