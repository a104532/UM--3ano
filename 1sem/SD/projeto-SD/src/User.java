package src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Objects;

public class User {
    private String user;
    private String pass;

    // Construtor
    public User(String user, String pass) {
        this.user = user;
        this.pass = pass;
    }

    // Getters e Setters
    public String getUser() {
        return user;
    }

    public String getPass() {
        return pass;
    }

    public static void send(User user, DataOutputStream out) throws IOException {
        out.writeUTF(user.getUser());
        out.writeUTF(user.getPass());
    }

    public static User receive(User user, DataInputStream in) throws IOException {
        String username = in.readUTF();
        String pass = in.readUTF();
        return new User(username, pass);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user1 = (User) o;
        return user.equals(user1.user) && pass.equals(user1.pass);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, pass);
    }
}