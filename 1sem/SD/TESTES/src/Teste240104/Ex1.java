package Teste240104;

import java.util.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class Ex1 {

    interface Manager {
        Raid join(String name, int minPlayers) throws InterruptedException;
    }

    interface Raid {
        List<String> players();
        void waitStart() throws InterruptedException;
        void leave();
    }

    public class ManagerImp implements Manager {
        private int R;
        private int currentRaids = 0;
        private Raid current = new RaidImp();
        private final Lock lock = new ReentrantLock();
        private final Condition condition = lock.newCondition();
        private final Queue<Raid> pending = new LinkedList<>();


        public Raid join(String name, int minPlayers) throws InterruptedException {
            lock.lock();
            try {
                Raid my = current;
                my.players().add(name);
                my.nMin = Math.max(my.nMin, minPlayers);
                if(my.players().size() < my.nMin){
                    while(my.players().size() < my.Min){
                        condition.await();
                    }
                }else{
                    current.waitStart();
                }
                return my;
            } finally{
                lock.unlock();
            }
        }

        public class RaidImp implements Raid {
            private final Lock l = new ReentrantLock();
            private final Condition c = l.newCondition();
            private final List<String> players = new ArrayList<>();
            private int nMin = 0;
            private int left = 0;


            public List<String> players(){
                l.lock();
                try{
                    return players;
                } finally{
                    l.unlock();
                }
            }
            public void waitStart() throws InterruptedException{
                lock.lock();
                try{
                        while(currentRaids >= R){
                            c.await();
                        }
                        currentRaids++;
                        condition.signalAll();


                }finally{
                    lock.unlock();
                }
            }

            public void leave(){
                l.lock();
                try{
                    left++;
                    if(left == players.size()) {
                        currentRaids--;
                        c.signalAll();
                    }
                } finally{
                    l.unlock();
                }
            }
        }
    }



}
