package main.graphgui;

import java.io.PrintStream;
import java.util.Scanner;
import java.util.zip.DataFormatException;

/**
 * Rozhranie pre prácu s neorientovaným grafom.
 * Umožňuje pridávať a mazať vrcholy aj hrany.
 * Vrcholy aj hrany sú objekty typu Vertex a Edge.
 * Vrcholy sú číslované 0,...,N-1, kde N je počet vrcholov,
 * a v algoritmoch je možné ku nim pristupovať buď cez tieto čísla (id)
 * alebo cez samotné objekty typu Vertex.
 * Pri mazaní vrcholov sa id ostatných vrcholov môžu meniť.
 * Vrcholy aj hrany si pamätajú hodnotu (value) typu int.
 * Vrcholy majú uložené aj údaje o svojich súradniciach, farbe a
 * veľkosti.
 * Každá hrana {u,v} je uložená ako dvojica objektov typu Edge,
 * pričom jeden z týchto objektov začína v vrchole u a končí vo vrchole v
 * a druhý naopak.
 */
public interface Graph {

  /**
   * Vráti počet vrcholov grafu.
   */
  public int getNumberOfVertices();

  /**
   * Vráti iterovateľnú skupinu všetkých vrcholov grafu.
   */
  public Iterable<Vertex> getVertices();

  /**
   * Vráti počet hrán grafu.
   */
  public int getNumberOfEdges();

  /**
   * Vráti iterovateľnú skupinu všetkých hrán grafu.
   * Každá hrana je v zozname iba raz v jednom z dvoch možných smerov.
   */
  public Iterable<Edge> getEdges();

  /**
   * Vráti vrchol s požadovaným id.
   * @param index id požadovaného vrcholu (číslo z intervalu 0..N-1)
   * @throws IndexOutOfBoundsException ak taký vrchol neexistuje
   */
  public Vertex getVertex(int index) throws IndexOutOfBoundsException;

  /**
   * Vráti iterovateľnú skupinu vrcholov susediacich s daným vrcholom.
   * @param vertex Vrchol, ktorého susedov požadujeme.
   */
  public Iterable<Vertex> adjVertices(Vertex vertex);

  /**
   * Vráti iterovateľnú skupinu identifikátorov vrcholov susediacich s vrcholom s daným id.
   * @param n id vrchola, ktorého susedov požadujeme
   * @return Iterable Integer
   * @throws IndexOutOfBoundsException ak taký index neexistuje
   */
  public Iterable<Integer> adjVertexIds(int n) throws IndexOutOfBoundsException;

  /**
   * Vráti iterovateľnú skupinu všetkých hrán incidentných so zadaným vrcholom.
   * Každá hrana vo vrátenom zozname bude mať zadaný vrchol vertex ako origin
   * a susedný vrchol ako  destination.
   * @param vertex Vrchol ktorého hrany žiadame.
   */
  public Iterable<Edge> adjEdges(Vertex vertex);

  /**
   * Vráti iterovateľnú skupinu všetkých hrán incidentných s vrcholom zadaným
   * pomocou id.
   * Každá hrana vo vrátenom zozname bude mať zadaný vrchol vertex ako origin
   * a susedný vrchol ako destination.
   * @param index id zadaného vrcholu
   * @throws IndexOutOfBoundsException ak vrchol so zadaným indexom neexistuje
   */
  public Iterable<Edge> adjEdges(int index) throws IndexOutOfBoundsException;

  /**
   * Do grafu pridá vrchol so zadanými súradnicami.
   * @return pridaný vrchol
   */
  public Vertex addVertex(double x, double y);

  /**
   * Vymaže zadaný vrchol. Ako dôsledok prečísluje vrcholy s vyššími číslami.
   * @param vertex vymazávaný vrchol
   */
  public void removeVertex(Vertex vertex);


  /**
   * Pridá hranu medzi vrcholmi v1 a v2.
   * Vytvorí teda dva objekty typu Edge, jeden z v1 do v2 a druhý naopak.
   * Farba a hodnota hrany budú prednastavené hodnoty.
   * @param v1 počiatočný vrchol hrany
   * @param v2 koncový vrchol hrany
   * @return Vytvorená hrana z v1 do v2.
   * @throws IllegalArgumentException ak taká hrana už existuje,
   *     alebo ak v1=v2 alebo ak v1 alebo v2 nie sú v grafe.
   */
  public Edge addEdge(Vertex v1, Vertex v2) throws IllegalArgumentException;

  /**
   * Pridá hranu medzi vrcholmi s indexami n1 a n2.
   * Vytvorí teda dva objekty typu Edge, jeden z n1 do n2 a druhý naopak.
   * @param n1 index počiatočného vrchola
   * @param n2 index koncového vrchola
   * @return Vytvorená hrana z n1 do n2.
   * @throws IndexOutOfBoundsException ak vrchol s id n1 alebo n2 neexistuje
   * @throws IllegalArgumentException ak taká hrana už existuje alebo ak n1 = n2
   */
  public Edge addEdge(int n1, int n2) throws IndexOutOfBoundsException, IllegalArgumentException;

  /**
   * Vymaže hranu e z grafu, ako aj jej kópiu v opačnom smere.
   * @param e Hrana, ktorú chceme zmazať.
   * @throws IllegalArgumentException ak taká hrana neexistuje
   */
  public void removeEdge(Edge e) throws IllegalArgumentException;



  /**
   * Vymaže hranu medzi vrcholmi v1 a v2 z grafu,
   * ako aj jej kópiu v opačnom smere.
   * @param v1 vrchol
   * @param v2 vrchol
   * @throws IllegalArgumentException ak taká hrana neexistuje
   */
  public void removeEdge(Vertex v1, Vertex v2) throws IllegalArgumentException;

  /**
   * Vymaže hranu medzi vrcholmi s indexami n1 a n2 z grafu,
   * ako aj jej kópiu v opačnom smere.
   * @param n1 index vrchola
   * @param n2 index vrchola
   * @throws IndexOutOfBoundsException  ak také indexy neexistujú
   * @throws IllegalArgumentException ak taká hrana neexistuje
   */
  public void removeEdge(int n1, int n2) throws IndexOutOfBoundsException, IllegalArgumentException;

  /**
   * Nájde hranu medzi vrcholmi v1 a v2.
   * @param v1 vrchol
   * @param v2 vrchol
   * @return nájdená hrana, alebo null ak neexistuje
   */
  public Edge findEdge(Vertex v1, Vertex v2);

  /**
   * Nájde hranu medzi vrcholmi s indexami n1 a n2.
   * @param n1 index vrchola
   * @param n2 index vrchola
   * @return nájdená hrana, alebo null ak neexistuje
   * @throws IndexOutOfBoundsException ak také indexy neexistujú
   */
  public Edge findEdge(int n1, int n2) throws IndexOutOfBoundsException;

  /**
   * Vymaže všetky vrcholy a hrany z grafu.
   */
  public void clear();

  /**
   * Vypíše graf do výstupného súboru.
   * @param out Printstream, kam sa vypisuje graf.
   * @param full Ak je nastavené na true, vypíšu sa aj farby
   *     a veľkosti vrcholov.
   */
  public void print(PrintStream out, boolean full);


  /**
   * Načíta graf zo Scannera s.
   * Všetky predtým existujúce vrcholy a hrany budú zmazané.
   *
   * @param s Scanner zo vstupného súboru
   * @throws DataFormatException v prípade, že formát vstupu nesedí
   */
  public void read(Scanner s) throws DataFormatException;

}
