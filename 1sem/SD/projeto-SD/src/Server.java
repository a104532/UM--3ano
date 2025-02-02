package src;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.locks.*;

public class Server {
    private final DataStorage dataStorage; // Armazenamento de dados
    private final Lock lock; // Lock para gerenciar o controle de concorrência
    private final Condition connectionAvailable; // Condition para controlar os clientes
    private int activeConnections; // Número de conexões ativas
    private final int maxConnections; // Número máximo de conexões concorrentes
    private final CustomQueue<Request> requestQueue;
    private final  Thread[] threadPool;

    public Server(int maxConnections) throws IOException {

        this.dataStorage = new DataStorage();
        this.maxConnections = maxConnections;
        this.lock = new ReentrantLock();
        this.connectionAvailable = lock.newCondition();
        this.activeConnections = 0;
        this.requestQueue = new CustomQueue<>();
        this.threadPool = new Thread[maxConnections];
    }

    private void initWorkerThreads() {
        for (int i = 0; i < threadPool.length; i++) {
            threadPool[i] = new Thread(() -> {
                while (true) {
                    try {
                        if(!requestQueue.isEmpty()) {
                            Request request = requestQueue.take();
                            processRequest(request);
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            });
            threadPool[i].start();
        }
    }

    public void addRequest(Request request) {
        requestQueue.put(request);
    }


    private void processRequest(Request request) {
        try {
            Frame frame = request.getFrame();
            Connection con = request.getConnection();
            DataStorage dataStorage = request.getDataStorage();

            System.out.println("Processando frame tipo: " + frame.getType());

            switch (frame.getType()) {
                case Frame.LOGIN: {
                    if (!(frame.getData() instanceof User)) {
                        System.out.println("Erro: Dados não são do tipo User");
                        return;
                    }
                    User user = (User) frame.getData();
                    boolean isAuthenticated = dataStorage.authenticateUser(user.getUser(), user.getPass());
                    con.send(new Frame(frame.getTag(),isAuthenticated,Frame.LOGIN,true));
                    break;
                }
                case Frame.CREATE_ACCOUNT:{
                    if (!(frame.getData() instanceof User)) {
                        System.out.println("Erro: Dados não são do tipo User");
                        return;
                    }
                    User user = (User) frame.getData();
                    boolean accountCreated = dataStorage.registerUser(user.getUser(), user.getPass());
                    con.send(new Frame(frame.getTag(),accountCreated,Frame.CREATE_ACCOUNT,true));
                    break;
                }
                case Frame.PUT:{
                    if (!(frame.getData() instanceof PutOne)) {
                        System.out.println("Erro: Dados não são do tipo PutOne");
                        return;
                    }
                    PutOne put = (PutOne) frame.getData();
                    dataStorage.put(put.getKey(), put.getValue());
                    con.send(new Frame(frame.getTag(), true, Frame.PUT, true));
                    break;
                }
                case Frame.GET:{
                    String key = (String) frame.getData();
                    byte[] value = (byte[]) dataStorage.get(key);
                    con.send(new Frame(frame.getTag(),value != null ? value : new byte[0],Frame.GET,true));
                    break;
                }
                case Frame.MULTIPUT: {
                    try {
                        Map<String, byte[]> map = (Map<String, byte[]>) frame.getData();
                        if (map != null && !map.isEmpty()) {
                            dataStorage.multiPut(map);
                            con.send(new Frame(frame.getTag(), true, Frame.MULTIPUT, true));
                        } else {
                            con.send(new Frame(frame.getTag(), false, Frame.MULTIPUT, true));
                        }
                    } catch (ClassCastException e) {
                        System.out.println("MultiPut: Erro de formato de dados - " + e.getMessage());
                        con.send(new Frame(frame.getTag(), false, Frame.MULTIPUT, true));
                    }
                    break;
                }
                case Frame.MULTIGET:{
                    Set<String> keys = (Set<String>) frame.getData();
                    Map<String, byte[]> results = dataStorage.multiGet(keys);
                    con.send(new Frame(frame.getTag(),results,Frame.MULTIGET,true));
                    break;
                }
                case Frame.GETWHEN:{
                    GetWhen getWhen = (GetWhen) frame.getData();
                    byte[] value = dataStorage.getWhen(getWhen.getKey(), getWhen.getConditionKey(), getWhen.getConditionValue());
                    con.send(new Frame(frame.getTag(), value != null ? value : new byte[0], Frame.GETWHEN, true));
                    break;
                }
                default:
                    System.out.println("Comando desconhecido: " + frame.getTag());
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void start(int port) throws IOException {
        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("Servidor iniciado na porta " + port);

        while (true) {
            Socket clientSocket = null;
            try {
                clientSocket = serverSocket.accept();

                lock.lock();
                try {
                    while (activeConnections >= maxConnections) {
                        System.out.println("Limite de conexões atingido. Cliente em espera...");
                        connectionAvailable.await();
                    }
                    activeConnections++;
                    System.out.println("Nova conexão aceita. Conexões ativas: " + activeConnections);
                    new Thread(new Worker(clientSocket, dataStorage)).start();
                } finally {
                    lock.unlock();
                }
            } catch (InterruptedException e) {
                if (clientSocket != null) {
                    clientSocket.close();
                }
                break;
            }
        }
    }

    private class Worker implements Runnable {
        private final Socket socket;
        private final DataStorage dataStorage;

        public Worker(Socket socket, DataStorage dataStorage) {
            this.socket = socket;
            this.dataStorage = dataStorage;
        }

        @Override
        public void run() {
            try(Connection connection = new Connection(socket)) {
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        Frame frame = connection.receive();
                        Request request = new Request(frame, connection, dataStorage);
                        addRequest(request);
                    } catch (EOFException e) {
                        System.out.println("Client disconnected");
                        break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                lock.lock();
                try {
                    activeConnections--;
                    connectionAvailable.signal();
                } finally {
                    lock.unlock();
                }
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) throws IOException {
        if (args.length < 2) {
            System.out.println("Uso: java Server <maxConnections> <port>");
            return;
        }
        try {
            int maxConnections = Integer.parseInt(args[0]);
            int port = Integer.parseInt(args[1]);

            Server server = new Server(maxConnections);
            server.initWorkerThreads();
            server.start(port);
        } catch (NumberFormatException e) {
            System.out.println("Erro: maxConnections e port devem ser números inteiros válidos");
        } catch (IOException e) {
            System.out.println("Erro ao iniciar o servidor: " + e.getMessage());
        }
    }
}




