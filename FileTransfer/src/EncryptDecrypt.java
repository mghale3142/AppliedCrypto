import java.io.BufferedReader;
//import java.io.Console;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
//import java.math.BigInteger;
//import java.nio.ByteBuffer;
//import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
//import java.security.Security;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
//import javax.crypto.SecretKey;
//import javax.crypto.SecretKeyFactory;
//import javax.crypto.spec.IvParameterSpec;
//import javax.crypto.spec.PBEKeySpec;

//import org.bouncycastle.jce.provider.BouncyCastleProvider;
//import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;

import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;

import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.util.encoders.Hex;
import org.bouncycastle.jce.spec.ECParameterSpec;
import org.bouncycastle.jce.spec.ECPrivateKeySpec;
import org.bouncycastle.jce.spec.ECPublicKeySpec;

public class EncryptDecrypt {
	
    public static void main(String [] args) throws Exception {
        
    	//args[0] is to encrypt or decrypt
    	
    	//args[1] is input filepath
    	
    	//args[2] is output filepath if args[0] is encrypt, it is a string containing the key if args[0] is decrypt
    	
    	//args[3] is string containing iv
    	
    	//args[4] is output filepath
        
    	
    	
    	if(args[0].equals("encrypt")){
    		encryptFile(args[1], args[2]);
    	}
    	
    	if(args[0].equals("decrypt")) {
    		byte [] key = Base64.decode(args[2]);
    		byte [] iv = Base64.decode(args[3]);
    		
    		decryptFile(args[1],key,iv,args[4]);
    	}
    	
    	if (!args[0].equals("encrypt") && !args[0].equals("decrypt")){
    		
    		System.out.println("Invalid Input");
    	}
        
        
        
        
    }	
	
	private static byte[] cipherData(PaddedBufferedBlockCipher cipher, byte[] data)
	        throws Exception
	{
	    int minSize = cipher.getOutputSize(data.length);
	    byte[] outBuf = new byte[minSize];
	    int length1 = cipher.processBytes(data, 0, data.length, outBuf, 0);
	    int length2 = cipher.doFinal(outBuf, length1);
	    int actualLength = length1 + length2;
	    byte[] result = new byte[actualLength];
	    System.arraycopy(outBuf, 0, result, 0, result.length);
	    return result;
	}

	//encryptFile(String filepath)
	
