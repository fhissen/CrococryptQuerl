package org.crococryptquerl.actions;

import java.io.OutputStream;

public class DataOut {
	private OutputStream file, header;
	
	public DataOut(OutputStream file, OutputStream header){
		this.file = file;
		this.header = header;
	}
	
	public OutputStream getFile(){
		return file;
	}
	
	public OutputStream getHeader(){
		return header;
	}
	
	public void writeFlush(){
		
	}

}
