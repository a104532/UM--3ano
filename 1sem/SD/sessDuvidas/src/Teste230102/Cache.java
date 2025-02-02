package Teste230102;

import java.util.*;
import java.util.concurrent.locks.*;

interface Cache {
    void put(int key, byte[] value) throws InterruptedException;
    byte[] get(int key);
    void evict(int key);
}

class CacheImpl{

    private Lock l = new ReentrantLock();

    private class Entry {
        byte[] value;
        Condition cond = l.newCondition();
    }

    final int N;
    private int size = 0;
    private Map<Integer, byte[]> map = new HashMap<>();
    private Set<Integer> keys = new HashSet<>();


    public CacheImpl(int N){ this.N = N;}


public void put(int key, byte[] value) throws InterruptedException {
    l.lock();
    try{
        Entry e = map.get(key);
        if(e == null){
            e = new Entry();
            map.put(key,e);
        }
        keys.add(key);
        while(map.size() == N && e.value == null){
            e.cond.await();
        }
        keys.remove(key);
        if(e.value == null){
            size+=1;
        }
        e.value = value;
    } finally{
        l.unlock();

    }
}

public byte[] get(int key) {
    l.lock();
    byte[] b;
    try {
        Entry e = map.get(key);
        b = e == null ? null : e.value;
    } finally {
        l.unlock();
    }
    return b;
}

public void evict(int key){
    l.lock();
    try{
        Entry e = map.get(key);
        if(e != null && e.value != null){
            size -= 1;
            if(!keys.isEmpty()){
                int k = keys.remove();
                e = map.get(k);
                e.cond.signalAll();
            }
        }
    } finally{
        l.unlock();
    }

}


}
