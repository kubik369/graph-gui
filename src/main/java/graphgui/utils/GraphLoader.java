package graphgui.utils;

import graphgui.Edge;
import graphgui.ExtendedGraph;
import graphgui.Graph;
import graphgui.State;
import graphgui.Vertex;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class GraphLoader {
  /**
   * Ulozi graf do suboru.
   * @param graph graf krory sa ma ulozit
   * @param file subor do ktoreho sa ma graf ulozit
   * @return true ak sa graf uspesne ulozil, inak false
   */
  public static boolean saveGraph(Graph graph, File file) {
    try (BufferedWriter out = new BufferedWriter(new FileWriter(file))) {
      boolean showColors = true;
      out.write(String.format("S farbami: %b\n", showColors));
      out.write(String.format("Pocet vrcholov: %d\n", graph.getNumberOfVertices()));
      for (int i = 0; i < graph.getNumberOfVertices(); i++) {
        out.write(String.format("Vrchol %d, hodnota %d", i, graph.getVertex(i).getValue()));
        if (showColors) {
          out.write(String.format(", farba %s", graph.getVertex(i).getColorName()));
        }
        out.write('\n');
      }
      out.write(String.format("Pocet hran: %d\n", graph.getNumberOfEdges()));
      for (Edge e: graph.getEdges()) {
        int orig = e.getOriginId();
        int dest = e.getDestinationId();
        if (orig > dest) {
          int tmp = orig;
          orig = dest;
          dest = tmp;
        }
        out.write(String.format(
            "Z vrcholu %d do vrcholu %d, hodnota %d",
            orig, dest, e.getValue()
        ));
        if (showColors) {
          out.write(String.format(", farba %s", e.getColorName()));
        }
        out.write('\n');
      }
      // ked bude loaded citat tento subor, vyignoruje vsetko po tento riadok
      out.write("\nVSTUP PRE PROGRAM\n");
      out.write(String.format("%d %d\n", graph.getNumberOfVertices(), graph.getNumberOfEdges()));
      for (int i = 0; i < graph.getNumberOfVertices(); i++) {
        Vertex v = graph.getVertex(i);
        out.write(String.format(
            "%d %s %f %f %f\n",
            v.getValue(), v.getColorName(), v.getSize(), v.getX(), v.getY()
        ));
      }
      for (Edge e: graph.getEdges()) {
        out.write(String.format(
            "%d %d %d %s\n",
            e.getOriginId(), e.getDestinationId(), e.getValue(), e.getColorName()
        ));
      }
    } catch (IOException e) {
      e.printStackTrace();
      return false;
    }
    return true;
  }

  /**
   * Nacita graf zo suboru.
   * @param f subor do ktoreho sa ma graf ulozit
   * @return true ak sa graf uspesne nacital, inak false
   */
  public static boolean loadGraph(File f) {
    try (Scanner in = new Scanner(new BufferedReader(new FileReader(f)))) {
      // reader ignoruje vsetky riadky az po tento
      while (!in.nextLine().equals("VSTUP PRE PROGRAM"));

      ExtendedGraph graph = State.getState().getExtendedGraph();
      graph.clear();
      State.getState().setVertexId(0);
      final int numOfVertices = in.nextInt();
      final int numOfEdges = in.nextInt();
      for (int i = 0; i < numOfVertices; i++) {
        int val =  in.nextInt();
        String col = in.next();
        double size = in.nextDouble(),
            x = in.nextDouble(),
            y = in.nextDouble();
        Vertex v = graph.addVertex(x, y);
        v.setColorName(col);
        v.setSize(size);
        v.setValue(val);
        graph.vertexChanged(v);
      }
      for (int i = 0; i < numOfEdges; i++) {
        final int u =  in.nextInt();
        final int v = in.nextInt();
        int val =  in.nextInt();
        String col = in.next();
        Edge e = graph.addEdge(graph.getVertex(u), graph.getVertex(v));
        e.setValue(val);
        e.setColorName(col);
        graph.edgeChanged(e);
      }
    } catch (IOException e) {
      e.printStackTrace();
      return false;
    }
    return true;
  }
}
