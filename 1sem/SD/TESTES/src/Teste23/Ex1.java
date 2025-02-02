package Teste23;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Ex1 {
    interface Cache {
        void put(int key, byte[] value);
        byte[] get(int key);
        void evict(int key);
    }

    public class CacheImp implements Cache {
        private final int N;
        private int size = 0;
        private Map<Integer, Entry> cache = new HashMap<>();
        private Set<Integer> keys = new HashSet<>();
        private final Lock l = new ReentrantLock();

        public CacheImp(int N) {
            this.N = N;
        }

        private class Entry{
            byte[] value;
            Condition condition = l.newCondition();
        }

        public void put(int key, byte[] value) {
            l.lock();
            try{
                Entry entry = cache.get(key);
                if(entry == null){
                    Entry e = new Entry();
                    cache.put(key, e);
                }
                keys.add(key);
                while(size == N && entry.value == null){
                    entry.condition.await();
                }
                keys.remove(key);
                if(entry.value == null){
                    size++;
                }
                entry.value = value;
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally{
                l.unlock();
            }
        }

        public byte[] get(int key) {
            l.lock();
            try{
                byte[] value = cache.get(key).value;
                if(value == null){
                    return null;
                }
                return value;
            }finally{
                l.unlock();
            }

        }

        public void evict(int key) {
            l.lock();
            try{
                Entry e = cache.get(key);
                if(e != null && e.value != null){
                    size--;
                    e.value = null;
                    cache.remove(key);
                    if(keys.isEmpty()){
                        int k = keys.remove();
                        e = cache.get(k);
                        e.condition.signal();
                    }
                }
            }finally{
                l.unlock();
            }
        }
    }

}
