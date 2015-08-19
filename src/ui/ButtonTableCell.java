package ui;

import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TableCell;

public class ButtonTableCell<S, T> extends TableCell<S, T> {
  private final Button button;


  public ButtonTableCell() {
    button = new Button("Edit");
  }

  @Override
  protected void updateItem(T item, boolean empty) {
    super.updateItem(item, empty);
    if(!empty){
      setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
      setGraphic(button);
    }
  }
}
