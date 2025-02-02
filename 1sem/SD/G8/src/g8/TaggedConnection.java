package g8;
import java.io.*;
import java.net.Socket;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TaggedConnection implements AutoCloseable {
    public static class Frame {
        public final int tag;
        public final byte[] data;

        public Frame(int tag, byte[] data) {
            this.tag = tag;
            this.data = data;
        }
    }
    Socket socket;
    DataInputStream in;
    DataOutputStream out;
    Lock ls = new ReentrantLock();
    Lock lr = new ReentrantLock();

    public TaggedConnection(Socket socket) throws IOException {
        this.socket = socket;
        in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));

    }

    public void send(Frame frame) throws IOException {
        send(new Frame(frame.tag, frame.data));
    }

    public void send(int tag, byte[] data) throws IOException {
        ls.lock();
        try{
            out.writeInt(tag);
            out.writeInt(4 + data.length);
            out.write(data);
            out.flush();

        } finally{
            ls.unlock();
        }
    }

    public Frame receive() throws IOException {
        lr.lock();
        try{
            int tag = in.readInt();
            int length = in.readInt();
            byte[] data = new byte[length - 4];
            in.readFully(data);
            return new Frame(tag, data);
        } finally{
            lr.unlock();
        }
    }

    public void close() throws IOException {
        socket.close();
    }
}