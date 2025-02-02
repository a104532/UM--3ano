import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

class Bank {

    private static class Account {
        private int balance;
        Account(int balance) { this.balance = balance; }
        private final Lock lock = new ReentrantLock();
        int balance() { return balance; }
        boolean deposit(int value) {
            balance += value;
            return true;
        }
        boolean withdraw(int value) {
            if (value > balance)
                return false;
            balance -= value;
            return true;
        }
    }

    private Map<Integer, Account> map = new HashMap<Integer, Account>();
    private int nextId = 0;
    private final ReentrantReadWriteLock mapLock = new ReentrantReadWriteLock();
    private final Lock readLock = mapLock.readLock();
    private final Lock writeLock = mapLock.writeLock();
    // create account and return account id
    public int createAccount(int balance) {
        writeLock.lock();
        try{
            Account c = new Account(balance);
            int id = nextId;
            nextId += 1;
            map.put(id, c);
            return id;
        } finally{
            writeLock.unlock();
        }

    }

    // close account and return balance, or 0 if no such account
    public int closeAccount(int id) {
        Account c;
        writeLock.lock();
        try {
            c = map.remove(id);
            if (c == null)
                return 0;
            c.lock.lock();
        } finally {
           writeLock.unlock();
        }
        try{
            return c.balance();
        } finally{
            c.lock.unlock();
        }
    }

    // account balance; 0 if no such account
    public int balance(int id) {
        Account c;
        readLock.lock();
        try{
            c = map.get(id);
            if (c == null)
                return 0;
            c.lock.lock();
        } finally{
            readLock.unlock();
        }
        try{
            return c.balance();
        } finally{
            c.lock.unlock();
        }
    }

    // deposit; fails if no such account
    public boolean deposit(int id, int value) {
        Account c;
        readLock.lock();
        try{
            c = map.get(id);
            if (c == null)
                return false;
            c.lock.lock();
        } finally{
            readLock.unlock();
        }
        try{
            return c.deposit(value);
        } finally{
            c.lock.unlock();
        }

    }

    // withdraw; fails if no such account or insufficient balance
    public boolean withdraw(int id, int value) {
        Account c;
        readLock.lock();
        try{
            c = map.get(id);
            if (c == null)
                return false;
            c.lock.lock();
        } finally{
            readLock.unlock();
        }
        try{
            return c.withdraw(value);
        } finally{
            c.lock.unlock();
        }

    }

    // transfer value between accounts;
    // fails if either account does not exist or insufficient balance
    public boolean transfer(int from, int to, int value) {
        Account cfrom, cto;
        readLock.lock();
        try{
            cfrom = map.get(from);
            cto = map.get(to);
            if (cfrom == null || cto ==  null)
                return false;

            if(from < to){
                cfrom.lock.lock();
                cto.lock.lock();
            } else{
                cto.lock.lock();
                cfrom.lock.lock();
            }
        } finally{
            readLock.unlock();
        }
        try{
            if(!cfrom.withdraw(value)){
                return false;
            }
            return cto.deposit(value);
        } finally{
            cfrom.lock.unlock();
            cto.lock.unlock();
        }
    }

    // sum of balances in set of accounts; 0 if some does not exist
    public int totalBalance(int[] ids) {
        readLock.lock();
        int total = 0;
        Account[] accs = new Account[ids.length];
        try{
            for (int i : ids) {
                Account c = map.get(i);
                if (c == null)
                    return 0;
                accs[i] = c;
            }
            for(int i = 0; i<ids.length; i++){
                accs[i].lock.lock();
            }
        } finally{
            readLock.unlock();
        }
        for(Account c : accs){
            total += c.balance();
            c.lock.unlock();
        }
        return total;

    }

}
