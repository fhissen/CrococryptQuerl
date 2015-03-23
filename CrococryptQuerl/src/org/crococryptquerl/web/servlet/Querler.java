package org.crococryptquerl.web.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.crococryptquerl.actions.DataExecutor;
import org.crococryptquerl.crypto.DataFile.PlainStream;
import org.crococryptquerl.utils.Utils;

public class Querler extends HttpServlet{
	private static final long serialVersionUID = 1L;
	
	private static final String ENCODING = "UTF-8";
	
	private static boolean NOSSL_WARNING = true;
	
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doit(false, req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doit(true, req, resp);
	}
	
	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doit(true, req, resp);
	}

	
	public void doit(boolean post, HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		if(NOSSL_WARNING && !req.isSecure()){
			NOSSL_WARNING = false;
			System.err.println("A request is using an insecure (non-TLS/non-SSL) connection! " +
					"It is not recommended to transmit passwords in plaintext!");
		}
		
		req.setCharacterEncoding(ENCODING);
		resp.setCharacterEncoding(ENCODING);

		String path = req.getRequestURI();
		if(path == null) path = "";
		else path = path.trim();
		if(path.startsWith("/")) path = path.substring(1, path.length());
		if(path.endsWith("/")) path = path.substring(0, path.length() - 1);
		
		if (path.length() == 0){
			h_pageok(resp, PAGES.upload);
			return;
		}
		
		PAGES page = null;
		try {
			page = PAGES.valueOf(path);
		} catch (Exception e) {}
		
		if(post){
			if(path.startsWith(UI_PLAIN_URL)){
				String id = path.substring(UI_PLAIN_URL.length(), path.length());
				if(id.length() == 0){
        			h_errorredirect(resp);
					return;
				}

				actionDownload(req, resp, false);
				return;
			}

			if(page == null){
				h_rootredirect(resp);
				return;
			}

			switch (page) {
			case download:
				actionDownload(req, resp, true);
				return;
				
			case upload:
				actionUpload(req, resp);
				return;
				
			default:
				h_rootredirect(resp);
				return;
			}
		}
		else{
			if(page != null){
				switch (page) {
				case download:
					h_pageok(resp, PAGES.download);
					return;
					
				case error:
					h_pageok(resp, PAGES.error);
					return;
					
				case upload:
					h_pageok(resp, PAGES.upload);
					return;
				}
			}
			
			if(path.startsWith(UI_DIRECT_URL)){
				String id = path.substring(UI_DIRECT_URL.length(), path.length());
				if(id.length() < 2 || id.contains("\"")){
        			h_errorredirect(resp);
					return;
				}

				h_nocache(resp);
				h_pageok(resp, PAGES.direct.msg(id));
			}
			else if(path.startsWith(UI_RESPATH)){
				String file = path.substring(UI_RESPATH.length(), path.length());
				RES res  = null;
				try {
					res = resources.get(file);
				} catch (Exception e) {}

				if(res != null){
					h_res(resp, res);
				}
				else{
					h_rootredirect(resp);
				}
			}
			else if(path.equalsIgnoreCase(UI_FAVICON)){
				RES res = resources.get(UI_FAVICON);

				if(res != null){
					h_res(resp, res);
				}
				else{
					h_rootredirect(resp);
				}
			}
			else{
				h_rootredirect(resp);
			}
		}
	}
	
	//---ACTIONS---
	private static final void actionDownload(HttpServletRequest req, HttpServletResponse resp, boolean headonly) throws ServletException, IOException {
		h_nocache(resp);
		
		String id = req.getParameter(_p(PARAMS.id));
		String pw = req.getParameter(_p(PARAMS.password));
		
		if(id == null || id.length() == 0 || pw == null || pw.length() < 8){
			h_errorredirect(resp);
			return;
		}
		
		PlainStream back = null;
		try {
			back = DataExecutor.read(id, pw, headonly);
		} catch (Exception e) {}
		
		if(back == null || back.name == null || back.name.length() == 0 || back.name.trim().length() == 256){
			try {
				if(back.is != null) back.is.close();
			} catch (Exception e2) {}
			h_errorredirect(resp);
			return;
		}
		
		if(headonly){
			resp.setStatus(307);
			resp.setHeader("Location", "/" + UI_PLAIN_URL + URLEncoder.encode(back.name, ENCODING).replace("+", "%20"));
			return;
		}

		resp.setStatus(HttpServletResponse.SC_OK);
		resp.setContentType("application/octet-stream");
		resp.setHeader("Content-Disposition", "attachment");
		
		try {
			new Utils().copy(back.is, resp.getOutputStream());
		} catch (Exception e) {
			try {
				if(back.is != null) back.is.close();
			} catch (Exception e2) {}
			h_errorredirect(resp);
		}
	}

	private static final void actionUpload(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		h_nocache(resp);

		try {
			if(ServletFileUpload.isMultipartContent(req)){
			      ServletFileUpload upload = new ServletFileUpload();
			      upload.setHeaderEncoding(ENCODING);
			      
			      FileItemIterator iterator = upload.getItemIterator(req);
			      
			      String pass = null, name = null;
			      InputStream fileis = null;
			      
			      while (iterator.hasNext()) {
				        FileItemStream item = iterator.next();
				        
				        if (item.isFormField()) {
				        	if(item.getFieldName().equals(_p(PARAMS.password))){
				        		pass = Utils.read(item.openStream());
				        	}
				        }
				        else {
				        	if(item.getFieldName().equals(_p(PARAMS.file))){
				        		if(pass == null || pass.length() < 8){
				        			h_errorredirect(resp);
				        			return;
				        		}

				        		fileis = item.openStream();
				        		name = item.getName();
				        		
						        if(name == null || name.length() == 0){
				        			h_errorredirect(resp);
								    return;
						        }

						        if(name.endsWith("\\")){
						        	name = "noname";
						        }
						        
						        int idx = name.lastIndexOf('\\') + 1;
						        if(idx > 0){
						        	name = name.substring(idx, name.length());
						        }

						        if(fileis == null){
				        			h_errorredirect(resp);
								    return;
						        }
						        
						        
							    String id = DataExecutor.write(fileis, name, pass);
							    if(id == null) {
				        			h_errorredirect(resp);
							    	return;
							    }
							    
							    h_pageok(resp, PAGES.success.msg(id));
							    return;
				        	}
				        }
			      }
			      
			      h_errorredirect(resp);
			}
			else{
    			h_errorredirect(resp);
			}
		} catch (Exception e) {
			e.printStackTrace();
			h_errorredirect(resp);
		}
	}

	
	//---UI---
	private static final String UI_FAVICON = "favicon.ico";
	private static final String UI_RESPATH = "res/";
	private static final String UI_PLAIN_URL = "file/";
	private static final String UI_DIRECT_URL = PAGES.direct.name() + "/";
	private static final String KEY_VAL = "###val###";

	enum PAGES{
		upload,
		download,
		direct,
		success,
		error,
		
		;
		
		private static HashMap<PAGES, String> cache = new HashMap<>();
		static{
			for(PAGES page: PAGES.values()){
				String tmp = Utils.read(PAGES.class.getResourceAsStream(page.filename()));
				if(tmp == null) System.err.println("Page " + page.filename() + " does not exist");
				cache.put(page, tmp);
			}
		}
		
		@Override
		public String toString(){
			return cache.get(this);
		}
		
		public String msg(String val){
			return toString().replace(KEY_VAL, val);
		}
		
		public String filename(){
			return name() + ".html";
		}
	}
	
	enum PARAMS{
		id,
		password,
		file
		
		;
	}
	
	enum TYPE{
		css,
		png,
		gif,
		jpg,
		jpeg,
		ico,
		
		;
		
		public String getMimetype(){
			switch (this) {
			case css:
				return "text/css";
				
			case ico:
				return "image/x-icon";

			case jpg:
			case jpeg:
				return "image/jpeg";
				
			case png:
			case gif:
				return "image/" + name();

			default:
				return "application/octet-stream";
			}
		}
	}
	
	enum RES{
		all (TYPE.css),
		favicon (TYPE.ico),
		shortcut (TYPE.png),
		back (TYPE.png),
		back2 (TYPE.png),
		
		;
		
		private static HashMap<RES, byte[]> cache = new HashMap<>();
		static{
			for(RES res: RES.values()){
				byte[] tmp = Utils.readBytes(RES.class.getResourceAsStream(res.filename()));
				if(tmp == null) System.err.println("Resource " + res.filename() + " does not exist");
				cache.put(res, tmp);
			}
		}

		private TYPE type;
		private RES(TYPE t){
			type = t;
		}
		
		public byte[] value(){
			return cache.get(this);
		}
		
		public void writeTo(OutputStream os){
			Utils.writeBytes(os, value());
		}
		
		public String mimetype(){
			return type.getMimetype();
		}
		
		public String filename(){
			return name() + "." + type.name();
		}
	}
	
	private static HashMap<String, RES> resources = new HashMap<>();
	static{
		RES[] all = RES.values();
		for(RES res: all){
			resources.put(res.filename(), res);
		}
	}
	
	
	//---HELPERS---
	@SuppressWarnings("rawtypes")
	private static final String _p(Enum p){
		return p.name();
	}

	private static final void h_nocache(HttpServletResponse resp){
		resp.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1
		resp.setHeader("Pragma", "no-cache"); // HTTP 1.0
		resp.setDateHeader("Expires", 0); // Proxies
	}
	
	private static final void h_pageok(HttpServletResponse resp, PAGES page) throws IOException{
		h_pageok(resp, page.toString());
	}

	private static final void h_pageok(HttpServletResponse resp, String page) throws IOException{
		resp.setContentType("text/html");
		resp.setStatus(HttpServletResponse.SC_OK);
		resp.getWriter().println(page);
	}

	private static final void h_res(HttpServletResponse resp, RES res) throws IOException{
		resp.setContentType(res.mimetype());
		res.writeTo(resp.getOutputStream());
	}

	private static final void h_rootredirect(HttpServletResponse resp) throws IOException{
		resp.sendRedirect("/");
	}

	private static final void h_errorredirect(HttpServletResponse resp) throws IOException{
		resp.sendRedirect("/" + PAGES.error.name());
	}

}
