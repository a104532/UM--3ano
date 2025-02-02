package Rec23;

import java.io.*;
import java.net.*;
import java.util.*;

public class Ex2Rec23 {

    void main(String[] args) throws IOException {
        int N = 10;
        int V = 6;
        ServerSocket ss = new ServerSocket(5000);
        Ex1Rec23 obj = new Ex2Rec23(N,V);

        while(true){
            Socket s = ss.accept();
            Worker w = new Worker(s, obj);
            Thread t = new Thread(w);
            t.start();
        }
    }


    public class Worker implements Runnable{
        Socket s;
        Ex1Rec23 obj;
        int kartnum;

        public Worker(Socket s, Ex1Rec23 obj){
            this.s = s;
            this.obj = obj;
        }

        @Override
        public void run() {
            try{
                DataInputStream is = new DataInputStream(new BufferedInputStream(s.getInputStream()));
                DataOutputStream os = new DataOutputStream(new BufferedOutputStream(s.getOutputStream()));

                kartnum = obj.reserva();
                os.writeInt(kartnum);
                os.flush();

                String command;
                while((command = is.readUTF()) != null){
                    switch(command){
                        case "preparado":
                            obj.preparado(kartnum);
                            break;
                        case "completaVolta":
                            obj.completaVolta(kartnum);
                            break;
                        case "voltasCompletas":
                            int[] voltas = obj.voltasCompletas();
                            os.writeInt(voltas.length);
                            for(int volta : voltas){
                                os.writeInt(volta);
                            }
                            os.flush();
                            break;
                        default:
                            os.writeUTF(command);
                            os.flush();
                            break;
                    }

                    if(!obj.getOneFinished()){
                        int winner = obj.vencedor();
                        os.writeInt(winner);
                        os.flush();
                        break;
                    }
                }
                s.shutdownInput();
                s.shutdownOutput();
                s.close();

            } catch (IOException e){
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
