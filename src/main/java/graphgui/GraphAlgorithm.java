package graphgui;

/**
 * Trieda, ktorá má obsahovať implementáciu požadovaného grafového
 * algoritmu z úlohy B. V tejto triede používajte iba metódy z
 * rozhraní Graph, Edge a Vertex. Nevolajte priamo žiadne metódy
 * z iných tried balíčka main.graphgui. Nemeňte hlavičku
 * konštruktora ani metódy performAlgorithm, môžete však
 * samozrejme zmeniť ich telo a pridávať do triedy vlastné metódy,
 * premenné a pomocné triedy.
 */
public class GraphAlgorithm {

  //PREMENNÉ TRIEDY, UPRAVTE SI PODĽA POTREBY

  /** Samotný graf, na ktorom spúšťame algoritmus. */
  private Graph graph;

  /** Vybraný vrchol, ktorý je parametrom algoritmu, môže byť aj null. */
  private Vertex selectedVertex;

  /** Vybraný vrchol, ktorý je parametrom algoritmu, môže byť aj null. */
  private Edge selectedEdge;

  // KONŠTRUKTOR: NEMEŇTE HLAVIČKU, TELO UPRAVTE PODĽA POTREBY
  /**
   * Konštruktor triedy, ktorý dostane graf, vybraný vrchol a hranu
   * (tento vrchol a hrana môžu byť aj null).
   */
  public GraphAlgorithm(Graph graph, Vertex selectedVertex, Edge selectedEdge) {
    // uloz vstupne udaje
    this.graph = graph;
    this.selectedVertex = selectedVertex;
    this.selectedEdge = selectedEdge;
  }


  // METÓDA performAlgorithm: NEMEŇTE HLAVIČKU, TELO UPRAVTE PODĽA POTREBY
  /**
   * Metóda, ktorá spustí výpočet a upraví graf podľa špecifikácie
   * v zadaní úlohy. Vráti výsledok v textovej forme sformátovaný podľa
   * pokynov v zadaní.
   */
  public String performAlgorithm() {
    // ukazkovy jednoduchy algoritmus:
    // ak selectedVertex nie je null, spustíme z neho
    // prehľadávanie do hĺbky, pričom počítame navštívené vrcholy
    // a nafarbíme ich na oranžovo
    int visitedVertices = 0;

    // ak bol nejaky vrchol vybrany
    if (selectedVertex != null) {
      // vytvor pole visited, vypln false
      int n = graph.getNumberOfVertices();
      boolean[] visited = new boolean[n];
      for (int i = 0; i < n; i++) {
        visited[i] = false;
      }

      // rekurzivne prehladavanie
      search(visited, selectedVertex);

      // prefarbi a spocitaj navstivene vrcholy
      for (int i = 0; i < n; i++) {
        if (visited[i]) {
          graph.getVertex(i).setColorName("orange");
          visitedVertices++;
        }
      }
    }
    // v tejto ukazke vratime pocet navstivenych vrcholov
    return "Pocet navstivenych vrcholov: " + visitedVertices;
  }

  // POMOCNÉ METÓDY A TRIEDY, MEŇTE A PRIDÁVAJTE PODĽA POTREBY
  // v ukážkovom príklade metóda search rekurzívne prehľadáva graf do hĺbky
  private void search(boolean[] visited, Vertex vertex) {
    visited[vertex.getIndex()] = true;
    for (Vertex neighbour : vertex.adjVertices()) {
      if (!visited[neighbour.getIndex()]) {
        visited[neighbour.getIndex()] = true;
        search(visited, neighbour);
      }
    }
  }
}
