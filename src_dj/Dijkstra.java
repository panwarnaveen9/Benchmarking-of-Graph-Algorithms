import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.mapred.RunningJob;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

/**
* This is an example Hadoop Map/Reduce application.
*
* It inputs a map in adjacency list format, and performs a breadth-first search.
* The input format is
* ID   EDGES|DISTANCE|COLOR
* where
* ID = the unique identifier for a node (assumed to be an int here)
* EDGES = the list of edges emanating from the node (e.g. 3,8,9,12)
* DISTANCE = the to be determined distance of the node from the source
* COLOR = a simple status tracking field to keep track of when we're finished with a node
* It assumes that the source node (the node from which to start the search) has
* been marked with distance 0 and color GRAY in the original input.  All other
* nodes will have input distance Integer.MAX_VALUE and color WHITE.
*/
public class Dijkstra extends Configured implements Tool {

  public static final Log LOG = LogFactory.getLog("org.apache.hadoop.examples.Dijkstra");
  static int end_flag1=0;
  /**
   * Nodes that are Color.WHITE or Color.BLACK are emitted, as is. For every
   * edge of a Color.GRAY node, we emit a new Node1 with distance incremented by
   * one. The Color.GRAY node is then colored black and is also emitted.
   */
  public static class MapClass extends MapReduceBase implements
      Mapper<LongWritable, Text, IntWritable, Text> {

    public void map(LongWritable key, Text value, OutputCollector<IntWritable, Text> output,
        Reporter reporter) throws IOException {

      Node1 node = new Node1(value.toString());
      Map<Integer,Integer> temp=node.getEdges();
      //int minimum = Integer.MAX_VALUE;
      // For each GRAY node, emit each of the edges as a new node (also GRAY)
      if (node.getColor() == Node1.Color.GRAY) {
        for (int v : temp.keySet()) {
          Node1 vnode = new Node1(v);
          vnode.setDistance(node.getDistance() + temp.get(v));
          vnode.setColor(Node1.Color.GRAY);
          output.collect(new IntWritable(vnode.getId()), vnode.getLine());
        }
        end_flag1++;
        // We're done with this node now, color it BLACK
        node.setColor(Node1.Color.BLACK);
      }

      // No matter what, we emit the input node
      // If the node came into this method GRAY, it will be output as BLACK
      output.collect(new IntWritable(node.getId()), node.getLine());

    }
  }

  /**
   * A reducer class that just emits the sum of the input values.
   */
  public static class Reduce extends MapReduceBase implements
      Reducer<IntWritable, Text, IntWritable, Text> {

    /**
     * Make a new node which combines all information for this single node id.
     * The new node should have
     * - The full list of edges
     * - The minimum distance
     * - The darkest Color
     */
    public void reduce(IntWritable key, Iterator<Text> values,
        OutputCollector<IntWritable, Text> output, Reporter reporter) throws IOException {

    Map<Integer,Integer> edges = null;
      int distance = Integer.MAX_VALUE;  
      Node1.Color color = Node1.Color.WHITE;

      while (values.hasNext()) {
        Text value = values.next();

        Node1 u = new Node1(key.get() + "\t" + value.toString());
        Map<Integer,Integer> temp=u.getEdges();
        // One (and only one) copy of the node will be the fully expanded
        // version, which includes the edges
        if (temp.size() > 0) {
          edges = u.getEdges();
        }
        int dis_flag=0;
        // Save the minimum distance
        if (u.getDistance() < distance) {
          //u.setColor(Node1.Color.GRAY);
        	if(distance!=Integer.MAX_VALUE)
        		dis_flag=1;
          distance = u.getDistance();
        }

        // Save the darkest color
        if(dis_flag==0){
        if (u.getColor().ordinal() > color.ordinal()) {
          color = u.getColor();
        }}// my if
        else
        	color = Node1.Color.GRAY;

      }

      Node1 n = new Node1(key.get());
      n.setDistance(distance);
      n.setEdges(edges);
      n.setColor(color);
      output.collect(key, new Text(n.getLine()));
    
    }
  }

  static int printUsage() {
    System.out.println("graphsearch [-m <num mappers>] [-r <num reducers>]");
    ToolRunner.printGenericCommandUsage(System.out);
    return -1;
  }

  private JobConf getJobConf(String[] args) {
    JobConf conf = new JobConf(getConf(), Dijkstra.class);
    conf.setJobName("graphsearch");

    // the keys are the unique identifiers for a Node1 (ints in this case).
    conf.setOutputKeyClass(IntWritable.class);
    // the values are the string representation of a Node1
    conf.setOutputValueClass(Text.class);

    conf.setMapperClass(MapClass.class);
    conf.setReducerClass(Reduce.class);

    for (int i = 0; i < args.length; ++i) {
      if ("-m".equals(args[i])) {
        conf.setNumMapTasks(Integer.parseInt(args[++i]));
      } else if ("-r".equals(args[i])) {
        conf.setNumReduceTasks(Integer.parseInt(args[++i]));
      }
    }

    return conf;
  }

  /**
   * The main driver for word count map/reduce program. Invoke this method to
   * submit the map/reduce job.
   *
   * @throws IOException
   *           When there is communication problems with the job tracker.
   */
  public int run(String[] args) throws Exception {

    int iterationCount = 0;

    while (keepGoing(iterationCount)) {
    	end_flag1=0;
      String input;
      if (iterationCount == 0)
        input = "/inputs/dj_graph2";
      else
        input = "output-graph-" + iterationCount;

      String output = "output-graph-" + (iterationCount + 1);

      JobConf conf = getJobConf(args);
      FileInputFormat.setInputPaths(conf, new Path(input));
      FileOutputFormat.setOutputPath(conf, new Path(output));
      RunningJob job = JobClient.runJob(conf);
      
      iterationCount++;
      if(end_flag1==0)
    	  break;
    }

    return 0;
  }
 
  private boolean keepGoing(int iterationCount) {
    /*if(iterationCount >= 4) {
      return false;
    }*/
   
    return true;
  }

  public static void main(String[] args) throws Exception {
    int res = ToolRunner.run(new Configuration(), new Dijkstra(), args);
    System.exit(res);
  }

}