import java.util.concurrent.locks.*;

public class Agreement {
    private Lock l = new ReentrantLock();
    private Condition cond = l.newCondition();

    private final int N;
    private int ctr = 0;

    private static class Instance{
        int res =  Integer.MIN_VALUE;
    }
    private Instance current = new Instance();

    public Agreement(int N){
        this.N = N;
    }

    public int propose(int choice) throws InterruptedException{
        l.lock();
        try{
            Instance my = this.current;
            my.res = Math.max(my.res, choice);
            this.ctr += 1;
            if(this.ctr < N){
                while(current == my)
                    cond.await();
            } else{
                cond.signalAll();
                this.ctr = 0;
                //res = Integer.MIN_VALUE;
                current = new Instance();
            }
            return my.res;
        } finally{
            l.unlock();
        }
    }
}
