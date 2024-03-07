package Utils;
import java.util.HashMap;
import java.util.Map;

public class Banco {
    HashMap<String,Conta> banquinho;

    public Banco() {
        banquinho = new HashMap<>();
        banquinho.put("vasco", new Conta("12345678910", "vasco", "qqqq", "rua de baixo", "987654321"));
        banquinho.put("flamengo", new Conta("12345678910", "flamengo", "qqqq", "rua de baixo", "987654321"));
        banquinho.put("fluminense", new Conta("12345678910", "fluminense", "qqqq", "rua de baixo", "987654321"));
    }

    public Map<String, Conta> getBanquinho() {
        return banquinho;
    }

    public void setBanquinho(HashMap<String, Conta> banquinho) {
        this.banquinho = banquinho;
        
    }

    public Conta getConta(String nome){
       return banquinho.get(nome);
    }
    
}
