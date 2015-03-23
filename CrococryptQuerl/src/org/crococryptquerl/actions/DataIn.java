package org.crococryptquerl.actions;

import java.io.InputStream;

public class DataIn {
	private InputStream file, header;
	
	public DataIn(InputStream file, InputStream header){
		this.file = file;
		this.header = header;
	}
	
	public InputStream getFile(){
		return file;
	}
	
	public InputStream getHeader(){
		return header;
	}

}
