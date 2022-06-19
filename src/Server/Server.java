package Server;

import Scrypt.ScryptExample;
import java.net.*;
import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.security.Key;
import java.security.SecureRandom;
import java.security.Security;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import org.bouncycastle.jcajce.provider.BouncyCastleFipsProvider;
import AES.Utils; 
import javax.crypto.SecretKey;
import java.util.Base64;
import javax.crypto.spec.SecretKeySpec;

public class Server {

    static InputStreamReader in = null;
    static BufferedReader bf = null;
    static Socket cliente = null;
    static PrintWriter pr = null;
    
    public static void main(String[] args) throws Exception, IOException, ParseException, NoSuchAlgorithmException{

        ServerSocket server = null;

        try {
          server = new ServerSocket(4999);
          System.out.println("Servidor iniciado com sucesso!");
        } catch (Exception e) {
          System.out.println("Erro ao iniciar servidor");
        }
       
        while(true) {
        cliente = server.accept();
        
        System.out.println("Cliente conectado!");
        
        if (in == null && bf == null) {
            in = new InputStreamReader(cliente.getInputStream());
            bf = new BufferedReader(in);
        }
        
        String usernameAuthTokenJsonString = bf.readLine();
        JSONParser parser = new JSONParser();
        JSONObject usernameAuthTokenJson = (JSONObject) parser.parse(usernameAuthTokenJsonString);

        String authTokenJson =  (String) usernameAuthTokenJson.get("authToken");  
        String usernameJson =  (String) usernameAuthTokenJson.get("username");
        
        String scryptHash = ScryptExample.ScryptGenerator(authTokenJson, usernameJson);
        
        if (pr == null) { 
            pr = new PrintWriter(cliente.getOutputStream());
        }
        
        pr.println(scryptHash);
        pr.flush();
             
        Scanner s = new Scanner(cliente.getInputStream());
        while (s.hasNext()) {
        
            int addProvider = Security.addProvider(new BouncyCastleFipsProvider());

            // Mensagem
            String mensagem = s.nextLine();
            System.out.println("Mensagem recebida: " + mensagem);
            byte[] cipherText = Utils.toByteArray(mensagem);

            // Key
            String encodedKey = s.nextLine();
            System.out.println("Key: " + encodedKey);
            byte[] decodedKey = Base64.getDecoder().decode(encodedKey);
            SecretKey originalKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES"); 

            // ivSpec
            String iv = s.nextLine();
            System.out.println("IV: " + iv);
            byte[] ivBytes = Utils.toByteArray(iv);
            IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);
        
            // Etapa de decifragem
            Cipher cipher = Cipher.getInstance("AES/CTR/NoPadding", "BCFIPS");
            cipher.init(Cipher.DECRYPT_MODE, originalKey, ivSpec);
            byte[] mensagemDecifrada = cipher.doFinal(cipherText);
        
            System.out.println("Mensagem decifrada: " + Utils.toString(mensagemDecifrada));
        }
    }
  }
}