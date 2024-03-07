package Cliente;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import Utils.Mensagem;

public class Cliente implements Runnable {
    private Socket cliente;
    InetAddress inet;
    String nome, ip;
    int porta;
    int meuHost;

    String VernKey, HmacKey, AesKey;

    public Cliente( String ip, int porta, int host, String VernKey, String HmacKey, String AesKey) {
        this.ip = ip;
        this.porta = porta;
        this.meuHost = host;
        this.VernKey = VernKey;
        this.AesKey = AesKey;
        this.HmacKey = HmacKey;
    }

    @Override
    public void run() {
        try {
            cliente = new Socket(ip, porta);
            System.out.println( "Cliente conectado na porta" + cliente.getInetAddress().getAddress());
            ImplCliente handlerCliente = new ImplCliente(cliente, VernKey, HmacKey, AesKey);
            Thread t1 = new Thread(handlerCliente);
            t1.start();
        } catch (IOException e) {
            // TODO: handle exception
            System.out.println(e);
        }
        

    }

    public static void main(String[] args) {
        Cliente clientinho = new Cliente("127.0.0.2", 5001, 0, "chavevernam", "chavehmac", "chaveaes");
        clientinho.run();
             
    }

    
}
