package graphgui;

/**
 * Rozhranie pre vrchol grafu.
 * Každý vrchol má svoj identifikátor (id),
 * čo je číslo od 0 po N-1, kde N je počet vrcholov grafu.
 * Tento identifikátor sa môže po vymazaní vrchola z grafu meniť.
 * Vo vrchole je tiež uložená celočíslená hodnota (value).
 * Napokon, vzhľad vrchola je určený jeho farbou (color),
 * polohou (position) a veľkosťou (size).
 * Farba je daná ako reťazec obsahujúci buď meno farby podľa
 * HTML a CSS špecifikácie, napr. "white", alebo RGB hodnotu.
 */
public interface Vertex {

    /**
     * Vráti id vrchola (číslo od 0 po N-1, kde N je počet vrcholov grafu)
     */
    int getId();

    /**
     * Vráti iterovateľnú skupinu vrcholov susediacich vrcholom this.
     */
    public Iterable<Vertex> adjVertices();

    /**
     * Vráti iterovateľnú skupinu identifikátorov vrcholov susediacich s vrcholom this.
     */
    public Iterable<Integer> adjVertexIds();

    /**
     * Vráti iterovateľnú skupinu všetkých hrán incidentných s vrcholom this.
     * Každá hrana vo vrátenom zozname bude mať vrchol this ako origin
     * a susedný vrchol ako destination.
     * @return Iterable Edge
     */
    public Iterable<Edge> adjEdges();

    /**
     * Nájde hranu z vrcholu this do zadaného vrcholu v.
     * @param v vrchol, do ktorého hranu hľadáme
     * @return nájdená hrana, alebo null ak neexistuje
     */
    Edge findEdge(Vertex v);

    /**
     * Vráti celočíselnú hodnotu uloženú vo vrchole.
     */
    int getValue();

    /**
     * Nastaví hodnotu vrchola na zadané číslo.
     * @param value nová hodnota vrchola.
     */
    void setValue(int value);

    /**
     * Vráti meno farby vrchola ako String.
     */
    public String getColorName();

    /**
     * Nastaví novú farbu vrchola.
     * @param color meno požadovanej novej farby pre vrchol.
     */
    public void setColorName(String color);

    /**
     * Vráti x-ovú súradnicu vrchola.
     */
    public double getX();

    /**
     * Nastaví novú x-ovú súradnicu vrchola.
     */
    public void setX(double X);

    /**
     * Vráti y-ovú súradnicu vrchola.
     */
    public double getY();

    /**
     * Nastaví novú y-ovú súradnicu vrchola.
     */
    public void setY(double Y);

    /**
     * Vráti veľkosť vrchola, teda dĺžku strany
     * štvorca predstavujúceho vrchol.
     */
    public double getSize();

    /**
     * Nastaví novú veľkosť vrchola, teda dĺžku strany
     * štvorca predstavujúceho vrchol.
     */
    public void setSize(double size);
}
