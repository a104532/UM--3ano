package Server;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MetricNT {

    private String device_id;
    private int type; //2-bandwidth 3-jitter 4-packetloss 5-latency (para bater certo com os tipos do nettask packet)
    private double value;
    private LocalDateTime date;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public MetricNT(String device_id,int type,String data){
        this.device_id = device_id;
        this.type = type;

        String parts[] = data.split(";");
        this.value = Double.parseDouble(parts[0].trim());
        this.date = LocalDateTime.parse(parts[1],FORMATTER);
    }

    public String getDevice_id() {
        return device_id;
    }

    public int getType() {
        return type;
    }

    public double getValue() {
        return value;
    }

    public LocalDateTime getDate() {
        return date;
    }

    @Override
    public String toString(){
        switch (type){
            case 2:
                return new String(date.format(FORMATTER) +" - bandwidth - "+ value + " Mbps ");
            case 3:
                return new String(date.format(FORMATTER) +" - jitter - "+ value + " ms ");
            case 4:
                return new String(date.format(FORMATTER) + " - packet loss - "+ value + " % ");
            case 5:
                return new String(date.format(FORMATTER) +" - latency - "+ value + " ms ");
        }

        return "";
    }

    public String toStringWId(){
        switch (type){
            case 2:
                return new String(date.format(FORMATTER) +" - Client " + device_id+ ": bandwidth - "+ value + " Mbps ");
            case 3:
                return new String(date.format(FORMATTER) +" - Client " + device_id+ ": jitter - "+ value + " ms ");
            case 4:
                return new String(date.format(FORMATTER) +" - Client " + device_id+ ": packet loss - "+ value + " % ");
            case 5:
                return new String(date.format(FORMATTER) +" - Client " + device_id+ ": latency - "+ value + " ms ");
        }

        return "";
    }

}
