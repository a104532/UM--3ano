package Rec24;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Ex1Rec {

    interface Manager {
        String newTransfer();
        Transfer getTransfer(String identifier);
    }

    interface Transfer {
        void enqueue(byte[] b) throws InterruptedException;
        byte[] dequeue() throws InterruptedException;
    }


    public class ManagerImp implements Manager{
        private final int maxCapacity = 16000000;
        private int usedBytes = 0;
        private int transferCount = 0;
        private final Map<String, TransferImp> transfers = new HashMap<>();
        private final Lock lock = new ReentrantLock();
        private final Condition global = lock.newCondition();

        public String newTransfer(){
            lock.lock();
            try{
                transferCount++;
                String id = transferCount + "";
                transfers.put(id,new TransferImp(id));
                return id;
            } finally{
                lock.unlock();
            }
        }

        public Transfer getTransfer(String identifier){
            lock.lock();
            try{
                return transfers.get(identifier);
            }finally{
                lock.unlock();
            }
        }

        public class TransferImp implements Transfer{
            private final int maxTransfer = 16000;
            private final int currentTransfer = 0;
            private final Lock l = new ReentrantLock();
            private final Condition isFull = l.newCondition();
            private final Condition isEmpty = l.newCondition();
            private final String transferId;
            private byte[] buffer;
            private boolean closed = false;

            public TransferImp(String transferId){
                this.transferId = transferId;
            }

            public void enqueue(byte[] b) throws InterruptedException{
                l.lock();
                try{
                    if (closed) {
                        throw new IllegalStateException("Transferência encerrada.");
                    }
                    while(buffer.length + b.length > maxTransfer){
                        isFull.await();
                    }
                    if(b == null){
                        closed = true;
                    }
                    buffer = Arrays.copyOf(buffer, buffer.length + b.length);
                    System.arraycopy(b, 0, buffer, buffer.length - b.length, b.length);
                    lock.lock();
                    try {
                        while(usedBytes + b.length > maxCapacity){
                            global.wait();
                        }
                        usedBytes += b.length;
                    } finally{
                        lock.unlock();
                    }
                    isEmpty.signalAll();
                }finally{
                    l.unlock();
                }



            }

            public byte[] dequeue() throws InterruptedException{
                l.lock();
                try{
                    while(buffer.length == 0 && !closed)
                        isEmpty.await();

                    if(buffer.length == 0 && closed){
                        return null;
                    }

                    byte[] b = Arrays.copyOf(buffer,buffer.length);
                    buffer = new byte[0];
                    lock.lock();
                    try{
                        usedBytes -= b.length;
                        global.signalAll();
                    }finally{
                        lock.unlock();
                    }
                    isFull.signalAll();
                    return b;
                }finally{
                    l.unlock();
                }

            }

        }

    }


}
