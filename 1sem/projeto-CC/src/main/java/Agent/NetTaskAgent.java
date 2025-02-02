package Agent;

import java.io.IOException;
import java.net.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import Packet.*;
import Task.*;

public class NetTaskAgent implements Runnable{
    private static InetAddress server_ip;
    private static InetAddress handler_ip;
    private static String device_id;
    private static int SERVER_PORT = 9876;
    private static int handler_port;
    private static NTSender sender;
    private static NTReceiver receiver;
    private static BlockingQueue<String> AFQueue;
    private static double lastJitter = 0;
    private static double lastPacketLoss = 0;
    private static Map<String, Integer> interface_map = new HashMap<>();

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static Lock l = new ReentrantLock();
    private static int nr_seq = 1; // não está a funcionar

    public static int getNr_seq() {
        return nr_seq;
    }

    public static void setNr_seq(int nr) {
        nr_seq = nr;
    }


    public NetTaskAgent(String server_ip, String device_id, BlockingQueue<String> queue) throws UnknownHostException {
        this.server_ip = InetAddress.getByName(server_ip);
        this.device_id = device_id;
        this.AFQueue = queue;
    }

    public void run(){
        DatagramSocket socket = null;
        try{
            socket = new DatagramSocket();
            sender = new NTSender(socket);
            receiver = new NTReceiver(socket);
            int aux;

            l.lock();
            try{
                aux = getNr_seq();
                setNr_seq(sender.sendData(device_id,server_ip,SERVER_PORT,aux,device_id,0));
            }finally{
                l.unlock();
            }

            LocalDateTime date = LocalDateTime.now();
            System.out.println(date.format(FORMATTER) + " NT: Establishing connection to server...");

            NetTaskPacket answer;
            l.lock();
            try{
                aux = getNr_seq();
                answer = receiver.receive(aux);
                setNr_seq(answer.getNr_seq()+1);
            } finally {
                l.unlock();
            }

            handler_port = receiver.getSenderPort();
            handler_ip = receiver.getSenderAddress();
            date = LocalDateTime.now();
            System.out.println(date.format(FORMATTER) + " NT: Confirmation of connection");


            if(answer.getType()==1){
                date = LocalDateTime.now();
                System.out.println(date.format(FORMATTER) + " NT: Tasks received, starting execution...");
                this.ScheduleNTMetricCollect(answer);
                this.ScheduleAFMetricCollect(answer);

                while (true) {
                    Thread.sleep(1000);
                }
            }
            else{
                date = LocalDateTime.now();
                System.out.println(date.format(FORMATTER) + " NT: No tasks received, closing connection...");
            }


        } catch (Exception e){
            e.printStackTrace();
        } finally{
            if(socket != null && !socket.isClosed()){
                socket.close();
            }
        }
    }

    public void ScheduleNTMetricCollect(NetTaskPacket packet){
        String tasks = packet.getData();
        List<Task> l = Task.StringToTasks(tasks);

        for(Task t : l) {
            if (!t.getBandwidth().startsWith("*")) { // quer dizer que tem de fazer a bandwidth
                String[] parts = t.getBandwidth().split(",");
                // aqui tem que se o iperf periodicamente
                // a minha dúvida é como é que se faz em relação ao nr de sequencia
                schedulerIperf(parts[0], parts[1], parts[2], Integer.parseInt(parts[3]), parts[4], Integer.parseInt(parts[5]), 2);
            }

            if (!t.getJitter().startsWith("*")) { // fazer o iperf para sacar o jitter
                String[] parts = t.getJitter().split(",");
                schedulerIperf(parts[0], parts[1], parts[2], Integer.parseInt(parts[3]), parts[4], Integer.parseInt(parts[5]), 3);
            }

            if (!t.getPacketLoss().startsWith("*")) { // fazer o iperf para sacar o jitter
                String[] parts = t.getPacketLoss().split(",");
                schedulerIperf(parts[0], parts[1], parts[2], Integer.parseInt(parts[3]), parts[4], Integer.parseInt(parts[5]), 4);
            }

            if (!t.getLatency().startsWith("*")) { // fazer o ping para sacar a latency
                String[] parts = t.getLatency().split(",");
                schedulerPing(parts[0], parts[1], Integer.parseInt(parts[2]), Integer.parseInt(parts[3]));
            }

        }

    }

