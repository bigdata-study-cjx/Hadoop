package cn.cjx.hadoop.io;

import org.apache.hadoop.io.Writable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class UserWritable implements Writable {

    private int id;
    private String name;

    public UserWritable(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public void set(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void write(DataOutput out) throws IOException {
        out.writeInt(id);
        out.writeUTF(name);
    }

    public void readFields(DataInput in) throws IOException {
        this.id = in.readInt();
        this.name = in.readUTF();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserWritable that = (UserWritable) o;

        if (id != that.id) return false;
        return name != null ? name.equals(that.name) : that.name == null;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "UserWritable{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
