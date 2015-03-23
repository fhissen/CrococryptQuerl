package org.crococryptquerl.web.server;

import org.crococryptquerl.web.servlet.Querler;
import org.eclipse.jetty.server.ConnectionFactory;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;


public class JettyLaunch {
	public static boolean startup(int port) {
	    Server server = new Server(port);
	    
        ServletHandler handler = new ServletHandler();
        handler.addServletWithMapping(Querler.class, "/*");
        
	    for(Connector y : server.getConnectors()) {
	        for(ConnectionFactory x  : y.getConnectionFactories()) {
	            if(x instanceof HttpConnectionFactory) {
	                ((HttpConnectionFactory)x).getHttpConfiguration().setSendServerVersion(false);
	            }
	        }
	    }

	    server.setHandler(handler);
	    try {
		    server.start();
		    return true;
		} catch (Exception e) {
			try {
				server.stop();
			} catch (Exception e2) {}
		}
	    
	    return false;
	}
}
