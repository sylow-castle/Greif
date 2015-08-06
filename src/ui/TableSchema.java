package ui;

import graph.Edge;
import graph.IdentifiedGraph;

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
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TabPane;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleStringProperty;

import java.io.IOException;

import runner.TextFileWriter;
import table.SimpleStringTable;
import database.DbFileLoader;

public class TableSchema {
  //フルパス
  private String filePath;

  //対応するタブ
  private TabPane schemaView;

  //頂点、辺等のテーブル名をキー
  //テーブルが持つ属性の名前一覧を値としたMap。
  private ObservableList<String> tableNames;
  private Map<String, SimpleStringTable> tables;
  private Map<String, String> columnToAttributeId;
  private Map<String, TableView<DefaultRow>> tableViews;

  //定数
  private static final String DOT_WRITE_PATH = "D:\\temp\\dotlang.dot";
  private static final String LABEL = "label";
  private static final String COLOR = "color";
  private static final String SHAPE = "shape";
  private static final String STYLE = "style";
  private static final String END = "end";
  private static final String START = "start";
  private static final String ID = "id";

  //

  public TableSchema(String filePath) throws SQLException, IOException {
    try {
      this.filePath = filePath;
      this.schemaView = new TabPane();
      this.tables = new HashMap<String, SimpleStringTable>();
      this.tableViews = new HashMap<String, TableView<DefaultRow>>();
      this.tableNames = FXCollections.<String>observableArrayList();
      this.columnToAttributeId = createDefaultAttributeMap();

      //テーブルの追加
      Connection dbFile = DbFileLoader.loadDbFile(filePath);
      Statement fileStatement = dbFile.createStatement();
      ResultSet tables = fileStatement.executeQuery("select name from sqlite_master where type = 'table'");

      while (tables.next()) {
        String tableName = tables.getString("name");
        this.tableNames.add(tableName);
      }

      for(String tableName : this.tableNames) {
        ResultSet values = dbFile.createStatement().executeQuery("select * from " + tableName);
        SimpleStringTable table = buildSimpleTable(values);
        this.tables.put(tableName, table);
        this.tableViews.put(tableName, buildTableView(tableName, table));
      }
      dbFile.close();
    } catch (SQLException e) {
      this.schemaView = null;
      e.printStackTrace();
    }
  }

  private SimpleStringTable buildSimpleTable(ResultSet tableData) {
    SimpleStringTable table = new SimpleStringTable();

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
        Map<String, String> record = new HashMap<String, String>();
        table.addRecord(record);

        //行の値の組立て
        for (String columnName : table.getColumns()) {
          record.put(columnName, tableData.getString(columnName));
        }
      }
    } catch (SQLException e) {
      this.schemaView = null;
      e.printStackTrace();
    }

    return table;
  }

  private TableView<DefaultRow> buildTableView(String tableId, SimpleStringTable table) {
    //シーングラフへのTab追加
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource("../ui/DefaultTableTab.fxml"));
      Tab tab = loader.<Tab> load();

      Map<String, String> tabName = getTabNameMap();

      //タブの設定
      tab.setText(tabName.get(tableId));
      tab.getContent().setId(tableId);
      schemaView.getTabs().add(tab);

      @SuppressWarnings("unchecked")
      TableView<DefaultRow> view = (TableView<DefaultRow>) tab.getContent().lookup("#table");
      DefaultTableTabController controller = loader.<DefaultTableTabController> getController();

      //列の設定
      ObservableList<String> columns = FXCollections.<String> observableArrayList();
      columns.addListener(controller.<String> getAddColumnListener());
      for (String columnName : table.getColumns()) {
        columns.add(columnName);
      }

      //行の設定
      for (Map<String, String> record : table.getAllRecords()) {
        DefaultRow row = new DefaultRow();
        view.getItems().add(row);

        //追加するDataRow用のvalueMapの組み立て
        ObservableMap<String, StringProperty> valueMap = FXCollections.<String, StringProperty> observableHashMap();
        row.setValueMap(valueMap);

        //valueMapの組立て
        for (String columnName : table.getColumns()) {
          SimpleStringProperty value = new SimpleStringProperty(record.get(columnName));
          valueMap.put(columnName, value);
        }
      }

      return view;
    } catch (IOException e) {
      // TODO 自動生成された catch ブロック
      e.printStackTrace();
      return null;
    }
  }

  private Map<String, String> getTabNameMap() {
    Map<String, String> tabNameMap = new HashMap<String, String>();
    tabNameMap.put("vertex", "頂点");
    tabNameMap.put("edge", "辺");

    return tabNameMap;
  }

  public TabPane getSchemaView() {
    return schemaView;
  }

  public List<DefaultRow> getVerticesAsList() {
    List<DefaultRow> list = new ArrayList<DefaultRow>();
    @SuppressWarnings("unchecked")
    TableView<DefaultRow> table = (TableView<DefaultRow>) schemaView.lookup("#vertex").lookup("#table");
    for(DefaultRow row : table.getItems()) {
      list.add(row);
    }
    return list;
  }

  public List<DefaultRow> getEdgesAsList() {
    List<DefaultRow> list = new ArrayList<DefaultRow>();
    @SuppressWarnings("unchecked")
    TableView<DefaultRow> table = (TableView<DefaultRow>) schemaView.lookup("#edge").lookup("#table");
    for(DefaultRow row : table.getItems()) {
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
      ECoder.add(new EdgeCoder(start, end));
    }

    GraphCoder GCoder = new GraphCoder(VCoder, ECoder);

    //生成したDot言語の書き出し
    new TextFileWriter(DOT_WRITE_PATH).writeFile(GCoder.writeDot());
  }

  public void save() {
    try {
      Connection dbFile = DbFileLoader.createMemoryDB();
      Statement stmt = dbFile.createStatement();
      stmt.addBatch("begin;");


      //テーブルの作成
      Map<String, SimpleStringTable> tables = new HashMap<String, SimpleStringTable>();
      for (String tableName : this.tableNames) {
        //テーブルの作成
        TableView<DefaultRow> view = (TableView<DefaultRow>) this.schemaView.lookup("#" + tableName).lookup("#table");
        SimpleStringTable table = this.tables.get(tableName);
        table.removeAllRecords();
        for(DefaultRow row : view.getItems()) {
          table.addRecord(row.degenerateRow());
        }

        tables.put(tableName, table);
      }

      //create文の作成
      for (String tableName : this.tableNames) {
        //create文作成
        String sql ="";
        sql = "create table " + tableName + " ";

        List<String> columns = new ArrayList<String>();
        for (String columName : tables.get(tableName).getColumns()) {
          columns.add(columName + " text");
        }
        sql = sql + "(" + for_now.Utilities.serealizeString(columns, ", ") + ");";
        stmt.addBatch(sql);
      }

      //insert文の作成
      for (String tableName : this.tableNames) {
        String sql ="";

        SimpleStringTable table = tables.get(tableName);
        //テーブルのinsert文
        for (Map<String, String> record : table.getAllRecords()) {
          List<String> columns = table.getColumns();
          sql = "insert into " + tableName + " ";
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
      stmt.executeUpdate("backup to " + this.filePath);
      dbFile.close();

    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
}