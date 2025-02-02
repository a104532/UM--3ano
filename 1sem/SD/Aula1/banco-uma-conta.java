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

    void n1(){
      l.lock();
      //importante dar unlock, assim pode dar runtimeexception porque as threads deixam de aceder
      //não dar return entre estes dois
      //solução, colocar um try
      try{
        //coisas
      } finally{
        l.unlock();
      }
    }

    public void n2(){
      l.lock();
      try{
        //coisas
      } finally{
        l.unlock();
      }
    }

  // Account balance
    public int balance() {
      l.lock();
      try{
        return savings.balance();
      } finally{
        l.unlock();
      }
    }

    // Deposit
    boolean deposit(int value) {
      l.lock();
      try {
        return savings.deposit(value);
      } finally {
        l.unlock();
      }
    }
  }

  class Depositor implements Runnable {

    final Bank B;
    final int I;
    final int V;
    public Depositor(Bank b,int i, int v){
      this.B = b;
      this.I=i;
      this.V = v;
    }
    public void run() {
      for (long i = 0; i < I; i++) {
        B.deposit(V);
      }
    }
  }

  class MainEx2{
    public static void main(String[] args) throws InterruptedException{
      //necessário dar os argumentos para correr
      final int N = Integer.valueOf(args[0]);
      final int I = Integer.valueOf(args[1]);
      final int V = Integer.valueOf(args[2]);

      Bank b = new Bank();
      Thread[] l = new Thread[N];

      for(int i=0;i<N;i++){
        l[i] = new Thread(new Depositor(b, I, V));
        l[i].start();
      }

      for(int i=0;i<N;i++){
        l[i].join();
      }

      System.out.println(b.balance());
    }
}
