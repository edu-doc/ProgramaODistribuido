package Conexoes.Primeiras;

import API.DTO.DroneDTO;
import Domain.Service.CentralService;
import Auxiliar.Cripto.ImplElGamal;
import Auxiliar.Cripto.ImplElGamal.CifraElGamal;
import Auxiliar.Cripto.Crypto;

import java.io.IOException;
import java.math.BigInteger;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class ReceptorMultiCast {

    // Chave ElGamal de Longo Prazo do Receptor (Borda)
    private static ImplElGamal elGamalReceiver;

    public static void main(String[] args) throws IOException {

        // 1. INICIALIZAÇÃO ELGAMAL (Gera o par de chaves Privada/Pública)
        elGamalReceiver = new ImplElGamal();
        System.out.println("--- Borda (Receptor) Inicializado ---");
        System.out.println("Chave Pública ElGamal: P=" + elGamalReceiver.getP().toString().substring(0, 10) + "..., G=" + elGamalReceiver.getG() + ", Y=" + elGamalReceiver.getY().toString().substring(0, 10) + "...");
        System.out.println("Chave Privada ElGamal (X) pronta para decifragem.");

        int porta = 55554;
        String mensagem = "";

        MulticastSocket ms = new MulticastSocket(porta);
        InetAddress multicastIP = InetAddress.getByName("224.0.0.1");
        InetSocketAddress grupo = new InetSocketAddress(multicastIP, porta);
        NetworkInterface interfaceRede = NetworkInterface.getByName("wireless_32768");

        ms.joinGroup(grupo, interfaceRede);

        System.out.println("Receptor contínuo ouvindo em " + multicastIP.getHostAddress() + ":" + porta);

        byte[] buffer = new byte[4096];
        DatagramPacket pacote = new DatagramPacket(buffer, buffer.length);

        while (true) {
            try {
                ms.receive(pacote);
                mensagem = new String(pacote.getData(), 0, pacote.getLength());
                formatarMensagem(mensagem);
            } catch (IOException e) {
                System.err.println("Erro ao receber pacote: " + e.getMessage());
            }

        }
    }

    static CentralService centralService = new CentralService();
    static List<Double> numeros;

    public static void formatarMensagem(String mensagem) {
        // Formato esperado: c1|c2|HMAC|CIPHERTEXT|SEPARADOR|POSICAO

        // Os 4 primeiros campos são grandes (BigInteger ou Base64), os 2 últimos são pequenos (String)
        String[] partesPrincipais = mensagem.split("\\|", 6);

        if (partesPrincipais.length != 6) {
            System.err.println("Erro: Formato de mensagem insegura inválido. Recebido " + partesPrincipais.length + " partes. Mensagem: " + mensagem);
            return;
        }

        try {
            // --- 1. DECIFRAR CHAVE DE SESSÃO COM ELGAMAL ---
            BigInteger c1 = new BigInteger(partesPrincipais[0]);
            BigInteger c2 = new BigInteger(partesPrincipais[1]);

            CifraElGamal cifraElGamal = new CifraElGamal(c1, c2);
            BigInteger chaveSimetricaBigInt = elGamalReceiver.decifrar(cifraElGamal);

            // --- 2. RECUPERAR CHAVES AES E HMAC ---
            byte[] chaveSimetricaCompleta = chaveSimetricaBigInt.toByteArray();

            if (chaveSimetricaCompleta[0] == 0) {
                byte[] temp = new byte[chaveSimetricaCompleta.length - 1];
                System.arraycopy(chaveSimetricaCompleta, 1, temp, 0, temp.length);
                chaveSimetricaCompleta = temp;
            }

            int aesLength = 16;
            int hmacLength = 32;

            if (chaveSimetricaCompleta.length != aesLength + hmacLength) {
                throw new SecurityException("Chave simétrica decifrada com tamanho inválido. Tam: " + chaveSimetricaCompleta.length);
            }

            byte[] aesKeyBytes = new byte[aesLength];
            System.arraycopy(chaveSimetricaCompleta, 0, aesKeyBytes, 0, aesLength);

            byte[] hmacKeyBytes = new byte[hmacLength];
            System.arraycopy(chaveSimetricaCompleta, aesLength, hmacKeyBytes, 0, hmacLength);

            SecretKey aesKey = new SecretKeySpec(aesKeyBytes, "AES");

            // --- 3. DECIFRAR E VERIFICAR DADOS COM AES/HMAC ---
            String hmacBase64 = partesPrincipais[2];
            String ciphertextBase64 = partesPrincipais[3];

            String mensagemCifradaHmac = hmacBase64 + ":" + ciphertextBase64;

            Crypto crypto = new Crypto(aesKey, hmacKeyBytes);
            String dadosAbertos = crypto.verificarEDecifrar(mensagemCifradaHmac);

            // --- 4. PROCESSAR DADOS DE DRONE ---
            String separador = partesPrincipais[4];
            String posicao = partesPrincipais[5];

            processarDadosDecifrados(dadosAbertos, separador, posicao);

        } catch (SecurityException e) {
            System.err.println("--- ALERTA DE SEGURANÇA ---");
            System.err.println("Rejeitado: Falha na verificação de HMAC (Integridade/Autenticidade). Pacote adulterado/inválido: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Erro de Criptografia/Decifragem: " + e.getMessage() + ". Ignorando pacote. ");
        }
    }

    private static void processarDadosDecifrados(String mensagem, String separador, String posicao) {
        String[] partes = mensagem.split(java.util.regex.Pattern.quote(separador));

        if (partes.length != 12) {
            System.err.println("Erro: Número incorreto de campos de dados após decifragem. Esperado 12, recebido " + partes.length + ".");
            return;
        }

        numeros = new ArrayList<>();

        // Recuperação dos 12 números
        for (String parte : partes) {
            try {
                numeros.add(Double.parseDouble(parte.trim()));
            } catch (NumberFormatException e) {
                System.err.println("Valor numérico inválido após decifragem: '" + parte + "'. ");
                return;
            }
        }

        if (numeros.get(0) > 800) { // Limite CO2 (ex: acima de 800 ppm)
            System.err.println("🚨 ALERTA BORDA (CO2): Nível elevado em " + posicao + ": " + String.format("%.2f ppm", numeros.get(0)));
        }

        if (numeros.get(4) > 35 || numeros.get(5) > 65) { // Limite PM2.5 ou PM10 (partículas finas)
            System.err.println("🚨 ALERTA BORDA (PARTÍCULAS): Qualidade do ar ruim em " + posicao + " (PM2.5/PM10: " + String.format("%.1f/%.1f", numeros.get(4), numeros.get(5)) + " µg/m³)");
        }

        // Ordem dos dados (índices 0 a 11):
        // co2, co, no2, so2, pm2_5, pm10, temperatura, umidade, ruido, radiacao, latitude, longitude

        // Cria o DTO com os 12 campos + Posição
        DroneDTO droneDTO = new DroneDTO(
                numeros.get(0), numeros.get(1), numeros.get(2), numeros.get(3),
                numeros.get(4), numeros.get(5),
                numeros.get(6), numeros.get(7),
                numeros.get(8), numeros.get(9),
                numeros.get(10), numeros.get(11),
                posicao
        );

        centralService.createDrone(droneDTO);
        System.out.println("Mensagem do drone do " + posicao + " recebida, decifrada, verificada e processada.");
        numeros.clear();
    }
}