package org.crococryptquerl.crypto;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

import org.crococryptquerl.utils.Utils;

public class DataFile {
	public static final int HEADER_SIZE = CryptHeader.SIZE + Header.SIZE;

	static {
		CryptoCentral.INIT();
	}

	public static final boolean write(InputStream orgis, String pw,
			String orgname, OutputStream destos_header, OutputStream destos_file) {
		try {
			byte[] s = Crypto.randIv();
			CryptoCentral cc = new CryptoCentral();
			cc.loadpw(pw.toCharArray(), s);
			Crypto c = cc.getCrypto();

			Utils utils = new Utils();
			
			Header h = new Header();

			h.optCompressed(orgname);
			h.setName(orgname);

			CryptHeader ch = new CryptHeader();
			ch.setIV(s);

			ch.createOut(destos_header);
			destos_header.flush();

			OutputStream out = c.makeCrocoOS(destos_header, ch.getIV());
			if(out == null) return false;

			h.createOut(out);
			out.flush();
			out.close();

			out = c.makeCrocoOSstream(destos_file, ch.getIV());

			int compress = Deflater.NO_COMPRESSION;
			if (h.isCompressed())
				compress = Deflater.BEST_SPEED;

			DeflaterOutputStream zos = new DeflaterOutputStream(out,
					new Deflater(compress), true);

			InputStream fis = orgis;

			utils.copy(fis, zos);
			fis.close();

			zos.flush();
			destos_file.flush();
			zos.close();
			destos_file.close();
			
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return false;

	}

	public static final PlainStream makePlain(String pw, InputStream encis_header, InputStream encis_file) {
		try {
			PlainStream b = new PlainStream();
			
			byte[] bothheader = Utils.readBytes(encis_header);
			byte[] buf = new byte[CryptHeader.SIZE];
			System.arraycopy(bothheader, 0, buf, 0, buf.length);
			CryptHeader ch = CryptHeader.instance(buf);
			
			CryptoCentral cc = new CryptoCentral();
			cc.loadpw(pw.toCharArray(), ch.getIV());
			Crypto c = cc.getCrypto();

			buf = new byte[Header.SIZE];
			System.arraycopy(bothheader, CryptHeader.SIZE, buf, 0, buf.length);
			buf = c.makeCrocoDec(buf, ch.getIV());
			Header h = Header.instance(buf);
			b.name = h.getNameString();
			b.len = h.getSize();

			if(encis_file != null){
				InputStream in = c.makeCrocoISstream(encis_file, ch.getIV());
				InflaterInputStream dis = new InflaterInputStream(in);
				b.is = dis;
			}

			return b;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static class PlainStream {
		public InputStream is;
		public String name;
		public long len;
	}

}
