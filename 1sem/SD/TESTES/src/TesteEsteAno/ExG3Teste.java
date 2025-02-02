package TesteEsteAno;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class ExG3Teste {
    public void main(String[] args) throws IOException {
        ServerSocket ss = new ServerSocket(1234);
        ExG2OTeste.ManagerImpl m = new ExG2OTeste.ManagerImpl();

        while(true){
            Socket s = ss.accept();
            Worker w = new Worker(s,m);
            Thread t = new Thread(w);
            t.start();
        }
    }

    public class Worker implements Runnable{
        private Socket s;
        private ExG2OTeste.ManagerImpl m;
        private ExG2OTeste.Trip trip = null;

        public Worker(Socket s, ExG2OTeste.ManagerImpl m){
            this.s = s;
            this.m = m;
        }

        public void run(){
            try{
                DataInputStream is = new DataInputStream(new BufferedInputStream(s.getInputStream()));
                DataOutputStream os = new DataOutputStream(new BufferedOutputStream(s.getOutputStream()));

                String command;
                while((command = is.readUTF()) != null){
                    switch(command){
                        case "permission":
                            int size = is.readInt();
                            trip = m.permission(size);
                            os.writeInt(trip.dockId());
                            break;
                        case "dockId":
                            if(trip == null){
                               os.writeUTF("Erro");
                            }
                            int id = trip.dockId();
                            os.writeInt(id);
                            break;
                        case "waitDisembark":
                            if(trip == null){
                                os.writeUTF("Erro");
                            }
                            trip.waitDisembark();
                            break;
                        case "depart":
                            if(trip == null){
                                os.writeUTF("Erro");
                            }
                            trip.depart();
                            trip = null;
                            break;
                        default:
                            break;
                    }
                    os.flush();
                    break;
                }
                is.close();
                os.close();
                s.close();
            } catch (IOException e){
                e.printStackTrace();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        }
    }
}
