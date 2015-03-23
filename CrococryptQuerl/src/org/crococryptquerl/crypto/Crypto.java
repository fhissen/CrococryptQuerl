package org.crococryptquerl.crypto;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Random;
import java.util.UUID;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.IvParameterSpec;

import org.apache.commons.codec.binary.Base64;


public class Crypto {
	static{
		CryptoCentral.INIT();
	}
	
	public final static String aes = "AES";
	public final static String ecb = "AES/ECB/NoPadding";
	public final static String cbc = "AES/CBC/PKCS5Padding";
	public final static String cbcnopad = "AES/CBC/NoPadding";
	public final static String hmac = "HmacSHA512";
	
	private FSecretKeySpec k1;
	

	public Crypto(FSecretKeySpec k1) {
		this.k1 = k1;
	}

	public void deinint(){
		k1.wipe(true);
		
		k1 = null;
	}
	
	public void deinitCacheOnly(){
		k1.wipe(false);
	}
	

	public OutputStream makeCrocoOS(OutputStream os, byte[] iv){
		try {
			Cipher ciph2;
			
			ciph2 = Cipher.getInstance(cbcnopad);
			ciph2.init(Cipher.ENCRYPT_MODE, k1, new IvParameterSpec(iv));

			return new CipherOutputStream(os, ciph2);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
	
	public OutputStream makeCrocoOSstream(OutputStream os, byte[] iv){
		try {
			Cipher ciph2;
			
			ciph2 = Cipher.getInstance(cbc);
			ciph2.init(Cipher.ENCRYPT_MODE, k1, new IvParameterSpec(iv));

			return new CipherOutputStream(os, ciph2);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	
	public InputStream makeCrocoISstream(InputStream is, byte[] iv){
		try {
			Cipher ciph2;
			
			ciph2 = Cipher.getInstance(cbc);
			ciph2.init(Cipher.DECRYPT_MODE, k1, new IvParameterSpec(iv));

			return new CipherInputStream(is, ciph2);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
	
	public byte[] makeCrocoDec(byte[] in, byte[] iv){
		try {
			Cipher ciph2;
			
			ciph2 = Cipher.getInstance(cbcnopad);
			ciph2.init(Cipher.DECRYPT_MODE, k1, new IvParameterSpec(iv));

			return ciph2.doFinal((in));
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public byte[] ecbEnc(byte[] in){
		try {
			Cipher ciph2;
			
			ciph2 = Cipher.getInstance(ecb);
			ciph2.init(Cipher.ENCRYPT_MODE, k1);

			return (ciph2.doFinal(in));
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
	
	public byte[] ecbDec(byte[] in){
		try {
			Cipher ciph2;
			
			ciph2 = Cipher.getInstance(ecb);
			ciph2.init(Cipher.DECRYPT_MODE, k1);

			return ciph2.doFinal((in));
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
	

	@Override
	protected void finalize() throws Throwable {
		deinint();
		super.finalize();
	}
	
	
	//////////////////////////
	
	private static final Random randio = new Random(System.currentTimeMillis());
	public static final void kill(byte[] b){
		if(b == null) return;
		randio.nextBytes(b);
	}

	public static final void kill(byte[]... bs){
		for(byte[]b: bs)
			randio.nextBytes(b);
	}

	public static final void kill(char[] b){
		if(b == null) return;
		Arrays.fill(b, (char)randio.nextInt());
	}
	
	public final static byte[] fast16(){
		byte[] ret = new byte[16];
		randio.nextBytes(ret);
		return ret;
	}

	
	
	private static final SecureRandom sr = new KeyGen().makeSR();
	public final static byte[] randIv(){
		byte[] ret = new byte[16];
		sr.nextBytes(ret);
		return ret;
	}

	public final static byte[] randIv(int size){
		byte[] ret = new byte[size];
		sr.nextBytes(ret);
		return ret;
	}
	
	public final static long makeid(){
		return UUID.randomUUID().getMostSignificantBits();
	}

	public static final int LENGTH = 100;
	public final static String makeLongId(){
		return Base64.encodeBase64URLSafeString(randIv(LENGTH));
	}

}
