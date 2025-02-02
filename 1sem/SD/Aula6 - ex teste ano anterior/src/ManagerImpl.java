import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ManagerImpl  implements Manager{

    private Lock l = new ReentrantLock();
    private Condition c = l.newCondition();

    private final int R = 10;
    private RaidImpl current = new RaidImpl();
    private int running = 0;


    public Raid join(String name, int minPlayers) throws InterruptedException{
        l.lock();
        try{
            RaidImpl my = current;
            my.players.add(name);
            my.maxMins = Math.max(my.maxMins,minPlayers);
            if (my.playes.size() < my.maxMins) {
                while(current == my) {
                    c.await();
                }
            }else {
                my.players = List.of(my.players.toArray(new String[0]));
                current = new RaidImpl();
                c.signalAll();
            }
            return my;
        } finally {
            l.unlock();
        }

    }

    private void finished(){
        l.lock();
        try{
            if(!pending,isEmpty()){
                running -= 1;
            } else{
                pending.remove(0);
            }
        } finally {
            l.unlock();
        }
    }

    private class RaidImpl implements Raid{
        Lock l = new ReentrantLock();
        Condition c = l.newCondition();
        List<String> players = new ArrayList<>();
        int maxMins = 0;
        int left = 0;

        public List<String> players(){
            return players;
        }
        public void waitStart() throws InterruptedException{
            try{

            }finally{
                l.unlock();
            }
        }

        public void leave(){
            l.lock();
            try{
                left += 1;
                if(left == players.size())
                    finished();
            } finally{
                l.unlock();
            }
        }
    }
}
