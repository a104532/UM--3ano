import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

class Contact {
    private String name;
    private int age;
    private long phoneNumber;
    private String company;     // Pode ser null
    private ArrayList<String> emails;

    public Contact(String name, int age, long phoneNumber, String company, List<String> emails) {
        this.name = name;
        this.age = age;
        this.phoneNumber = phoneNumber;
        this.company = company;
        this.emails = new ArrayList<>(emails);
    }

    public String name() { return name; }
    public int age() { return age; }
    public long phoneNumber() { return phoneNumber; }
    public String company() { return company; }
    public List<String> emails() { return new ArrayList(emails); }

    // @TODO
    public void serialize(DataOutputStream out) throws IOException {
        out.writeUTF(name);
        out.writeInt(age);
        out.writeLong(phoneNumber);
        out.writeBoolean(company != null);
        if(company != null) {
            out.writeUTF(company);
        }
        out.writeInt(emails.size());
        for(String email : emails) {
            out.writeUTF(email);
        }
         // out.flush(); não é necessário porque não é a mensagem completa, necessáriamente
        // por exemplo se fizermos serialize da lista de contactos, ia dar flush de todos,
        // sendo que a mensagem é apenas a lista de contactos e não o contacto em si
        // ou seja, é uma má ideia fazer no serializable


    }

    // @TODO
    public static Contact deserialize(DataInputStream in) throws IOException {
        String name = in.readUTF();
        int age = in.readInt();
        long phoneNumber = in.readLong();
        Boolean b = in.readBoolean();
        String company = b ? in.readUTF() : null;
        int size = in.readInt();
        List<String> emails = new ArrayList<>();
        for(int i = 0; i<size; i++){
            emails.add(in.readUTF());
        }

        Contact contacto = new Contact(name, age, phoneNumber, company, emails);
        return contacto;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(this.name).append(";");
        builder.append(this.age).append(";");
        builder.append(this.phoneNumber).append(";");
        builder.append(this.company).append(";");
        builder.append(this.emails.toString());
        builder.append("}");
        return builder.toString();
    }

}
