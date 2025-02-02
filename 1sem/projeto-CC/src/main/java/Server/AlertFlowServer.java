package Server;

import java.net.ServerSocket;
import java.net.Socket;

public class AlertFlowServer implements Runnable {

    public void run(){
        ServerSocket ss = null;

        try{
            ss = new ServerSocket(6666);

            while (true) {
                Socket socket = ss.accept();
                new Thread(new ClientHandlerAF(socket)).start();
            }


        } catch(Exception e){
            e.printStackTrace();
        }
    }

}
