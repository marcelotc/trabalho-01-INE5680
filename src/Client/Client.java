package Client;

import java.io.FileReader;
import java.io.FileWriter;
import java.util.Scanner;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import AuthToken.AuthTokenPBKDF;
import Server.Example2fa;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.io.*;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.util.Timer;
import java.util.TimerTask;

public class Client {
    
    public static void menu(){
        System.out.println("\tCadastro de clientes");
        System.out.println("0. Sair");
        System.out.println("1. Cadastro");
        System.out.println("2. Login");
        System.out.println("3. Excluir");
        System.out.println("Opcao:");
    }
    
    public static String generateToken(String username, String password, Socket iServer) throws NoSuchAlgorithmException, IOException {
        /*Gera token de autenticação*/
        String authToken = AuthTokenPBKDF.AuthTokenGenerator(username, password, 1000);
        
        System.out.println("AuthToken gerado: " + authToken);
        
        /* Criação de um JSON com as keys username e authToken para passar para 
            o servidor via socket
        */
        String message;
        JSONObject json = new JSONObject();
        json.put("username", username);        
        json.put("authToken", authToken);

        message = json.toString();
        String authTokenJson = (String) json.get("authToken");
        System.out.println("JSON: "+ message);        
        System.out.println("authToken JSONONN: "+ authTokenJson);

        /*Manda token de autenticação para o servidor*/
        PrintWriter pr = new PrintWriter(iServer.getOutputStream());
        pr.println(message);        
        pr.flush();
        
        InputStreamReader in = new InputStreamReader(iServer.getInputStream());
        BufferedReader bf = new BufferedReader(in);
        
        String scryptHash = bf.readLine();
        System.out.println("mensagem do servidor : "+ scryptHash);
        
        return scryptHash;
    }
    
    public static void signUp(Socket i) throws NoSuchAlgorithmException, IOException{
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

       String passowordToken = generateToken(username, password, i);
        
        //String timeStamp = new SimpleDateFormat("HH:mm:ss").format(new Date());
        
        /*Salva dados no JSON*/
        obj.put("Username", username);
        obj.put("Token", passowordToken);           
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
    
    public static void signIn(Socket iServer) throws NoSuchAlgorithmException, IOException, InvalidKeyException, InvalidAlgorithmParameterException{
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
        
        String passowordToken = generateToken(username, password, iServer);
        
        obj.put("Username", username);
        obj.put("Token", passowordToken);
        
        for(int i=0;i<size;i++){
            if(obj.equals(jrr.get(i))){
                // Se as credenciais forem válidas então o código 2FA é gerado
                Example2fa.generateTfa();
                break;
            }else if(i==size-1){
                JOptionPane.showMessageDialog(null,"Usuário/Senha incorreta!");
            }
        }
    }
    
    public static void delete(){
        System.out.println("delete!");
    }
    
    public static void main(String[] args) throws NoSuchAlgorithmException, IOException, InvalidKeyException, InvalidAlgorithmParameterException {
        
        /*Abre a comunicação via socket com o servidor*/
        Socket iServer = new Socket("localhost", 4999);
        /*================================================*/
        
        final JDialog dialog = new JDialog();
        dialog.setAlwaysOnTop(true);  
        
        int opcao;
        Scanner entrada = new Scanner(System.in);
        
        do{
            menu();
            opcao = entrada.nextInt();
            
            switch(opcao){
            case 1:
                signUp(iServer);
                break;
                
            case 2:
                signIn(iServer);
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