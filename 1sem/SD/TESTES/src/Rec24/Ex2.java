package Rec24;



import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Ex2 {

    public static void  main(String[] args) throws IOException {

        ServerSocket ss = new ServerSocket(8888);
        Ex1.Managerio manager = new Ex1.Managerio();

        while (true) {
           Socket client = ss.accept();
            Worker worker = new Worker(client,manager);
            Thread t = new Thread(worker);
            t.start();
        }

    }

    public static class Worker implements Runnable {
        private Socket s;
        private Ex1.Managerio manager;
        public Worker(Socket s, Ex1.Managerio manager) {
            this.s = s;
            this.manager = manager;
        }

        @Override
        public void run() {
            try{
                DataInputStream is = new DataInputStream(new BufferedInputStream(s.getInputStream()));
                DataOutputStream os = new DataOutputStream(new BufferedOutputStream(s.getOutputStream()));

                String command;
                while((command = is.readUTF())!=null){
                    switch(command){
                        case "newTransfer":
                            String id = manager.newTransfer();
                            os.writeUTF(id);
                            os.flush();
                            break;
                        case "sendData":
                            String identifierSD = is.readUTF();
                            Ex1.Transfer tSD = manager.getTransfer(identifierSD);
                            int bytesToEnqueue = is.readInt();
                            int bytesRead;
                            byte[] buffer = new byte[16000];
                            while ((bytesRead = is.read(buffer)) != -1) {
                                byte[] actualData = Arrays.copyOf(buffer, bytesToEnqueue);
                                tSD.enqueue(actualData);
                            }
                            break;

                        case "getData":
                            String identifierGD = is.readUTF();
                            TransferObj tGD = manager.getTransfer(identifierGD);
                            byte[] data2 = tGD.dequeue();
                            os.write(data2);
                            os.flush();
                            break;

                        default:
                            break;
                    }
                }
                is.close();
                os.close();
                s.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }


    }
}
