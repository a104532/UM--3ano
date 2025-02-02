import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.locks.*;

class Register{
    Lock l = new ReentrantLock();
    int sum = 0;
    int count = 0;
    void add(int num){
        l.lock();
        try{
            sum += num;
        } finally{
            l.unlock();
        }

    }

    int average(){
        l.lock();
        try{
            return count > 0 ? sum/count : 0;
        } finally{
            l.unlock();
        }

    }
}

class ClientHandler extends Thread {
    private Socket socket;
    Register register;

    public ClientHandler(Socket socket, Register register) {
        this.socket = socket;
        this.register = register;
    }
    public void run(){
        try{
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream());


            String line;
            int sum = 0;
            while ((line = in.readLine()) != null) {
                int num = Integer.parseInt(line);
                sum += num;
                register.add(num); // atualizar o estado partilhado

                out.println(sum);
                out.flush();
            }

            out.println(register.average());
            out.flush();

            //socket.shutdownOutput();
            //socket.shutdownInput();
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


public class EchoServer {

    public static void main(String[] args) {
        try {
            ServerSocket ss = new ServerSocket(12345);
            Register register = new Register();

            while (true) {
                Socket socket = ss.accept();

                new ClientHandler(socket,register).start();

            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
