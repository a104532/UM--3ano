package g8;

import java.io.IOException;

public class Demultiplexer implements AutoCloseable {
    public Demultiplexer(TaggedConnection conn) { }
    public void start() { }
    public void send(TaggedConnection.Frame frame) throws IOException { }
    public void send(int tag, byte[] data) throws IOException {  }
    public byte[] receive(int tag) throws IOException, InterruptedException { return null; }
    public void close() throws IOException { }
}
