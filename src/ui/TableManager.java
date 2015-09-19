package ui;
import java.util.HashMap;
import java.util.Map;

import table.Column;
import table.SimpleStringTable;
import ui.TableSchema.TableObject;
import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.SetChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.TextFieldTreeCell;
import javafx.util.Callback;

public class TableManager {
  private final TableSchema schema;
  private final TreeView<String> view;
  private final Map<TreeItem<String> , TableObject> objectMap;

  public Node getView(){
    return this.view;
  }

  public TableManager(TableSchema schema) {
    this.schema = schema;
    this.view = new TreeView<String>();
    this.view.setEditable(true);
    this.objectMap = new HashMap<>();

    view.setCellFactory(new Callback<TreeView<String>, TreeCell<String>>() {
      @Override
      public TreeCell<String> call(TreeView<String> param) {
        TreeCell<String> returnCell;
        returnCell = TextFieldTreeCell.forTreeView().call(param);
        returnCell.setEditable(true);

        ChangeListener<? super TreeItem<String>> listener = new ChangeListener<TreeItem<String>>() {
          @Override
          public void changed(ObservableValue<? extends TreeItem<String>> observable, TreeItem<String> oldValue,
              TreeItem<String> newValue) {
              if(null != newValue){
                returnCell.setContextMenu(createContextMenu(newValue));

                MenuItem edit = new MenuItem("名前を変更");
                edit.setOnAction(new EventHandler<ActionEvent>() {
                  @Override
                  public void handle(ActionEvent arg0) {
                    param.edit(newValue);
                  }
                });
                returnCell.getContextMenu().getItems().add(edit);
              }
          }
        };

        returnCell.treeItemProperty().addListener(listener);
        return returnCell;
      }
    });

    //ルートアイテムの設定
    TreeItem<String> rootItem = new TreeItem<String>("Tables");
    rootItem.setExpanded(true);
    view.setRoot(rootItem);
    this.objectMap.put(rootItem, TableObject.ROOT);

    //テーブル追加・削除時の動作設
    schema.addTablesListener(new TableChangeListener(rootItem, TableObject.TABLE));
  }


  void addTableObject(TreeItem<String> parent, TreeItem<String> addItem, String element, TableObject type) {
    addItem.setExpanded(true);
    addItem.setValue(element);
    parent.getChildren().add(addItem);
    objectMap.put(addItem, type);

    if(type.equals(TableObject.TABLE)) {
      SimpleStringTable addedTable = schema.getTable(addItem.getValue());
      addedTable.addColumnListener(new ColumnsChangeListener(addItem));
    }


  }

  private class TableChangeListener implements SetChangeListener<String> {
    private TreeItem<String> item;
    private TableObject childType;

    TableChangeListener(TreeItem<String> item, TableObject childType) {
      this.item = item;
      this.childType = childType;
    }

    @Override
    public void onChanged(SetChangeListener.Change<? extends String> change) {
      if(change.wasAdded()) {
        addTableObject(item, new TreeItem<String>(), change.getElementAdded(), childType);
      }

      if(change.wasRemoved()) {
        TreeItem<String> removeItem = null;
        for(TreeItem<String> item : item.getChildren()) {
          if(item.getValue().equals(change.getElementRemoved())) {
            removeItem = item;
            break;
          }
        }

        item.getChildren().remove(removeItem);
        objectMap.remove(removeItem);
      }
    }
  }

  private class ColumnsChangeListener implements SetChangeListener<Column> {
    private TreeItem<String> item;
    private TableObject childType = TableObject.COLUMN;

    ColumnsChangeListener(TreeItem<String> item) {
      this.item = item;
    }

    @Override
    public void onChanged(SetChangeListener.Change<? extends Column> change) {
      //TODO 削除
      System.out.println("Colmn listener was called");
      if(change.wasAdded()) {
        Column addedColumn = change.getElementAdded();
        TreeItem<String> columnItem = new TreeItem<String>();
        addTableObject(item, columnItem, addedColumn.getName(), childType);

        //ColumnにTreeItemの名前を変えるリスナーを設定
        //ColumnからTreeItemへの変化は無条件で起こす。
        addedColumn.nameProperty().addListener(new ChangeListener<String>() {
          @Override
          public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
            //TODO 削除
            System.out.println("カラム ─通知→ アイテム");
            columnItem.setValue(newValue);
          }
        });

        //TreeItemにColumnの名前を変えるリスナーを設定。
        //ただしTreeItemからColumnへの変化は二つが異なっている時のみ
        ChangeListener<? super String> listener;
        listener = new ChangeListener<String> () {
          @Override
          public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
            //TODO 削除
            System.out.println("アイテム ─通知→ カラム");
            if(!addedColumn.getName().equals(newValue)) {
              System.out.println("valueが変更されました");
              addedColumn.setName(newValue);
            }
          }
        };
        columnItem.valueProperty().addListener(listener);
      }

      if(change.wasRemoved()) {
        TreeItem<String> removeItem = null;
        for(TreeItem<String> item : item.getChildren()) {
          if(item.getValue().equals(change.getElementRemoved().getName())) {
            removeItem = item;
            break;
          }
        }
        item.getChildren().remove(removeItem);
        objectMap.remove(removeItem);
      }
    }
  }

  ContextMenu createContextMenu(TreeItem<String> item) {
    ContextMenu menu = new ContextMenu();




    MenuItem addTable = new MenuItem("新しい表を追加");
    addTable.setOnAction((ActionEvent e) -> {
      schema.addTable("new Table" + TableObject.count);
      TableObject.count++;
      return;
    });

    MenuItem addColumn = new MenuItem("新しい列を追加");


    MenuItem removeTable = new MenuItem("この表を削除");
    removeTable.setOnAction((ActionEvent e) -> {
      schema.removeTable(item.getValue());
    });


    MenuItem removeColumn = new MenuItem("この列を削除");
    removeColumn.setOnAction((ActionEvent e) -> {
      schema.getTable(item.getParent().getValue()).removeColumn(item.getValue());
    });

    switch (objectMap.get(item)) {
    case ROOT :
      menu.getItems().add(addTable);
      break;

    case TABLE:
      menu.getItems().add(addTable);
      menu.getItems().add(removeTable);
      menu.getItems().add(addColumn);
      addColumn.setOnAction((ActionEvent e) -> {
        schema.getTable(item.getValue()).addColumn("new Column" + TableObject.count);
        TableObject.count++;
        return;
      });
      break;

    case COLUMN:
      menu.getItems().add(addColumn);
      addColumn.setOnAction((ActionEvent e) -> {
        schema.getTable(item.getParent().getValue()).addColumn("new Column" + TableObject.count);
        TableObject.count++;
        return;
      });

      menu.getItems().add(removeColumn);

      break;
    }
    return menu;
  }


}
