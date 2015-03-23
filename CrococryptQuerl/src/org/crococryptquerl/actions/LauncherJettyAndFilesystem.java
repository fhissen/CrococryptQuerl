package org.crococryptquerl.actions;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLDecoder;

import org.crococryptquerl.crypto.Validator;
import org.crococryptquerl.web.server.JettyLaunch;

public class LauncherJettyAndFilesystem implements IDataProvider{
	private static String basedir;
	static{
		try {
			String decodedPath = URLDecoder.decode(LauncherJettyAndFilesystem.class.getProtectionDomain().getCodeSource().getLocation().getPath(), "UTF-8");
			basedir = new File(decodedPath).getParentFile().getAbsolutePath();
		} catch (Exception e) {
			try {
				basedir = new File(".").getCanonicalPath();
			} catch (Exception e2) {
				basedir = null;
			}
		}
		
		if(basedir != null){
			try {
				File filedir = new File(basedir, "file_container");
				
				if(filedir.exists()){
					if(!filedir.isDirectory()) throw new Exception("Container directory could not be created!");
				}
				else{
					if(!filedir.mkdir()) throw new Exception("Container directory could not be created!");
				}
				
				basedir = filedir.getAbsolutePath();
			} catch (Exception e) {
				e.printStackTrace();
				basedir = null;
			}
		}
	}
	
	
	public static void main(String[] args) throws Exception{
		DataExecutor.setDataAdapter(new LauncherJettyAndFilesystem());
		int port = 8888;
		boolean running = JettyLaunch.startup(port);
		
		if(!running){
			System.err.println("\n");
			System.err.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
			System.err.println("Jetty could not be started. Is the port " + port + " already in use?");
			System.err.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		}
		else{
			System.out.println("\n");
			System.out.println("CrococryptQuerl on Jetty is now up and running. You might open a browser now: http://localhost:" + port + "/");
		}
	}

	
	
	@Override
	public DataIn read(String id) {
		File dest = new File(basedir, id);

		if(Validator.notvalidCharOrDir(basedir, dest.getAbsolutePath())) return null;

		try {
			InputStream file = new FileInputStream(dest);
			InputStream header = new FileInputStream(dest.getAbsolutePath() + ".chead");
			
			return new DataIn(file, header);
		} catch (Exception e) {
			System.err.println(e.toString());
			return null;
		}
	}

	@Override
	public DataOut write(String id) {
		OutputStream file, header;
		
		try {
			File outfile = new File(basedir, id);

			header = new FileOutputStream(outfile.getAbsolutePath() + ".chead");
			file = new FileOutputStream(outfile);
		} catch (Exception e) {
			System.err.println(e.toString());
			return null;
		}
		
		return new DataOut(file, header);
	}
}
