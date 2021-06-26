package cn.cjx.hadoop.mr.sort.secondSort;

import org.apache.hadoop.io.WritableComparable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Objects;

public class PairWritable implements WritableComparable<PairWritable> {
    // a#12,12
    private String first;
    private int second;

    public PairWritable() {
    }

    public PairWritable(String first, int second) {
        this.first = first;
        this.second = second;
    }

    public String getFirst() {
        return first;
    }

    public void setFirst(String first) {
        this.first = first;
    }

    public int getSecond() {
        return second;
    }

    public void setSecond(int second) {
        this.second = second;
    }

    @Override
    public String toString() {
        return "PairWritable{" +
                "first='" + first + '\'' +
                ", second=" + second +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PairWritable that = (PairWritable) o;
        if (second != that.second) return false;
        return first != null ? first.equals(that.first) : that.first == null;
    }

    @Override
    public int hashCode() {
        int result = first != null ? first.hashCode() : 0;
        result = 31 * result + second;
        return result;
    }

    public int compareTo(PairWritable o) {
        int comp = this.getFirst().compareTo(o.getFirst());
        if (0 == comp){
            return Integer.valueOf(this.getSecond()).compareTo(Integer.valueOf(o.getSecond()));
        }
        return comp;
    }

    public void write(DataOutput out) throws IOException {
        out.writeUTF(first);
        out.writeInt(second);
    }

    public void readFields(DataInput in) throws IOException {
        this.first = in.readUTF();
        this.second = in.readInt();
    }
}
