import java.util.concurrent.locks.*;

class Bank {

  private static class Account {
    private int balance;

    Account(int balance) {
      this.balance = balance;
    }

    int balance() {
      return balance;
    }

    boolean deposit(int value) {
      balance += value;
      return true;
    }
  }

  // Our single account, for now
  private Account savings = new Account(0);

  Lock l = new ReentrantLock();


  // Account balance
  public int balance() {
      l.lock();
      try{
          return savings.balance();
      } finally {
          l.unlock();
      }

  }

  // Deposit
  boolean deposit(int value) {
    l.lock();
    try{
      return savings.deposit(value);
    } finally {
      l.unlock();
    }

  }

 class Depositor implements Runnable {

    final Bank B;
    final int I;
    final int V;

    public Depositor(int i, int v, Bank b) {
      this.I = i;
      this.V = v;
      this.B = b;
    }
   @Override
    public void run() {
      for(long i = 0; i<I; i++){
        B.deposit(V);
      }
    }

  }

 public static class MainEx2{
      public static void main(String[] args) throws InterruptedException{
        int N = 10;
        Bank bank = new Bank();
        int I = 1000;
        int V = 100;

        Thread[] threads = new Thread[N];

        for(int i = 0; i<N; i++){
          threads[i] = new Thread(bank.new Depositor(I, V, bank));
          threads[i].start();
        }

        for(int i = 0; i<N; i++){
          threads[i].join();
        }

        System.out.println(bank.balance());
      }

  }
}
