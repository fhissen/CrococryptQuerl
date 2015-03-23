package org.crococryptquerl.crypto;

import java.io.OutputStream;

import org.crococryptquerl.utils.Utils;


public class CryptHeader extends AHeader {
	private static final int IVSIZE = 16;
	
	
	private int attr_suiteno = 0;
	private long attr_keyid = 0;
	private byte[] attr_iv = new byte[IVSIZE];
	
	public static final int SIZE = ILEN + LLEN + IVSIZE;


	private static final int MAGICNUMBER = 19190850;
	private static final int SUIT_2 = MAGICNUMBER + 1; // == AES

	public CryptHeader() {
		attr_suiteno  = SUIT_2;
	}
	
	public boolean isSupported(){
		return attr_suiteno == SUIT_2;
	}
	
	public static CryptHeader instance(byte[] in){
		CryptHeader ch = new CryptHeader();
		ch.readFrom(in);
		return ch;
	}
	
	public final void readFrom(byte[] in) throws IllegalArgumentException{
		if(in == null || in.length != SIZE) throw new IllegalArgumentException("Byte array IN is of wrong size");
		
		byte[] buf = new byte[ILEN];
		
		int ctr = 0;
		System.arraycopy(in, ctr, buf, 0, ILEN);
		attr_suiteno = Utils.bytesToInt(buf);

		buf = new byte[LLEN];
		System.arraycopy(in, ctr+=ILEN, buf, 0, LLEN);
		attr_keyid = Utils.bytesToLong(buf);

		System.arraycopy(in, ctr+=LLEN, attr_iv, 0, IVSIZE);
	}

	@Override
	public final byte[] createOut() {
		byte[] ret = new byte[SIZE];
		
		int ctr = 0;
		
		System.arraycopy(Utils.intToBytes(attr_suiteno), 0, ret, ctr+=IVSIZE, ILEN);
		System.arraycopy(Utils.longToBytes(attr_keyid), 0, ret, ctr, LLEN);
		System.arraycopy(attr_iv, 0, ret, ctr+=LLEN, IVSIZE);
		
		return ret;
	}
	
	public final void createOut(OutputStream out) {
		try {
			out.write(Utils.intToBytes(attr_suiteno));
			out.write(Utils.longToBytes(attr_keyid));
			out.write(attr_iv);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public long getKeyid() {
		return attr_keyid;
	}

	public void setKeyid(long attr_keyid) {
		this.attr_keyid = attr_keyid;
	}

	public byte[] getIV() {
		return attr_iv;
	}
	
	public void setIV(byte[] attr_iv) {
		if(attr_iv.length != IVSIZE) System.err.println("ERR with IV!");
		
		this.attr_iv = attr_iv;
	}
	
}
