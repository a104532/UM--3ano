import ui.TextUI;

public class Main {
    public static void main(String[] args) {
        try {
            new TextUI().menuLogin();
        }
        catch (Exception e) {
            System.out.println("Erro: "+e.getMessage()+" ["+e.toString()+"]");
        }
    }
}