package g8;
import java.net.Socket;

public class SimpleClient {
    public static void main(String[] args) throws Exception {
        Socket s = new Socket("localhost", 12345);
        FramedConnection c = new FramedConnection(s);

        // send requests
        c.send("Ola".getBytes());
        c.send(("Tu não prendas o cabelo\n" +
                "Eu gosto de solto vê-lo\n" +
                "Pois te fica muito bem\n" +
                "Quando nele dá o vento\n" +
                "\n" +
                "Que lindo é o teu cabelo\n" +
                "Que chega até à cintura\n" +
                "E vai adornar teu corpo\n" +
                "Realçando tua formosura\n" +
                "\n" +
                "Tu és a mulher que eu sonhei\n" +
                "Tu és a que me sabe enamorar\n" +
                "Minha vida só existes tu\n" +
                "E quando te tenho a ti\n" +
                "A ninguém mais quero amar\n" +
                "\n" +
                "Quando passas pela rua\n" +
                "Sente inveja uma das flores\n" +
                "Só pra ver a tua cara\n" +
                "Debruçada na janela\n" +
                "\n" +
                "Que cara linda é a tua\n" +
                "Bonita como nenhuma\n" +
                "Por nada quero perder-te\n" +
                "És minha maior fortuna").getBytes());
        c.send("Hello".getBytes());

        // get replies
        byte[] b1 = c.receive();
        byte[] b2 = c.receive();
        byte[] b3 = c.receive();
        System.out.println("Some Reply: " + new String(b1));
        System.out.println("Some Reply: " + new String(b2));
        System.out.println("Some Reply: " + new String(b3));

        c.close();
    }
}
