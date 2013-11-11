
import java.util.HashMap;
import java.util.Map;

import org.apache.hadoop.io.Text;

public class Node1 {

  public static enum Color {
    WHITE, GRAY, BLACK
  };

  private final int id;
  private int distance;
 // private List<HashMap<Integer,Integer>> edges = new ArrayList<HashMap<Integer,Integer>>();
  Map<Integer,Integer> edges = new HashMap<Integer,Integer>();
  private Color color = Color.WHITE;

  public Node1(String str) {
	//System.out.println("\n\n\n------------------------ "+ str +" --------------- \n");
    String[] map = str.split("\t");
    //System.out.println("\n\n\n------------------------" + map[0] + " \n\n" +  map[1] + "\n --------------- \n");
    String key = map[0];
    String value = map[1];	

    String[] tokens = value.split("\\|");

    this.id = Integer.parseInt(key);

    for (String s : tokens[0].split(",")) {
      if (s.length() > 0) {
    	  String[] temp = s.split(":");
        edges.put(Integer.parseInt(temp[0]),Integer.parseInt(temp[1]));
      }
    }
    
    if (tokens[1].equals("Integer.MAX_VALUE")) {
      this.distance = Integer.MAX_VALUE;
    } else {
      this.distance = Integer.parseInt(tokens[1]);
    }
    
    this.color = Color.valueOf(tokens[2]);

  }

  public Node1(int id) {
    this.id = id;
  }

  public int getId() {
    return this.id;
  }

  public int getDistance() {
    return this.distance;
  }

  public void setDistance(int distance) {
    this.distance = distance;
  }

  public Color getColor() {
    return this.color;
  }

  public void setColor(Color color) {
    this.color = color;
  }

  public Map<Integer,Integer> getEdges() {
    return this.edges;
  }

  public void setEdges(Map<Integer,Integer> edges) {
    this.edges = edges;
  }

  public Text getLine() {
    StringBuffer s = new StringBuffer();
    int cnt=0;
    int si=edges.size();
    for (int v : edges.keySet()) {
    	//
    	String temp = Integer.toString(v) + ":" + Integer.toString(edges.get(v));
    	if(cnt<si-1)
    		s.append(temp).append(',');
    	else
    		s.append(temp);
    	cnt++;
    			
    }
    //s.deleteCharAt(s.length()-1); 
    s.append("|");

    if (this.distance < Integer.MAX_VALUE) {
      s.append(this.distance).append("|");
    } else {
      s.append("Integer.MAX_VALUE").append("|");
    }

    s.append(color.toString());

    return new Text(s.toString());
  }

}