    public void ScheduleAFMetricCollect(NetTaskPacket packet){
        String tasks = packet.getData();
        List<Task> l = Task.StringToTasks(tasks);

        for(Task t : l){
            String aux = t.getInterface_stats();
            String[] interfaces = aux.split(",");
            for(String s : interfaces){
                String key = t.getTask_id() + s;
                interface_map.put(key, 0);
            }
            aux = t.getAlertFlowConditionsString();
            String[] ls = aux.split(",");
            schedulerAFMetrics(t.getTask_id(), interfaces, t.getFrequency(), Integer.parseInt(ls[0]), Integer.parseInt(ls[1]),
                    Integer.parseInt(ls[2]), Integer.parseInt(ls[3]), Integer.parseInt(ls[4]));
        }
    }

    public static void schedulerAFMetrics(String task_id, String[] interfaces, int frequency, int cpuU,
                                          int ramU, int isU, int packetU, int jitterU){
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> {
            try{
                double cpu = MetricCollector.runCpuUsage();
                if(cpu > cpuU){
                    AFQueue.put("Agent "+ device_id + " exceeded cpu usage: actual-> "+cpu+"%  limit-> "+cpuU+"%");
                }
                double ram = MetricCollector.runRamUsage();
                if(ram > ramU){
                    AFQueue.put("Agent "+ device_id + " exceeded ram usage: actual-> "+ram+"%  limit-> "+ramU+"%");
                }
                for(String i : interfaces){
                    int count = MetricCollector.runIfconfig(i);
                    String key = task_id + i;
                    int istat = (count - interface_map.get(key)) / frequency;
                    if(istat > isU){ // subtrair o count antigo ao count atual e dividir pela frequencia para ter pacotes por segundo
                        AFQueue.put("Agent "+ device_id + " exceeded interface stats on interface " + i + ": actual-> "+
                                istat +" pps  limit-> "+ramU+" pps");
                    }
                    interface_map.put(key,count); // atualizar o count para o atual
                }
                if(lastJitter > jitterU){
                    AFQueue.put("Agent "+ device_id + " exceeded jitter: actual-> "+
                            lastJitter+"ms  limit-> "+jitterU+"ms");
                }
                if(lastPacketLoss > packetU){
                    AFQueue.put("Agent "+ device_id + " exceeded packet loss: actual-> "+
                            lastPacketLoss+"%  limit-> "+packetU+"%");
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

        }, 0, frequency, TimeUnit.SECONDS);
    }

    public static void schedulerIperf(String tool, String role, String server_address,
                                      int duration,String transport_type, int frequency, int type){
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> {

            double result = MetricCollector.runIperf(tool,role,server_address,duration,transport_type,type);
            if(type==3 && result != -1)
                lastJitter = result;
            else if(type==4 && result != -1)
                lastPacketLoss = result;
            LocalDateTime now = LocalDateTime.now();
            String res = Double.toString(result) + ';' + now.format(FORMATTER);
            try {
                l.lock();
                int aux;
                try{
                    aux = getNr_seq();
                    setNr_seq(sender.sendData(res,handler_ip,handler_port,aux,device_id,type));
                } finally{
                    l.unlock();
                }

                switch(type){
                    case 2:
                        System.out.println(now.format(FORMATTER) + " NT: bandwidth - "+ result + " Mbps  nr_seq - "+ aux);
                        break;
                    case 3:
                        System.out.println(now.format(FORMATTER) + " NT: jitter - "+ result + " ms  nr_seq - "+ aux);
                        break;
                    case 4:
                        System.out.println(now.format(FORMATTER) + " NT: packet loss - "+ result + "%  nr_seq - "+ aux);
                        break;
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }, 0, frequency, TimeUnit.SECONDS);

    }

    public static void schedulerPing(String tool, String destination, int count, int frequency){
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> {
            double result = MetricCollector.runPing(tool,destination,count);
            LocalDateTime now = LocalDateTime.now();
            String res = Double.toString(result) + ';' + now.format(FORMATTER);
            try {
                int aux;
                l.lock();
                try{
                    aux = getNr_seq();
                    setNr_seq(sender.sendData(res,handler_ip,handler_port,aux,device_id,5));
                }finally {
                    l.unlock();
                }
                System.out.println(now.format(FORMATTER) + " NT: latency - "+ result + " ms  nr_seq - "+ aux);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }}, 0, frequency, TimeUnit.SECONDS);

    }

}






