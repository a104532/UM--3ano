package ui;

import java.util.*;

public class Menu {
    public interface Handler {
        void execute();
    }

    public interface PreCondition {
        boolean validate();
    }

    private static Scanner is = new Scanner(System.in);

    private String title;
    private List<String> options;
    private List<PreCondition> available;
    private List<Handler> handlers;

    public Menu() {
        this.title = "Menu";
        this.options = new ArrayList<>();
        this.available = new ArrayList<>();
        this.handlers = new ArrayList<>();
    }

    public Menu(String title, List<String> options) {
        this.title = title;
        this.options = new ArrayList<>(options);
        this.available = new ArrayList<>();
        this.handlers = new ArrayList<>();
        this.options.forEach(s-> {
            this.available.add(()->true);
            this.handlers.add(()->System.out.println("\nATENÇÃO: Opção não implementada!"));
        });
    }

    public Menu(List<String> options) { this("Menu", options); }

    public Menu(String title, String[] options) {
        this(title, Arrays.asList(options));
    }

    public Menu(String[] options) {
        this(Arrays.asList(options));
    }

    public void option(String name, PreCondition p, Handler h) {
        this.options.add(name);
        this.available.add(p);
        this.handlers.add(h);
    }

    public void runOnce() {
        int op;
        show();
        op = readOption();
        if (op > 0 && !this.available.get(op - 1).validate()) {
            System.out.println("Opção indisponível!");
        } else if (op > 0) {
            this.handlers.get(op - 1).execute();
        }
    }

    public void run() {
        int op;
        do {
            show();
            op = readOption();
            if (op > 0 && !this.available.get(op - 1).validate()) {
                System.out.println("Opção indisponível! Tente novamente.");
            } else if (op > 0) {
                this.handlers.get(op - 1).execute();
            }
        } while (op != 0);
    }

    public void setPreCondition(int i, PreCondition b) {
        this.available.set(i - 1,b);
    }

    public void setHandler(int i, Handler h) {
        this.handlers.set(i - 1, h);
    }

    private void show() {
        System.out.println("*** " + this.title + " ***\n");
        for (int i = 0; i < this.options.size(); i++) {
            System.out.print(i + 1);
            System.out.print(" - ");
            System.out.println(this.available.get(i).validate() ? this.options.get(i) : "---");
        }
        System.out.println("0 - Sair");
    }

    private int readOption() {
        int op;

        System.out.print("Opção: ");
        try {
            String line = is.nextLine();
            op = Integer.parseInt(line);
        } catch (NumberFormatException e) {
            op = -1;
        }

        if (op<0 || op>this.options.size()) {
            System.out.println("Opção Inválida!");
            op = -1;
        }

        return op;
    }
}