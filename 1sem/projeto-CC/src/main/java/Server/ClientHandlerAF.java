package Server;

import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ClientHandlerAF implements Runnable {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    ClientHandlerAF(Socket socket){
        this.socket = socket;
    }

    public void run(){
        try{
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream());

            String agent_id = in.readLine();
            LocalDateTime date = LocalDateTime.now();
            String s = new String(date.format(FORMATTER) +" -  Agent "+ agent_id + " connected ");
            NMS_Server.AFAdd(s);

            out.println("Connected to server");
            out.flush();

            String message;
            while ((message = in.readLine()) != null) {
                if(message.equals("exit")){
                    date = LocalDateTime.now();
                    s = new String(date.format(FORMATTER) +" -  Agent "+ agent_id + " disconnected ");
                    NMS_Server.AFAdd(s);
                    break;
                }
                date = LocalDateTime.now();
                s = new String(date.format(FORMATTER) +" - " +message);
                NMS_Server.AFAdd(s);
            }

            socket.close();
            in.close();
            out.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
