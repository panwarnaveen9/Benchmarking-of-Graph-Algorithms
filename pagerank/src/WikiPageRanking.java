import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;


import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.TextInputFormat;
import org.apache.hadoop.mapred.TextOutputFormat;




public class WikiPageRanking {
    
    private static NumberFormat nf = new DecimalFormat("00");
    
    public static void main(String[] args) throws Exception {
        WikiPageRanking pageRanking = new WikiPageRanking();
        
       //pageRanking.runXmlParsing("/wiki/xml_100MB", "/wiki/iter00");
       String str= "/wiki/iter"; //file name iter00
        
        int runs = 0;
        for (; runs < 5; runs++) {
            pageRanking.runRankCalculation(str+nf.format(runs), str+nf.format(runs + 1));
        }
        
        pageRanking.runRankOrdering(str+nf.format(runs), "/wiki/result"); 
        
    }
    
    public void runXmlParsing(String inputPath, String outputPath) throws IOException {
        JobConf conf = new JobConf(WikiPageRanking.class);
        
        conf.set(XmlInputFormat.START_TAG_KEY, "<page>");
        conf.set(XmlInputFormat.END_TAG_KEY, "</page>");
        
        // Input / Mapper
        FileInputFormat.setInputPaths(conf, new Path(inputPath));
        conf.setInputFormat(XmlInputFormat.class);
        conf.setMapperClass(WikiPageLinksMapper.class);
        
        // Output / Reducer
        FileOutputFormat.setOutputPath(conf, new Path(outputPath));
        conf.setOutputFormat(TextOutputFormat.class);
        conf.setOutputKeyClass(Text.class);
        conf.setOutputValueClass(Text.class);
        conf.setReducerClass(WikiLinksReducer.class);
        
        JobClient.runJob(conf);
    }

    private void runRankCalculation(String inputPath, String outputPath) throws IOException {
        JobConf conf = new JobConf(WikiPageRanking.class);
        
        conf.setOutputKeyClass(Text.class);
        conf.setOutputValueClass(Text.class);
        
        conf.setInputFormat(TextInputFormat.class);
        conf.setOutputFormat(TextOutputFormat.class);
        
        FileInputFormat.setInputPaths(conf, new Path(inputPath));
        FileOutputFormat.setOutputPath(conf, new Path(outputPath));
        
        conf.setMapperClass(RankCalculateMapper.class);
        conf.setReducerClass(RankCalculateReduce.class);
        
        JobClient.runJob(conf);
    }
    
    private void runRankOrdering(String inputPath, String outputPath) throws IOException {
        JobConf conf = new JobConf(WikiPageRanking.class);
        
        conf.setOutputKeyClass(FloatWritable.class);
        conf.setOutputValueClass(Text.class);
        conf.setInputFormat(TextInputFormat.class);
        conf.setOutputFormat(TextOutputFormat.class);
        
        FileInputFormat.setInputPaths(conf, new Path(inputPath));
        FileOutputFormat.setOutputPath(conf, new Path(outputPath));
        
        conf.setMapperClass(RankingMapper.class);
        
        JobClient.runJob(conf);
    }
    
}
