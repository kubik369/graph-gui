package main.graphgui;

/**
 * Rozhranie pre hranu grafu.
 * Každá hrana spája dva rôzne vrcholy u a v,
 * pričom je reprezentovaná dvoma objektami typu Edge.
 * Jeden reprezentuje smer z u do v a druhý z v do u.
 * Tieto dva objekty predstavujú ekvivalentnú
 * repretentáciu tej istej hrany.
 * Koncové vrcholy hrany sa nazývajú origin (začiatok)
 * a destination (koniec).
 * Okrem má každá hrana priradenú tiež celočíselnú hodnotu (value).
 * Oba smery tej istej hrany majú vždy tú istú hodnotu.
 */
public interface Edge {

  /** Vráti počiatočný vrchol hrany. */
  public Vertex getOrigin();

  /** Vráti id počiatočného vrchola hrany. */
  public int getOriginId();

  /** Vráti koncový vrchol hrany. */
  public Vertex getDestination();

  /** Vráti id koncového vrchola hrany. */
  public int getDestinationId();

  /** Vráti celočíselnú hodnotu uloženú na hrane. */
  int getValue();

  /**
   * Nastaví hodnotu hrany na zadané číslo. Modifikuje aj hodnotu hrany idúcej v opačnom smere.
   * @param value nová hodnota hrany.
   */
  void setValue(int value);

  /**
   * Zistí, či hrana this a hrana e sú ekvivalentné,
   * čo nastane vtedy, keď je to ten istý objekt, alebo ak
   * sú to kópie tej istej hrany idúce v opačnom smere.
   */
  public boolean isEquivalent(Edge e);

  /** Vráti objekt reprezentujúci tú istú hranu v opčanom smere. */
  Edge getReverse();

  /**
   * Vráti buď this alebo reverse() podľa toho, ktorý smer hrany je
   * primárny, teda uložený v zozname všetkých hrán grafu.
   */
  Edge getPrimary();
}
