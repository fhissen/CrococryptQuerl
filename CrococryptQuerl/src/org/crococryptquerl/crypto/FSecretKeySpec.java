package org.crococryptquerl.crypto;
import java.util.ArrayList;
import java.util.Arrays;

import javax.crypto.spec.SecretKeySpec;


public class FSecretKeySpec extends SecretKeySpec{
	private static final long serialVersionUID = 1L;
	
	@Override
	public boolean equals(Object obj) {
		return this == obj;
	}

	@Override
	public String getAlgorithm() {
		return alg;
	}

	private ArrayList<byte[]> all = new ArrayList<>();
	
	@Override
	public byte[] getEncoded() {
		byte[] next = Arrays.copyOf(k, k.length);
		all.add(next);
		return next;
	}

	@Override
	public String getFormat() {
		return "RAW";
	}
	
	public void wipe(boolean key_inclusive){
		for(byte[] b: all)
			Crypto.kill(b);
		all.clear();
		
		if(key_inclusive) Crypto.kill(k);
	}

	private byte[] k;
	private String alg;

	public FSecretKeySpec(byte[] key, String algorithm) {
		super(new byte[key.length], algorithm);
		
		k = key;
		alg = algorithm;
	}
	
	public FSecretKeySpec(byte[] key) {
		this(key, "AES");
	}
	
	public final static FSecretKeySpec make(byte[] key){
		return new FSecretKeySpec(key, "AES");
	}
}
