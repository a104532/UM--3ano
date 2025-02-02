import java.util.Random;

class Mover implements Runnable {
    BancoMultiplasContas b;
    int s; // Number of accounts

    public Mover (BancoMultiplasContas b, int s) { this.b=b; this.s=s; }

    public void run() {
        System.out.println ("Started thread-" + Thread.currentThread().getId());
        final int moves = 10000000;
        int from, to;
        Random rand = new Random();

        for (int m=0; m < moves; m++) {
            from=rand.nextInt(s); // Get one
            while ((to=rand.nextInt(s))==from); // Slow way to get distinct
            b.transfer(from,to,1);
        }
    }
}

public class BankTest {
    public static void main(String[] args) throws InterruptedException {
        final int N = 10;

        BancoMultiplasContas b = new BancoMultiplasContas(N);

        for (int i = 0; i < N; i++) {
            b.deposit(i, 1000);
        }

        System.out.println (b.totalBalance());

        Thread t1 = new Thread(new Mover(b,10));
        Thread t2 = new Thread(new Mover(b,10));


        t1.start(); t2.start();
        t1.join(); t2.join();

        System.out.println(b.totalBalance());
    }
}

