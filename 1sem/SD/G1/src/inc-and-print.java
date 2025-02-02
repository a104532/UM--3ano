import java.util.concurrent.locks.ReentrantLock;

class Increment implements Runnable {


    int N = 10;
    @Override
    public void run() {
        final long I = 100;

        for (long i = 0; i < I; i++)
            System.out.println(i);
    }
}

class MainEx1 {
    public static void main(String[] args) throws InterruptedException {
        int N = 10;
        Thread[] threads = new Thread[N];

        //Inicialização de todas as threads
        for(int i = 0; i<N; i++){
            threads[i] = new Thread(new Increment());
            threads[i].start();
        }

        //Agora é necessário concluir as mesmas
        for(int i = 0; i<N; i++){
            threads[i].join();
        }

        System.out.println("No fim");
    }
}
