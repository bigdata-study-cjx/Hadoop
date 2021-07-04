package cn.cjx.hadoop.join;

import org.apache.hadoop.io.Writable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class ReduceJoinWritable implements Writable {
    private String tag;
    private String data;

    public ReduceJoinWritable() {
    }

    public ReduceJoinWritable(String tag, String data) {
        this.set(tag, data);
    }

    public void set(String tag, String data) {
        this.tag = tag;
        this.data = data;
    }

    public void write(DataOutput out) throws IOException {
        out.writeUTF(this.getTag());
        out.writeUTF(this.getData());
    }

    public void readFields(DataInput in) throws IOException {
        this.setTag(in.readUTF());
        this.setData(in.readUTF());
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ReduceJoinWritable{" +
                "tag='" + tag + '\'' +
                ", data='" + data + '\'' +
                '}';
    }
}
