package ui;

import graph.IdentifiedGraph;

import java.net.URL;
import java.sql.SQLException;

import coder.DotAttribute;
import coder.EdgeCoder;
import coder.GraphCoder;
import coder.SimpleAttribute;
import coder.VertexCoder;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TableView;
import javafx.scene.control.TabPane;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.io.IOException;

import runner.TextFileWriter;
import table.Column;
import table.SimpleStringTable;
import database.DbFileLoader;

public class TableSchema {
  //フルパス
  private String filePath;

  //対応するタブ
  private TabPane schemaView;

  //頂点、辺等のテーブル名をキー
  //テーブルが持つ属性の名前一覧を値としたMap。
  private final ObservableSet<StringProperty> tableNames;
  private final ObservableSet<SimpleStringTable> tables;
  private final Map<String, SimpleStringTable> tableMap;
  private final Map<String, TableView<DefaultRow>> tableViews;
  private SimpleStringTable vertexTable;
  private SimpleStringTable edgeTable;

  //定数
  private static final String DOT_WRITE_PATH = runner.Main.prop.getProperty("DOTFilePath");

  public TableSchema(String filePath) throws SQLException, IOException {
    this.filePath = filePath;
    this.schemaView = new TabPane();

    this.tableNames = FXCollections.<StringProperty> observableSet();
    this.tables = FXCollections.<SimpleStringTable> observableSet();
    this.tableMap = new HashMap<String, SimpleStringTable>();

    this.tableViews = new HashMap<String, TableView<DefaultRow>>();

    //テーブル変更時の動作
    tables.addListener(new SetChangeListener<SimpleStringTable>() {
      @Override
      public void onChanged(SetChangeListener.Change<? extends SimpleStringTable> change) {
        if (change.wasAdded()) {
          SimpleStringTable table = change.getElementAdded();
          //tableNames、tableMapとの整合性を取る
          tableMap.put(table.getName(), table);
          tableNames.add(new SimpleStringProperty(table.getName()));
          tableViews.put(table.getName(), buildTableView(table.getName(), table));
        }

        if (change.wasRemoved()) {
          String name = change.getElementRemoved().getName();
          Tab removeTab = null;
          for(Tab tab : schemaView.getTabs()) {
            if(null != tab.getId() && tab.getId().equals(name)){
              removeTab = tab;
              break;
            }
          }
          schemaView.getTabs().remove(removeTab);
          tableMap.remove(name);
          tableNames.remove(name);
        }
      }
    });

    //見てくれ部分
    Node content = (new TableManager(this)).getView();
    Tab managerTab = new Tab("テーブル一覧");
    managerTab.setContent(content);
    this.schemaView.getTabs().add(managerTab);

    try {
      //テーブルの追加
      Connection dbFile = DbFileLoader.loadDbFile(filePath);
      Statement fileStatement = dbFile.createStatement();
      ResultSet tables = fileStatement.executeQuery("select name from sqlite_master where type = 'table'");

      while (tables.next()) {
        String tableName = tables.getString("name");
        this.addTable(tableName);
      }

      for (StringProperty tableNameProp : this.tableNames) {
        String tableName = tableNameProp.get();
        ResultSet values = dbFile.createStatement().executeQuery("select * from " + tableName);
        loadTableData(tableMap.get(tableName), values);
      }


      Set<String> names =
          this.tableNames.stream()
          .map(tbl -> tbl.get())
          .collect(Collectors.toSet());
      if(names.contains("vertex")) {
        this.vertexTable = this.tableMap.get("vertex");
      }

      if(names.contains("edge")) {
        this.edgeTable = this.tableMap.get("edge");
      }
      dbFile.close();
    } catch (SQLException e) {
      this.schemaView = null;
      e.printStackTrace();
    }
  }
  enum TableObject {
    ROOT,
    TABLE,
    COLUMN;

    public static int count = 0;
  }

  public void addTable(String name) {
    tables.add(new SimpleStringTable(name));
  }

  public void removeTable(String name) {
    if(tableNames.contains(name)) {
      tables.remove(tableMap.get(name));
    }
  }

  public void addTablesListener(SetChangeListener<? super StringProperty> listener) {
    tableNames.addListener(listener);
  }

  public void removeTablesListener(SetChangeListener<? super StringProperty> listener) {
    tableNames.removeListener(listener);
  }

  public SimpleStringTable getTable(String tableName) {
    return this.tableMap.get(tableName);
  }

  private void loadTableData(SimpleStringTable table, ResultSet tableData) {
    try {
      //列の設定
      //あわせて列名のListの作成とColumnの追加
      int cardOfColumns = tableData.getMetaData().getColumnCount();
      for (int i = 0; i < cardOfColumns; i++) {
        String columnName = tableData.getMetaData().getColumnName(i + 1);
        table.addColumn(columnName);
      }

      //行の設定
      while (tableData.next()) {
        Map<Column, String> record = table.getTemplateRecord();

        //行の値の組立て
        for (Column column: record.keySet()) {
          record.put(column, tableData.getString(column.getName()));
        }

        table.addRecord(record);

      }
    } catch (SQLException e) {
      this.schemaView = null;
      e.printStackTrace();
    }
  }

