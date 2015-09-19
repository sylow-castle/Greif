package ui;

import graph.Edge;
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

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TableView;
import javafx.scene.control.TabPane;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
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
  private final ObservableSet<String> tableNames;
  private final ObservableSet<SimpleStringTable> tables;
  private final Map<String, SimpleStringTable> tableMap;
  private final Map<String, TableView<DefaultRow>> tableViews;

  private Map<String, String> columnToAttributeId;

  //定数
  private static final String DOT_WRITE_PATH = runner.Main.prop.getProperty("DOTFilePath");
  private static final String LABEL = "label";
  private static final String COLOR = "color";
  private static final String SHAPE = "shape";
  private static final String STYLE = "style";
  private static final String END = "end";
  private static final String START = "start";
  private static final String ID = "id";

  public TableSchema(String filePath) throws SQLException, IOException {
    this.filePath = filePath;
    this.schemaView = new TabPane();

    this.tableNames = FXCollections.<String> observableSet();
    this.tables = FXCollections.<SimpleStringTable> observableSet();
    this.tableMap = new HashMap<String, SimpleStringTable>();

    this.tableViews = new HashMap<String, TableView<DefaultRow>>();

    this.columnToAttributeId = createDefaultAttributeMap();

    //テーブル変更時の動作
    tables.addListener(new SetChangeListener<SimpleStringTable>() {
      @Override
      public void onChanged(SetChangeListener.Change<? extends SimpleStringTable> change) {
        if (change.wasAdded()) {
          SimpleStringTable table = change.getElementAdded();
          //tableNames、tableMapとの整合性を取る
          tableMap.put(table.getName(), table);
          tableNames.add(table.getName());
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

      for (String tableName : this.tableNames) {
        ResultSet values = dbFile.createStatement().executeQuery("select * from " + tableName);
        loadTableData(tableMap.get(tableName), values);
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

  public void addTablesListener(SetChangeListener<? super String> listener) {
    tableNames.addListener(listener);
  }

  public void removeTablesListener(SetChangeListener<? super String> listener) {
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
      tab.setText(tableId);
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

  public List<DefaultRow> getVerticesAsList() {
    List<DefaultRow> list = new ArrayList<DefaultRow>();
    @SuppressWarnings("unchecked")
    TableView<DefaultRow> table = (TableView<DefaultRow>) schemaView.lookup("#vertex").lookup("#table");
    for (DefaultRow row : table.getItems()) {
      list.add(row);
    }
    return list;
  }

  public List<DefaultRow> getEdgesAsList() {
    List<DefaultRow> list = new ArrayList<DefaultRow>();
    @SuppressWarnings("unchecked")
    TableView<DefaultRow> table = (TableView<DefaultRow>) schemaView.lookup("#edge").lookup("#table");
    for (DefaultRow row : table.getItems()) {
      list.add(row);
    }
    return list;
  }

  private static Map<String, String> createDefaultAttributeMap() {
    Map<String, String> defaultAttributeMap = new HashMap<String, String>();
    defaultAttributeMap.put(ID, ID);
    defaultAttributeMap.put(LABEL, LABEL);
    defaultAttributeMap.put(START, START);
    defaultAttributeMap.put(END, END);
    defaultAttributeMap.put(STYLE, STYLE);
    defaultAttributeMap.put(SHAPE, SHAPE);
    defaultAttributeMap.put(COLOR, COLOR);

    return defaultAttributeMap;
  }

  public void export() {
    IdentifiedGraph<String> baseGraph;
    Map<String, DefaultRow> vertexInfo;
    Map<Edge, DefaultRow> edgeInfo;

    baseGraph = new IdentifiedGraph<String>();
    vertexInfo = new HashMap<String, DefaultRow>();
    edgeInfo = new HashMap<Edge, DefaultRow>();

    //グラフのトポロジーを設定
    //頂点の追加
    for (DefaultRow vertex : this.getVerticesAsList()) {
      String key = vertex.getValueMap().get(ID).get();
      baseGraph.addVertex(key);
      vertexInfo.put(key, vertex);
    }

    //辺の追加
    for (DefaultRow edge : this.getEdgesAsList()) {
      String start = edge.getValueMap().get(START).get();
      String end = edge.getValueMap().get(END).get();

      if (start != null && end != null) {
        Edge addedEdge = baseGraph.addEdge(start, end);
        edgeInfo.put(addedEdge, edge);
      }
    }

    //Dot言語の構成
    List<String> givenColumnName = new ArrayList<String>();
    givenColumnName.add(LABEL);
    givenColumnName.add(SHAPE);
    givenColumnName.add(COLOR);

    Set<VertexCoder> VCoder = new HashSet<VertexCoder>();
    //頂点に属性を付与
    for (String vertex : baseGraph.getAllVertexAsSet()) {
      List<DotAttribute> attrList = new ArrayList<DotAttribute>();
      Map<String, StringProperty> row = vertexInfo.get(vertex).getValueMap();
      for (String columnName : givenColumnName) {
        attrList.add(new SimpleAttribute(
            columnToAttributeId.get(columnName), row.get(columnName).get()));
      }

      VCoder.add(new VertexCoder(vertex, attrList));
    }

    //辺に属性を付与
    Set<EdgeCoder> ECoder = new HashSet<EdgeCoder>();
    for (Edge edge : baseGraph.getAllEdgeAsSet()) {
      String start = baseGraph.valueOf(edge.getStart());
      String end = baseGraph.valueOf(edge.getEnd());
      ECoder.add(new EdgeCoder(start, end, true));
    }

    GraphCoder GCoder = new GraphCoder(VCoder, ECoder);

    //生成したDot言語の書き出し
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