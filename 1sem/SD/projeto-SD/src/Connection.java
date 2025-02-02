package src;


import java.io.*;
import java.net.Socket;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Connection implements AutoCloseable {

    Socket socket;
    DataInputStream in;
    DataOutputStream out;
    Lock ls = new ReentrantLock();
    Lock lr = new ReentrantLock();

    public Connection(Socket socket) throws IOException {
        this.socket = socket;
        in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));

    }

    public void send(Frame frame) throws IOException {
        ls.lock();
        try{
            frame.serialize(out);
            out.flush();

        } finally{
            ls.unlock();
        }
    }

    public Frame receive() throws IOException {
        lr.lock();
        try{
            return Frame.deserialize(in);
        } finally{
            lr.unlock();
        }
    }

    public void close() throws IOException {
        socket.close();
    }
}
