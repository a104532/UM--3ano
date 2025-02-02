    package src;

    import java.io.*;
    import java.net.Socket;
    import java.nio.charset.StandardCharsets;
    import java.util.*;
    import java.util.concurrent.locks.*;

    public class Client {

        private int currentFrameId = 0;

        private Socket socket;
        private Lock lock;
        private final Thread receiverThread;
        private final Thread[] threadPoolClient;
        private final CustomQueue<Frame> sendQueue;
        private final Connection connection;
        final Map<String,String> mapRespostas;
        private final Map<Integer, GetWhen> getWhenResults;
        private volatile boolean running;


        public Client(Socket socket) throws IOException {
            this.socket = socket;
            this.lock = new ReentrantLock();
            this.threadPoolClient = new Thread[5];
            this.sendQueue = new CustomQueue<>();
            this.connection = new Connection(socket);
            this.mapRespostas = new HashMap<>();
            this.receiverThread = new Thread(this::receiveResponses);
            this.getWhenResults = new HashMap<>();
            this.running = true;
        }

        public static void main(String[] args) {
            if (args.length != 2) {
                System.out.println("Uso: java src.Client <host> <porta>");
                System.exit(1);
            }
            try {
                String host = args[0];
                int port = Integer.parseInt(args[1]);
                Socket socket = new Socket(host, port);
                Client client = new Client(socket);
                client.initWorkerThreads();
                client.receiverThread.start();

                //para permitir um encerramento limpo
                Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                    try {
                        client.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }));

                TextUI textUI = new TextUI(client);

                textUI.run();  // Chama o método run da TextUI para rodar a interface
            } catch (IOException e) {
                System.out.println("Erro ao iniciar a aplicação: " + e.getMessage());
            }
        }

        private void initWorkerThreads() {
            for (int i = 0; i < threadPoolClient.length; i++) {
                threadPoolClient[i] = new Thread(() -> {
                    while (running) {
                        try {
                            if(!sendQueue.isEmpty()) {
                                Frame frame = sendQueue.take();
                                connection.send(frame);
                            }
                        } catch (InterruptedException | IOException e) {
                            if (running) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
                threadPoolClient[i].start();
            }
        }

        private void receiveResponses() {
            while (running) {
                try {
                    Frame response = connection.receive();
                    processResponse(response);
                } catch (IOException e) {
                    if (running) {
                        e.printStackTrace();
                    }
                }
            }
        }

        private void processResponse(Frame response) {
            String operationType;
            String result;

            switch (response.getType()) {
                case Frame.LOGIN:
                    operationType = "Login";
                    result = ((Boolean) response.getData()) ? "Sucesso" : "Falha";
                    break;

                case Frame.CREATE_ACCOUNT:
                    operationType = "Criar Conta";
                    result = ((Boolean) response.getData()) ? "Sucesso" : "Falha";
                    break;

                case Frame.PUT:
                    operationType = "Put";
                    if (response.getData() == null) {
                        result = "Falha";
                    } else {
                        result = ((Boolean) response.getData()) ? "Sucesso" : "Falha";
                    }
                    break;

                case Frame.GET:
                    operationType = "Get";
                    byte[] getValue = (byte[]) response.getData();
                    result = getValue != null ? new String(getValue, StandardCharsets.UTF_8) : "Não encontrado";
                    break;

                case Frame.MULTIGET:
                    operationType = "MultiGet";
                    Map<String, byte[]> multiGetResults = (Map<String, byte[]>) response.getData();
                    StringBuilder multiGetStr = new StringBuilder();
                    if (multiGetResults != null) {
                        for (Map.Entry<String, byte[]> entry : multiGetResults.entrySet()) {
                            multiGetStr.append(entry.getKey()).append(": ")
                                    .append(new String(entry.getValue(), StandardCharsets.UTF_8))
                                    .append("; ");
                        }
                    }
                    result = multiGetStr.toString();
                    break;

                case Frame.MULTIPUT:
                    operationType = "MultiPut";
                    if (response.getData() == null) {
                        result = "Falha";
                    } else {
                        result = ((Boolean) response.getData()) ? "Sucesso" : "Falha";
                    }
                    break;

                case Frame.GETWHEN:
                    operationType = "GetWhen";
                    byte[] getWhenValue = (byte[]) response.getData();
                    result = new String(getWhenValue, StandardCharsets.UTF_8);

                    synchronized(getWhenResults) {
                        GetWhen getWhen = getWhenResults.get(response.getTag());
                        if (getWhen != null) {
                            getWhen.setResult(getWhenValue);
                            getWhenResults.remove(response.getTag());
                        }
                    }
                    break;

                default:
                    operationType = "Unknown";
                    result = "Operação desconhecida";
            }

            mapRespostas.put(operationType + "-" + response.getTag(), result);
            System.out.println("\nResposta processada: " + operationType + " -> " + result);
        }

        public void addFrame(Frame frame) {
            sendQueue.put(frame);
        }


        public boolean authenticate(String username, String password) throws IOException, AuthenticationFailedException {
            lock.lock();
            try {
                int frameId = IncrementFrameId();
                User user = new User(username, password);
                Frame frame = new Frame(frameId, user, Frame.LOGIN, false);
                addFrame(frame);

                // Aguardar resposta
                String key = "Login-" + frameId;
                for (int i = 0; i < 20; i++) { // 10 segundos no total
                    if (mapRespostas.containsKey(key)) {
                        return mapRespostas.get(key).equals("Sucesso");
                    }
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        throw new IOException("Autenticação interrompida");
                    }
                }
                throw new IOException("Timeout na autenticação");
            } finally {
                lock.unlock();
            }
        }

        public boolean createAccount(String username, String password) throws IOException, UserAlreadyExistsException {
            lock.lock();
            try {
                int frameId = IncrementFrameId();
                User user = new User(username, password);
                Frame frame = new Frame(frameId, user, Frame.CREATE_ACCOUNT, false);
                addFrame(frame);

                String key = "Criar Conta-" + frameId;
                for (int i = 0; i < 20; i++) {
                    if (mapRespostas.containsKey(key)) {
                        return mapRespostas.get(key).equals("Sucesso");
                    }
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        throw new IOException("Criação de conta interrompida");
                    }
                }
                throw new IOException("Timeout na criação da conta");
            } finally {
                lock.unlock();
            }
        }

        public void put(String key, byte[] value) throws IOException {
            lock.lock();
            try {
                if (key == null || value == null) {
                    System.err.println("Key ou value não podem ser null");
                    return;
                }
                int frameId = IncrementFrameId();
                PutOne putData = new PutOne(key, value);
                Frame frame = new Frame(frameId, putData, Frame.PUT, false);
                addFrame(frame);
            } finally {
                lock.unlock();
            }
        }

        public void get(String key) throws IOException {
            lock.lock();
            try {
                int frameId = IncrementFrameId();
                Frame frame = new Frame(frameId, key, Frame.GET, false);
                addFrame(frame);

            } finally {
                lock.unlock();
            }
        }

        public void multiPut(Map<String, byte[]> data) throws IOException {
            lock.lock();
            try {
                int frameId = IncrementFrameId();
                Frame frame = new Frame(frameId, data, Frame.MULTIPUT, false);
                addFrame(frame);
            } finally {
                lock.unlock();
            }
        }

        public void multiGet(Set<String> keys) throws IOException {
            lock.lock();
            try {
                int frameId = IncrementFrameId();
                Frame frame = new Frame(frameId, keys, Frame.MULTIGET, false);
                addFrame(frame);
            } finally {
                lock.unlock();
            }
        }

        public void getWhen(String key, String condKey, byte[] condValue) throws IOException {
            lock.lock();
            try {
                int frameId = IncrementFrameId();
                GetWhen getWhen = new GetWhen(key, condKey, condValue);

                synchronized(getWhenResults) {
                    getWhenResults.put(frameId, getWhen);
                }

                Frame frame = new Frame(frameId, getWhen, Frame.GETWHEN, false);
                addFrame(frame);

                // Non-blocking thread for result
                Thread resultThread = new Thread(() -> {
                    try {
                        byte[] result = getWhen.waitForResult();
                        System.out.println("\nResultado GetWhen para " + key + ": " +
                                new String(result, StandardCharsets.UTF_8));
                        System.out.print("\nDigite qualquer coisa e clique Enter: "); // Restaurar o prompt
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                });
                resultThread.setDaemon(true);
                resultThread.start();
            } finally {
                lock.unlock();
            }
        }

        public void getAllRespostas(){
            lock.lock();
            try {

                if (mapRespostas.isEmpty()) {
                    System.out.println("Nenhuma resposta disponível.");
                    return;
                }
                System.out.println("\n=== Respostas das Operações ===");
                for (Map.Entry<String, String> entry : mapRespostas.entrySet()) {
                    System.out.println(entry.getKey() + " -> " + entry.getValue());
                }
                System.out.println("==============================\n");
            } finally{
                lock.unlock();
            }

        }

        public int IncrementFrameId(){
            lock.lock();
            try{
                return currentFrameId++;
            } finally{
                lock.unlock();
            }
        }


        public void close() throws IOException {
            running = false;
            for (Thread thread : threadPoolClient) {
                thread.interrupt();
            }
            receiverThread.interrupt();
            socket.close();
        }
    }




