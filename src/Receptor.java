
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.security.Key;
import java.security.KeyStore;
import java.security.MessageDigest;
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
        int bytesTotalesArcchivo;
        try{                                               
            FileInputStream input = new FileInputStream(inFile);
            bytesTotalesArcchivo = input.available();//metodo que devuelve el total de bytes que hay
            byte [] encriptado = new byte[bytesTotalesArcchivo];//tamaño de la llave aes encriptada en bytes            
            input.read(encriptado);
            input.close();
            System.out.println("tamaño2"+bytesTotalesArcchivo);
            
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
            byte[] llaveAesDesencriptada = rsa.doFinal(AEs);                        
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
            int faltaPorLeer=bytesTotalesArcchivo-512;
            byte[] TextoEncriptado= new byte[faltaPorLeer];
            int t=0;
            for(int i=512;i<bytesTotalesArcchivo;i++){                
                  TextoEncriptado[t++]=encriptado[i];                               
                  
            }
            Cipher aes = Cipher.getInstance("AES/ECB/PKCS5Padding");
            Key alKey = new SecretKeySpec(llaveAesDesencriptada, 0, llaveAesDesencriptada.length, "AES"); 
            aes.init(Cipher.DECRYPT_MODE,alKey);
            byte[] TextoDesencriptado = aes.doFinal(TextoEncriptado);
            // Texto obtenido, igual al original.
            System.out.println("Texto desencriptado:"+new String(TextoDesencriptado));
            //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            //calculamos el hash del texto desencriptado
            MessageDigest md = MessageDigest.getInstance( "MD5" );
            md.update(TextoDesencriptado);
            byte[] digest = md.digest();
            //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            //comparamos el hash desencriptado con el hash del texto extraido
            if(HashDesencriptado==digest){
                System.out.println("Firma digital es valida");
            }else{
                System.out.println("Firma digital no es valida");
            }
            
    
        }catch(Exception e){
            System.out.println("exception");
        }
        
       

        
        

            
    }
}
