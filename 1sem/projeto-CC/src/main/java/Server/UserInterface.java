package Server;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

public class UserInterface implements Runnable{

    Scanner sc = new Scanner(System.in);
    Menu menu;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public void run(){
        try {
            main_menu();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void main_menu() throws IOException {
        this.menu = new Menu(new String[]{
                "\nMENU\n",
                "Connection info",
                "Alertflow messages",
                "All metrics",
                "Metrics from a agent",
                "Metrics from a period of time"
        });
        menu.setHandler(1, this::ConnectionInfo);
        menu.setHandler(2, this::AlertFlowMessages);
        menu.setHandler(3, () -> menu_OrderOptions(1,""));
        menu.setHandler(4, this::AgentId);
        menu.setHandler(5, () -> menu_OrderOptions(2,""));

        menu.execute();
    }

    public void ConnectionInfo() throws IOException {
        NMS_Server.ConnectionPrint();

        pressAnyKey();
        main_menu();
    }

    public void AlertFlowMessages() throws IOException {
        NMS_Server.AFPrint();

        pressAnyKey();
        main_menu();
    }

    public void menu_OrderOptions(int i,String id) throws IOException {
        if(i>=3){
            this.menu = new Menu(new String[]{
                    "\nORDER OPTIONS\n",
                    "Order by date",
                    "Order by metric type"
            });

            if(i==3){
                menu.setHandler(1, () -> agentByDate(id));
                menu.setHandler(2, () -> agentByMetric(id));
            }
            else{
                menu.setHandler(1, () -> menu_getDates(1,id));
                menu.setHandler(2, () -> menu_getDates(2,id));
            }
        }
        else{
            this.menu = new Menu(new String[]{
                    "\nORDER OPTIONS\n",
                    "Order by agent",
                    "Order by date",
                    "Order by metric type"
            });
            if(i==1){
                menu.setHandler(1, this:: allByAgent);
                menu.setHandler(2, this:: allByDate);
                menu.setHandler(3, this:: allByMetric);
            }
            else{
                menu.setHandler(1, () -> menu_getDates(3, ""));
                menu.setHandler(2, () -> menu_getDates(4, ""));
                menu.setHandler(2, () -> menu_getDates(5, ""));
            }
        }

        menu.execute();
    }

    public void AgentId() throws IOException {
        System.out.println("\nAgent id: ");
        String id = sc.nextLine();

        boolean exist = NMS_Server.idExist(id);

        if(!exist){
            System.out.println("Agent id doesn´t exist or agent has not sent metrics yet");
            pressAnyKey();
            main_menu();
        }
        else
            menu_MetricsAgent(id);
    }

    public void menu_MetricsAgent(String id) throws IOException {
        this.menu = new Menu(new String[]{
                "\nAGENT METRICS OPTIONS\n",
                "All metrics",
                "Metrics from a period of time"
        });
        menu.setHandler(1, () -> menu_OrderOptions(3,id));
        menu.setHandler(2, () -> menu_OrderOptions(4, id));

        menu.execute();
    }

    public LocalDateTime getDate(){
        String d1;
        LocalDateTime date1;

        while(true){
            System.out.println("Enter date (format: yyyy-MM-dd HH:mm:ss): ");
            d1 = sc.nextLine();

            try {
                date1 = LocalDateTime.parse(d1,FORMATTER);
                break;
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format. Please enter date in format yyyy-MM-dd.");
            }
        }

        return date1;
    }

    public void menu_getDates(int i, String id) throws IOException {
        LocalDateTime date1;
        LocalDateTime date2;

        while(true){
            System.out.println("\nInitial date (format: yyyy-MM-dd HH:mm:ss): ");
            String d1 = sc.nextLine();

            try{
                date1 = LocalDateTime.parse(d1,FORMATTER);
                if(LocalDateTime.now().isBefore(date1))
                    System.out.println("Invalid date. Date is after actual date.");
                else
                    break;
            } catch(DateTimeParseException e){
                System.out.println("Invalid date format. Please enter date in format yyyy-MM-dd HH:mm:ss.");
            }

        }

        while(true){
            System.out.println("\nEnd date (format: yyyy-MM-dd HH:mm:ss) or write now (date now): ");
            String d2 = sc.nextLine();

            try{
                if(d2.equals("now")){
                    date2 = LocalDateTime.now();
                    break;
                }
                else{
                    date2 = LocalDateTime.parse(d2,FORMATTER);
                    if(LocalDateTime.now().isBefore(date2))
                        System.out.println("Invalid date. Date is after actual date.");
                    else
                        break;
                }

            } catch(DateTimeParseException e){
                System.out.println("Invalid date format. Please enter date in format yyyy-MM-dd HH:mm:ss.");
            }

        }

        if(date2.isBefore(date1)){
            System.out.println("End date is before start date. Enter a new end date.");
            date2 = getDate();
        }

        switch(i){
            case 1:
                AgentPeriodByDate(id,date1,date2);
                break;
            case 2:
                AgentPeriodByMetric(id,date1,date2);
                break;
            case 3:
                PeriodByAgent(date1,date2);
                break;
            case 4:
                PeriodByDate(date1,date2);
                break;
            case 5:
                PeriodByMetric(date1,date2);
                break;
        }
    }

    public void allByAgent() throws IOException {
        NMS_Server.printAll();

        pressAnyKey();
        main_menu();
    }

    public void allByDate() throws IOException {
        NMS_Server.printAllByDate();

        pressAnyKey();
        main_menu();
    }

    public void allByMetric() throws IOException {
        NMS_Server.printAllByMetric();

        pressAnyKey();
        main_menu();
    }



    public void agentByDate(String id) throws IOException {

        List<MetricNT> l = NMS_Server.getMetricsForDevice(id);

        if(!l.isEmpty()){
            System.out.println("Metrics Agent "+ id);
            for(MetricNT m : l)
                m.toString();
        }

        pressAnyKey();
        main_menu();
    }

    public void agentByMetric(String id) throws IOException {

        List<MetricNT> l = NMS_Server.getMetricsForDevice(id);

        l.sort(Comparator.comparing(MetricNT::getType));

        if(!l.isEmpty()){
            System.out.println("Metrics Agent "+ id);
            for(MetricNT m : l)
                System.out.println(m.toString());
        }

        pressAnyKey();
        main_menu();
    }


    public void AgentPeriodByDate(String id, LocalDateTime d1, LocalDateTime d2) throws IOException {

        List<MetricNT> l = NMS_Server.getMetricsForDevice(id);

        for(MetricNT m : l){
            if(!m.getDate().isBefore(d1) && !m.getDate().isAfter(d2))
                System.out.println(m.toString());

            if(m.getDate().isAfter(d2))
                break;
        }

        pressAnyKey();
        main_menu();
    }

    public void AgentPeriodByMetric(String id, LocalDateTime d1, LocalDateTime d2) throws IOException {

        List<MetricNT> l = NMS_Server.getMetricsForDevice(id);

        l.stream().filter(metric -> !metric.getDate().isBefore(d1) && !metric.getDate().isAfter(d2))
                .sorted((m1, m2) -> {
                    int typeComparison = Integer.compare(m1.getType(), m2.getType());
                    if (typeComparison == 0) {
                        return m1.getDate().compareTo(m2.getDate());
                    }
                    return typeComparison;
                });

        for(MetricNT m : l){
            System.out.println(m.toString());
        }

        pressAnyKey();
        main_menu();
    }

    public void PeriodByAgent(LocalDateTime d1, LocalDateTime d2) throws IOException {
        NMS_Server.printPeriodByAgent(d1,d2);

        pressAnyKey();
        main_menu();
    }

    public void PeriodByDate(LocalDateTime d1, LocalDateTime d2) throws IOException {
        NMS_Server.printPeriodByDate(d1,d2);

        pressAnyKey();
        main_menu();
    }

    public void PeriodByMetric(LocalDateTime d1, LocalDateTime d2) throws IOException {
        NMS_Server.printPeriodByMetric(d1,d2);

        pressAnyKey();
        main_menu();
    }

    public void pressAnyKey(){
        System.out.println("Press any key to continue");
        String x = sc.nextLine();
    }


}
