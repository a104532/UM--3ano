import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class E1Teste24 {
    private RaidImp current = new RaidImp();
    private int R;
    private ReentrantLock lock = new ReentrantLock();
    private Condition cond = lock.newCondition();
    private int nRaids = 0;
    public static class ManagerImp implements Manager{

        @Override
        public Raid join(String name, int minPlayers) throws InterruptedException {
            lock.lock();
            try{
                current.players().add(name);
                current.nMin = Math.max(minPlayers, current.nMin);
                if(current.players().size() < current.nMin) {
                    while (current.players().size() < current.nMin) {
                        cond.await();
                    }
                } else{
                    current.waitStart();
                }
                return current;
            } finally{
                lock.unlock();
            }
        }
    }
public class RaidImp implements Raid{
    private int nMin;
    private List<String> players = new ArrayList<>();
    private ReentrantLock l = new ReentrantLock();
    private Condition condition = l.newCondition();
    int left = 0;

    @Override
    public List<String> players() {
        return players;
    }

    @Override
    public void waitStart() throws InterruptedException {
        lock.lock();
        try{
            if(nRaids == R){
                condition.await();
            }else{
                cond.signalAll();
                nRaids++;
            }
        } finally{
            lock.unlock();
        }
    }

    @Override
    public void leave() {
        lock.lock();
        try{
            left ++;
            if(left == players.size()){
                nRaids--;
                condition.signalAll();
            }
        } finally{
            lock.unlock();
        }
    }
}
}



interface Manager {
    Raid join(String name, int minPlayers) throws InterruptedException;
}

interface Raid {
    List<String> players();
    void waitStart() throws InterruptedException;
    void leave();
}