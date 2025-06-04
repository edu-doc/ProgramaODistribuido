package Domain.Servidor;

import Domain.Banco.Banco;
import Domain.Banco.No;
import Domain.Model.Entity.Drone;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import java.util.List;


public class ImplServidorBanco implements Runnable{
    public static Banco<Drone> bancoDeDados = new Banco<>();
    public Socket socketServidorAplicacao;
    private ObjectInputStream entrada;
    private ObjectOutputStream saida;
    private boolean conexao = true;
    public static int cont = 0;


    public ImplServidorBanco(Banco<Drone> bancoDeDados, Socket socketServidorAplicacao) {
        this.bancoDeDados = bancoDeDados;
        this.socketServidorAplicacao = socketServidorAplicacao;
    }

    @Override
    public void run() {
        System.out.println("Conexão " + cont + " com o proxy " + socketServidorAplicacao.getInetAddress().getHostAddress());

        try {
            saida = new ObjectOutputStream(socketServidorAplicacao.getOutputStream());
            entrada = new ObjectInputStream(socketServidorAplicacao.getInputStream());

            while (conexao) {
                try {
                    List<Object> lista = (List<Object>) entrada.readObject();
                    String comando = (String) lista.get(0);
                    processarEscolha(comando, lista);
                } catch (ClassNotFoundException e) {
                    System.err.println("Erro na leitura do objeto: " + e.getMessage());
                }
            }

            entrada.close();
            saida.close();
            socketServidorAplicacao.close();

        } catch (IOException e) {

        } finally {
            try {
                socketServidorAplicacao.close();
            } catch (IOException e) {
                System.err.println("Erro ao fechar conexão do proxy: " + e.getMessage());
            }
        }
    }

    private void processarEscolha(String comando, List<Object> lista) throws IOException {
        switch (comando) {
            case "cadastro":
                Drone drone = (Drone) lista.get(1);
                int chave = (Integer) lista.get(2);
                bancoDeDados.inserir(chave,drone);
                saida.writeObject("Cadastro realizado com sucesso!");
                saida.flush();
                break;
            case "remover":
                int idRemover = (Integer) lista.get(1);
                // bancoDeDados.remover(idRemover);
                saida.writeObject("Remoção realizada com sucesso!");
                saida.flush();
                break;
            case "listar":
                List<Drone> listaOS = bancoDeDados.listar(); // Obtém a lista de OS do banco
                saida.writeObject(listaOS); // Envia a lista de OS para o proxy
                saida.flush();
                break;
            case "buscar":
                int idBuscar = (Integer) lista.get(1);
                No resultado = bancoDeDados.buscar(idBuscar);
                if (resultado != null) {
                    saida.writeObject(resultado); // Retorna um objeto do tipo Banco.No
                } else {
                    saida.writeObject(null); // Retorna null se não encontrar
                }
                saida.flush();
                break;
            case "alterar":
                int idAlterar = (Integer) lista.get(1);
                String nome = (String) lista.get(2);
                String descricao = (String) lista.get(3);

                // bancoDeDados.atualizar(idAlterar, nome, descricao);
                saida.writeObject("Alteração realizada com sucesso!");
                saida.flush();
                break;
            default:
                saida.writeObject("Comando inválido!");
                saida.flush();
                break;
        }
    }
}
