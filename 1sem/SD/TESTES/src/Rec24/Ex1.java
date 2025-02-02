package Rec24;

import java.util.ArrayList;
import java.util.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Ex1 {

    private int globalBytesLimit = 16000000;
    private int usedBytesTotal = 0;
    private ReentrantLock lockManager = new ReentrantLock();
    private final Condition globalLimit = lockManager.newCondition();


    static class Managerio implements Manager{
        private int transf_id = 0;
        private final Map<String, TransferObj> transfMap = new HashMap<>();



        @Override
        public String newTransfer() {
            lockManager.lock();
            try{
                TransferObj transfer = new TransferObj(Integer.toString(transf_id));
                transf_id++;
                transfMap.put(transfer.getId(),transfer);
                return transfer.getId();
            } finally{
                lockManager.unlock();
            }
        }

        @Override
        public Transfer getTransfer(String identifier) {
            lockManager.lock();
            try{
                TransferObj res = transfMap.get(identifier);
                if(res != null){
                    return res;
                }else{
                    return null;
                }
            } finally{
                lockManager.unlock();
            }
        }
    }

    class TransferObj extends Managerio implements Transfer{
        private final String id;
        private final int limit = 16000;
        private byte[] buffer = new byte[0];
        private final ReentrantLock lockTransfer = new ReentrantLock();
        private final Condition isFull = lockTransfer.newCondition();
        private final Condition isEmpty = lockTransfer.newCondition();

        public TransferObj(String id) {
            this.id = id;
        }

        @Override
        public void enqueue(byte[] b) throws InterruptedException {
            lockTransfer.lock();
            try{
                while (buffer.length + b.length > limit){
                    isFull.await();
                }
                lockManager.lock();
                try{
                    while(usedBytesTotal + b.length > globalBytesLimit){
                        globalLimit.await();
                    }
                    usedBytesTotal += b.length;
                    buffer = Arrays.copyOf(b,b.length + buffer.length);
                    System.arraycopy(b,0,buffer,buffer.length - b.length,b.length);

                }finally{
                    lockManager.unlock();
                }
                isEmpty.signalAll();

            } finally{
                lockTransfer.unlock();
            }
        }

        @Override
        public byte[] dequeue() throws InterruptedException {
            lockTransfer.lock();
            try{
                while(buffer.length == 0){
                    isEmpty.await();
                }
                byte[] b = Arrays.copyOf(buffer,buffer.length);
                buffer = new byte[0];
                lockManager.lock();
                try{
                    usedBytesTotal -= b.length;
                    globalLimit.signalAll();
                } finally{
                    lockManager.unlock();
                }
                isFull.signalAll();
                return b;
            } finally{
                lockTransfer.unlock();
            }
        }

        public String getId(){
            return this.id;
        }
    }

    interface Manager {
        String newTransfer();
        Transfer getTransfer(String identifier);
    }

    interface Transfer {
        void enqueue(byte[] b) throws InterruptedException;
        byte[] dequeue() throws InterruptedException;
    }
}
