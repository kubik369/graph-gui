package graphgui;

/**
 * Trieda špecificky nastavená pre jednotlivé skúšky.
 * Obsahuje názov vstupného a výstupného súboru,
 * volanie editora a pod.
 */
public class ExamSpecifics {

  /**
   * Metóda vytvorí objekt typu Editor a zavolá niektorú z jeho metód na editovanie.
   */
  public static void edit(ExtendedGraph graph) throws Exception {
    Editor e = new Editor((Graph)graph);
    try {
      if (State.getState().getSelectedVertex() != null) {
        e.editVertex(State.getState().getSelectedVertex());
      } else {
        e.edit();
      }
    } catch (Exception ex) {
      throw ex;
    }
  }

  /**
   * Metóda vráti meno vstupného súboru.
   */
  public static String getInFileName() {
    return "vstup.txt";
  }

  /**
   * Metóda vráti meno výstupného súboru.
   */
  public static String getOutFileName() {
    return "vystup.txt";
  }
}
