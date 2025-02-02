import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.Condition;

public class Agreement {
    private Lock lock = new ReentrantLock();
    private Condition condition = lock.newCondition();
    private final int N;
    private int counter = 0;

    private static class Instance{
        int res = Integer.MIN_VALUE;
    }
    private Instance current = new Instance();

    public Agreement(int N){
        this.N = N;
    }

    public int propose (int choice) throws InterruptedException{
        lock.lock();
        try{
            Instance my = this.current;
            my.res = Math.max(my.res, choice);
            counter++;
            if(counter < N){
                while(current == my)
                    condition.await();
            }
            else{
                current = new Instance();
                condition.signalAll();
                counter = 0;
            }
            return my.res;
        } finally{
            lock.unlock();
        }
    }

}

































