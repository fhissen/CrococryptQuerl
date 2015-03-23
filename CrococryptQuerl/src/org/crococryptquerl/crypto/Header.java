package org.crococryptquerl.crypto;

import java.io.OutputStream;

import org.crococryptquerl.utils.Utils;


public class Header extends AHeader {
	private static final int NAMELEN_NUM = 256;
	private static final int NAMELEN = NAMELEN_NUM * 2;

	private byte[] attr_name = new byte[NAMELEN];
	private long attr_create = 0;
	private long attr_modif = 0;
	private long attr_size = 0;
	private int attr_fileattribs = 0;
	private int attr_settings = 0;

	
	public static final int SIZE = NAMELEN + LLEN + LLEN + LLEN + ILEN + ILEN;
	
	
	private static final int F_COMPRESS = 1<<0;
	private static final int F_DIRECTORY = 1<<1;
	
	public static Header instance(byte[] in){
		Header h = new Header();
		h.readFrom(in);
		return h;
	}
	
	public final byte[] createOut(){
		byte[] ret = new byte[SIZE];
		
		int ctr = 0;
		System.arraycopy(attr_name, 0, ret, ctr, NAMELEN);
		System.arraycopy(Utils.longToBytes(attr_create), 0, ret, ctr+=NAMELEN, LLEN);
		System.arraycopy(Utils.longToBytes(attr_modif), 0, ret, ctr+=LLEN, LLEN);
		System.arraycopy(Utils.longToBytes(attr_size), 0, ret, ctr+=LLEN, LLEN);
		System.arraycopy(Utils.intToBytes(attr_fileattribs), 0, ret, ctr+=LLEN, ILEN);
		System.arraycopy(Utils.intToBytes(attr_settings), 0, ret, ctr+=ILEN, ILEN);
		
		return ret;
	}
	
	public final void createOut(OutputStream out) {
		try {
			out.write(attr_name);
			out.write(Utils.longToBytes(attr_create));
			out.write(Utils.longToBytes(attr_modif));
			out.write(Utils.longToBytes(attr_size));
			out.write(Utils.intToBytes(attr_fileattribs));
			out.write(Utils.intToBytes(attr_settings));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	public final void readFrom(byte[] in) throws IllegalArgumentException{
		if(in == null || in.length != SIZE){
			if(in==null)
				throw new IllegalArgumentException("Byte array IN is of wrong size: null");
			else
				throw new IllegalArgumentException("Byte array IN is of wrong size: " + in.length);
		}

		System.arraycopy(in, 0, attr_name, 0, NAMELEN);
		
		byte[] buf = new byte[LLEN];
		
		int ctr = NAMELEN;
		System.arraycopy(in, ctr, buf, 0, LLEN);
		attr_create = Utils.bytesToLong(buf);
		System.arraycopy(in, ctr+=LLEN, buf, 0, LLEN);
		attr_modif = Utils.bytesToLong(buf);
		System.arraycopy(in, ctr+=LLEN, buf, 0, LLEN);
		attr_size = Utils.bytesToLong(buf);
		
		buf = new byte[ILEN];
		System.arraycopy(in, ctr+=LLEN, buf, 0, ILEN);
		attr_fileattribs = Utils.bytesToInt(buf);
		System.arraycopy(in, ctr+=ILEN, buf, 0, ILEN);
		attr_settings = Utils.bytesToInt(buf);
	}
	
	public final long getCreated(){
		return attr_create;
	}
	
	public final long getModified(){
		return attr_modif;
	}
	
	public final long getSize(){
		return attr_size;
	}
	
	private final char[] getNameChar(){
		return Utils.bytesToChars(attr_name);
	}
	
	public final String getNameString(){
		return new String(getNameChar()).trim();
	}
	
	
	public final void setName(String s){
		if(s.length() >= NAMELEN_NUM) s = s.substring(0, NAMELEN_NUM - 1);
		
		attr_name = Utils.stringToBytes(s + new String(new char[NAMELEN_NUM - s.length()]));
	}
	
	public final void setCreated(long l){
		attr_create = l;
	}
	
	public final void setModified(long l){
		attr_modif = l;
	}
	
	public final void setSize(long l){
		attr_size = l;
	}
	
	private final String[] ext = {
			".jpg",
			".gif",
			".png",
			".jpeg",
			".mpeg",
			".mpg",
			".mp4",
			".mp3",
			".ogg",
			".mp2",
			".m2v",
			".zip",
			".bzip2",
			".bz2",
			".xz",
			".gz",
			".jar",
			".apk",
			".cab",
			".7z",
			
	};
	
	public final void optCompressed(String filename){
		if(!filename.contains(".")) return;
		filename = filename.toLowerCase();
		filename = filename.substring(filename.lastIndexOf("."), filename.length());
		
		for(String tmp: ext)
			if(filename.equals(tmp)) return;
		
		attr_settings = attr_settings | F_COMPRESS;
	}

	public final void setCompressed(){
		attr_settings = attr_settings | F_COMPRESS;
	}
	
	public final void setDir(){
		attr_settings = attr_settings | F_DIRECTORY;
	}
	
	public final boolean isCompressed(){
		return isF(F_COMPRESS);
	}
	
	public final boolean isDir(){
		return isF(F_DIRECTORY);
	}
	
	public final void wipe(){
		Crypto.kill(attr_name);
		attr_create = Long.MAX_VALUE;
		attr_modif = Long.MAX_VALUE;
		attr_settings = Byte.MAX_VALUE;
	}

	private final boolean isF(int flag){
		return flag == (flag & attr_settings);
	}

}
