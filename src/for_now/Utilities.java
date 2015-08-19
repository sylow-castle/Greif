package for_now;

import java.util.List;

public class Utilities {

  public static int boolToNum(boolean v) {
    if(v){
      return 1;
    }
    return 0;
  }

  public static String serealizeString(List<String> list, String delimiter) {
    if(null == list ) {
      return "";
    }

    if(null == delimiter ) {
      delimiter = "";
    }

    String connectedString = "";
    String delim = "";
    for(String string : list) {
      connectedString = connectedString + delim + string;
      delim = delimiter;
    }
    return connectedString;
  }
}
