package main.graphgui;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.zip.DataFormatException;

/**
 * Trieda GraphImplementation implementuje rozhranie Graph na prácu s grafmi.
 * V svojej časti programu pristupujte ku grafu len cez
 * rozhrania Graph, Vertex a Edge.
 */
public class GraphImplementation implements Graph {

  private ArrayList<VertexImplementation> vertices;
  private ArrayList<EdgeImplementation> edges;

  /** Konštruktor vytvorí prázdny graf. */
  public GraphImplementation() {
    vertices = new ArrayList<VertexImplementation>();
    edges = new ArrayList<EdgeImplementation>();
  }

  @Override
  public int getNumberOfVertices() {
    return vertices.size();
  }

  @Override
  public Vertex getVertex(int index) throws IndexOutOfBoundsException {
    return vertices.get(index);
  }

  @Override
  public VertexImplementation addVertex(double x, double y) {
    VertexImplementation v = new VertexImplementation(this, vertices.size(), x, y);
    vertices.add(v);
    return v;
  }

  @Override
  public void removeVertex(Vertex vertex) {
    checkVertex(vertex);
    for (Edge e : vertex.adjEdges()) {
      removeEdge(e);
    }
    boolean wasDeleted = vertices.remove(vertex);
    if (!wasDeleted) {
      throw new IllegalArgumentException("no such vertex");
    }
    internalRecomputeVertexIds();
  }

  @Override
  public Iterable<Vertex> getVertices() {
    return Collections.unmodifiableList(new ArrayList<Vertex>(vertices));
  }

  @Override
  public Iterable<Vertex> adjVertices(Vertex vertex) {
    checkVertex(vertex);
    return vertex.adjVertices();
  }

  @Override
  public Iterable<Edge> adjEdges(Vertex vertex) {
    checkVertex(vertex);
    return vertex.adjEdges();
  }

  @Override
  public Iterable<Edge> adjEdges(int n) throws IndexOutOfBoundsException {
    return getVertex(n).adjEdges();
  }

  @Override
  public Iterable<Integer> adjVertexIds(int n) throws IndexOutOfBoundsException {
    return getVertex(n).adjVertexIds();
  }

  private void checkVertex(Vertex v) throws IllegalArgumentException {
    int id = v.getId();
    if (id < 0 || id >= getNumberOfVertices() ||  vertices.get(id) != v) {
      throw new IllegalArgumentException("neexistujuci vrchol");
    }
  }

  @Override
  public EdgeImplementation addEdge(Vertex v1, Vertex v2) throws IllegalArgumentException {
    checkVertex(v1);
    checkVertex(v2);
    if (findEdge(v1, v2) != null) {
      throw new IllegalArgumentException("hrana uz existuje");
    }
    if (v1 == v2) {
      throw new IllegalArgumentException("zaciatok a koniec hrany ten isty");
    }
    VertexImplementation vi1 = (VertexImplementation)v1;
    VertexImplementation vi2 = (VertexImplementation)v2;
    EdgeImplementation res = EdgeImplementation.createEdgePair(this, vi1, vi2);
    vi1.internalAddEdge(res);
    vi2.internalAddEdge(res.getReverse());
    edges.add(res);
    return res;
  }

  @Override
  public Edge addEdge(int n1, int n2) throws IndexOutOfBoundsException, IllegalArgumentException {
    return addEdge(getVertex(n1), getVertex(n2));
  }

  @Override
  public void removeEdge(Edge e) throws IllegalArgumentException {
    EdgeImplementation ei1 = (EdgeImplementation)e;
    EdgeImplementation ei2 = ei1.getReverse();
    EdgeImplementation erem;

    boolean wasRemoved;
    if (ei1.isPrimary()) {
      wasRemoved  = edges.remove(ei1);
      erem = ei1;
    } else {
      wasRemoved  = edges.remove(ei2);
      erem = ei2;
    }
    if (!wasRemoved) {
      throw new IllegalArgumentException("no such edge");
    }

    VertexImplementation vi1 = ei1.getOrigin();
    VertexImplementation vi2 = ei1.getDestination();

    wasRemoved = vi1.internalRemoveEdge(ei1);
    wasRemoved = wasRemoved && vi2.internalRemoveEdge(ei2);
    if (!wasRemoved) {
      throw new IllegalArgumentException("graph inconsistent");
    }
  }

  @Override
  public void removeEdge(Vertex n1, Vertex n2) throws IllegalArgumentException {
    Edge e = findEdge(n1, n2);
    if (e == null) {
      throw new IllegalArgumentException("no such edge");
    }
    removeEdge(e);
  }

  @Override
  public void removeEdge(int n1, int n2) throws IndexOutOfBoundsException {
    removeEdge(getVertex(n1), getVertex(n2));
  }

  @Override
  public Edge findEdge(Vertex v1, Vertex v2) {
    checkVertex(v1);
    checkVertex(v2);
    return v1.findEdge(v2);
  }

  @Override
  public Edge findEdge(int n1, int n2) throws IndexOutOfBoundsException {
    return findEdge(getVertex(n1), getVertex(n2));
  }

  @Override
  public Iterable<Edge> getEdges() {
    return Collections.unmodifiableList(new ArrayList<Edge>(edges));
  }

  @Override
  public int getNumberOfEdges() {
    return edges.size();
  }

  void internalRecomputeVertexIds() {
    int id = 0;
    for (VertexImplementation n : vertices) {
      n.internalSetId(id++);
    }
  }

  @Override
  public void print(PrintStream out, boolean full) {
    out.println(getNumberOfVertices());
    for (Vertex n : getVertices()) {
      out.print(n.getX() + " " + n.getY() + " " + n.getValue());
      if (full) {
        out.print(" " + n.getColorName() + " " + n.getSize());
      }
      out.println();
    }
    for (Edge e : getEdges()) {
      out.println(e.getOriginId() + " " + e.getDestinationId() + " " + e.getValue());
    }
  }

  @Override
  public void clear() {
    for (Vertex n : getVertices()) {
      removeVertex(n);
    }
  }

  @Override
  public void read(Scanner s) throws DataFormatException {
    clear();  // remove all existing vertices

    String line = "";
    try {
      line = s.nextLine();
      Scanner s2 = new Scanner(line);
      int n = s2.nextInt();
      for (int i = 0; i < n; i++) {
        line = s.nextLine();
        s2 = new Scanner(line);
        double x = s2.nextDouble();
        double y = s2.nextDouble();
        Vertex v = addVertex(x, y);
        int value = s2.nextInt();
        v.setValue(value);
        if (s2.hasNext()) {
          String colorName = s2.next();
          double size = s2.nextDouble();
          v.setColorName(colorName);
          v.setSize(size);
        }
      }

      while (s.hasNextLine()) {
        line = s.nextLine();
        s2 = new Scanner(line);

        int from = s2.nextInt();
        int to = s2.nextInt();
        int value = s2.nextInt();
        Edge newEdge = addEdge(from, to);
        newEdge.setValue(value);
      }
    } catch (NoSuchElementException e) {
      throw new DataFormatException("ten subor je nejaky divny (" + line + ")");
    }
  }

  /**
   * Pomocná metóda, ktorou Vertex oznamuje svoje zmeny.
   * Tu je prázdna, ale preťažená v ExtendedGraph.
   */
  public void vertexChanged(Vertex v) {
  }

  /**
   * Pomocná metóda, ktorou Edge oznamuje svoje zmeny.
   * Tu prázdna, ale preťažená v ExtendedGraph.
   */
  public void edgeChanged(Edge e) {
  }
}
