import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.Security;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.crypto.BlockCipher;
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
	/**
	 * encryptFile(String filepath)
	 * decryptFile(String filepath, Byte[] key, Byte[] iv)
	 */
	public static void encryptFile(String filepath) {
		
	}
public static void decryptFile(String filepath, Byte[] key, Byte[] iv) {
		
	}
//getbyte method
public static Byte[] getByteArr(String something) {
	return null;
}
	/**
	 * 
	 * @param args
	 * @throws Exception
	 */
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