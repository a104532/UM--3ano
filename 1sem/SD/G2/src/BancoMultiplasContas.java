import java.util.concurrent.locks.*;

public class BancoMultiplasContas {

    private static class Account {
        private int balance;
        Lock l = new ReentrantLock(); //usado para os locks de cada conta individual

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

    public BancoMultiplasContas (int n) {
        slots=n;
        av=new Account[slots];
        for (int i=0; i<slots; i++)
            av[i]=new Account(0);
    }


    // Account balance
    public int balance (int id) {
        if (id < 0 || id >= slots)
            return 0;
        av[id].l.lock();
        try{
            return av[id].balance();
        } finally{
            av[id].l.unlock();
        }

    }

    // Deposit
    public boolean deposit (int id, int value) {
        if (id < 0 || id >= slots)
            return false;
        av[id].l.lock();
        try{
            return av[id].deposit(value);
        } finally{
            av[id].l.unlock();
        }

    }

    // Withdraw; fails if no such account or insufficient balance
    public boolean withdraw (int id, int value) {
        if (id < 0 || id >= slots)
            return false;
        av[id].l.lock();
        try{
            return av[id].withdraw(value);
        } finally{
            av[id].l.unlock();
        }
    }


    // Transfer
    public boolean transfer (int from, int to, int value) {
        if(from < 0 || from >= slots || to < 0 || to >= slots || from == to )
        return false; // verificação de erros

        if (from < to){
            av[from].l.lock();
            av[to].l.lock();
        } else{
            av[to].l.lock();
            av[from].l.lock();
        }

        try{
            if(av[from].balance >= value){
                av[from].withdraw(value);
                av[to].deposit(value);
                return true;
            }
            return false;
        } finally{
            av[from].l.unlock();
            av[to].l.unlock();
        }
    }

    // TotalBalance
    public int totalBalance () {
        int sum = 0;
        for(int i=0; i<slots; i++){
            sum+=av[i].balance();
        }
        return sum;
    }
}

