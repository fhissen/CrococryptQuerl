package org.crococryptquerl.web.browsers;

public class SimpleDetect {
	public enum BrowserType{
		IEold,
		Safari,
		modern,
		
		;
	}
	
	public static final BrowserType detect(String useragent){
		if(useragent == null || useragent.length() == 0) return BrowserType.modern;
		
		if(useragent.contains("Chrome") || useragent.contains("Firefox"))
			return BrowserType.modern;

		if(useragent.contains("AppleWebKit") && useragent.contains("Safari"))
			return BrowserType.Safari;
		
		if(useragent.contains("MSIE 6.") || useragent.contains("MSIE 7."))
			return BrowserType.IEold;
		
		return BrowserType.modern;
	}
}
