package Server;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class NMS_Server {

    private static ConcurrentHashMap<String, List<MetricNT>> mapMetrics = new ConcurrentHashMap<>();

    private static List<String> ConnectionList = Collections.synchronizedList(new ArrayList<>());

    private static List<String> AFList = Collections.synchronizedList(new ArrayList<>());

    public static boolean idExist(String id){
        return mapMetrics.containsKey(id);
    }


    public static void addMetric(String deviceId, MetricNT newMetric) {
        mapMetrics.compute(deviceId, (key, existingList) -> {
            if (existingList == null) {
                existingList = new ArrayList<>();
            }
            existingList.add(newMetric);
            // Sort the list by date in ascending order
            Collections.sort(existingList, Comparator.comparing(MetricNT::getDate));
            return existingList;
        });
    }

    public static List<MetricNT> getMetricsForDevice(String deviceId) {
        return mapMetrics.getOrDefault(deviceId, Collections.emptyList());
    }

    public static void printAll() {
        for (Map.Entry<String, List<MetricNT>> entry : mapMetrics.entrySet()) {
            String key = entry.getKey();
            List<MetricNT> metrics = entry.getValue();

            System.out.println(key +" metrics");
            for (MetricNT metric : metrics) {
                System.out.println(metric.toString());
            }
        }
    }

    public static void printAllByDate(){
        List<MetricNT> l = mapMetrics.values().stream()
                .flatMap(List::stream)
                .sorted(Comparator.comparing(MetricNT::getDate))
                .toList();

        for(MetricNT m : l)
            System.out.println(m.toStringWId());
    }

    public static void printAllByMetric(){
        List<MetricNT> l = mapMetrics.values().stream()
                .flatMap(List::stream)
                .sorted((m1, m2) -> {
                    int typeComparison = Integer.compare(m1.getType(), m2.getType());
                    if (typeComparison == 0) {
                        return m1.getDate().compareTo(m2.getDate());
                    }
                    return typeComparison;
                })
                .toList();

        for(MetricNT m : l)
            System.out.println(m.toStringWId());
    }


    public static void printPeriodByAgent(LocalDateTime d1, LocalDateTime d2){
        for (Map.Entry<String, List<MetricNT>> entry : mapMetrics.entrySet()) {
            String key = entry.getKey();
            List<MetricNT> metrics = entry.getValue();

            System.out.println(key +" metrics");
            for (MetricNT m : metrics) {
                if(!m.getDate().isBefore(d1) && !m.getDate().isAfter(d2))
                    System.out.println(m.toString());
                else if(m.getDate().isAfter(d2))
                    break;
            }
        }
    }

    public static void printPeriodByDate(LocalDateTime d1, LocalDateTime d2){
        List<MetricNT> l = mapMetrics.values().stream()
                .flatMap(List::stream)
                .filter(metric ->
                        !metric.getDate().isBefore(d1) &&
                                !metric.getDate().isAfter(d2)
                )
                .sorted(Comparator.comparing(MetricNT::getDate))
                .toList();

        for(MetricNT m : l)
            System.out.println(m.toStringWId());
    }

    public static void printPeriodByMetric(LocalDateTime d1, LocalDateTime d2){
        List<MetricNT> l = mapMetrics.values().stream()
                .flatMap(List::stream)
                .filter(metric ->
                        !metric.getDate().isBefore(d1) &&
                                !metric.getDate().isAfter(d2)
                )
                .sorted((m1, m2) -> {
                    int typeComparison = Integer.compare(m1.getType(), m2.getType());
                    if (typeComparison == 0) {
                        return m1.getDate().compareTo(m2.getDate());
                    }
                    return typeComparison;
                })
                .toList();

        for(MetricNT m : l)
            System.out.println(m.toStringWId());
    }

    public static void ConnectionAdd(String s){
        ConnectionList.add(s);
    }

    public static void AFAdd(String s){
        AFList.add(s);
    }

    public static void ConnectionPrint(){
        System.out.println("CONNECTION INFO");
        for(String s : ConnectionList)
            System.out.println(s);
    }

    public static void AFPrint(){
        System.out.println("ALERTFLOW MESSAGES");
        for(String s : AFList)
            System.out.println(s);
    }

    public static void main(String[] args) throws IOException {

        String path = args[0]; //json passado como argumento

        NetTaskServer netTaskServer = new NetTaskServer(path);
        AlertFlowServer alertFlowServer = new AlertFlowServer();
        UserInterface userInterface = new UserInterface();

        new Thread(netTaskServer).start();
        new Thread(alertFlowServer).start();
        new Thread(userInterface).start();

    }
}
