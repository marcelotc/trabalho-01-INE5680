/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 * Carla Merkle Westphall
 * Adaptado de: https://medium.com/@ihorsokolyk/two-factor-authentication-with-java-and-google-authenticator-9d7ea15ffee6
 * RFCs relevantes:
 *
 * http://tools.ietf.org/html/rfc4226 - HOTP: An HMAC-Based One-Time Password Algorithm
 * http://tools.ietf.org/html/rfc6238 - TOTP: Time-Based One-Time Password Algorithm
 *
 * Para ler: https://levelup.gitconnected.com/how-google-authenticator-hmac-based-one-time-password-and-time-based-one-time-password-work-17c6bdef0deb
 *
 */
package Server;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.SecureRandom;
import java.util.Scanner;
import org.apache.commons.codec.binary.Base32;
import org.apache.commons.codec.binary.Hex;
import de.taimos.totp.TOTP;
import java.io.IOException;
import java.net.URLEncoder;
import java.io.UnsupportedEncodingException;
import com.google.zxing.common.ByteMatrix;
import java.io.FileOutputStream;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Carla Merkle Westphall Adaptado de:
 * https://medium.com/@ihorsokolyk/two-factor-authentication-with-java-and-google-authenticator-9d7ea15ffee6
 *
 */
public class Example2fa {

    public static String generateSecretKey() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[20];
        random.nextBytes(bytes);
        Base32 base32 = new Base32();
        return base32.encodeToString(bytes);
    }

    // https://github.com/taimos/totp/blob/master/src/main/java/de/taimos/totp/TOTP.java
    // TOTP Code permanece válido por 30 segundos
    public static String getTOTPCode(String secretKey) {
        Base32 base32 = new Base32();
        byte[] bytes = base32.decode(secretKey);
        String hexKey = Hex.encodeHexString(bytes);
        return TOTP.getOTP(hexKey);
    }

    public static String getGoogleAuthenticatorBarCode(String secretKey, String account, String issuer) {
        try {
            return "otpauth://totp/"
                    + URLEncoder.encode(issuer + ":" + account, "UTF-8").replace("+", "%20")
                    + "?secret=" + URLEncoder.encode(secretKey, "UTF-8").replace("+", "%20")
                    + "&issuer=" + URLEncoder.encode(issuer, "UTF-8").replace("+", "%20");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
    }

    public static void createQRCode(String barCodeData, String filePath, int height, int width)
            throws WriterException, IOException {
        ByteMatrix matrix = new MultiFormatWriter().encode(barCodeData, BarcodeFormat.QR_CODE,
                width, height);
        try (FileOutputStream out = new FileOutputStream(filePath)) {
            MatrixToImageWriter.writeToStream(matrix, "png", out);
        }

    }

    public static boolean generateTfa() throws InvalidKeyException, InvalidAlgorithmParameterException {

        try {

            // Cria chave secreta simétrica
            String secret = generateSecretKey();
            //System.out.println("Chave = " + secret);
            String TOTPcode = getTOTPCode(secret);
            System.out.println("TOTP Code = " + TOTPcode);

            String email = "email@gmail.com";
            String companyName = "Empresa";
            String barCodeUrl = getGoogleAuthenticatorBarCode(secret, email, companyName);
            //System.out.println("Bar Code URL = " + barCodeUrl);

            int width = 246;
            int height = 246;

            // Fica no diretório do projeto.
            createQRCode(barCodeUrl, "matrixURL.png", height, width);

            //System.out.println("Procure o arquivo matrixCode.png no diretorio do projeto e leia o QR code para digitar o código");
            createQRCode(TOTPcode, "matrixCode.png", height, width);
            
            Timer timer = new Timer();

            TimerTask task = new TimerTask(){
                private int i = 15;
                public void run(){
                    if (i >= 0) {
                        System.out.println("Entre o código de autenticação : "+ i--);
                    }
                }
            };
            timer.scheduleAtFixedRate(task, 0, 1000); //1000ms = 1sec

            Scanner scanner = new Scanner(System.in);
            
            String code = scanner.nextLine(); // as vezes não limpa o buffer

            if (code.equals(getTOTPCode(secret))) {
                System.out.println("Logado com sucesso!");                
                System.out.println("Digite uma mensagem para o servidor: ");
                timer.cancel();
                return true;
            } else {
                System.out.println("Código 2FA inválido");
                timer.cancel();
                return false;
            }
        } catch (WriterException ex) {
            Logger.getLogger(Example2fa.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Example2fa.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
}
