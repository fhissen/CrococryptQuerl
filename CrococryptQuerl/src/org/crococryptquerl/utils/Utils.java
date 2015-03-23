package org.crococryptquerl.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;



public class Utils {
	private final byte buffer[] = new byte[1024 * 1024];

	public boolean copy(InputStream in, OutputStream out) {
		try {
			int bytes;
			while (true) {
				bytes = in.read(buffer);
				if (bytes <= -1) break;
				out.write(buffer, 0, bytes);
			}
			return true;
		} catch (Exception e) {
			System.err.println("error reading or writing: " + e.getMessage());
			e.printStackTrace();
			return false;
		}
		finally{
			try {
				in.close();
				out.close();
			} catch (Exception e2) {}
		}
	}
	
	//---------------------------------------------
	
	public static String readFile(File file){
		FileInputStream fis = null;
		ByteArrayOutputStream baos = new ByteArrayOutputStream(); 
	
		try {
			fis = new FileInputStream(file);
			
			byte[] thearray = new byte[1024 * 16];
			int b = 0;
			
			while (true){
				try {
					b = fis.read(thearray);
					if (b>=0){
						baos.write(thearray, 0, b);
					}
					else{
						fis.close();
						break;
					}
				} catch (Exception e) {
					break;
				}			
			}
	
		} catch (IOException e) {
			return null;
		} finally{
			try {
				if(fis!=null)fis.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	
		try {
			return new String(baos.toString("UTF-8"));
		} catch (Exception e) {
			return null;
		}
	}

	public static void writeFile(String string, String file){
		try {
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(string.getBytes("UTF-8"));
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static final String read(InputStream is){
		byte[] thearray = new byte[1024 * 16];
		ByteArrayOutputStream baos = new ByteArrayOutputStream(); 
		int b = 0;
		
		while (true){
			try {
				b = is.read(thearray);
				if (b>=0){
					baos.write(thearray, 0, b);
				}
				else{
					is.close();
					break;
				}
			} catch (Exception e) {
				break;
			}			
		}

		try {
			return new String(baos.toString("UTF-8").trim());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static final void writeBytes(OutputStream os, byte[] bytes){
		try {
			os.write(bytes);
			os.flush();
		} catch (Exception e) {}
	}
	
	public static final byte[] readBytes(InputStream is){
		byte[] thearray = new byte[1024 * 16];
		ByteArrayOutputStream baos = new ByteArrayOutputStream(); 
		int b = 0;
		
		while (true){
			try {
				b = is.read(thearray);
				if (b>=0){
					baos.write(thearray, 0, b);
				}
				else{
					is.close();
					break;
				}
			} catch (Exception e) {
				return null;
			}			
		}

		return baos.toByteArray();
	}
	
    public static final void deltree(Path thedir){
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(thedir)) {
            for (Path path : directoryStream) {
            	if(Files.isDirectory(path, LinkOption.NOFOLLOW_LINKS)) deltree(path);
            	else Files.delete(path);
            }
            
            Files.delete(thedir);
        } catch (IOException ex) {
        	ex.printStackTrace();
        }
    }
    
	public static final int noOfFiles(Path thedir){
		int ret = 0;
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(thedir)) {
            for (@SuppressWarnings("unused") Path path : directoryStream) {
            	ret++;
            }
        } catch (IOException ex) {
        	ex.printStackTrace();
        }
        return ret;
	}

	public final static byte[] stringToBytes(String str) {
		byte[] b = new byte[str.length() << 1];
		for (int i = 0; i < str.length(); i++) {
			char strChar = str.charAt(i);
			int bpos = i << 1;
			b[bpos] = (byte) ((strChar & 0xFF00) >> 8);
			b[bpos + 1] = (byte) (strChar & 0x00FF);
		}
		return b;
	}
	
	public final static byte[] charsToBytes(char[] chars) {
		byte[] b = new byte[chars.length << 1];
		for (int i = 0; i < chars.length; i++) {
			char strChar = chars[i];
			int bpos = i << 1;
			b[bpos] = (byte) ((strChar & 0xFF00) >> 8);
			b[bpos + 1] = (byte) (strChar & 0x00FF);
		}
		return b;
	}
	
	public final static char[] bytesToChars(byte[] bytes) {
		char[] buffer = new char[bytes.length >> 1];
		for (int i = 0; i < buffer.length; i++) {
			int bpos = i << 1;
			char c = (char) (((bytes[bpos] & 0x00FF) << 8) + (bytes[bpos + 1] & 0x00FF));
			buffer[i] = c;
		}
		
		return buffer;
	}

	public final static String bytesToString(byte[] bytes) {
		return new String(bytesToChars(bytes));
	}
	
	public static final byte[] longToBytes(long x) {
	    ByteBuffer buffer = ByteBuffer.allocate(Long.SIZE / 8);
	    buffer.putLong(x);
	    return buffer.array();
	}

	public static final long bytesToLong(byte[] bytes) {
	    ByteBuffer buffer = ByteBuffer.allocate(Long.SIZE / 8);
	    buffer.put(bytes);
	    buffer.flip(); 
	    return buffer.getLong();
	}

	public static final byte[] intToBytes(int x) {
	    ByteBuffer buffer = ByteBuffer.allocate(Integer.SIZE / 8);
	    buffer.putInt(x);
	    return buffer.array();
	}

	public static final int bytesToInt(byte[] bytes) {
	    ByteBuffer buffer = ByteBuffer.allocate(Integer.SIZE / 8);
	    buffer.put(bytes);
	    buffer.flip(); 
	    return buffer.getInt();
	}
}
