package com.hunteron.hessian.connection;

import java.io.IOException;
import java.net.URL;

import com.caucho.hessian.client.HessianConnection;
import com.caucho.hessian.client.HessianURLConnectionFactory;

/**
 * Internal factory for creating connections to the server. 
 * The default factory is java.net
 * 
 * @author rocca.peng@hunteron.com
 * @Description 
 * @Date  2015年4月15日 下午4:09:05
 */
public class HessianHeaderURLConnectionFactory extends HessianURLConnectionFactory {

	/**
	 * Opens a new or recycled connection to the HTTP server.
	 */
	public HessianConnection open(URL url) throws IOException {
		HessianConnection open = super.open(url);
		HessianHeaderCache.addHeader(open, url.toString());
		return open;
	}
}
