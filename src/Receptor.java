
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
        float tamaño;
        try{                       
            tamaño=inFile.length();
            System.out.println("tamaño del archivo:"+tamaño);
            byte [] encriptado = new byte[512];//tamaño de la llave aes encriptada en bytes
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
            // leemos los primeros 256 bytes pertenecientes a la llave Aes encriptada
            byte[] AEs= new byte[256];            
            for(int i=0;i<256;i++){                
                  AEs[i]=encriptado[i];                               
            }
            Cipher rsa = Cipher.getInstance("RSA");
            rsa.init(Cipher.DECRYPT_MODE, privatekey);//incializamos el objeto rsa
            byte[] bytesDesencriptados = rsa.doFinal(AEs);            
            String AesDesencriptada = new String(bytesDesencriptados);
            System.out.println("key desencriptada:"+AesDesencriptada);
            //hasta aqui funciona bien
            byte[] Hash= new byte[256];
            int h=0;
            for(int i=256;i<512;i++){                
                  Hash[h++]=encriptado[i];                               
            }
            Cipher cifradorRsaHash = Cipher.getInstance("RSA");
            cifradorRsaHash.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] HashDesencriptado = cifradorRsaHash.doFinal(Hash);
            System.out.println("holitas");

            
    
        }catch(Exception e){
            System.out.println("exception");
        }
        
       

        
        

            
    }
}
