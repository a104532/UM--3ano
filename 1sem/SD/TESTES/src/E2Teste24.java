import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class E2Teste24 {

    public void main(String[] args) throws IOException{
        ServerSocket ss = new ServerSocket(8888);
        E1Teste24.ManagerImp m = new E1Teste24.ManagerImp();

        while(true){
            Socket s = ss.accept();
            Worker w = new Worker(s,m);
            Thread t = new Thread(w);
            t.start();
        }

    }

    public class Worker implements Runnable{
        private Socket s;
        private E1Teste24.ManagerImp m;

        public Worker(Socket s, E1Teste24.ManagerImp m) {
            this.s = s;
            this.m = m;
        }

        Raid raid;
        @Override
        public void run() {
            try{
                DataInputStream is = new DataInputStream(new BufferedInputStream(s.getInputStream()));
                DataOutputStream os = new DataOutputStream(new BufferedOutputStream(s.getOutputStream()));

                String command;
                while((command = is.readUTF()) != null){
                    switch(command){
                        case "join":
                            String name = is.readUTF();
                            int minPlayers = is.readInt();
                            raid = m.join(name, minPlayers);
                            raid.waitStart();
                        case "leave":
                        raid.leave();
                        default:
                            os.writeUTF("");
                    }
                }
                is.close();
                os.close();
                s.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

    }
}
