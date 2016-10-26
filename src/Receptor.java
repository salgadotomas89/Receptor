
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import javax.crypto.Cipher;



/**
 *
 * @author Tomas Salgado, Diego Riquelme
 */
public class Receptor {

    public static void main(String[] args) throws FileNotFoundException{
        File inFile       = new File("ArchivoSalida.txt");//archivo cifrado
        File keyStoreFile = new File("almacenDeLLaves.jks");//almacen de llaves
        String password   = ("seguridad123");//storepass
        
        try{                       
            
            byte [] encriptado = new byte[256];//tamaño de la llave aes encriptada en bytes
            FileInputStream input = new FileInputStream(inFile);
            input.read(encriptado);
            input.close();
            //Carga el keystore
            KeyStore myKeyStore = KeyStore.getInstance("JKS");
            FileInputStream inStream = new FileInputStream(keyStoreFile);
            myKeyStore.load(inStream, password.toCharArray());
            // Lee las llaves privada y publica del keystore.
            Certificate cert = myKeyStore.getCertificate("millave");//le pasamos nuestro alias de nuestro keystore
            PublicKey publicKey = cert.getPublicKey();
            @SuppressWarnings("unused")
            PrivateKey privatekey = (PrivateKey) myKeyStore.getKey("millave", "123456789".toCharArray()); 
            // Se desencripta
            Cipher rsa = Cipher.getInstance("RSA");
            rsa.init(Cipher.DECRYPT_MODE, privatekey);//incializamos el objeto rsa
            byte[] bytesDesencriptados = rsa.doFinal(encriptado);            
            String textoDesencripado = new String(bytesDesencriptados);
            System.out.println("key desencriptada:"+textoDesencripado);
           

            
    
        }catch(Exception e){
            System.out.println("exception");
        }
        
       

        
        

            
    }
}
