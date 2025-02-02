import java.util.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Lock;


class Warehouse {
    private Map<String, Product> map =  new HashMap<String, Product>();
    Lock l = new ReentrantLock();

    private class Product {
        int quantity = 0;
        Condition condition = l.newCondition();
    }

    private Product get(String item) {
        Product p = map.get(item);
        if (p != null) return p;
        p = new Product();
        map.put(item, p);
        return p;
    }

    public void supply(String item, int quantity) {
        l.lock();
        try{
            Product p = get(item);
            p.quantity += quantity;
            p.condition.signalAll();
        } finally{
            l.unlock();
        }

    }

    private Product missing(Product[] a){
        for(Product p : a){
            if(p.quantity <= 0) return p;
        }
        return null;
    }

    // Errado se faltar algum produto...
    public void consume(Set<String> items) throws InterruptedException {
        l.lock();
        try{
            Product[] a = new Product[items.size()];
            int i = 0;
            for (String s : items){
                a[i++] = get(s);
            }
            while(missing(a) != null){
                missing(a).condition.await();
            }
            for(Product p : a){
                p.quantity--;
            }
        } finally{
            l.unlock();
        }
    }

}
