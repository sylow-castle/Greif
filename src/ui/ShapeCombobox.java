package ui;

import coder.nodeAttribute.Shape;
import javafx.scene.control.ComboBox;

public class ShapeCombobox {
  public ShapeCombobox(){

  }

  public static ComboBox<Shape> getCmb() {
    ComboBox<Shape> shapecmb = new ComboBox<Shape>();
    for(Shape s : Shape.values()) {
      shapecmb.getItems().add(s);
    }

    return shapecmb;
  }
}
