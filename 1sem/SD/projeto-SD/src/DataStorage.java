package src;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.*;

public class DataStorage {
    private final Map<String, byte[]> storage; // Mapeamento de chave -> valor
    private final Map<String,String> users; // Mapeamento de Utilizadores
    private final ReentrantReadWriteLock storageLock;
    private final ReentrantReadWriteLock usersLock;
    private final Condition condition;


    public DataStorage() {
        this.storage = new HashMap<>();
        this.users = new HashMap<>();
        this.storageLock = new ReentrantReadWriteLock();
        this.usersLock = new ReentrantReadWriteLock();
        this.condition = storageLock.writeLock().newCondition();
    }

    public boolean registerUser(String username, String password){
        usersLock.writeLock().lock();
        try{
            if(users.containsKey(username)){
                return false;
            }
            users.put(username, password);
            return true;
        }finally {
            usersLock.writeLock().unlock();
        }
    }

    public boolean authenticateUser(String username, String password){
        usersLock.readLock().lock();
        try{
            return password.equals(users.get(username));
        } finally{
            usersLock.readLock().unlock();
        }
    }

    // Escrita simples
    public void put(String key, byte[] value) {
        storageLock.writeLock().lock();
        try {
            storage.put(key, value);
            condition.signalAll();

        } finally {
            storageLock.writeLock().unlock();
        }
    }

    // Leitura simples
    public byte[] get(String key) {
        storageLock.readLock().lock();
        try {
            return storage.get(key);
        } finally {
            storageLock.readLock().unlock();
        }
    }

    // Escrita composta
    public void multiPut(Map<String, byte[]> pairs) {
        storageLock.writeLock().lock();
        try {
            pairs.forEach((key, value) -> {
                System.out.println("Armazenando chave: " + key + ", Valor: " + new String(value, StandardCharsets.UTF_8));
                storage.put(key, value);
            });
                condition.signalAll();
        } finally {
            storageLock.writeLock().unlock();
        }
    }

    // Leitura composta
    public Map<String, byte[]> multiGet(Set<String> keys) {
        storageLock.readLock().lock();
        try {
            Map<String, byte[]> result = new HashMap<>();
            for (String key : keys) {
                if (storage.containsKey(key)) {
                    result.put(key, storage.get(key));
                }
            }
            return result;
        } finally {
            storageLock.readLock().unlock();
        }
    }

    // Leitura condicional
    public byte[] getWhen(String key, String keyCond, byte[] valueCond) {
        storageLock.readLock().lock();
        try {
            if (checkCondition(keyCond, valueCond)) {
                return storage.get(key);
            }
        } finally {
            storageLock.readLock().unlock();
        }

        storageLock.writeLock().lock();
        try {
            while (!Thread.currentThread().isInterrupted()) {
                if (checkCondition(keyCond, valueCond)) {
                    return storage.get(key);
                }
                condition.await();
            }
            return null;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        } finally {
            storageLock.writeLock().unlock();
        }
    }

    private boolean checkCondition(String keyCond, byte[] valueCond) {
        return storage.containsKey(keyCond) && Arrays.equals(storage.get(keyCond), valueCond);
    }
}