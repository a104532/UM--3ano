import java.util.*;
import java.util.concurrent.locks.*;

class Warehouse {
    private Map<String, Product> map =  new HashMap<String, Product>();

    private class Product {
        int quantity = 0;
        Condition cond = l.newCondition();
    }

    Lock l = new ReentrantLock();
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
            p.cond.signalAll();
        } finally{
            l.unlock();
        }

    }
    private Product missing(Product[] a) {
        for (Product p : a)
            if (p.quantity <= 0) return p;

        return null;
    }

    // Errado se faltar algum produto...
    public void consume(Set<String> items) throws InterruptedException {
        l.lock();
        try{
            Product[] a = new Product[items.toArray().length];
            int i = 0;
            for (String s : items) {
                a[i++] = get(s);
            }
            while(missing(a) != null)
                missing(a).cond.await();

            for(;;){
                Product p = missing(a);
                if(p == null) break;
                p.cond.await();
            }
            for(int j= 0; j< items.toArray().length; j++){
                if(a[i].quantity <= 0) {
                    a[i].cond.await();
                    i = 0;
                } else{
                    i++;
                }
            }
            for (Product p : a){
                p.quantity--;
            }
        } finally{
            l.unlock();
        }

    }

}
