package src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class PutOne {
    private final String key;
    private final byte[] value;

    public PutOne(String key, byte[] value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public byte[] getValue() {
        return value;
    }

    public static void send(PutOne put, DataOutputStream out) throws IOException {
        out.writeUTF(put.key);
        out.writeInt(put.value.length);
        out.write(put.value);
    }

    public static Object receive(DataInputStream in) throws IOException {
        String key = in.readUTF();
        int length = in.readInt();
        byte[] value = new byte[length];
        in.readFully(value);

        return new PutOne(key, value);
    }
}
