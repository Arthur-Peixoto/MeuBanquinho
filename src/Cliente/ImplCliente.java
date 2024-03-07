package Cliente;

import Utils.Banco;
import Utils.Conta;
import Utils.Mensagem;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Map.Entry;

import Cifras.AES;
import Cifras.Hmac;
import Cifras.Vernamm;

import java.util.Scanner;

public class ImplCliente implements Runnable {
    Socket cliente;
    boolean conexao = true;
    
    int contador = 0;
    int opcao = 2;
    String VernKey, HmacKey, AesKey;
    ObjectInputStream entrada;
    ObjectOutputStream saida;
    AES aes = new AES();
    

    public ImplCliente(Socket cliente, String vernKey, String hmacKey, String aesKey) {
        this.cliente = cliente;
        VernKey = vernKey;
        HmacKey = hmacKey;
        AesKey = aesKey;
    }

    @Override
    public void run() {
       
        Conta conta = null;
        Banco banquinho = new Banco();

        contador++;

        while (true) {
            try {
                Scanner scanner = new Scanner(System.in);
                saida = new ObjectOutputStream(cliente.getOutputStream());
                entrada = new ObjectInputStream(cliente.getInputStream());

                while (opcao != 1) {
                    System.out
                            .println("Cliente \n :Escolha uma opção de mensagem\n1-Logar\n2-Cadastrar");
                    opcao = scanner.nextInt();
                    scanner.nextLine();
                    switch (opcao) {
                        case 1:
                            String nome1 = " ", senha1 = " ";
                            boolean isConected = false;
                            while (!isConected) {
                                System.out.println("Digite seu Nome: ");
                                nome1 = scanner.nextLine();

                                System.out.println("Digite sua senha: ");
                                senha1 = scanner.nextLine();

                                for (Entry<String, Conta> entry : banquinho.getBanquinho().entrySet()) {
                                    String chave = entry.getKey();
                                    Conta continha = entry.getValue();
                                    conta = continha;
                                    if (chave.equals(nome1)) {
                                        isConected = true;
                                    }
                                }

                            }
                            System.out.println("1-Tranferência: ");
                            System.out.println("2-Pegar saldo: ");
                            System.out.println("3-Depositar: ");
                            System.out.println("4-Saque: ");
                            System.out.println("5-Investimento: ");
                            int opcao = scanner.nextInt();

                            String concatenate1 = nome1 + "-" + senha1;
                            concatenate1 = codifica(concatenate1);
                            Mensagem mensagemzinh = new Mensagem<Conta>(conta, HmacKey, concatenate1, opcao);

                            saida.writeObject(mensagemzinh);
                            saida.flush();

                            ouvirThread();

                            break;
                        case 2:
                            System.out.println("Digite seu Nome: ");
                            String nome = scanner.nextLine();

                            System.out.println("Digite seu CPF: ");
                            String cpf = scanner.nextLine();

                            System.out.println("Digite sua senha: ");
                            String senha = scanner.nextLine();

                            System.out.println("Digite sua Endereço: ");
                            String endereco = scanner.nextLine();

                            System.out.println("Digite seu telefone: ");
                            String telefone = scanner.nextLine();

                            conta = new Conta(cpf, nome, senha, endereco, telefone);
                            String concatenate2 = nome + "-" + cpf + "-" + senha + "-" + endereco + "-" + telefone;
                            concatenate2 = codifica(concatenate2);
                            Mensagem mensagemzinha = new Mensagem<Conta>(conta, HmacKey, concatenate2, 6);

                            saida.writeObject(mensagemzinha);
                            saida.flush();

                            break;

                        default:
                            break;
                    }
                }
            } catch (Exception e) {
                // TODO: handle exception
            }
        }
        
    }

    public String decodifa(String mensagem){
        try {
            mensagem = AES.descriptografar(AesKey,mensagem);
            mensagem = Vernamm.decifrar(mensagem, VernKey);
            mensagem = Hmac.hMac(HmacKey, mensagem);
        } catch (Exception e) {
            e.printStackTrace();
        }
       

        return mensagem;
    }

    public String codifica(String recebeMensagem) {    
        try {
            recebeMensagem = Hmac.hMac(HmacKey, recebeMensagem);
            recebeMensagem = Vernamm.cifrar(recebeMensagem, VernKey);
            recebeMensagem = aes.criptografar(AesKey,recebeMensagem);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return recebeMensagem;
    }

    public void ouvirThread(){
        try {
            saida = new ObjectOutputStream(cliente.getOutputStream());
            entrada = new ObjectInputStream(cliente.getInputStream());
            String message = entrada.readUTF();
            System.out.println(message);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
