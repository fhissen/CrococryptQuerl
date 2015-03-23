package org.crococryptquerl.crypto;

import java.security.SecureRandom;

import javax.crypto.KeyGenerator;


public class KeyGen {
	static{
		CryptoCentral.INIT();
	}
	
	public SecureRandom makeSR(){
		SecureRandom sr = new SecureRandom();
		//Initialize SR standard seed
		sr.nextBoolean();
		
		return sr;
	}
	
	public KeyGenerator make(int sizeinbits) {
		try {
			KeyGenerator key_gen = KeyGenerator.getInstance(Crypto.aes);
			key_gen.init(sizeinbits, makeSR());
			long max = (System.currentTimeMillis() % 11) + 1;
			
			//Don't know if it is meaningful, but sure it does not hurt!
			for(int i=0; i < max; i++){
				key_gen.generateKey();
			}

			return key_gen;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public byte[] raw(int sizeinbits) {
		return make(sizeinbits).generateKey().getEncoded();
	}
	
}
