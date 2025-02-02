package Agent;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class NMS_Agent {

    private static final BlockingQueue<String> taskQueue = new LinkedBlockingQueue<>(10);


    public static void main(String[] args) throws Exception {
        // Iniciar comunicação UDP e TCP
        String server_ip = args[0];
        String device_id = args[1];

        NetTaskAgent netTaskAgent = new NetTaskAgent(server_ip,device_id, taskQueue);
        AlertFlowAgent alertFlowAgent = new AlertFlowAgent(server_ip, device_id, taskQueue);

        Thread nt = new Thread(netTaskAgent);
        nt.start();
        Thread af = new Thread(alertFlowAgent);
        af.start();


        while(true){
            if(!nt.isAlive()){
                taskQueue.put("exit");
                break;
            }
        }

    }

}
