package Packet;

import Server.NMS_Server;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Objects;

public class NTSender {
    private DatagramSocket socket;
    private InetAddress receiverAddress;
    private int receiverPort;
    private String deviceId;
    private int type;
    private static final int TIMEOUT = 2000; // milliseconds
    private static final int MAX_RETRIES = 5;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public NTSender(DatagramSocket socket) throws SocketException {
        this.socket = socket;
        socket.setSoTimeout(TIMEOUT);
    }

    public int sendData(String data, InetAddress receiverAddress, int receiverPort,int nr_seq, String deviceId, int type) throws IOException {
        this.receiverAddress = receiverAddress;
        this.receiverPort = receiverPort;
        this.deviceId = deviceId;
        this.type=type;

        int offset = 0;
        int chunkSize = 1024;
        int last=0;

        if (data.isEmpty()) {
            NetTaskPacket packet = new NetTaskPacket(nr_seq, 0, 1, type, "");

            String packetStr = NetTaskPacket.NetTaskPacketToString(packet);

            if (!sendWithRetry(packetStr, nr_seq)) {
                LocalDateTime date =LocalDateTime.now();
                String message;
                if(type==-1 || type==1){ //mandados pelo server
                    message = (date.format(FORMATTER) + " - Failed to send packet to agent" + deviceId + " after maximum retries");
                    NMS_Server.ConnectionAdd(message);
                }
                else{ // mandados pelo agente
                    System.out.print(date.format(FORMATTER) + " NT: Failed to send packet to server after maximum retries");
                }

            }

            return nr_seq + 1;
        }

        for (int i = 0; i < data.length(); i += chunkSize) {
            String chunk = data.substring(i, Math.min(data.length(), i + chunkSize));
            boolean isLast = (i + chunkSize >= data.length());
            if(isLast) last=1;
            NetTaskPacket packet = new NetTaskPacket(nr_seq,0,last,type,chunk);
            String packetStr = NetTaskPacket.NetTaskPacketToString(packet);

            if (!sendWithRetry(packetStr, nr_seq)) {
                LocalDateTime date = LocalDateTime.now();
                String message;
                if(type==-1 || type==1){ //mandados pelo server
                    message = (date.format(FORMATTER) + " - Failed to send packet to agent" + deviceId + " after maximum retries");
                    NMS_Server.ConnectionAdd(message);
                }
                else{ // mandados pelo agente
                    System.out.print(date.format(FORMATTER) + " NT: Failed to send packet to server after maximum retries");
                }
            }
            nr_seq++;
        }
        return nr_seq; // dá return do novo numero de sequencia a ser usado no proximo pacote
        //retirei o incremento do 1 porque no fim do for ele já aumenta o nr de seq para o prox
    }

    private boolean sendWithRetry(String packetStr, int expectedSequence) throws IOException {
        int retries = 0;
        byte[] packetBytes = packetStr.getBytes(StandardCharsets.UTF_8);

        while (retries < MAX_RETRIES) {
            DatagramPacket datagramPacket = new DatagramPacket(packetBytes, packetBytes.length, receiverAddress, receiverPort);

            socket.send(datagramPacket);

            try {
                byte[] ackBuffer = new byte[1024];
                DatagramPacket ackPacket = new DatagramPacket(ackBuffer, ackBuffer.length);
                socket.receive(ackPacket);

                String ackStr = new String(ackPacket.getData(), 0, ackPacket.getLength(),
                        StandardCharsets.UTF_8);
                NetTaskPacket packet = NetTaskPacket.StringToNetTaskPacket(ackStr);

                if (packet.getAck()==1 && packet.getNr_seq() == expectedSequence) {
                    return true; // recebeu o ack
                }
            } catch (SocketTimeoutException e) {
                retries++;
            }
        }

        return false; // tentou 5 vezes e não deu
    }
}
