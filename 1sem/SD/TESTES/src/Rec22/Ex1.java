package Rec22;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Ex1 {
    interface Reuniao {
        void participa(int lista) throws InterruptedException;
        void abandona(int lista);
        int naSala();
        int aEspera();
    }

    public class ReuniaoImp implements Reuniao {
        private final Lock lock = new ReentrantLock();
        private final Map<Integer, Condition> filaEspera = new HashMap<>();

        private int listaAtual = -1;
        private int membrosNaSala = 0;
        private Map<Integer, Integer> membrosEspera = new HashMap<>();



        public void participa(int lista) throws InterruptedException{
            lock.lock();
            try{
                membrosEspera.put(lista, membrosEspera.get(lista) + 1);

                Condition condicaoLista = filaEspera.get(lista);
                if(condicaoLista == null){
                    condicaoLista = lock.newCondition();
                    filaEspera.put(lista, condicaoLista);
                }
                while(listaAtual != -1 || listaAtual != lista){
                    condicaoLista.await();
                }
                membrosEspera.put(lista, membrosEspera.get(lista) - 1);
                listaAtual = lista;
                membrosNaSala++;

            } finally{
                lock.unlock();
            }
        }

        public void abandona(int lista){
            lock.lock();
            try{
                if(listaAtual == lista){
                    membrosNaSala--;

                    if(membrosNaSala == 0){
                        listaAtual = -1;
                        int proximaLista = -1;
                        int maxEspera = 0;
                        for(Map.Entry<Integer, Integer> entry : membrosEspera.entrySet()){
                            if(entry.getValue() > maxEspera){
                                maxEspera = entry.getValue();
                                proximaLista = entry.getKey();
                            }
                        }

                        if(proximaLista != -1){
                            filaEspera.get(proximaLista).signalAll();
                        }
                    }

                }
            }finally{
                lock.unlock();
            }
        }

        public int naSala(){
            lock.lock();
            try{
                return membrosNaSala;
            }finally{
                lock.unlock();
            }
        }

        public int aEspera(){
            lock.lock();
            try{
                return membrosEspera.values().stream().mapToInt(Integer::intValue).sum();
            }finally{
                lock.unlock();
            }
        }
    }
}
