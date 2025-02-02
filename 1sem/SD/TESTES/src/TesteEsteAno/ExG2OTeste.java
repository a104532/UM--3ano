package TesteEsteAno;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ExG2OTeste {
    interface Manager{
        Trip permission(int size) throws InterruptedException;
    }

    interface Trip{
        int dockId();
        void waitDisembark() throws InterruptedException;
        void finishedDisembark();
        void depart();
    }

    public static class ManagerImpl implements Manager {
        private static final boolean[] docas = new boolean[28];
        private static final Lock lock = new ReentrantLock();
        private static final Condition full = lock.newCondition();


        public ManagerImpl() {
            for (int i = 0; i < docas.length; i++) {
                docas[i] = false;
            }
        }

        public Trip permission(int size) throws InterruptedException {
            lock.lock();
            try {
                while (isFull()) {
                    full.await();
                }
                int dockId = assignDock();
                return new TripImpl(dockId);
            } finally {
                lock.unlock();
            }

        }

        public boolean isFull() {
            for (boolean d : docas) {
                if (d) {
                    return false;
                }
            }
            return true;
        }

        public int assignDock() {
            for (int i = 0; i < docas.length; i++) {
                if (docas[i]) {
                    docas[i] = true;
                    return i;
                }
            }
            return -1;
        }


        public static class TripImpl implements Trip {
            private final int dockId;
            private final Lock tlock = new ReentrantLock();
            private final Condition tfull = tlock.newCondition();
            private boolean finished;


            public TripImpl(int dockId) {
                this.dockId = dockId;
                finished = false;
            }

            public int dockId() {
                tlock.lock();
                try {
                    return dockId;
                } finally {
                    tlock.unlock();
                }

            }

            public void waitDisembark() throws InterruptedException {
                tlock.lock();
                try {
                    while (!finished) {
                        tfull.await();
                    }

                } finally {
                    tlock.unlock();
                }

            }

            @Override
            public void finishedDisembark() {
                tlock.lock();
                try {
                    finished = true;
                    tfull.signal();
                } finally {
                    tlock.unlock();
                }
            }

            @Override
            public void depart() {
                lock.lock();
                try {
                    docas[dockId] = false;
                    full.signal();
                } finally {
                    lock.unlock();
                }
            }
        }
    }
}
