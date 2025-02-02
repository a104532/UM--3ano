package Server;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import Packet.*;
import Task.Task;

public class ClientHandlerNT implements Runnable {

        private final NetTaskPacket helloPacket;
        private final InetAddress clientAddress;
        private final int clientPort;
        private String device_id;
        private int nr_seq=2;
        private NTSender sender;
        private NTReceiver receiver;
        private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public ClientHandlerNT(NetTaskPacket helloPacket, InetAddress clientAddress, int clientPort) {
            this.helloPacket = helloPacket;
            this.clientAddress = clientAddress;
            this.clientPort = clientPort;
        }

    @Override
    public void run() {
        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket();
            sender = new NTSender(socket);
            receiver = new NTReceiver(socket);

            device_id = helloPacket.getData();

            LocalDateTime date = LocalDateTime.now();
            String s = new String(date.format(FORMATTER) + " - Received hello packet from " +device_id);
            NMS_Server.ConnectionAdd(s);
            List<Task> tasksForDevice = Task.getTasksForDevice(device_id, NetTaskServer.getTaskList());


            if(tasksForDevice.isEmpty()){ // fechar a ligação se não há tasks para mandar
                sender.sendData("",clientAddress,clientPort,nr_seq, device_id, -1);
                date = LocalDateTime.now();
                s = (date.format(FORMATTER) +" - No tasks to send to agent "+ device_id +". Closing connection ...");
                NMS_Server.ConnectionAdd(s);
            }
            else{
                String tasks = Task.TasksToString(tasksForDevice,device_id);
                nr_seq = sender.sendData(tasks,clientAddress,clientPort,nr_seq,device_id,1);
                date = LocalDateTime.now();
                s = (date.format(FORMATTER) +" - Tasks sent to agent "+ device_id +". Waiting for metrics ...");
                NMS_Server.ConnectionAdd(s);

                while(true){
                    try{
                        socket.setSoTimeout(100000); // 1 minuto
                        NetTaskPacket packet = receiver.receive(nr_seq);
                        NMS_Server.addMetric(device_id, new MetricNT(device_id,packet.getType(),packet.getData()));
                        nr_seq = packet.getNr_seq() + 1;
                    } catch (SocketTimeoutException e){
                        date = LocalDateTime.now();
                        s = (date.format(FORMATTER) +" - Socket timed out while waiting for a packets from agent " + device_id+". Closing connection");
                        NMS_Server.ConnectionAdd(s);
                        break;
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally{
            if(socket != null && !socket.isClosed()){
                socket.close();
            }
        }
    }
}

