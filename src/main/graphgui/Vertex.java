package main.graphgui;

/**
 * Rozhranie pre vrchol grafu.
 * Každý vrchol má svoj identifikátor (id),
 * čo je číslo od 0 po N-1, kde N je počet vrcholov grafu.
 * Tento identifikátor sa môže po vymazaní vrcholu z grafu meniť.
 * Vo vrchole je tiež uložená celočíslená hodnota (value).
 * Napokon, vzhľad vrcholu je určený jeho farbou (color),
 * polohou (position) a veľkosťou (size).
 * Farba je daná ako reťazec obsahujúci buď meno farby podľa
 * HTML a CSS špecifikácie, napr. "white", alebo RGB hodnotu.
 */
public interface Vertex {

  /** Vráti id vrcholu (číslo od 0 po N-1, kde N je počet vrcholov grafu). */
  int getId();

  /** Vráti iterovateľnú skupinu vrcholov susediacich vrcholom this. */
  public Iterable<Vertex> adjVertices();

  /** Vráti iterovateľnú skupinu identifikátorov vrcholov susediacich s vrcholom this. */
  public Iterable<Integer> adjVertexIds();

  /**
   * Vráti iterovateľnú skupinu všetkých hrán incidentných s vrcholom this.
   * Každá hrana vo vrátenom zozname bude mať vrchol this ako origin
   * a susedný vrchol ako destination.
   *
   * @return Iterable Edge
   */
  public Iterable<Edge> adjEdges();

  /**
   * Nájde hranu z vrcholu this do zadaného vrcholu v.
   *
   * @param v vrchol, do ktorého hranu hľadáme
   * @return nájdená hrana, alebo null ak neexistuje
   */
  Edge findEdge(Vertex v);

  /** Vráti celočíselnú hodnotu uloženú vo vrchole. */
  int getValue();

  /**
   * Nastaví hodnotu vrcholu na zadané číslo.
   *
   * @param value nová hodnota vrcholu.
   */
  void setValue(int value);

  /** Vráti meno farby vrcholu ako String. */
  public String getColorName();

  /**
   * Nastaví novú farbu vrcholu.
   *
   * @param color meno požadovanej novej farby pre vrchol.
   */
  public void setColorName(String color);

  /**
   * Vráti x-ovú súradnicu vrcholu.
   *
   * @return x-ová súradnica vrcholu.
   */
  public double getX();

  /**
   * Nastaví novú x-ovú súradnicu vrcholu.
   *
   * @param x nová súradnica vrcholu.
   */
  public void setX(double x);

  /**
   * Vráti y-ovú súradnicu vrcholu.
   *
   * @return y-ová súradnica vrcholu.
   */
  public double getY();

  /**
   * Nastaví novú y-ovú súradnicu vrcholu.
   *
   * @param y nová súradnica vrcholu.
   */
  public void setY(double y);

  /**
   * Vráti veľkosť vrcholu, teda dĺžku strany štvorca predstavujúceho vrchol.
   *
   * @return strana hrany štvorca vrcholu
   */
  public double getSize();

  /**
   * Nastaví novú veľkosť vrcholu, teda dĺžku strany štvorca predstavujúceho vrchol.
   *
   * @param size nová veľkosť hrany štvorca vrcholu
   */
  public void setSize(double size);
}
