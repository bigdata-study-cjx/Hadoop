package cn.cjx.hadoop.mr.sort;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class SortMR extends Configured implements Tool {

    /**
     * map
     * TODO
     */
    private static class SortMapper extends Mapper<LongWritable, Text, Text, IntWritable> {
        private Text outputKey = new Text();
        private IntWritable outputValue = new IntWritable();

        @Override
        protected void setup(Context context) throws IOException, InterruptedException {
            //TODO
        }

        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            String[] values = value.toString().split(" ");
            if (2 != values.length)
                return;

            outputKey.set(values[0]);
            outputValue.set(Integer.valueOf(values[1]));
            context.write(outputKey, outputValue);
        }

        @Override
        protected void cleanup(Context context) throws IOException, InterruptedException {
            //TODO
        }
    }

    /**
     * reduce
     */
    private static class SortReduce extends Reducer<Text, IntWritable, Text, IntWritable> {

        private IntWritable outputValue = new IntWritable();

        @Override
        protected void setup(Context context) throws IOException, InterruptedException {
            //TODO
        }

        @Override
        protected void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
            ArrayList<Integer> valueList = new ArrayList<Integer>();

            for (IntWritable value : values) {
                valueList.add(value.get());
            }

            Collections.sort(valueList);

            for (Integer value : valueList) {
                outputValue.set(value);
                context.write(key, outputValue);
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
        job.setMapperClass(SortMapper.class);
        // TODO
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(IntWritable.class);

        //1.分区
        //job.setPartitionerClass();

        //2.排序
        //job.setSortComparatorClass();

        //3.combiner
        // job.setCombinerClass(WordCountCombiner.class);

        //compress
        // http://bigdata:19888/ -> Job -> Configuration -> 所有MapReduce的参数
        // 开启压缩
//        configuration.set("mapreduce.map.output.compress","true");
        // 设置压缩算法
//        configuration.set("mapreduce.map.output.compress.codec","org.apache.hadoop.io.compress.SnappyCodec");

        //5.分组
        //job.setGroupingComparatorClass();

        //3.3) reduce
        job.setReducerClass(SortReduce.class);
        // TODO
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);

        // 设置reduce数量
        job.setNumReduceTasks(2);
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
                    "hdfs://172.168.0.2:9000/user/root/datas/secondSort.txt",
                    "hdfs://172.168.0.2:9000/user/root/mr/result/output"
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
            ToolRunner.run(configuration, new SortMR(), args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
