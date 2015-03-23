package org.crococryptquerl.crypto;

import java.security.Security;

import javax.crypto.Mac;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.crococryptquerl.utils.Utils;


public class CryptoCentral {
	public static final int ITERATIONS = 100000;
	public static final int KEYSIZE_BITS = 256;
	public static final int KEYSIZE_BYTES = KEYSIZE_BITS / 8;

	private static boolean INIT = false;
	public static final void INIT(){
		if(INIT) return;
		
		INIT = true;
		BouncyCastleProvider bc = new BouncyCastleProvider();
		Security.addProvider(bc);
	}

	static{
		INIT();
	}

	private byte[] tmp_esk1;
	
	public final boolean loadpw(char[] pw, byte[] s){
		if(pw == null || pw.length == 0) return false;
		try {
			byte[] bbuf1 = Utils.charsToBytes(pw);
			tmp_esk1 = new PBKDF2(Mac.getInstance(Crypto.hmac)).generateDerivedParameters(KEYSIZE_BITS, bbuf1, s, ITERATIONS);
			
			Crypto.kill(bbuf1);
			Crypto.kill(pw);
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		return false;
	}
	

	public final Crypto getCrypto(){
		try {
			return new Crypto(FSecretKeySpec.make((tmp_esk1)));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}

	
	@Override
	protected void finalize() throws Throwable {
		if(tmp_esk1 != null) Crypto.kill(tmp_esk1);
	}
}
