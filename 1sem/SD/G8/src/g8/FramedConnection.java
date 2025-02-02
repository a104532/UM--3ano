package g8;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;


public class FramedConnection implements AutoCloseable {
    private final Socket socket;
    private final DataInputStream in;
    private final DataOutputStream out;

    Lock ls = new ReentrantLock();
    Lock lr = new ReentrantLock();

    public FramedConnection(Socket socket) throws IOException {
        this.socket = socket;
        in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
    }
    public void send(byte[] data) throws IOException {
        ls.lock();
        try{
            out.writeInt(data.length);
            out.write(data);
            out.flush();
        } finally{
            ls.unlock();
        }

    }
    public byte[] receive() throws IOException {
        lr.lock();
        try{
            int length = in.readInt();
            byte[] data = new byte[length];
            in.readFully(data);

            return data;
        } finally{
           lr.unlock();
        }
    }
    public void close() throws IOException {
        socket.close();
    }
}
