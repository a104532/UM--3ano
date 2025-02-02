class MyThread extends Thread{
    public void run(){
        for(long i = 0; i <1L << 32; i++);
        System.out.println("Na Thread 1");
    }
}

class MyRunnble implements Runnable{
    public void run(){
        for(long i = 0; i <1L << 32; i++);
        System.out.println("Na Thread 2");
    }
}
class Main{
    public static void main(String[] args) throws InterruptedException { // declarar que algum problema pode acontecer
        Thread t1 = new MyThread();
        //MyRunnble r = new MyRunnble(); <-- não convém fazer isto
        //Thread t2 = new Thread(r)
        //Thread t3 = new Thread(r) <-- fazer um próprio runnble para cada thread
        Thread t2 = new Thread(new MyRunnble());
        Thread t3 = new Thread(new MyRunnble());
        // t1.run(); <-- nunca fazer
        t1.start(); // corre primeiro na main neste caso, não espera pela thread
        t2.start();
        System.out.println("Na Main");
        t1.join(); //bloqueia o invocador, ao declarar a exception, já consigo correr
        t2.join(); // só continua depois de acabar o t2
        System.out.println("No fim");
    }
}
