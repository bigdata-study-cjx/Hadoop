package cn.cjx.hadoop.join;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
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
import java.util.List;

public class ReduceJoin extends Configured implements Tool {

    /**
     * map
     * TODO
     */
    private static class ReduceJoinMapper extends Mapper<LongWritable, Text, Text, ReduceJoinWritable> {

        private Text outputKey = new Text();
        private ReduceJoinWritable outputValue = new ReduceJoinWritable();

        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            String[] values = value.toString().split(",");
            if (3 != values.length && 4 != values.length) return;

            //customer
            if (3 == values.length) {
                String cid = values[0];
                String name = values[1];
                String telephone = values[2];
                outputKey.set(cid);
                outputValue.set(TableName.CUSTOMER, name + "," + telephone);
            }

            //order
            if (4 == values.length) {
                String cid = values[1];
                String price = values[2];
                String productName = values[3];
                outputKey.set(cid);
                outputValue.set(TableName.ORDER, price + "," + productName);
            }

//            System.out.println("Map: "+ outputKey.toString()+", "+ outputValue.toString());
            context.write(outputKey, outputValue);
        }

    }

    /**
     * reduce
     */
    private static class ReduceJoinReduce extends Reducer<Text, ReduceJoinWritable, NullWritable, Text> {

        private Text outputValue = new Text();

        @Override
        protected void reduce(Text key, Iterable<ReduceJoinWritable> values, Context context) throws IOException, InterruptedException {
            // <cid,List(customerInfo,orderInfo,orderInfo,orderInfo)>
/*            System.out.println(key.toString());
            for (ReduceJoinWritable value : values) {
                System.out.println(value.getTag());
                System.out.println(value.getData());
            }*/
            String customerInfo = null;
            List<String> orderList = new ArrayList<String>();

            for (ReduceJoinWritable value : values) {
                if (TableName.CUSTOMER.equals(value.getTag())) {
                    customerInfo = value.getData();
                } else if (TableName.ORDER.equals(value.getTag())) {
                    orderList.add(value.getData());
                }
            }

            /*System.out.println("customerInfo: "+ customerInfo);
            for (String s : orderList) {
                System.out.println("orderInfo: "+ s);
            }*/
            for (String orderInfo : orderList) {
                if(customerInfo == null) continue;
                outputValue.set(key.toString() + "," + customerInfo + "," + orderInfo);
                context.write(NullWritable.get(), outputValue);
            }
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
        job.setMapperClass(ReduceJoinMapper.class);
        // TODO
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(ReduceJoinWritable.class);

        //3.3) reduce
        job.setReducerClass(ReduceJoinReduce.class);
        // TODO
        job.setOutputKeyClass(NullWritable.class);
        job.setOutputValueClass(Text.class);

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
                    "hdfs://172.168.0.2:9000/user/root/datas/join",
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
            int status = ToolRunner.run(configuration, new ReduceJoin(), args);
            System.exit(status);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
