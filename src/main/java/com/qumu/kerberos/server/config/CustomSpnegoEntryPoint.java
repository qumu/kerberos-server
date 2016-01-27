package com.qumu.kerberos.server.config;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.kerberos.web.authentication.SpnegoEntryPoint;

/**
 * This is invoked in case no authentication is provided or it has failed. The default
 * behaviour will be send a 401 Unauthorized response back.
 * 
 * @author davidfernandez
 */
public class CustomSpnegoEntryPoint extends SpnegoEntryPoint {
	
	private static final Log LOG = LogFactory.getLog(SpnegoEntryPoint.class);
	
	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException ex)
			throws IOException, ServletException {
		
		if (LOG.isDebugEnabled()) {
			LOG.debug("Add header WWW-Authenticate:Negotiate to " + request.getRequestURL());
		}
		
		response.addHeader("WWW-Authenticate", "Negotiate");
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		response.flushBuffer();
	}
	

}
