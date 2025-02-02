package src;

import java.io.IOException;
import java.util.*;

public class Menu {

    public interface Handler {
        void execute() throws IOException;
    }

    private final List<String> options;
    private final List<Handler> handlers;
    private final Scanner scanner;

    public Menu(String title, String[] options) {
        this.options = Arrays.asList(options);
        this.handlers = new ArrayList<>();
        this.scanner = new Scanner(System.in);

        // Adiciona handlers padrão
        for (int i = 0; i < options.length; i++) {
            this.handlers.add(() -> System.out.println("\nATENÇÃO: Opção não implementada!"));
        }
    }

    public void run() throws IOException {
        int option;
        do {
            showMenu();
            option = readOption();
            if (option > 0) {
                handlers.get(option - 1).execute();
            }
        } while (option != 0);
    }

    private void showMenu() {
        System.out.println("\n*** Menu ***");
        for (int i = 0; i < options.size(); i++) {
            System.out.println("(" + (i + 1) + ") " + options.get(i));
        }
        System.out.println("(0) Sair");
    }

    private int readOption() {
        System.out.print("Escolha uma opção: ");
        try {
            return scanner.nextInt();
        } catch (InputMismatchException e) {
            scanner.nextLine(); // Limpa o buffer
            return -1;
        }
    }

    public void setHandler(int option, Handler handler) {
        if (option > 0 && option <= options.size()) {
            handlers.set(option - 1, handler);
        } else {
            System.out.println("Opção inválida.");
        }
    }

}
