package Agent;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.BlockingQueue;

public class AlertFlowAgent implements Runnable{

    private String server_ip;
    private int server_port = 6666;
    private String device_id;
    private static BlockingQueue<String> AFQueue;
    private BufferedReader in;
    private static PrintWriter out;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public AlertFlowAgent(String server_ip, String device_id, BlockingQueue<String> queue){
        this.server_ip = server_ip;
        this.device_id = device_id;
        this.AFQueue = queue;
    }

    public static void endConnection(){
        out.println("exit");
        out.flush();
    }

    public void run() {
        try {
            Socket socket = new Socket(server_ip, server_port);

            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream());

            out.println(device_id);
            out.flush();

            String response = in.readLine();
            LocalDateTime date = LocalDateTime.now();
            System.out.println(date.format(FORMATTER) + " AF: "+response);


            try {
                while (true) {
                    String line = AFQueue.take();
                    if(line.equals("exit")){
                        out.print(line);
                        out.flush();
                        date = LocalDateTime.now();
                        System.out.println(date.format(FORMATTER) + " AF: Closing connection ...");
                        break;
                    }

                    System.out.println("AF: "+line);
                    out.println(line);
                    out.flush();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            socket.close();
            in.close();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
