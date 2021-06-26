package cn.cjx.hadoop.mr.sort.secondSort;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.mapreduce.Partitioner;

public class FirstPartitioner extends Partitioner<PairWritable, IntWritable> {
    @Override
    public int getPartition(PairWritable key, IntWritable intWritable, int numReduceTasks) {
        return (key.hashCode() & Integer.MAX_VALUE) % numReduceTasks;
    }
}
