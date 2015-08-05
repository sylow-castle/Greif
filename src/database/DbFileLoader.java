package database;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DbFileLoader {

  public static Connection createMemoryDB() throws SQLException {
    Connection connection = null;

    try {
      connection = DriverManager.getConnection("jdbc:sqlite::memory:");
    } catch (SQLException e) {
      e.printStackTrace();
    }

    return connection;
  }


  public static Connection loadDbFile(String filePath)  {
    Connection dbFileconnection = null;
    try {
      dbFileconnection = DriverManager.getConnection("jdbc:sqlite:" + filePath);
      return dbFileconnection;
    } catch(SQLException e) {
      // if the error message is "out of memory",
      // it probably means no database file is found
      System.err.println(e.getMessage());
      return null;
    }
  }

  public static ResultSet loadTable(Connection connection, String tableName) {
    try {
      String sql = "select * from " + tableName;
      ResultSet rs = connection.createStatement().executeQuery(sql);
      return rs;
    } catch(SQLException e) {
      // if the error message is "out of memory",
      // it probably means no database file is found
      System.err.println(e.getMessage());
      return null;
    }
  }


  public static boolean printTableData(Connection Db, String TableName) throws SQLException{
    try {
      Statement statement = Db.createStatement();
      ResultSet rs = statement.executeQuery("select * from " + TableName);
      String[] ColumnName = new String[rs.getMetaData().getColumnCount()];

      for(int i = 0; i < ColumnName.length; i++) {
        ColumnName[i] = rs.getMetaData().getColumnName(i+1);
      }

      while(rs.next()){
        for(int i = 0; i < ColumnName.length; i++) {
          System.out.print(", " + rs.getString(i+1));
        }
        System.out.println("");
      }

      return true;
    } catch (SQLException e){
      e.printStackTrace();
      return false;
    }
  }


}