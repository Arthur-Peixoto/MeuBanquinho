package Cifras;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class RSA {
    private PublicKey publickey;
    private PrivateKey privatekey;

    public RSA(){
        this.init();
        this.printKeys();
    }

    public void init(){
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(1024);
            KeyPair pair = generator.generateKeyPair();
            privatekey = pair.getPrivate();
            publickey = pair.getPublic();
        } catch (Exception ignored) {
            // TODO: handle exception
        }
    }

    public String encrypt(String message) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException{
        byte[] messageToBytes = message.getBytes();
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, publickey);
        byte[] encryptedBytes = cipher.doFinal(messageToBytes);
        return enconde(encryptedBytes);
    }

    public String enconde(byte[] data){
        return Base64.getEncoder().encodeToString(data);
    }

    public String decrypt(String encryptedMessage) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException{
        byte[] encryptedBytes = decode(encryptedMessage);
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, privatekey);
        byte[] decryptedMessage = cipher.doFinal(encryptedBytes);        
        
        return new String(decryptedMessage, "UTF8");
    }

    public byte[] decode(String data){
        return Base64.getDecoder().decode(data);
    }

    public void printKeys(){
        System.out.println("Public Key\n"+enconde(publickey.getEncoded()));
        System.out.println("Private Key\n"+enconde(privatekey.getEncoded()));
    }
}
