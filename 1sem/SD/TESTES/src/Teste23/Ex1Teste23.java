package Teste23;

import java.util.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Ex1Teste23 {
    public class CacheService{

        private class Entry{
            byte[] value;
            Condition cond = lock.newCondition();
        }


        private final int N;
        int size = 0;
        private Map<Integer, Entry> cacheMap = new HashMap<>();
        private Set<Integer> keys = new HashSet<>();
        private final ReentrantLock lock = new ReentrantLock();
        private final Condition full = lock.newCondition();

        public CacheService(int n) {
            N = n;
        }



        void put(int key, byte[] value) throws InterruptedException {
            lock.lock();
            try{
                Entry e = cacheMap.get(key);
                if(e == null){
                    e = new Entry();
                    cacheMap.put(key, e);
                }
                keys.add(key);
                while(size == N && e.value == null){
                    e.cond.await();
                }
                keys.remove(key);
                if(e.value == null){
                    size ++;
                }
                e.value = value;
            } finally{
                lock.unlock();
            }
        }

        byte[] get(int key) {
            lock.lock();
            try{
                Entry e = cacheMap.get(key);
                return e == null ? null : e.value;
            } finally{
                lock.unlock();
            }
        }

        void evict(int key) {
            lock.lock();
            try{
                Entry e = cacheMap.get(key);
                if(e != null && e.value != null){
                    size --;
                    e.value = null;
                    cacheMap.remove(key);
                    if(!keys.isEmpty()){ // caso esteja vazio não precisa de mandar o sinal
                        int k = keys.remove(0);
                        e = cacheMap.get(k);
                        e.cond.signal();
                    }
                }
            }finally{
                lock.unlock();
            }

        }



    }

}

interface Cache {
    void put(int key, byte[] value);
    byte[] get(int key);
    void evict(int key);
}