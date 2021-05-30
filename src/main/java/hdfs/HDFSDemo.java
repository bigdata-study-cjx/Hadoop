package hdfs;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;

import java.io.IOException;

public class HDFSDemo {

    private FileSystem getFileSystem() throws IOException {
        Configuration configuration = new Configuration();//需要把文件：core-site.xml，hdfs-site.xml复制到 resources 目录下
        return FileSystem.get(configuration);
    }

    private void readHDFSFile(String filePath) {
        FSDataInputStream fsDataInputStream = null;
        try {
            Path path = new Path(filePath);
            fsDataInputStream = this.getFileSystem().open(path);
            IOUtils.copyBytes(fsDataInputStream, System.out, 4096, false);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fsDataInputStream != null) {
                IOUtils.closeStream(fsDataInputStream);
            }
        }
    }

    public static void main(String[] args) {
        String filePath = "hdfs://172.168.0.2:9000/user/root/data/core-site.xml";
        HDFSDemo hdfsDemo = new HDFSDemo();
        hdfsDemo.readHDFSFile(filePath);
    }
}
