import java.util.concurrent.locks.*;

public class BancoMultiplasContas {

    private static class Account {
        private int balance;
        Account (int balance) { this.balance = balance; }
        int balance () { return balance; }
        boolean deposit (int value) {
            balance += value;
            return true;
        }
        boolean withdraw (int value) {
            if (value > balance)
                return false;
            balance -= value;
            return true;
        }
    }

    // Bank slots and vector of accounts
    private final int slots;
    private Account[] av;
    //feito
    private Lock l = new ReentrantLock();
    //---
    public BancoMultiplasContas (int n) {
        slots=n;
        av=new Account[slots];
        for (int i=0; i<slots; i++)
            av[i]=new Account(0);
    }


    // Account balance
    public int balance (int id) {
        l.lock();
        try {
            if (id < 0 || id >= slots)
                return 0;
            return av[id].balance();
        } finally {
            l.unlock();
        }
    }

    // Deposit
    public boolean deposit (int id, int value) {
        if (id < 0 || id >= slots)
            return false;
        l.lock();
        try {
            return av[id].deposit(value);
        } finally{
            l.unlock();
        }
    }

    // Withdraw; fails if no such account or insufficient balance
    public boolean withdraw (int id, int value) {
        if (id < 0 || id >= slots)
            return false;
        l.lock();
        try {
            return av[id].withdraw(value);
        } finally{
            l.unlock();
        }
    }

    // Transfer
    public boolean transfer (int from, int to, int value) {
        if(from < 0 || from >= slots || to < 0 || to >= slots)
            return false;
        //Account cf = av[from];
        //Account ct = av[to];
        //cf.l.lock();
        //ct.l.lock();
        //try {
            return withdraw(from, value) && deposit(to, value);
        //} finally{
            //cf.l.unlock();
            //ct.l.unlock();
        //}
    }

    // TotalBalance
    public int totalBalance () {
        int sum = 0;
        for(int i = 0; i<slots;i++){
            sum += balance(i);
        }
        return sum;
    }
}

