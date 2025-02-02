package Packet;

import Task.Task;

import java.util.List;

public class NetTaskPacket {
    private int nr_seq;
    private int ack;
    private int isLast;
    private int type;
    private String data;

    // type -1 - terminar ligação
    // type 0 - olá do cliente ao server
    // type 1 - mandar tasks
    // type 2 - bandwidth
    // type 3 - jitter
    // type 4 - packetLoss
    // type 5 - latency

    public NetTaskPacket(){
    }

    public NetTaskPacket(int nr_seq, int ack,int isLast, int type, String data){
        this.nr_seq = nr_seq;
        this.ack = ack;
        this.isLast = isLast;
        this.type = type;
        this.data = data;
    }

    public NetTaskPacket(int nr_seq, int type){ //ack
        this.nr_seq = nr_seq;
        this.ack = 1;
        this.isLast = 1;
        this.type = type;
        this.data = "";
    }

    public void setNr_seq(int nr_seq){
        this.nr_seq = nr_seq;
    }

    public void setAck(int ack){
        this.ack = ack;
    }

    public void setIsLast(int isLast){
        this.isLast = isLast;
    }

    public void setType(int type){
        this.type = type;
    }

    public void setData(String data){
        this.data = data;
    }

    public int getNr_seq(){
        return this.nr_seq;
    }

    public int getAck(){
        return this.ack;
    }

    public int getIsLast(){
        return this.isLast;
    }
    public int getType(){
        return this.type;
    }

    public String getData(){
        return this.data;
    }

    public static String NetTaskPacketToString(NetTaskPacket packet){

        return String.format("%d/%d/%d/%d/%s",packet.nr_seq,packet.ack,packet.isLast,packet.type,packet.data);
    }

    public static NetTaskPacket StringToNetTaskPacket(String message){
        String[] parts = message.split("/");
        NetTaskPacket packet = new NetTaskPacket();

        packet.nr_seq = Integer.parseInt(parts[0]);
        packet.ack = Integer.parseInt(parts[1]);
        packet.isLast = Integer.parseInt(parts[2]);
        packet.type = Integer.parseInt(parts[3]);
        if(packet.type==-1 || packet.ack==1)
            packet.data="";
        else
            packet.data = parts[4];

        return packet;
    }

}
