package Domain.Banco;

import java.io.FileWriter;
import java.io.IOException;

public class EscritorLog {

private String nomeArquivo;

    public EscritorLog(String nomeArquivo) {
        this.nomeArquivo = nomeArquivo;
    }

    public void escreverLog(String log) {
        try (FileWriter writer = new FileWriter(nomeArquivo, true)) {
            writer.write(log);
            writer.write("\n");
            writer.write(System.lineSeparator());
        } catch (IOException e) {
            System.out.println("Ocorreu um erro ao cadastrar o log");
            e.printStackTrace();
        }
    }
}
