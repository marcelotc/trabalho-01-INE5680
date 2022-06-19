package Server;

import Scrypt.ScryptExample;
import java.net.*;
import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Server {
    
    public static void main(String[] args) throws IOException, ParseException, NoSuchAlgorithmException{
        ServerSocket ii = new ServerSocket(4999);
        
        while(true) {
        Socket cliente = ii.accept();
        
        System.out.println("cliente conectado!");
        
        InputStreamReader in = new InputStreamReader(cliente.getInputStream());
        BufferedReader bf = new BufferedReader(in);

        String usernameAuthTokenJsonString = bf.readLine();
        JSONParser parser = new JSONParser();
        JSONObject usernameAuthTokenJson = (JSONObject) parser.parse(usernameAuthTokenJsonString);

        String authTokenJson =  (String) usernameAuthTokenJson.get("authToken");  
        String usernameJson =  (String) usernameAuthTokenJson.get("username");
        
        String scryptHash = ScryptExample.ScryptGenerator(authTokenJson, usernameJson);
                    
        PrintWriter pr = new PrintWriter(cliente.getOutputStream());
        pr.println(scryptHash);
        pr.flush();

        Scanner s = new Scanner(cliente.getInputStream());
        while (s.hasNextLine()) {
            System.out.println(s.nextLine());
        }
    }
  }
}