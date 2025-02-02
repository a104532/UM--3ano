import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

class ContactList extends ArrayList<Contact> {

    // @TODO
    public void serialize(DataOutputStream out) throws IOException {
        // usar mesma estratégia dos emails
        
    }

    // @TODO
    public static ContactList deserialize(DataInputStream in) throws IOException {
        return null;
    }

}
