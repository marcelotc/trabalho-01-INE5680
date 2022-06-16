package Server;

import java.net.*;
import java.io.*;

public class Server {
    
    public static void main(String[] args) throws IOException{
        ServerSocket ii = new ServerSocket(4999);
        Socket i = ii.accept();
        
        System.out.println("cliente conectado!");
        
        InputStreamReader in = new InputStreamReader(i.getInputStream());
  BufferedReader bf = new BufferedReader(in);
  
  String str = bf.readLine();
  System.out.println("cliente : "+ str);
  
  PrintWriter pr = new PrintWriter(i.getOutputStream());
        pr.println("Servidor ativo!");
        pr.flush();
    }
    
}