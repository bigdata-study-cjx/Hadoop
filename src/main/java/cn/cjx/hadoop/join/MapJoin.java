package cn.cjx.hadoop.join;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class MapJoin extends Configured implements Tool {

    static Map<String, String> customerMap = new HashMap<String, String>();

    /**
     * map
     * TODO
     */
    private static class MapJoinMapper extends Mapper<LongWritable, Text, Text, Text> {

        private Text outputKey = new Text();
        private Text outputValue = new Text();

        @Override
        protected void setup(Context context) throws IOException, InterruptedException {
            Configuration configuration = context.getConfiguration();
            URI[] cacheFiles = Job.getInstance(configuration).getCacheFiles();
            Path path = new Path(cacheFiles[0]);
            FileSystem fileSystem = FileSystem.get(configuration);
            InputStream inputStream = fileSystem.open(path);

            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                if (line.trim().length() > 0) {
                    customerMap.put(line.split(",")[0], line);
                }
            }

            bufferedReader.close();
            inputStreamReader.close();
            inputStream.close();
        }

        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            //100,1,45.50,product-1
            String lineValue = value.toString();
            StringTokenizer stringTokenizer = new StringTokenizer(lineValue, ",");
            while (stringTokenizer.hasMoreTokens()) {
                String wordValue = stringTokenizer.nextToken();
                if (customerMap.get(wordValue) != null) {
                    outputKey.set(wordValue);
                    outputValue.set(customerMap.get(wordValue) + lineValue);
                    context.write(outputKey, outputValue);
                    break;
                }
            }
        }

        @Override
        protected void cleanup(Context context) throws IOException, InterruptedException {
            //TODO
        }
    }

    /**
     * run
     *
     * @param args
     * @return
     * @throws Exception
     */
    public int run(String[] args) throws Exception {
        //driver
        //1) get conf
        Configuration configuration = this.getConf();
        //2) create job
        Job job = Job.getInstance(configuration, this.getClass().getSimpleName());
        job.setJarByClass(this.getClass());
        //3.1) input
        Path path = new Path(args[0]);
        FileInputFormat.addInputPath(job, path);
        //3.2) map
        job.setMapperClass(MapJoinMapper.class);
        // TODO
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(Text.class);

        URI uri = new URI(args[2]);
        job.addCacheFile(uri);

        //3.4) output
        Path output = new Path(args[1]);
        FileOutputFormat.setOutputPath(job, output);
        //4) commit
        boolean suc = job.waitForCompletion(true);
        return suc ? 0 : 1;
    }

    public static void main(String[] args) {
        if (args.length < 2) {
            args = new String[]{
                    "hdfs://172.168.0.2:9000/user/root/datas/join/order.txt",
                    "hdfs://172.168.0.2:9000/user/root/mr/result/output",
                    "hdfs://172.168.0.2:9000/user/root/datas/join/custom.txt",
            };
        }

        Configuration configuration = new Configuration();
        try {
            //判断路径是否存在
            Path fileOutPath = new Path(args[1]);
            FileSystem fileSystem = FileSystem.get(configuration);
            if (fileSystem.exists(fileOutPath)) {
                fileSystem.delete(fileOutPath, true);
            }
            ToolRunner.run(configuration, new MapJoin(), args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
