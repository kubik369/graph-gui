package graphgui.utils;

import javafx.geometry.Bounds;
import javafx.geometry.VPos;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontSmoothingType;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextBoundsType;

public class GraphicsHelpers {
  /**
   * Metoda ktora nam vrati Text ktory bude napchany v obldzniku ktory mu zadame.
   * @param bounds Obldznik ktory ohranicuje rozmery Text-u
   * @param c Farba textu
   * @param s Samotny text
   *
   * @return Text s co najvacsim pismom vopchany do zadaneho obdlznika
   */
  public static Text createBoundedText(String s, Rectangle bounds, Color c) {
    Text text = new Text(s);
    text.setFontSmoothingType(FontSmoothingType.LCD);
    text.setTextAlignment(TextAlignment.CENTER);
    text.setTextOrigin(VPos.CENTER);
    text.setFill(c);
    // nastavime na velke pismo a potom zoskalujeme
    text.setFont(new Font("Verdana", 100));
    text.setBoundsType(TextBoundsType.LOGICAL_VERTICAL_CENTER);
    Bounds textBounds = text.getBoundsInLocal();
    double scale = Math.min(bounds.getWidth() / textBounds.getWidth(),
        bounds.getHeight() / (text.getLayoutBounds().getHeight()));
    text.setScaleX(bounds.getWidth() / textBounds.getWidth());
    text.setScaleY(bounds.getHeight() / textBounds.getWidth());
    // mpocas skalovania sa menia "layout bounds"
    text.setX(
        bounds.getX() + bounds.getWidth() / 2 - text.getLayoutBounds().getWidth() / 2);
    text.setY(bounds.getY() + bounds.getHeight() / 2);
    
    return text;
  }
}
