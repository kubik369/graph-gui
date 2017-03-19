package main.graphgui;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Trieda vrcholov grafu, implementácia rozhrania Vertex.
 * V svojej časti programu pristupujte ku grafu len cez
 * rozhrania Graph, Vertex a Edge.
 */
public final class VertexImplementation implements Vertex {

  private static final int defaultValue = 1;
  private final double defaultSize = 20.0;
  private static final String defaultColorName = "white";

  private GraphImplementation graph;
  private ArrayList<EdgeImplementation> edges;
  private String colorName = defaultColorName;
  private int id;
  private int value = defaultValue;
  @SuppressWarnings("checkstyle:MemberName")
  private double x;
  @SuppressWarnings("checkstyle:MemberName")
  private double y;
  private double size = defaultSize;

  VertexImplementation(GraphImplementation graph, int id, double x, double y) {
    edges = new ArrayList<EdgeImplementation>();
    this.id = id;
    this.graph = graph;
    this.x = x;
    this.y = y;
  }

  @Override
  public int getId() {
    return id;
  }

  void internalSetId(int id) {
    if (id != this.id) {
      this.id = id;
      graph.vertexChanged(this);
    }
  }

  @Override
  public Iterable<Vertex> adjVertices() {
    ArrayList<Vertex> a = new ArrayList<Vertex>();
    for (Edge e : edges) {
      a.add(e.getDestination());
    }
    return Collections.unmodifiableList(a);
  }

  @Override
  public Iterable<Integer> adjVertexIds() {
    ArrayList<Integer> a = new ArrayList<Integer>();
    for (Edge e : edges) {
      a.add(e.getDestinationId());
    }
    return Collections.unmodifiableList(a);
  }

  @Override
  public Iterable<Edge> adjEdges() {
    return Collections.unmodifiableList(new ArrayList<Edge>(edges));
  }

  void internalAddEdge(EdgeImplementation e) {
    edges.add(e);
  }

  boolean internalRemoveEdge(EdgeImplementation e) {
    return edges.remove(e);
  }

  @Override
  public EdgeImplementation findEdge(Vertex v) {
    for (EdgeImplementation e : edges) {
      if (e.getDestination() == v) {
        return e;
      }
    }
    return null;
  }

  @Override
  public void setValue(int value) {
    if (value != this.value) {
      this.value = value;
      graph.vertexChanged(this);
    }
  }

  @Override
  public int getValue() {
    return value;
  }

  @Override
  public void setX(double x) {
    if (x != this.x) {
      this.x = x;
      graph.vertexChanged(this);
    }
  }

  @Override
  public void setY(double y) {
    if (y != this.y) {
      this.y = y;
      graph.vertexChanged(this);
    }
  }

  @Override
  public void setColorName(String colorName) {
    if (!this.colorName.equals(colorName)) {
      this.colorName = colorName;
      graph.vertexChanged(this);
    }
  }

  @Override
  public void setSize(double size) {
    if (size != this.size) {
      this.size = size;
      graph.vertexChanged(this);
    }
  }

  @Override
  public double getSize() {
    return size;
  }

  @Override
  public double getX() {
    return x;
  }

  @Override
  public double getY() {
    return y;
  }

  @Override
  public String getColorName() {
    return colorName;
  }

  @Override
  public String toString() {
    Integer id = getId();
    return String.format("%s (%2f,%2f) value %d", id.toString(), getX(), getY(), value);
  }
}
