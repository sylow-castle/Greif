package coder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

public class AttributeCoder implements DotWriter{
  private SortedMap<String, String> attributeMap;

  protected AttributeCoder(Collection<? extends DotAttribute> attributes) {
    attributeMap = new TreeMap<String, String>();

    if(null != attributes) {
      for(DotAttribute a : attributes) {
        attributeMap.put(a.getKey(), a.getValue());
      }
    }
  }

  @Override
  public List<String> writeDot() {
    List<String> result = new ArrayList<String>();

    if(attributeMap.isEmpty()) {
      result.add("");
      return result;
    }

    String code = "[";
    String delimiter = "";
    for(String Name : attributeMap.keySet()) {
      if( (null != attributeMap.get(Name)) && (attributeMap.get(Name).length() > 0) ){
        code = code + delimiter + "\"" + Name + "\"=\"" + attributeMap.get(Name) + "\"";
        delimiter = ", ";
      }
    }
    code = code + "]";


    result.add(code);
    return result;
  }




}
