
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.security.Key;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;



/**
 *
 * @author Tomas Salgado, Diego Riquelme
 */
public class Receptor {

    public static void main(String[] args) throws FileNotFoundException{
        File inFile       = new File("ArchivoSalida.txt");//archivo cifrado
        File keyStoreFile = new File("almacenDeLLaves.jks");//almacen de llaves
        String password   = ("seguridad123");//storepass
        float tamaño;
        try{                       
            tamaño=inFile.length();
            System.out.println("tamaño del archivo:"+tamaño);
            byte [] encriptado = new byte[528];//tamaño de la llave aes encriptada en bytes
            FileInputStream input = new FileInputStream(inFile);
            input.read(encriptado);
            input.close();
            ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            //Carga el keystore
            KeyStore myKeyStore = KeyStore.getInstance("JKS");
            FileInputStream inStream = new FileInputStream(keyStoreFile);
            myKeyStore.load(inStream, password.toCharArray());
            // Lee las llaves privada y publica del keystore.
            Certificate cert = myKeyStore.getCertificate("millave");//le pasamos nuestro alias de nuestro keystore
            PublicKey publicKey = cert.getPublicKey();
            @SuppressWarnings("unused")
            PrivateKey privatekey = (PrivateKey) myKeyStore.getKey("millave", "123456789".toCharArray()); 
            // leemos los primeros 256 bytes pertenecientes a la llave Aes encriptada
            byte[] AEs= new byte[256];            
            for(int i=0;i<256;i++){                
                  AEs[i]=encriptado[i];                               
            }
            Cipher rsa = Cipher.getInstance("RSA");
            rsa.init(Cipher.DECRYPT_MODE, privatekey);//incializamos el objeto rsa
            byte[] bytesDesencriptados = rsa.doFinal(AEs);                        
            ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            byte[] HashEncriptado= new byte[256];
            int h=0;
            for(int i=256;i<512;i++){                
                  HashEncriptado[h++]=encriptado[i]; 
               
            }
            Cipher cifradorRsaHash = Cipher.getInstance("RSA");
            cifradorRsaHash.init(Cipher.DECRYPT_MODE, publicKey);
            byte[] HashDesencriptado = cifradorRsaHash.doFinal(HashEncriptado);
            ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            byte[] TextoEncriptado= new byte[16];
            int t=0;
            for(int i=512;i<528;i++){                
                  TextoEncriptado[t++]=encriptado[i];                               
                  
            }
            Cipher aes = Cipher.getInstance("AES/ECB/PKCS5Padding");
            Key alKey = new SecretKeySpec(bytesDesencriptados, 0, bytesDesencriptados.length, "AES"); 
            aes.init(Cipher.DECRYPT_MODE,alKey);
            byte[] TextoDesencriptado = aes.doFinal(TextoEncriptado);
            // Texto obtenido, igual al original.
            System.out.println("Texto desencriptado:"+new String(TextoDesencriptado));
            
    
        }catch(Exception e){
            System.out.println("exception");
        }
        
       

        
        

            
    }
}
