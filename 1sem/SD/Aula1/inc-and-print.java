class Increment implements Runnable {

    final long I;
    public Increment(long I){ this.I = I;}
    public void run(){

        for (long i = 0; i < I; i++)
            System.out.println(i+1);
    }
}

class MainEx1{
    public static void main(String[] args) throws InterruptedException {
        final int N = Integer.valueOf(args[0]);
        final int I = Integer.valueOf(args[1]);
        Thread[] a = new Thread[N];
        for(int i = 0; i < N; i++){
            a[i] = new Thread(new Increment(I));
        }

        for(int i = 0; i < N; i++){
            a[i].start();
        }

        for(int i = 0; i < N; i++){
            a[i].join();
        }

        System.out.println("No fim");
    }
}

