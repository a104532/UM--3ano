package Server;

import java.net.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.io.*;
import java.util.concurrent.ConcurrentHashMap;

import Packet.NTReceiver;
import Packet.NetTaskPacket;
import Task.*;

public class NetTaskServer implements Runnable {
    private static List<Task> taskList =  new ArrayList<>(); //lista de tarefas carregadas do json
    private final int UDP_PORT = 9876;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public NetTaskServer(String filepath) throws IOException {
        taskList = Task.jsonReader(filepath);
        System.out.println("Json file loaded");
    }

    public static List<Task> getTaskList(){
        return taskList;
    }

    public void run() {
        DatagramSocket socket = null;
        try{
            socket = new DatagramSocket(UDP_PORT);
            NTReceiver receiver = new NTReceiver(socket);

            while (true) {
                NetTaskPacket helloMessage = receiver.receive(1);
                LocalDateTime now = LocalDateTime.now();
                String s = new String( now.format(FORMATTER) +" - Agent " + helloMessage.getData() + " connected");

                NMS_Server.ConnectionAdd(s);

                new Thread(new ClientHandlerNT(helloMessage,receiver.getSenderAddress(), receiver.getSenderPort())).start();
            }
        } catch(Exception e){
            e.printStackTrace();
        } finally{
            if (socket != null && !socket.isClosed()) { //isto está certo?
                socket.close();
            }
        }
    }
}