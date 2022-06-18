package AuthToken;

import org.apache.commons.codec.binary.Hex;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.Security;
import javax.crypto.SecretKey;
import org.bouncycastle.jcajce.provider.BouncyCastleFipsProvider;

/**
 * User: Carla
 * Date: Fevereiro 2021
 */

public class AuthTokenPBKDF {

    /**
     * Gerar chave derivada da senha
     * @param key
     * @param salt
     * @param iterations
     * @return
     */
        public static String generateDerivedKey(
            String password, String salt, Integer iterations) {
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), 
                salt.getBytes(), iterations, 128);
        SecretKeyFactory pbkdf2 = null;
        String derivedPass = null;
        try {
            pbkdf2 = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512", "BCFIPS");
            SecretKey sk = pbkdf2.generateSecret(spec);
            derivedPass = Hex.encodeHexString(sk.getEncoded());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return derivedPass;
    }
    
    /*Usado para gerar o salt  */
    public String getSalt() throws NoSuchAlgorithmException {
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
        //SecureRandom sr = new SecureRandom();
        byte[] salt = new byte[16];
        sr.nextBytes(salt);
        return Hex.encodeHexString(salt);
    }

    public static String AuthTokenGenerator(String username, String password, Integer iterations) throws NoSuchAlgorithmException {
        AuthTokenPBKDF obj = new AuthTokenPBKDF();     
        
        // Instanciar um novo Security provider
        int addProvider;
        addProvider = Security.addProvider(new BouncyCastleFipsProvider());
        
        String salt = (username + password);
        int it = 1000;
                          
        String chaveDerivada = generateDerivedKey(password, salt, it);
               
        return chaveDerivada;
        
    }


}
