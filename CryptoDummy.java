// Classe “CryptoDummy.java”
import java.io.*;
import java.nio.charset.StandardCharsets;  
public   class CryptoDummy
{  private     byte[]   textoCifrado;
   private     byte[]   textoDecifrado;
   private     byte[]   bytes;

   public   CryptoDummy()
   {  
      bytes = null;
      textoCifrado = null;
      textoDecifrado = null;
   }

   public void geraChave(File fDummy) throws IOException  
   {  // Gera uma chave Dummy simetrica (dk: 0 a 100):
      int   dk = (int) (Math.random()*101);
      // Grava a chave Dummy simetrica em formato serializado  
      ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fDummy));  
      oos.writeObject(dk);  
      oos.close();
   }

   public  String  autoCifra(String texto, File fDummy)
   	throws   IOException, ClassNotFoundException
   {  
      bytes = texto.getBytes("ISO-8859-1");

      geraCifra(bytes, fDummy);

      return new String(bytes, StandardCharsets.UTF_8);

   }

   public  void  geraCifra(byte[] texto, File fDummy)
   	throws   IOException, ClassNotFoundException
   {  
      ObjectInputStream ois = new ObjectInputStream (new FileInputStream (fDummy));  
      int iDummy = (Integer) ois.readObject();
      ois.close();
      textoCifrado = texto;
      for(int i = 0; i < texto.length; i++)
      {  textoCifrado[i] = (byte) (textoCifrado[i] + i + iDummy);
      }
   }


   
   public  String  autoDecifra(String texto, File fDummy)
   	throws   IOException, ClassNotFoundException
   {  
      bytes = texto.getBytes("ISO-8859-1");

      geraDecifra(bytes, fDummy);

      return new String(bytes, StandardCharsets.UTF_8);

   }

   public  void  geraDecifra(byte[] texto, File fDummy)
   	throws   IOException, ClassNotFoundException
   {  
      ObjectInputStream ois = new ObjectInputStream (new FileInputStream (fDummy));  
      int iDummy = (Integer) ois.readObject();
      ois.close();
      textoDecifrado = texto;
      for(int i = 0; i < texto.length; i++)
      {  textoDecifrado[i] = (byte) (textoDecifrado[i] - i - iDummy);
      }
   }



      public  byte[]   getTextoCifrado() throws   Exception
   {  return   textoCifrado;
   }

   public  byte[]   getTextoDecifrado()  throws   Exception
   {  return   textoDecifrado;
   }
}

