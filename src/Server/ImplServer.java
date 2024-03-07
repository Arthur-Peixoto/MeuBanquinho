package Server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
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
    String aesKey = "chaveaes";

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
                            }
                            int saldoDaConta = conta.getSaldo();
                            conta.setSaldo(saldoDaConta + valor);

                            retorno = conta.getNome() + " recebeu " + valor + " de " + continha.getNome();
                        }
                        String retornoCripto = Vernamm.cifrar("vernam", retorno);
                        saida.writeUTF(retornoCripto);
                        saida.flush();
                        break;
                    case 2:
                        String retornoSaldo = "Saldo: " + continha.getSaldo();
                        System.out.println(retornoSaldo);
                        String retornoCriptoSaldo = Vernamm.cifrar("vernam", retornoSaldo);
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
                        String retornoCriptoInvestimentos = Vernamm.cifrar("vernam", retornoInvestimentos);
                        retornoCriptoInvestimentos = aes.cifrar(retornoCriptoInvestimentos);
                        Mensagem<String> retorninho = new Mensagem<>(hmac, retornoCriptoInvestimentos);
                        saida.writeObject(retorno);
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
                        String retornoCriptoSaque = Vernamm.cifrar("vernam", retornoSaque);
                        saida.writeUTF(retornoCriptoSaque);
                        saida.flush();
                        break;
                    case 5:
                        System.out.println("deposito");
                        System.out.println("Quanto deseja investir?");
                        int deposito = scanner.nextInt();
                        continha.setSaldo(continha.getSaldo() + deposito);
                        String retornoDeposito = "Depósito realizado com sucesso!";
                        String retornoCriptoDeposito = Vernamm.cifrar("vernam", retornoDeposito);
                        saida.writeUTF(retornoCriptoDeposito);
                        saida.flush();
                        break;
                    case 6:
                        String retornoCriacaoConta = "Conta criada com sucesso!";
                        String retornoCriptoCriacaoConta = Vernamm.cifrar("vernam", retornoCriacaoConta);
                        saida.writeUTF(retornoCriptoCriacaoConta);
                        saida.flush();
                        break;

                    default:
                        String retornoInvalido = "Operação inválida!";
                        String retornoCriptoInvalido = Vernamm.cifrar("vernam", retornoInvalido);
                        saida.writeUTF(retornoCriptoInvalido);
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

}
