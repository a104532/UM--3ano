import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Lock;

public class Barrier {
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition condition = lock.newCondition();
    private final int N;
    private int waitingThreads = 0;  // Threads que estão na barreira
    private int passedThreads = 0;   // Threads que passaram pela barreira

    Barrier(int N) {
        this.N = N;
    }

    public void await() throws InterruptedException {
        lock.lock();
        try {
            // Espera até que todas as threads da fase anterior tenham passado
            while (passedThreads > 0) {
                condition.await();
            }

            // Incrementa o contador de threads que chegaram à barreira
            waitingThreads++;

            if (waitingThreads < N) {
                // Aguarda outras threads na barreira
                while (waitingThreads < N) {
                    condition.await();
                }
            } else {
                // Última thread a chegar libera todas
                condition.signalAll();
            }

            // Incrementa o contador de threads que passaram e atualiza contadores
            passedThreads++;
            if (passedThreads == N) {
                // Reinicia os contadores para reutilização da barreira
                waitingThreads = 0;
                passedThreads = 0;
                condition.signalAll(); // Libera threads da próxima fase
            }
        } finally {
            lock.unlock();
        }
    }
}
