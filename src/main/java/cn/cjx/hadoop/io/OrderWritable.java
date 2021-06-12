package cn.cjx.hadoop.io;


import org.apache.hadoop.io.WritableComparable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class OrderWritable implements WritableComparable<OrderWritable> {

    private String orderId;
    private float price;

    public OrderWritable(String orderId, float price) {
        this.set(orderId, price);
    }

    public void set(String orderId, float price) {
        this.orderId = orderId;
        this.price = price;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public int compareTo(OrderWritable o) {
        int compare = this.getOrderId().compareTo(o.getOrderId());
        if (0 == compare) {
            compare = Float.valueOf(price).compareTo(Float.valueOf(o.getPrice()));
        }
        return compare;
    }

    public void write(DataOutput out) throws IOException {
        out.writeUTF(orderId);
        out.writeFloat(price);
    }

    public void readFields(DataInput in) throws IOException {
        this.orderId = in.readUTF();
        this.price = in.readInt();
    }
}