	private static void encryptFile(String filepath, String outputFilepath) {
		
		Cipher newCipher = null;
		try {
			newCipher = Cipher.getInstance("AES/CBC/NOPADDING");
		} catch (NoSuchAlgorithmException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (NoSuchPaddingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        

        
        FileInputStream inputTextFile = null;
		try {
			inputTextFile = new FileInputStream(filepath);
		} catch (FileNotFoundException e) {
			System.out.println("File Not Found");
			//e.printStackTrace();
			return;
		}
                
        InputStreamReader isr = new InputStreamReader(inputTextFile);
        BufferedReader br = new BufferedReader(isr);
        
        String line;
        String plaintext = "";
        
        try {
			while ((line = br.readLine()) != null) {
			    //System.out.println(line);
				plaintext += line;
				plaintext += "\n";
			 }
		} catch (IOException e) {
			System.out.println("Error Reading File");
			return;
			//e.printStackTrace();
		}
        
        try {
			br.close();
		} catch (IOException e) {
			System.out.println("Error Reading File");
			return;
		}
        
        //System.out.println(plaintext);
        
        //String plaintext = "This is a long and unique string value that will be encoded and then decoded using the bouncy castle AES engine";      // response from the Python script

        
        //System.out.println("made it");
        
        byte[] plainAsBytes = plaintext.getBytes();

        
        byte[] IV = null;
		try {
			IV = generateIV(newCipher);
		} catch (Exception e) {
			System.out.println("Error Generating IV");
			return;
		}      

        byte[] Key = null;
		try {
			Key = generateKey(newCipher);
		} catch (Exception e) {
			System.out.println("Error Generating Key");
			return;
		}
        

        byte[] encryptedData = null;
		try {
			encryptedData = encrypt(plainAsBytes, Key, IV);
		} catch (Exception e) {
			System.out.println("Error Encrypting Data");
			return;
		}

        
        //String ciphertext = new String(encryptedData, StandardCharsets.UTF_8);
        
        String ciphertext = Base64.toBase64String(encryptedData);
        
        PrintWriter cipherOut = null;
		try {
			cipherOut = new PrintWriter(outputFilepath);
		} catch (FileNotFoundException e) {
			System.out.println("Error Writing File");
			return;
		}
        cipherOut.println(ciphertext);
        
        cipherOut.close();
        
        System.out.println("Key:");
        System.out.println(Base64.toBase64String(Key));
        System.out.println("IV:");
        System.out.println(Base64.toBase64String(IV));
		
	}
	
	private static void decryptFile(String filepath, byte[] key, byte[] iv, String outputFilepath){
		
		FileInputStream inputCipherFile = null;
		try {
			inputCipherFile = new FileInputStream(filepath);
		} catch (FileNotFoundException e4) {
			System.out.println("File Not Found");
			return;
		}
        
        InputStreamReader isr2 = new InputStreamReader(inputCipherFile);
        BufferedReader br2 = new BufferedReader(isr2);
        
        String line2;
        String readCiphertext = "";
        
        try {
			while ((line2 = br2.readLine()) != null) {
			    //System.out.println(line);
				readCiphertext += line2;
				//readCiphertext += "\n";
			 }
		} catch (IOException e3) {
			System.out.println("Error Reading File");
			return;
		}
        
        try {
			br2.close();
		} catch (IOException e2) {
			System.out.println("Error Reading File");
			return;
		}
        
        
        
        byte[] readEncryptedData = Base64.decode(readCiphertext);

        
        byte[] decryptedData = null;
		try {
			decryptedData = decrypt(readEncryptedData, key, iv);
		} catch (Exception e1) {
			System.out.println("Error Decrypting File");
			return;
		}
        //byte[] decryptedData = decrypt(encryptedData, Key, IV);

        String recoveredPlaintext = new String(decryptedData);
        
        PrintWriter decryptOut = null;
		try {
			decryptOut = new PrintWriter(outputFilepath);
		} catch (FileNotFoundException e) {
			System.out.println("Error Writing File");
			return;
		}
        decryptOut.println(recoveredPlaintext);
        
        decryptOut.close();
        
        System.out.println("Completed");
	}
	
	private static byte[] decrypt(byte[] cipher, byte[] key, byte[] iv) throws Exception
	{
	    PaddedBufferedBlockCipher aes = new PaddedBufferedBlockCipher(new CBCBlockCipher(
	            new AESEngine()));
	    CipherParameters ivAndKey = new ParametersWithIV(new KeyParameter(key), iv);
	    aes.init(false, ivAndKey);
	    return cipherData(aes, cipher);
	}

	private static byte[] encrypt(byte[] plain, byte[] key, byte[] iv) throws Exception
	{
	    PaddedBufferedBlockCipher aes = new PaddedBufferedBlockCipher(new CBCBlockCipher(
	            new AESEngine()));
	    CipherParameters ivAndKey = new ParametersWithIV(new KeyParameter(key), iv);
	    aes.init(true, ivAndKey);
	    return cipherData(aes, plain);
	}
	
	private static byte [] generateIV(Cipher cipher) throws Exception {
        byte [] ivBytes = new byte[cipher.getBlockSize()];
        random.nextBytes(ivBytes);
        return ivBytes;
    }
	
	private static byte [] generateKey(Cipher cipher) throws Exception {
        byte [] keyBytes = new byte[32];
        random.nextBytes(keyBytes);
        return keyBytes;
    }
	
	private static final SecureRandom random = new SecureRandom();
}
