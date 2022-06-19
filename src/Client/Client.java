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
import java.security.Key;
import java.security.SecureRandom;
import java.security.Security;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import org.bouncycastle.jcajce.provider.BouncyCastleFipsProvider;
import AES.Utils;  
import java.util.Base64;

public class Client {

    static InputStreamReader in = null;
    static BufferedReader bf = null;
    static PrintWriter pr = null;
    static Socket iServer = null;

    public static void menu(){
        System.out.println("\tCadastro de clientes");
        System.out.println("0. Sair");
        System.out.println("1. Cadastrar usuário");
        System.out.println("2. Login");
        System.out.println("Opcao:");
    }
    
    public static String generateToken(String username, String password, Socket iServer) throws NoSuchAlgorithmException, IOException {
        /*Gera token de autenticação*/
        String authToken = AuthTokenPBKDF.AuthTokenGenerator(username, password, 1000);

        /* Criação de um JSON com as keys username e authToken para passar para 
            o servidor via socket
        */
        String usernameAuthTokenJson;
        JSONObject json = new JSONObject();
        json.put("username", username);        
        json.put("authToken", authToken);
        usernameAuthTokenJson = json.toString();

        /*Manda token de autenticação para o servidor*/
        if (pr == null) { 
            pr = new PrintWriter(iServer.getOutputStream());
        }
        
        pr.println(usernameAuthTokenJson);        
        pr.flush();

        if (in == null && bf == null) {
            in = new InputStreamReader(iServer.getInputStream());
            bf = new BufferedReader(in);
        }
        
        /* scryptHash vindo do servidor*/
        String scryptHash = bf.readLine();

        return scryptHash;
    }
    
    public static void signUp(Socket i) throws Exception, NoSuchAlgorithmException, IOException{
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        JSONParser jsonParser = new JSONParser();

        try{
            FileReader file = new FileReader("UserData.json");
            jsonArray = (JSONArray)jsonParser.parse(file);
            file.close();   
        }catch(Exception ex){
            JOptionPane.showMessageDialog(null,"Erro ao ler arquivo JSON");
        }
        
        Scanner input1 = new Scanner(System.in);
        System.out.println("Usuário: ");
        String username = input1.next();
        
        Scanner input2 = new Scanner(System.in);
        System.out.println("Senha : ");
        String password = input2.next();

        String passowordToken = generateToken(username, password, i);
                
        /*Salva dados no JSON*/
        jsonObject.put("Username", username);
        jsonObject.put("Token", passowordToken);           
        jsonArray.add(jsonObject);
        
        try{
            FileWriter file = new FileWriter("UserData.json");
            file.write(jsonArray.toJSONString());
            file.close();
        }catch(Exception ex){
            JOptionPane.showMessageDialog(null,"Erro ao ler gravar arquivo JSON");
        }
        JOptionPane.showMessageDialog(null,"Usuário cadastrado com sucesso!");
    }
    
    public static void signIn(Socket iServer) throws Exception, NoSuchAlgorithmException, IOException, InvalidKeyException, InvalidAlgorithmParameterException{
        JSONObject jsonObject = new JSONObject();        
        JSONArray jsonArray = new JSONArray();
        JSONParser jsonParser = new JSONParser();
        
        try{
            FileReader file = new FileReader("UserData.json");
            jsonArray = (JSONArray)jsonParser.parse(file);
            file.close();
        }catch(Exception ex){
            JOptionPane.showMessageDialog(null,"Erro ao ler arquivo JSON");
        }
        
        int size = jsonArray.size();

        Scanner input1 = new Scanner(System.in);
        System.out.println("Usuário: ");
        String username = input1.next();
        
        Scanner input2 = new Scanner(System.in);
        System.out.println("Senha : ");
        String password = input2.next();
        
        String passowordToken = generateToken(username, password, iServer);
        
        jsonObject.put("Username", username);
        jsonObject.put("Token", passowordToken);

        boolean tfaSuccess = false;

        
        for(int i=0;i<size;i++){
            System.out.println(jsonArray.get(i));  
            if(jsonObject.equals(jsonArray.get(i))){
                // Se as credenciais forem válidas então o código 2FA é gerado
                boolean generateTfaResponse = Example2fa.generateTfa();
                tfaSuccess = generateTfaResponse;
                break;
            }else if(i==size-1){
                JOptionPane.showMessageDialog(null,"Usuário/Senha incorreta!");
                tfaSuccess = false;
            }
        }
        if (tfaSuccess) {
            sendMessageToserver(iServer);
        } else {
            System.exit(0);
        }
    }
    
    public static void sendMessageToserver(Socket iServer) throws Exception, IOException{
        Scanner scanner = new Scanner(System.in);
        String input;
        
        do{
            input = scanner.nextLine();
            if (input.equalsIgnoreCase("sair")) {
                iServer.close();
                System.exit(0);
                continue;
            }else {
                PrintWriter printWriter = new PrintWriter(iServer.getOutputStream(),true);
                
                int addProvider = Security.addProvider(new BouncyCastleFipsProvider());

                SecureRandom	random = new SecureRandom();
                IvParameterSpec ivSpec = Utils.createCtrIvForAES(1, random);
                Key             key = Utils.createKeyForAES(128, random);
                Cipher          cipher = Cipher.getInstance("AES/CTR/NoPadding", "BCFIPS");
                
                // Etapa de cifragem
                cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);
                byte[] cipherText = cipher.doFinal(Utils.toByteArray(input));
                byte[] ivBytes = ivSpec.getIV();

                String encodedKey = Base64.getEncoder().encodeToString(key.getEncoded());

                printWriter.println(cipherText);
                System.out.println("Mensagem cifrada: " + cipherText);

                printWriter.println(encodedKey);
                System.out.println("Key: " + encodedKey);

                printWriter.println(ivBytes);
                System.out.println("IV: " + ivBytes);

                continue;
            }
        }while (!(input.equals("sair")));
        
    }

    public static void main(String[] args) throws Exception, NoSuchAlgorithmException, IOException, InvalidKeyException, InvalidAlgorithmParameterException {
        
         /*Abre a comunicação via socket com o servidor*/
        iServer = new Socket("localhost", 4999);
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
            
            default:
                System.out.println("Opção inválida.");
            }
        } while(opcao != 0);
    }
}