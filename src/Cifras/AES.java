package Cifras;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

public class AES {
    private  KeyGenerator geradorDeChaves;
    private SecretKey chave;
    private  String mensagem;
    private  String mensagemCifrada;

    public AES() {
        gerarChave();
    }

    public void gerarChave() {
        try {
            geradorDeChaves = KeyGenerator
                    .getInstance("AES");
            chave = geradorDeChaves
                    .generateKey();
            System.out.println("Chave gerada: "
                    + chave.toString());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public String cifrar(String textoAberto) {
        byte[] bytesMensagemCifrada;
        Cipher cifrador;
        // Encripta mensagem
        mensagem = textoAberto;
        try {
            cifrador = Cipher
                    .getInstance("AES/ECB/PKCS5Padding");
            cifrador.init(Cipher.ENCRYPT_MODE, chave);
            bytesMensagemCifrada = cifrador.doFinal(mensagem.getBytes());
            mensagemCifrada = Base64
                    .getEncoder()
                    .encodeToString(bytesMensagemCifrada);
            System.out.println(">> Mensagem cifrada = "
                    + mensagemCifrada);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }
        return mensagemCifrada;
    }

    public String decifrar(String textoCifrado) {
        // Decriptação
        byte[] bytesMensagemCifrada = Base64
                .getDecoder()
                .decode(textoCifrado);
        Cipher decriptador;
        try {
            decriptador = Cipher.getInstance("AES/ECB/PKCS5Padding");
            decriptador.init(Cipher.DECRYPT_MODE, chave);
            byte[] bytesMensagemDecifrada = decriptador.doFinal(bytesMensagemCifrada);
            String mensagemDecifrada = new String(bytesMensagemDecifrada);
            /*
             * System.out.println("<< Mensagem decifrada = "
             * + mensagemDecifrada);
             */
            mensagem = mensagemDecifrada;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        return mensagem;
    }
}