package org.crococryptquerl.actions;

import java.io.InputStream;

import org.crococryptquerl.crypto.Crypto;
import org.crococryptquerl.crypto.DataFile;
import org.crococryptquerl.crypto.Validator;
import org.crococryptquerl.crypto.DataFile.PlainStream;

public class DataExecutor {
	/**
	 * 
	 * @param id
	 * @param pw
	 * @return object to receive the plain data
	 */
	public final static PlainStream read(String id, String pw, boolean headeronly){
		if(adapter == null){
			System.err.println("No DataProvider registered");
			return null;
		}

		if(Validator.invalidChar(id)){
			System.out.println("Transmitted ID contains unwanted chars");
			return null;
		}

		try {
			DataIn data = adapter.read(id);
			if(data == null) return null;
			
			PlainStream back;
			if(headeronly){
				data.getFile().close();
				back = DataFile.makePlain(pw, data.getHeader(), null);
			}
			else{
				back = DataFile.makePlain(pw, data.getHeader(), data.getFile());
			}
			
			return back;
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 
	 * @param in
	 * @param filename
	 * @param pw
	 * @return the identifier of the object
	 */
	public final static String write(InputStream in, String filename, String pw){
		if(adapter == null){
			System.err.println("No DataProvider registered");
			return null;
		}
		
		if(Validator.invalidChar(filename)) return null;

		try {
			String id = Crypto.makeLongId();
			DataOut data = adapter.write(id);
			if(data == null) return null;

			if(!DataFile.write(in, pw, filename, data.getHeader(), data.getFile())){
				return null;
			}
			
			data.writeFlush();
			
			return id;
		} catch (Exception e) {
			return null;
		}
	}
	
	//-----------------
	private static IDataProvider adapter;
	
	public static final void setDataAdapter(IDataProvider datadapter){
		adapter = datadapter;
	}

}
