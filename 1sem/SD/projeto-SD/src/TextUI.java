package src;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class TextUI {

    private final Client client;
    private final Menu menuLogin;
    private Menu menuOptions;
    private Scanner scin;
    private boolean isAuthenticated;

    public TextUI(Client client) throws IOException {
        this.client = client;
        this.scin = new Scanner(System.in);
        this.isAuthenticated = false;

        this.menuLogin = new Menu("Menu Principal", new String[]{
                "Login",
                "Registo"
        });
        this.menuLogin.setHandler(1, this::login);
        this.menuLogin.setHandler(2, this::register);
        scin =  new Scanner(System.in);
    }

    public void login() {
        try {
            System.out.print("Username: ");
            String username = scin.next();
            System.out.print("Password: ");
            String password = scin.next();


            if(client.authenticate(username, password)){
                isAuthenticated = true;
                System.out.println("Login bem-sucedido!");
                pressAnyKey(); // Aguarda entrada para continuar

                // Inicia o menu de opções após login
                this.menuOptions = new Menu("Menu Options", new String[]{
                        "Put",
                        "Get",
                        "MultiPut",
                        "MultiGet",
                        "GetWhen",
                        "ViewResults"
                });
                this.menuOptions.setHandler(1, this::put);
                this.menuOptions.setHandler(2, this::get);
                this.menuOptions.setHandler(3, this::multiPut);
                this.menuOptions.setHandler(4, this::multiGet);
                this.menuOptions.setHandler(5, this::getWhen);
                this.menuOptions.setHandler(6, this::viewResults);

                clearTerminal();
                menuOptions.run(); // Executa diretamente o menu de operações
            }
        } catch (AuthenticationFailedException e) {
            System.out.println("Falha na autenticação: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("Erro na comunicação: " + e.getMessage());
        }
    }

    public void register() {
        try {
            System.out.print("Novo Username: ");
            String username = scin.next();
            System.out.print("Nova Password: ");
            String password = scin.next();

            if (client.createAccount(username, password)) {
                System.out.println("Conta criada com sucesso!");
            } else {
                System.out.println("Erro: Não foi possível criar a conta. Username pode já existir.");
            }
        } catch (UserAlreadyExistsException e) {
            System.out.println("Erro: User já existe." + e.getMessage());
        } catch (IOException e) {
            System.out.println("Erro na comunicação: " + e.getMessage());
        }
    }

    private void ensureAuthenticated() {
        if (!isAuthenticated) {
            throw new IllegalStateException("Operação não permitida: o utilizador não está autenticado.");
        }
    }

    public void put() {
        try {
            ensureAuthenticated();
            System.out.print("Digite a chave: ");
            String key = scin.nextLine();
            System.out.print("Digite o valor: ");
            String value = scin.nextLine();

            client.put(key, value.getBytes(StandardCharsets.UTF_8));
            System.out.println("Par chave-valor inserido com sucesso!");
        } catch (IllegalStateException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println("Erro ao inserir dados: " + e.getMessage());
        }
    }

    public void get() {
        try {
            ensureAuthenticated();
            System.out.print("Digite a chave: ");
            String key = scin.nextLine();

            client.get(key);
            System.out.println("Operação enviada com sucesso!");
        } catch (IllegalStateException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println("Erro ao obter dados: " + e.getMessage());
        }
    }

    public void multiPut() {
        try {
            ensureAuthenticated();
            System.out.println("Digite os pares chave-valor no formato key1:value1,key2:value2");
            String input = scin.nextLine();

            Map<String, byte[]> data = new HashMap<>();
            for (String pair : input.split(",")) {
                String[] kv = pair.split(":");
                if (kv.length == 2) {
                    data.put(kv[0], kv[1].getBytes(StandardCharsets.UTF_8));
                }
            }

            client.multiPut(data);
            System.out.println("MultiPut concluído com sucesso!");
        } catch (IllegalStateException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println("Erro no MultiPut: " + e.getMessage());
        }
    }

    public void multiGet() {
        try {
            ensureAuthenticated();
            System.out.println("Digite as chaves separadas por vírgulas: ");
            String input = scin.nextLine();

            Set<String> keys = new HashSet<>(Arrays.asList(input.split(",")));
            client.multiGet(keys);
            System.out.println("Operação enviada com sucesso!");
        } catch (IllegalStateException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println("Erro no MultiGet: " + e.getMessage());
        }
    }

    public void getWhen() {
        try {
            ensureAuthenticated();
            System.out.print("Digite a chave principal: ");
            String key = scin.nextLine();
            System.out.print("Digite a chave condicional: ");
            String condKey = scin.nextLine();
            System.out.print("Digite o valor condicional: ");
            String condValue = scin.nextLine();

            client.getWhen(key, condKey, condValue.getBytes(StandardCharsets.UTF_8));
            System.out.println("GetWhen iniciado. Pode continuar a usar outras operações.");

        } catch (IllegalStateException | IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void viewResults(){
        try{
            ensureAuthenticated();
            client.getAllRespostas();
            pressAnyKey();
        } catch (IllegalStateException e) {
            System.out.println(e.getMessage());
        }
    }

    public void run() {
        try {
            clearTerminal();
            System.out.println("Bem-vindo! Por favor, faça login ou registre-se.");
            pressAnyKey();
            menuLogin.run(); // Executa o menu de login
        } catch (IOException e) {
            System.out.println("Erro na execução do menu: " + e.getMessage());
        } finally {
            try {
                client.close();
            } catch (IOException e) {
                System.out.println("Erro ao fechar o cliente: " + e.getMessage());
            }
        }
    }

    public void clearTerminal() {
        try {
            if (System.getProperty("os.name").contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (IOException | InterruptedException e) {
            System.out.println("Erro ao limpar o terminal: " + e.getMessage());
        }
    }

    public void pressAnyKey(){
        System.out.println("Press any key to continue");
        String x = scin.nextLine();
    }

}

