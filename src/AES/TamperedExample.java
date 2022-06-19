package AES;

import java.security.Key;
import java.security.SecureRandom;
import java.security.Security;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import org.bouncycastle.jcajce.provider.BouncyCastleFipsProvider; 
import javax.crypto.SecretKey;
import java.util.Base64;
import javax.crypto.spec.SecretKeySpec;
/**
 * TesteModifica
 * Tampered message, plain encryption, AES in CTR mode
 */
public class TamperedExample
{   
    public static void main(
        String[]    args)
        throws Exception
    {
        // Instanciar um novo Security provider
        
        int addProvider = Security.addProvider(new BouncyCastleFipsProvider());
        
        SecureRandom	random = new SecureRandom();
        IvParameterSpec ivSpec = Utils.createCtrIvForAES(1, random);
        Key             key = Utils.createKeyForAES(128, random);
        Cipher          cipher = Cipher.getInstance("AES/CTR/NoPadding", "BCFIPS");
        String          input = "ola teste";

        System.out.println("Texto plano    : " + input);
        
        // etapa de cifragem
        
        cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);
        
        byte[] cipherText = cipher.doFinal(Utils.toByteArray(input));

        System.out.println("Texto plano cifrado  : " + cipherText);

        System.out.println("Key  : " + key);
        System.out.println("IV  : " + ivSpec);
        
        // etapa de decifragem
        
        String encodedKey = Base64.getEncoder().encodeToString(key.getEncoded());
        byte[] iv = ivSpec.getIV();
        

        String mensagem = Utils.toString(cipherText);
        byte[] cipherText2 = Utils.toByteArray(mensagem);
        
        byte[] decodedKey = Base64.getDecoder().decode(encodedKey);
        SecretKey originalKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES"); 

        IvParameterSpec ivSpec2 = new IvParameterSpec(iv);

        System.out.println("Key  : " + originalKey);
        System.out.println("IV  : " + ivSpec2);

        Cipher cipher2 = Cipher.getInstance("AES/CTR/NoPadding", "BCFIPS");
        cipher2.init(Cipher.DECRYPT_MODE, originalKey, ivSpec2);
        
        byte[] plainText = cipher2.doFinal(cipherText);

        
        System.out.println("Texto decifrado: " + Utils.toString(plainText));
    }
}
