import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;



public class EchoClient {

    public static void main(String[] args) {
        try {
            Socket socket = new Socket("localhost", 12345);

            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream());

            BufferedReader systemIn = new BufferedReader(new InputStreamReader(System.in));

            String userInput;
            while ((userInput = systemIn.readLine()) != null) {
                out.println(userInput);
                out.flush();

                String response = in.readLine();

                System.out.println("Server response : " + response);

            }


            socket.shutdownOutput(); //fechar apenas o sentido de escrita do cliente
            String media = in.readLine();
            System.out.println("Média final : " + media);
            socket.shutdownInput();
            socket.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
