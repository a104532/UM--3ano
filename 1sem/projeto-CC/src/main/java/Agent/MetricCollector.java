package Agent;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MetricCollector {
    public static double runIperf(String tool, String role, String server_address, int duration, String transport_type,int type) {
        String result1 = "";
        String result2 = "";
        List<String> output = new ArrayList<>();
        try {
            List<String> command = new ArrayList<>();
            command.add(tool);

            if (role.equals("c")) {
                command.add("-" + role);
                command.add(server_address);
            }

            else if (role.equals("s")) {
                command.add("-" + role);
            }

            command.add("-t");
            command.add(String.valueOf(duration));

            if (transport_type.equals("u")) {
                command.add("-" + transport_type);
            }
            ProcessBuilder builder = new ProcessBuilder(command);

            builder.redirectErrorStream(true);
            Process process = builder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                output.add(line);
            }
            process.waitFor();

            if(type==2){// bandwidth
                return parseBandwidth(output.get(output.size()-2), output.getLast());
            }

            else if(type==3){//jitter
                return parseJitter(output.get(output.size()-2), output.getLast());
            }
            else{// packet loss == 4
                return parsePacketLoss(output.get(output.size()-2), output.getLast());
            }

        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }

    }

    public static double runPing(String tool, String destination, int count) {
        String result = "";
        try {
            ProcessBuilder builder = new ProcessBuilder(tool, "-c", String.valueOf(count), destination);
            builder.redirectErrorStream(true);
            Process process = builder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                result = line; // assim só guarda a última linha que é a que importa
            }
            process.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }

        return parseLatency(result);

    }

    public static double parseBandwidth(String lastLine1, String lastLine2) {
        // Regex patterns for different iperf output formats
        String[] patterns = {
                // Pattern for iperf3 output
                ".*\\s(\\d+\\.\\d+)\\s*Mbits/sec.*",
                // Pattern for older iperf versions
                ".*\\s(\\d+\\.\\d+)\\s*Mbps.*"
        };

        // Check both lines, prioritizing the last line
        String[] linesToCheck = {lastLine2, lastLine1};
        double bandwidth= -1;

        for (String line : linesToCheck) {
            if (line != null && line.trim().endsWith(":")) {
                continue;
            }

            // Try each pattern
            for (String patternStr : patterns) {
                Pattern pattern = Pattern.compile(patternStr);
                Matcher matcher = pattern.matcher(line != null ? line : "");

                if (matcher.find()) {
                    try {
                        bandwidth =  Double.parseDouble(matcher.group(1));
                    } catch (NumberFormatException e) {
                        continue;
                    }
                }
            }
        }

        return bandwidth;
    }

    public static double parseJitter(String lastLine1, String lastLine2) {
        String pattern = ".*\\s(\\d+\\.\\d+)\\s*ms.*";

        String[] linesToCheck = {lastLine2, lastLine1};
        double jitter= -1;

        for (String line : linesToCheck) {
            if (line != null && line.trim().endsWith(":")) {
                continue;
            }

            Pattern jitterPattern = Pattern.compile(pattern);
            Matcher matcher = jitterPattern.matcher(line != null ? line : "");

            if (matcher.find()) {
                try {
                    jitter = Double.parseDouble(matcher.group(1));
                } catch (NumberFormatException e) {
                    continue;
                }
            }
        }

        return jitter;
    }

    public static double parsePacketLoss(String lastLine1, String lastLine2) {

        String pattern = ".*\\((\\d+(\\.\\d+)?)%\\).*";

        String[] linesToCheck = {lastLine2, lastLine1};
        double packet_loss= -1;

        for (String line : linesToCheck) {
            if (line != null && line.trim().endsWith(":")) {
                continue;
            }

            Pattern lossPattern = Pattern.compile(pattern);
            Matcher matcher = lossPattern.matcher(line != null ? line : "");

            if (matcher.find()) {
                try {
                    // Extract the percentage directly from the parentheses
                    packet_loss =  Double.parseDouble(matcher.group(1)); // dá return da percentagem
                } catch (NumberFormatException e) {
                    continue;
                }
            }
        }

        return packet_loss;
    }

    //ex da ultima linha do output: rtt min/avg/max/mdev = 16.556/17.230/17.900/0.477 ms
    public static double parseLatency(String lastLine) {
        double averageLatency = -1;
        try {
            if (lastLine.contains("rtt")) {
                String[] parts = lastLine.split("=");
                if (parts.length > 1) {
                    String[] metrics = parts[1].trim().split("/");
                    if (metrics.length > 1) {
                        averageLatency = Double.parseDouble(metrics[1]); // O segundo valor é o average
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return averageLatency;
    }

    public static double runCpuUsage() {
        String result = "";
        try {
            ProcessBuilder builder = new ProcessBuilder("bash", "-c", "top -bn1 | grep \"Cpu(s)\" | awk '{print $2 + $4}'");
            builder.redirectErrorStream(true);
            Process process = builder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                result=line;
            }
            process.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Double.parseDouble(result);

    }

    public static double runRamUsage() {
        String result = "";
        try {
            ProcessBuilder builder = new ProcessBuilder("bash", "-c", "free -m | awk '/Mem:/ {printf \"%.2f\", $3/$2 * 100.0}'");
            builder.redirectErrorStream(true);
            Process process = builder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                result = line;
            }
            process.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Double.parseDouble(result);

    }

    //interfaces - ifconfig + nome da interface (ver os pacotes enviados)

    public static int runIfconfig(String interfaceName){
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("ifconfig", interfaceName);
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            Pattern packetsSentPattern = Pattern.compile("TX\\s+packets\\s+(\\d+)");

                String line;
                while ((line = reader.readLine()) != null) {
                    Matcher matcher = packetsSentPattern.matcher(line);
                    if (matcher.find()) {
                        // Wait for the process to complete
                        process.waitFor();

                        return Integer.parseInt(matcher.group(1));
                    }
                }

            process.waitFor();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1;
    }




}
