package Packet;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.TreeMap;

public class NTReceiver {
    private DatagramSocket socket;

    private InetAddress senderAddress;

    private int senderPort;

    public NTReceiver(DatagramSocket socket) throws SocketException {
        this.socket = socket;
    }

    public NetTaskPacket receive(int expectedSequenceNumber) throws IOException {
        byte[] buffer = new byte[1024];
        Map<Integer, String> receivedData = new TreeMap<>();
        boolean receiving = true;
        int aux=0;
        NetTaskPacket output = new NetTaskPacket();

        while (receiving) {
            DatagramPacket dpacket = new DatagramPacket(buffer, buffer.length);
            socket.receive(dpacket);
            this.senderAddress = dpacket.getAddress();
            this.senderPort = dpacket.getPort();

            String packetStr = new String(dpacket.getData(), 0, dpacket.getLength(),
                    StandardCharsets.UTF_8);
            NetTaskPacket packet = NetTaskPacket.StringToNetTaskPacket(packetStr);

            if(aux==0){
                output.setAck(packet.getAck());
                output.setIsLast(1);
                output.setType(packet.getType());
                aux=1;
            }

            if(packet.getAck()==0){ // só manda ack se o pacote recebido não for um ack
                // mandar ack
                NetTaskPacket ack = new NetTaskPacket(packet.getNr_seq(),packet.getType());
                String ackStr = NetTaskPacket.NetTaskPacketToString(ack);
                byte[] ackBytes = ackStr.getBytes(StandardCharsets.UTF_8);

                DatagramPacket ackPacket = new DatagramPacket(ackBytes, ackBytes.length, dpacket.getAddress(), dpacket.getPort());
                socket.send(ackPacket);
            }


            // Process received data
            if (packet.getNr_seq() == expectedSequenceNumber) {
                receivedData.put(expectedSequenceNumber, packet.getData());
                expectedSequenceNumber++;
                if (packet.getIsLast()==1) {
                    receiving = false;
                }
            }
        }

        output.setNr_seq(expectedSequenceNumber-1);

        // aqui ele junta todos os pedaços de dados
        StringBuilder finalData = new StringBuilder();
        for (String chunk : receivedData.values()) {
            finalData.append(chunk);
        }

        output.setData(finalData.toString());

        return output;
    }

    public InetAddress getSenderAddress(){
        return this.senderAddress;
    }

    public int getSenderPort(){
        return this.senderPort;
    }
}
