package Rec23;

import java.awt.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Ex1 {
    interface Controlador {
        int reserva();
        void preparado(int kart) throws InterruptedException;
        void completaVolta(int kart);
        int[] voltasCompletas();
        int vencedor() throws InterruptedException;
    }

    public class ControladorImpl implements Controlador {
        private final int N;
        int counter = -1;
        private final Map<Integer,Integer> kartistas = new HashMap<>();
        private final int V;
        private final Lock l = new ReentrantLock();
        private final Condition notReady = l.newCondition();
        private final Condition stillRacing = l.newCondition();
        private boolean started = false;
        private int vencedor = -1;
        private boolean oneFinished = false;


        public ControladorImpl(int N, int V, Map<Integer,Integer> kartistas) {
            this.N = N;
            this.V = V;
            for(int i = 0; i<N; i++){
                kartistas.put(i,0);
            }
        }

        public int reserva(){
            l.lock();
            try{
                int kart = counter++;
                kartistas.put(kart, 0);
                return kart;
            }finally {
                l.unlock();
            }
        }



        public void preparado(int kart) throws InterruptedException{
            l.lock();
            try{
                while(kartistas.size() != N-1){
                    notReady.await();
                }
                started = true;
                notReady.signalAll();
            }finally {
                l.unlock();
            }
        }

        public void completaVolta(int kart){
            l.lock();
            try{
                if(kartistas.get(kart) == V-1){
                    kartistas.remove(kart);
                    counter--;
                    if(!oneFinished){
                        vencedor = kart;
                    }
                    if(kartistas.size() ==  0){
                        stillRacing.signalAll();
                    }
                }
                else
                    kartistas.put(kart, kartistas.get(kart) + 1);
            }finally{
                l.unlock();
            }
        }
        public int[] voltasCompletas(){
            l.lock();
            try{
                int[] voltas = new int[N];
                int i = 0;
                for(int k : kartistas.values()){
                    voltas[i++] = k;
                }
                return voltas;
            } finally {
                l.unlock();
            }
        }
        public int vencedor() throws InterruptedException{
            l.lock();
            try{
                while(kartistas.size() > 0){
                    stillRacing.await();
                }
                return vencedor;
            } finally {
                l.unlock();
            }
        }
    }
}