  private TableView<DefaultRow> buildTableView(String tableId, SimpleStringTable table) {
    //シーングラフへのTab追加
    try {
      URL url = ui.view.fxmlRoot.class.getResource("DefaultTableTab.fxml");
      FXMLLoader loader = new FXMLLoader(url);
      Tab tab = loader.<Tab> load();

      //タブの設定
      tab.setId(tableId);
      tab.textProperty().bind(table.NameProperty());
      tab.getContent().setId(tableId);
      schemaView.getTabs().add(tab);

      @SuppressWarnings("unchecked")
      TableView<DefaultRow> view = (TableView<DefaultRow>) tab.getContent().lookup("#table");
      DefaultTableTabController controller = loader.<DefaultTableTabController> getController();

      //テーブルの元データを設定
      controller.setTableModel(table);

      return view;
    } catch (IOException e) {
      // TODO 自動生成された catch ブロック
      e.printStackTrace();
      return null;
    }
  }

  public TabPane getSchemaView() {
    return schemaView;
  }

  public List<Map<Column, String>> getVerticesAsList() {
    return this.vertexTable.getAllRecords();
  }

  public List<Map<Column, String>> getEdgesAsList() {
    return this.edgeTable.getAllRecords();
  }

  public void export() {
    IdentifiedGraph<String> baseGraph;
    baseGraph = new IdentifiedGraph<String>();

    //グラフを設定
    //頂点の追加
    Set<VertexCoder> VCoder = new HashSet<VertexCoder>();
    for (Map<Column, String> vertex : this.getVerticesAsList()) {
      String id = vertex.get(this.vertexTable.getColumn("id"));
      baseGraph.addVertex(id);

      //頂点に属性を付与
      List<DotAttribute> attrList = new ArrayList<DotAttribute>();
      for (String columnName : this.vertexTable.getColumns()) {
        String attrValue = vertex.get(this.vertexTable.getColumn(columnName));
        attrList.add(new SimpleAttribute(columnName, attrValue));
      }
      VCoder.add(new VertexCoder(id, attrList));
    }

    //辺の追加
    Set<EdgeCoder> ECoder = new HashSet<EdgeCoder>();
    for (Map<Column, String> edge : this.getEdgesAsList()) {
      String startId = edge.get(this.edgeTable.getColumn("start"));
      String endId = edge.get(this.edgeTable.getColumn("end"));
      if (startId != null && endId != null) {
        baseGraph.addEdge(startId, endId);
        ECoder.add(new EdgeCoder(startId, endId, true));
      }
    }

    GraphCoder GCoder = new GraphCoder(VCoder, ECoder);
    //Dot言語の書き出し
    new TextFileWriter(DOT_WRITE_PATH).writeFile(GCoder.writeDot());
  }

  public void save() {
    this.save(this.filePath);
  }

  public void save(String filePath) {
    try {
      Connection dbFile = DbFileLoader.createMemoryDB();
      Statement stmt = dbFile.createStatement();
      stmt.addBatch("begin;");

     //create文の作成
      for (SimpleStringTable table : this.tables) {
        //create文作成
        String sql = "";
        sql = "create table " + table.getName() + " ";

        List<String> columns = new ArrayList<String>();
        for (String columName : table.getColumns()) {
          columns.add(columName + " text");
        }
        sql = sql + "(" + for_now.Utilities.serealizeString(columns, ", ") + ");";
        stmt.addBatch(sql);
      }

      //insert文の作成
      for (SimpleStringTable table : this.tables) {
        String sql = "";

        //テーブルのinsert文
        List<Map<String, String>> records = new ArrayList<>();
        for (Map<Column, String> record : table.getAllRecords()) {
          Map<String, String> row = new HashMap<>();
          for(Column column : record.keySet()) {
            row.put(column.getName(), record.get(column));
          }
          records.add(row);
        }

        for (Map<String, String> record : records) {
          List<String> columns = table.getColumns();
          sql = "insert into " + table.getName() + " ";
          sql = sql + "(" + for_now.Utilities.serealizeString(columns, ", ") + ") ";

          //代入用の値の文字列化
          List<String> values = new ArrayList<String>();
          for (String columnName : columns) {
            String brace = "'";
            if (null == record.get(columnName)) {
              brace = "";
            }
            values.add(brace + record.get(columnName) + brace);
          }

          sql = sql + "values (" + for_now.Utilities.serealizeString(values, ", ") + ");";
          stmt.addBatch(sql);
        }
      }

      stmt.addBatch("commit;");
      stmt.executeBatch();
      stmt.executeUpdate("backup to " + filePath);
      dbFile.close();

    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
}