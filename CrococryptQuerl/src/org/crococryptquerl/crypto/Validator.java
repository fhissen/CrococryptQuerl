package org.crococryptquerl.crypto;

import java.io.File;

public class Validator {
	public static final boolean notvalidCharOrDir(String base, String sub){
		try {
			if(invalidChar(sub)) return true;

			if(base != null && !new File(sub).getCanonicalPath().startsWith(base)) return true;
			
			return false;
		} catch (Exception e) {
			return true;
		}
	}
	
	public static final boolean invalidChar(String tmp){
		if(tmp == null || tmp.indexOf('\u0000') >= 0) return true;
		return false;
	}
}
