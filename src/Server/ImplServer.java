package Server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.Map.Entry;

import Cifras.AES;
import Cifras.Vernamm;
import Utils.Banco;
import Utils.Conta;
import Utils.Mensagem;

public class ImplServer implements Runnable {

    String hmacKey =  "chavehmac";
    String vernKey = "chavevernam";
    String aesKey = "gR6@L2#Np8!TzQ7x";

    ServerSocket servidor;
    AES aes;
    int porta;
    Socket cliente;
    ObjectInputStream entrada;
    ObjectOutputStream saida;
    boolean condicao = true;
    Banco banquinho = new Banco();

    public ImplServer(Socket cliente) {
        this.cliente = cliente;
        this.run();
    }

    @Override
    public void run() {

        Scanner scanner = new Scanner(System.in);

        String retorno = "";
        try {
            System.out.println("Nova conexão com " +
                    cliente.getInetAddress().getHostAddress());
            saida = new ObjectOutputStream(cliente.getOutputStream());
            entrada = new ObjectInputStream(cliente.getInputStream());

            while (condicao) {
                Mensagem mensagem = (Mensagem) entrada.readObject();
                String hmac = mensagem.getHmac();
                if(!hmac.equals(hmacKey)){
                    System.out.println("te peguei canalha!");
                }
                else{
                String cripto = mensagem.getCriptografada();
                cripto = decodifa(cripto);
                Conta continha = (Conta) mensagem.getMensagem();
                switch (mensagem.getOperacao()) {
                    case 1:
                        System.out.println("tranferência");
                        System.out.println("pra quem você quer transferir?");
                        String nome = scanner.nextLine();
                        System.out.println("qual valor você quer transferir?");
                        int valor = scanner.nextInt();
                        for (Entry<String, Conta> entry : banquinho.getBanquinho().entrySet()) {
                            String chave = entry.getKey();
                            Conta conta = entry.getValue();
                            if (chave.equals(nome)) {
                                continha.setSaldo(valor - conta.getSaldo());
                                conta.setSaldo(valor + conta.getSaldo());
                                break;
                            }
                            int saldoDaConta = conta.getSaldo();
                            conta.setSaldo(saldoDaConta + valor);

                            retorno = conta.getNome() + "-" + continha.getNome();
                        }
                        String retornoCripto = Vernamm.cifrar(retorno,vernKey);
                        saida.writeUTF(retornoCripto);
                        saida.flush();
                        break;
                    case 2:
                        String retornoSaldo = "Saldo: " + continha.getSaldo();
                        System.out.println(retornoSaldo);
                        String retornoCriptoSaldo = Vernamm.cifrar(retornoSaldo, vernKey);
                        saida.writeUTF(retornoCriptoSaldo);
                        saida.flush();
                        break;
                    case 3:
                        System.out.println("investimentos");
                        int saldo = continha.getSaldo();
                        System.out.println("na poupança ele renderá:\nEm 3 meses: " + ((saldo * 0.5) * 3 + saldo)
                                + "Reais\nEm 6 meses: " + ((saldo * 0.5) * 6 + saldo)
                                + "Reais\nEm 12 meses: " + ((saldo * 0.5) * 12 + saldo));
                        System.out.println("Deseja investir na renda fixa? (SIM(S) / NAO(N))");
                        String resp = scanner.nextLine();
                        if (resp.equals("S")) {
                            System.out.println("Quanto deseja investir?");
                            int invest = scanner.nextInt();
                            System.out.println("na poupança ele renderá:\nEm 3 meses: " + ((invest * 1.5) * 3 + invest)
                                    + "Reais\nEm 6 meses: " + ((invest * 1.5) * 6 + invest)
                                    + "Reais\nEm 12 meses: " + ((invest * 1.5) * 12 + invest));
                            System.out.println("Deseja realmente investir essa quantia? (SIM(S) / NAO(N))");
                            String res = scanner.nextLine();
                            if (res.equals("S")) {
                                if (saldo < invest) {
                                    System.out.println("Você não está tão rico assim");
                                } else {
                                    continha.setSaldo(saldo - invest);
                                }

                            }
                        }
                        String retornoInvestimentos = "Investimentos realizados!";
                        codifica(retornoInvestimentos);
                        saida.writeObject(retornoInvestimentos);
                        saida.flush();
                        break;
                    case 4:
                        System.out.println("saque");
                        System.out.println("Quanto deseja investir?");
                        int sald = continha.getSaldo();
                        int saque = scanner.nextInt();
                        if (sald < saque) {
                            System.out.println("Você não está tão rico assim");
                        } else {
                            continha.setSaldo(sald - saque);

                        }
                        String retornoSaque = "Saque realizado com sucesso!";
                        retornoSaque = codifica(retornoSaque);
                        saida.writeUTF(retornoSaque);
                        saida.flush();
                        break;
                    case 5:
                        System.out.println("deposito");
                        System.out.println("Quanto deseja investir?");
                        int deposito = scanner.nextInt();
                        continha.setSaldo(continha.getSaldo() + deposito);
                        String retornoDeposito = "Depósito realizado com sucesso!";
                        retornoDeposito = codifica(retornoDeposito);
                        saida.writeUTF(retornoDeposito);
                        saida.flush();
                        break;
                    case 6:
                        banquinho.setConta(continha);
                        String retornoCriacaoConta = "Conta criada com sucesso!";
                        retornoCriacaoConta = codifica(retornoCriacaoConta);
                        saida.writeUTF(retornoCriacaoConta);
                        saida.flush();
                        break;

                    default:
                        String retornoInvalido = "Operação inválida!";
                        retornoInvalido = codifica(retornoInvalido);
                        saida.writeUTF(retornoInvalido);
                        saida.flush();
                        break;
                }
            }
        }
        } catch (IOException e) {
            System.out.println(e);
        } catch (ClassNotFoundException e) {
            System.out.println(e);
            e.printStackTrace();
        }
        scanner.close();
    }

    public String decodifa(String mensagem){
        try {
        mensagem = aes.descriptografar(aesKey,mensagem);
        mensagem = Vernamm.decifrar(mensagem, vernKey);
        } catch (Exception e) {
            // TODO: handle exception
        }
        
        return mensagem;
    }

    public String codifica(String recebeMensagem) {
        try {
        recebeMensagem = Vernamm.cifrar(recebeMensagem, vernKey);
        recebeMensagem = aes.descriptografar(aesKey,recebeMensagem);
        } catch (Exception e) {
            // TODO: handle exception
        }
        return recebeMensagem;
    }


}
