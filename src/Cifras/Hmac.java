package Cifras;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class Hmac {

    public static String calcular(String chaveSecreta, String mensagem) throws NoSuchAlgorithmException, InvalidKeyException {
        SecretKeySpec chave = new SecretKeySpec(chaveSecreta.getBytes(), "HmacSHA256");
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(chave);
        byte[] hmacBytes = mac.doFinal(mensagem.getBytes());
        return Base64.getEncoder().encodeToString(hmacBytes);
    }

    public static boolean verificar(String chaveSecreta, String mensagem, String hmacRecebido) throws NoSuchAlgorithmException, InvalidKeyException {
        String hmacCalculado = calcular(chaveSecreta, mensagem);
        return hmacRecebido.equals(hmacCalculado);
    }

    public static void main(String[] args) {
        String chaveSecreta = "chaveSecreta123456";
        String mensagemOriginal = "Mensagem secreta para autenticar";

        try {
            // Calcular o HMAC
            String hmacCalculado = Hmac.calcular(chaveSecreta, mensagemOriginal);
            System.out.println("HMAC Calculado: " + hmacCalculado);

            // Simular o recebimento da mensagem e do HMAC
            // Aqui você teria a mensagem recebida e o HMAC recebido

            // Verificar a autenticidade da mensagem
            String hmacRecebido = hmacCalculado; // Simulando o HMAC recebido
            if (Hmac.verificar(chaveSecreta, mensagemOriginal, hmacRecebido)) {
                System.out.println("Mensagem autenticada com sucesso!");
            } else {
                System.out.println("Mensagem não autenticada. Possível alteração na mensagem.");
            }
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            System.out.println("Erro ao calcular ou verificar o HMAC: " + e.getMessage());
        }
    }
}
