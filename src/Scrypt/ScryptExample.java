/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Scrypt;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.Security;
import org.apache.commons.codec.binary.Hex;
import org.bouncycastle.jcajce.provider.BouncyCastleFipsProvider;

/**
 *
 * @author carla
 * 
 */
public class ScryptExample {

    /*Usado para gerar o salt  */
    public byte[] getSalt() throws NoSuchAlgorithmException {
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
        //SecureRandom sr = new SecureRandom();
        byte[] salt = new byte[16];
        sr.nextBytes(salt);
        return salt;
    }

    public static String ScryptGenerator(String authTokenJson, String usernameJson) throws NoSuchAlgorithmException {
        ScryptExample obj = new ScryptExample();

        // Instanciar um novo Security provider
        // Nesse exemplo eh usado o BC padrao
        int addProvider;
        addProvider = Security.addProvider(new BouncyCastleFipsProvider());
        if (Security.getProvider("BCFIPS") == null) {
            System.out.println("Bouncy Castle provider NAO disponivel");
        } else {
            System.out.println("Bouncy Castle provider esta disponivel");
        }

        // Explicação sobre parametros:
        // https://cryptobook.nakov.com/mac-and-key-derivation/scrypt
       byte[] salt = obj.getSalt(); // 128 bits - 16 bytes

        
        // Sal na string
        /**
        byte[] salt = null;
        String valorSalt = "53efb4b1157fccdb9902676329debc52";
        
        try {
            salt = Hex.decodeHex(valorSalt.toCharArray());
        } catch (DecoderException ex) {
            Logger.getLogger(ScryptExample.class.getName()).log(Level.SEVERE, null, ex);
        }
        * */
         
        
        int costParameter = 2048; // exemplo: 2048 (afeta uso de memória e CPU)

        int blocksize = 8; // exemplo: 8

        int parallelizationParam = 1; // exemplo: 1
        
        char [] tokenUsername = (authTokenJson + usernameJson).toCharArray();       
        byte [] iv = (authTokenJson + usernameJson).getBytes();

        byte[] derivedKeyFromScrypt;
        derivedKeyFromScrypt = SCRYPT.useScryptKDF(tokenUsername, iv, 
                costParameter,
                blocksize, parallelizationParam);
        

        System.out.println("Chave derivada usando scrypt: ");
        System.out.println(Hex.encodeHexString(derivedKeyFromScrypt));    
        
        return Hex.encodeHexString(derivedKeyFromScrypt);
    }
}


