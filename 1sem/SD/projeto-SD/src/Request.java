package src;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Request {
    private final Frame frame;
    private final Lock lock = new ReentrantLock();
    private final Condition completed = lock.newCondition();
    private final Connection connection;
    private final DataStorage dataStorage;


    public Request(Frame frame, Connection connection, DataStorage dataStorage) {
        this.frame = frame;
        this.connection = connection;
        this.dataStorage = dataStorage;
    }

    public Frame getFrame() {
        return frame;
    }

    public Connection getConnection() {
        return connection;
    }

    public DataStorage getDataStorage() {
        return dataStorage;
    }
}
