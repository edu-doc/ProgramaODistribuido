package Conexoes.Ultima;

import Domain.Model.Entity.Drone;
import java.io.*;
import java.net.Socket;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

public class ClienteParaServidor {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 22234;
    private static final int TIMEOUT = 30000; // 30 segundos
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public void conexaoCentralServidor() {
        try (
                Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))
        ) {
            socket.setSoTimeout(TIMEOUT);

            System.out.println("Conectando ao Load Balancer na porta " + SERVER_PORT);

            String porta = in.readLine();
            System.out.println("Load Balancer retornou a porta: " + porta);
            int novaPorta = Integer.parseInt(porta);

            try (
                    Socket novoSocket = new Socket(SERVER_ADDRESS, novaPorta);
                    ObjectOutputStream objOut = new ObjectOutputStream(novoSocket.getOutputStream());
                    ObjectInputStream objIn = new ObjectInputStream(novoSocket.getInputStream())
            ) {
                novoSocket.setSoTimeout(TIMEOUT);
                System.out.println("Redirecionado para o servidor na porta " + novaPorta);

                // Identifica como Cliente
                objOut.writeUTF("Cliente");
                objOut.flush();

                Scanner scanner = new Scanner(System.in);
                int opcao = -1; // Inicializa com um valor inválido

                do {
                    mostrarMenu();
                    try {
                        opcao = Integer.parseInt(scanner.nextLine().trim());
                    } catch (NumberFormatException e) {
                        System.out.println("Por favor, digite um número válido.");
                        continue;
                    }

                    // Envia a opção escolhida
                    objOut.writeInt(opcao);
                    objOut.flush();

                    if (opcao != 0) {
                        try {
                            switch (opcao) {
                                case 1:
                                    processarResposta(objIn);
                                    break;
                                case 2:
                                    System.out.print("Digite a posição (Norte, Sul, Leste, Oeste): ");
                                    String posicao = scanner.nextLine().trim();
                                    objOut.writeUTF(posicao);
                                    objOut.flush();
                                    processarResposta(objIn);
                                    break;
                                case 3:
                                    System.out.print("Digite a data (dd/MM/yyyy): ");
                                    String data = scanner.nextLine().trim();
                                    try {
                                        // Valida o formato da data
                                        LocalDate.parse(data, formatter);
                                        objOut.writeUTF(data);
                                        objOut.flush();
                                        processarResposta(objIn);
                                    } catch (DateTimeParseException e) {
                                        System.out.println("Formato de data inválido. Use dd/MM/yyyy");
                                    }
                                    break;
                                case 4:
                                    System.out.print("Digite a posição (Norte, Sul, Leste, Oeste): ");
                                    String posicaoCombinada = scanner.nextLine().trim();
                                    System.out.print("Digite a data (dd/MM/yyyy): ");
                                    String dataCombinada = scanner.nextLine().trim();
                                    try {
                                        // Valida o formato da data
                                        LocalDate.parse(dataCombinada, formatter);
                                        objOut.writeUTF(posicaoCombinada);
                                        objOut.flush();
                                        objOut.writeUTF(dataCombinada);
                                        objOut.flush();
                                        processarResposta(objIn);
                                    } catch (DateTimeParseException e) {
                                        System.out.println("Formato de data inválido. Use dd/MM/yyyy");
                                    }
                                    break;
                                default:
                                    System.out.println("Opção inválida!");
                            }
                        } catch (ClassNotFoundException e) {
                            System.err.println("Erro ao processar resposta: " + e.getMessage());
                            e.printStackTrace();
                        } catch (IOException e) {
                            System.err.println("Erro na comunicação: " + e.getMessage());
                            e.printStackTrace();
                            System.out.println("Tentando reconectar...");
                            return;
                        }
                    }
                } while (opcao != 0);

                scanner.close();

            } catch (IOException e) {
                System.out.println("Erro ao conectar na nova porta: " + e.getMessage());
                e.printStackTrace();
            }

        } catch (IOException e) {
            System.out.println("Erro na conexão inicial: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void processarResposta(ObjectInputStream objIn) throws IOException, ClassNotFoundException {
        Object resposta = objIn.readObject();
        if (resposta instanceof List<?>) {
            List<?> lista = (List<?>) resposta;
            if (lista.isEmpty()) {
                System.out.println("\nNenhum drone encontrado.");
            } else {
                System.out.println("\nResultado da busca:");
                for (Object o : lista) {
                    if (o instanceof Drone) {
                        System.out.println(o);
                    } else {
                        System.out.println("Objeto inválido recebido: " + o);
                    }
                }
            }
        } else {
            System.out.println("\nResposta do servidor: " + resposta);
        }
    }

    private void mostrarMenu() {
        System.out.println("\n=== Menu do Cliente ===");
        System.out.println("1. Listar Todos os Drones");
        System.out.println("2. Buscar Drones por Posição");
        System.out.println("3. Buscar Drones por Data");
        System.out.println("4. Buscar Drones por Posição e Data");
        System.out.println("0. Sair");
        System.out.print("Escolha uma opção: ");
    }

    public static void main(String[] args) {
        new ClienteParaServidor().conexaoCentralServidor();
    }
}

