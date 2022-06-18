package Server;

import Scrypt.ScryptExample;
import java.net.*;
import java.io.*;
import java.security.NoSuchAlgorithmException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Server {
    
    public static void main(String[] args) throws IOException, ParseException, NoSuchAlgorithmException{
        ServerSocket ii = new ServerSocket(4999);
        Socket i = ii.accept();
        
        System.out.println("cliente conectado!");
        
        InputStreamReader in = new InputStreamReader(i.getInputStream());
        BufferedReader bf = new BufferedReader(in);

        String str = bf.readLine();
        JSONParser parser = new JSONParser();
        JSONObject json = (JSONObject) parser.parse(str);

        String authTokenJson =  (String) json.get("authToken");  
        String usernameJson =  (String) json.get("username");

        System.out.println("cliente JSON : "+ str);  
        System.out.println("cliente username : "+ authTokenJson);  
        System.out.println("cliente authtoken : "+ usernameJson);
        
        /* TODO */
        /* A função SCRYPT entra aqui e passa como parâmetro authTokenJson e  usernameJson
           e salva no JSON e lembrar de modificar o salt do PBKDF e o IV do Scrypt para serem fixos
           
            Com o hash criado pela função do Scrypt guardar no JSON do cliente para recuperar depois 
            quando fizer o login
        */
        String scryptHash = ScryptExample.ScryptGenerator(authTokenJson, usernameJson);
            
        System.out.println("ScryptHash servidor : "+ scryptHash);
        
        PrintWriter pr = new PrintWriter(i.getOutputStream());
        pr.println(scryptHash);
        pr.flush();
    }
    
}