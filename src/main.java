import java.io.FileReader;
import java.io.FileWriter;
import java.util.Scanner;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class main {
    
    public static void menu(){
        System.out.println("\tCadastro de clientes");
        System.out.println("0. Sair");
        System.out.println("1. Cadastro");
        System.out.println("2. Login");
        System.out.println("3. Excluir");
        System.out.println("Opcao:");
    }

    public static void signUp(){
        JSONObject obj = new JSONObject();
        JSONArray jrr = new JSONArray();
        JSONParser jp = new JSONParser();
        try{
            FileReader file = new FileReader("UserData.json");
            jrr=(JSONArray)jp.parse(file);
        }catch(Exception ex){
            JOptionPane.showMessageDialog(null,"Error occured");
        }
        
        Scanner input1 = new Scanner(System.in);
        System.out.println("Usuário: ");
        String username = input1.next();
        
        Scanner input2 = new Scanner(System.in);
        System.out.println("Senha : ");
        String password = input2.next();

        
        obj.put("Username", username);
        obj.put("Password", password);
        jrr.add(obj);
        
        try{
            FileWriter file = new FileWriter("UserData.json");
            file.write(jrr.toJSONString());
            file.close();
        }catch(Exception ex){
            JOptionPane.showMessageDialog(null,"Error occured");
        }
        JOptionPane.showMessageDialog(null,"Usuário cadastrado!");
    }
    
    public static void signIn(){
        JSONArray jrr = new JSONArray();
        Object ob = null;
        JSONParser Jp = new JSONParser();
        //fetch file--
        try{
            FileReader file = new FileReader("UserData.json");
            ob=Jp.parse(file);
            jrr=(JSONArray) ob;
            file.close();
        }catch(Exception ex){
            JOptionPane.showMessageDialog(null,"Error Occured While fetching");
        }
        
        JSONObject obj = new JSONObject();
        int size = jrr.size();
        
        Scanner input1 = new Scanner(System.in);
        System.out.println("Usuário: ");
        String username = input1.next();
        
        Scanner input2 = new Scanner(System.in);
        System.out.println("Senha : ");
        String password = input2.next();
        
        obj.put("Username", username);
        obj.put("Password", password);
        
        for(int i=0;i<size;i++){
            if(obj.equals(jrr.get(i))){
                JOptionPane.showMessageDialog(null,"Usuário logado!");
                break;
            }else if(i==size-1){
                JOptionPane.showMessageDialog(null,"Usuário/Senha incorreta!");
            }
        }
    }
    
    public static void delete(){
        System.out.println("delete!");
    }
    
    public static void main(String[] args) {
        final JDialog dialog = new JDialog();
        dialog.setAlwaysOnTop(true);  
        
        int opcao;
        Scanner entrada = new Scanner(System.in);
        
        do{
            menu();
            opcao = entrada.nextInt();
            
            switch(opcao){
            case 1:
                signUp();
                break;
                
            case 2:
                signIn();
                break;
                
            case 3:
                delete();
                break;
            
            default:
                System.out.println("Opção inválida.");
            }
        } while(opcao != 0);
    }
}