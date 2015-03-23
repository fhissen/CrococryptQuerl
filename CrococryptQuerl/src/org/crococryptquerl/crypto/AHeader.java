package org.crococryptquerl.crypto;

import java.io.OutputStream;


public abstract class AHeader {
	static final int LLEN = 8;
	static final int ILEN = 4;

	public abstract byte[] createOut();
	public abstract void createOut(OutputStream out);

	public abstract void readFrom(byte[] in) throws IllegalArgumentException;
}
