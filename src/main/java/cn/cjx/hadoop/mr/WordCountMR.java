package cn.cjx.hadoop.mr;

import com.google.common.collect.Lists;
import org.apache.hadoop.conf.Configuration;
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

import java.io.IOException;
import java.util.List;

public class WordCountMR {
    // 1.map
    private static class WordCountMapper extends Mapper<LongWritable, Text, Text, IntWritable> {
        private Text mapOutputKey = new Text();
        private IntWritable mapOutputValue = new IntWritable(1);

        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            System.out.println("map=> keyIn:" + key + " valueIn:" + value);
            String[] strings = value.toString().split(" ");
            for (String string : strings) {
                mapOutputKey.set(string);
                System.out.println("map=> keyOut:" + mapOutputKey + " valueOut:" + mapOutputValue);
                context.write(mapOutputKey, mapOutputValue);
            }
        }
    }

    // 2.reduce
    private static class WordCountReduce extends Reducer<Text, IntWritable, Text, IntWritable> {
        private IntWritable outputValue = new IntWritable();

        @Override
        protected void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
            List<IntWritable> list = Lists.newArrayList(values);
            System.out.println("reduce=> keyIn:" + key + " values:" + list);
            int sum = 0;
            for (IntWritable value : list) {
                sum += value.get();
            }
            outputValue.set(sum);
            System.out.println("reduce=> keyOut:" + key + " values:" + outputValue);
            context.write(key, outputValue);
        }
    }

    private int run(String[] args) throws Exception {
        //driver
        //1) get conf
        Configuration configuration = new Configuration();
        //2) create job
        Job job = Job.getInstance(configuration, this.getClass().getSimpleName());
        job.setJarByClass(this.getClass());
        //3.1) input
        Path path = new Path(args[0]);
        FileInputFormat.addInputPath(job, path);
        //3.2) map
        job.setMapperClass(WordCountMapper.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(IntWritable.class);
        //3.3) reduce
        job.setReducerClass(WordCountReduce.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(IntWritable.class);
        //3.4) output
        Path output = new Path(args[1]);
        FileOutputFormat.setOutputPath(job, output);
        //4) commit
        boolean suc = job.waitForCompletion(true);
        return suc ? 0 : 1;
    }

    // 注释
    public static void main(String[] args) {
        if (args.length<2){
            args = new String[]{
                    "hdfs://172.168.0.2:9000/user/root/data/wordcount.txt",
                    "hdfs://172.168.0.2:9000/user/root/mr/result/output"
            };
        }

        WordCountMR wordCountMR = new WordCountMR();
        try {
            //判断路径是否存在
            Path fileOutPath = new Path(args[1]);
            FileSystem fileSystem = FileSystem.get(new Configuration());
            if (fileSystem.exists(fileOutPath)) {
                fileSystem.delete(fileOutPath, true);
            }
            int status = wordCountMR.run(args);
            System.exit(status);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
