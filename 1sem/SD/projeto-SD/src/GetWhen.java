package src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class GetWhen {
    private final String key;
    private final String conditionKey;
    private final byte[] conditionValue;
    private byte[] result;
    private boolean success;

    public GetWhen(String key, String conditionKey, byte[] conditionValue) {
        this.key = key;
        this.conditionKey = conditionKey;
        this.conditionValue = conditionValue;
        this.success = false;
    }

    public String getKey() {
        return key;
    }

    public String getConditionKey() {
        return conditionKey;
    }

    public byte[] getConditionValue() {
        return conditionValue;
    }

    public static void send(GetWhen getWhen, DataOutputStream out) throws IOException {
        out.writeUTF(getWhen.key);
        out.writeUTF(getWhen.conditionKey);
        out.writeInt(getWhen.conditionValue.length);
        out.write(getWhen.conditionValue);
    }

    public static GetWhen receive(DataInputStream in) throws IOException {
        String key = in.readUTF();
        String conditionKey = in.readUTF();
        int length = in.readInt();
        byte[] conditionValue = new byte[length];
        in.readFully(conditionValue);
        return new GetWhen(key, conditionKey, conditionValue);
    }

    synchronized void setResult(byte[] result) {
        this.result = result;
        this.success = true;
        notifyAll();
    }

    synchronized byte[] waitForResult() throws InterruptedException {
        while (!success) {
            wait();
        }
        return result;
    }

}
