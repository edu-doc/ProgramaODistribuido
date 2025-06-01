package Domain.Servidor;

public class Servidor2 {
    public static void main(String[] args) {
        try {
            ImplServidor implServidor = new ImplServidor("S2", "224.0.0.10", 55560);
            Thread threadServidor = new Thread(implServidor);
            threadServidor.start();

            System.out.println("Servidor iniciado na thread.");
        } catch (Exception e) {
            System.err.println("Erro ao iniciar servidor: " + e.getMessage());
        }
    }
}
