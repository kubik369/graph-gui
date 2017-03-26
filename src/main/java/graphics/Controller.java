package graphics;

import graphgui.MyPanel;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;

/**
 * Created by siegrift on 3/25/17.
 */
public class Controller {

  public MyPanel graphPanel;
  public AnchorPane rightPanel;
  public AnchorPane leftPanel;
  public Button mode1Button;
  public Button mode2Button;
  public Button mode3Button;
  public TextField commandLineField;
  public Label statusLabel;
  public BorderPane root;
  public AnchorPane mainPanel;
  public Label vertexLabel;
  public Label vertexNumberLabel;
  public TextField vertexValueField;
  public Label vertexColorClickableLabel;
  public ComboBox fromVertexComboBox;
  public ComboBox toVertexComboBox;
  public TextField edgeValueField;
  public Label edgeColorClickableField;
  public MenuBar menuBar;
  public Menu fileMenu;
  public Menu editMenu;
  public Menu helpMenu;
  public MenuItem closeProgramMenuItem;
}
