package src;

import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Frame implements Serializable {
    private final int tag;        // Identificador unico do pedido
    private final Object data;    // Data associada ao pedido
    private final int type;       // Tipo de pedido
    private final boolean isResponse;


    // Types of requests
    public static final int LOGIN = 1;
    public static final int CREATE_ACCOUNT = 2;
    public static final int PUT = 3;
    public static final int GET = 4;
    public static final int MULTIPUT = 5;
    public static final int MULTIGET = 6;
    public static final int GETWHEN = 7;

    public Frame(int tag, Object data, int type, boolean isResponse) {
        this.tag = tag;
        this.data = data;
        this.type = type;
        this.isResponse = isResponse;

    }

    public int getTag() {
        return tag;
    }

    public Object getData() {
        return data;
    }

    public int getType() {
        return type;
    }

    public void serialize(DataOutputStream out) throws IOException {
        out.writeInt(tag);
        out.writeInt(type);
        out.writeBoolean(isResponse);

        switch (type) {
            case LOGIN:
                if(isResponse)
                    out.writeBoolean((boolean) data);
                else
                    User.send((User) data, out);
                break;

            case CREATE_ACCOUNT:
                if(isResponse)
                    out.writeBoolean((boolean) data);
                else
                    User.send((User) data, out);
                break;

            case PUT:
                if(isResponse)
                    out.writeBoolean((boolean) data);
                else
                    PutOne.send((PutOne) data, out);
                break;

            case GET:
                if(isResponse){
                    byte[] bytes = (byte[]) data;
                    out.writeInt(bytes.length);
                    out.write(bytes);
                }
                else
                    out.writeUTF((String) data);
                break;
            case MULTIPUT:
                if(isResponse)
                    out.writeBoolean((boolean) data);
                else{
                    Map<String, byte[]> map = (Map<String, byte[]>) data;
                    out.writeInt(map.size());
                    for (Map.Entry<String, byte[]> entry : map.entrySet()) {
                        out.writeUTF(entry.getKey());
                        out.writeInt(entry.getValue().length);
                        out.write(entry.getValue());
                    }
                }
                break;
            case MULTIGET:
                if(isResponse){
                    Map<String, byte[]> map = (Map<String, byte[]>) data;
                    out.writeInt(map.size());
                    for (Map.Entry<String, byte[]> entry : map.entrySet()) {
                        out.writeUTF(entry.getKey());
                        out.writeInt(entry.getValue().length);
                        out.write(entry.getValue());
                    }
                } else{
                    Set<String> keys = (Set<String>) data;
                    out.writeInt(keys.size());  // Enviar número de chaves
                    for (String key : keys) {
                        out.writeUTF(key);
                    }
                }
                break;
            case GETWHEN:
                if(isResponse){
                    byte[] bytes = (byte[]) data;
                    out.writeInt(bytes.length);
                    out.write(bytes);
                } else{
                    GetWhen.send((GetWhen) data, out);
                }
                break;

            default:
                throw new IOException("Unsupported frame type: " + type);
        }
    }

    public static Frame deserialize(DataInputStream in) throws IOException {
        int tag = in.readInt();
        int type = in.readInt();
        boolean isResponse = in.readBoolean();
        Object data = null;

        switch (type) {
            case LOGIN, CREATE_ACCOUNT:
                if(isResponse)
                    data = in.readBoolean();
                else
                    data = User.receive(null, in);
                break;

            case PUT:
                if(isResponse) {
                    data = in.readBoolean();
                } else {
                    data = PutOne.receive(in);
                }
                break;

            case GET:
                if (isResponse) {
                    int length = in.readInt();
                    byte[] bytes = new byte[length];
                    in.readFully(bytes);
                    data = bytes;
                } else {
                    data = in.readUTF();
                }
                break;
            case MULTIPUT:
                if(isResponse)
                    data = in.readBoolean();
                else {
                    int mapSize = in.readInt();
                    Map<String, byte[]> map = new HashMap<>();
                    for (int i = 0; i < mapSize; i++) {
                        String key = in.readUTF();
                        int valueLength = in.readInt();
                        byte[] value = new byte[valueLength];
                        in.readFully(value);
                        map.put(key, value);
                    }
                    data = map;
                }
                break;
            case MULTIGET:
                if (isResponse) {
                    int mapSize = in.readInt();
                    Map<String, byte[]> map = new HashMap<>();
                    for (int i = 0; i < mapSize; i++) {
                        String key = in.readUTF();
                        int valueLength = in.readInt();
                        byte[] value = new byte[valueLength];
                        in.readFully(value);
                        map.put(key, value);
                    }
                    data = map;
                } else {
                    int keyCount = in.readInt();
                    Set<String> keys = new HashSet<>();
                    for (int i = 0; i < keyCount; i++) {
                        keys.add(in.readUTF());
                    }
                    data = keys;
                }
                break;
            case GETWHEN:
                if (isResponse) {
                    int length = in.readInt();
                    byte[] bytes = new byte[length];
                    in.readFully(bytes);
                    data = bytes;
                } else {
                    data = GetWhen.receive(in);
                }
                break;

            default:
                throw new IOException("Unsupported frame type: " + type);
        }
        return new Frame(tag, data, type, isResponse);
    }
}
