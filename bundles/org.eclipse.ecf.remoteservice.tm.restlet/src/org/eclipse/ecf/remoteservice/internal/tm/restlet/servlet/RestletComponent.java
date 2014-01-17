/*******************************************************************************
 * Copyright (c) 2011 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.remoteservice.internal.tm.restlet.servlet;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.osgi.framework.BundleContext;
import org.osgi.service.log.LogService;
import org.restlet.ext.osgi.IApplicationProvider;
import org.restlet.ext.osgi.IResourceProvider;

public class RestletComponent {

	public static final String RESTLET_URLPREFIX_ = "org.eclipse.ecf.remoteservice.restlet.exporturl";
	public static final String RESTLET_URLPREFIX_PROTOCOL = System.getProperty(
			RESTLET_URLPREFIX_ + ".protocol", "http");
	public static final String RESTLET_URLPREFIX_HOST = System.getProperty(
			RESTLET_URLPREFIX_ + ".host", "localhost");
	public static final String RESTLET_URLPREFIX_PORT = System.getProperty(
			RESTLET_URLPREFIX_ + ".port", "8080");
	public static final String RESTLET_SERVICE_EXPORTED_INTERFACES = "restlet.service.exported.interfaces";

	class HttpService {
		private org.osgi.service.http.HttpService httpService;
		@SuppressWarnings("rawtypes")
		private Map properties;

		public HttpService(org.osgi.service.http.HttpService httpService,
				@SuppressWarnings("rawtypes") Map properties) {
			this.httpService = httpService;
			this.properties = properties;
		}

		public org.osgi.service.http.HttpService getHttpService() {
			return httpService;
		}

		@SuppressWarnings("rawtypes")
		public Map getProperties() {
			return properties;
		}

		public void registerApplicationProvider(ApplicationProvider ap) {
			RestletApplicationServlet restletApplicationServlet = new RestletApplicationServlet(
					getRemoteUrlPrefix(ap), ap);
			String alias = restletApplicationServlet.getAlias();
			try {
				getHttpService().registerServlet(alias,
						restletApplicationServlet,
						restletApplicationServlet.getInitParams(),
						restletApplicationServlet.getContext());
				registeredServlets.put(alias, restletApplicationServlet);
				// Now export
				restletTopologyManager
						.exportRemoteApplicationServlet(restletApplicationServlet);
			} catch (Exception e) {
				if (logService != null)
					logService.log(LogService.LOG_ERROR,
							"Failed to register the application servlet at alias: '"
									+ alias + "'", e);
			}
		}

		public void unregisterApplicationProvider(ApplicationProvider ap) {
			String alias = ap.getApplicationProvider().getAlias();
			RestletApplicationServlet restletApplicationServlet = registeredServlets
					.remove(alias);
			// unregister servlet
			if (restletApplicationServlet != null) {
				// unexport
				restletTopologyManager
						.unexportRemoteApplicationServlet(restletApplicationServlet);
				try {
					getHttpService().unregister(alias);
				} catch (Throwable t) {
					if (logService != null)
						logService.log(LogService.LOG_ERROR,
								"Failed to unregister the application servlet at alias: '"
										+ alias + "'", t);
				}
			}
		}
	}

	class ApplicationProvider {
		private IApplicationProvider applicationProvider;
		@SuppressWarnings("rawtypes")
		private Map properties;

		public ApplicationProvider(IApplicationProvider applicationProvider,
				@SuppressWarnings("rawtypes") Map properties) {
			this.applicationProvider = applicationProvider;
			this.properties = properties;
		}

		public IApplicationProvider getApplicationProvider() {
			return applicationProvider;
		}

		@SuppressWarnings("rawtypes")
		public Map getProperties() {
			return properties;
		}
	}

	private HttpService httpService;
	private LogService logService;

	private Set<ApplicationProvider> applicationProviders = Collections
			.synchronizedSet(new HashSet<ApplicationProvider>());
	private Map<String, RestletApplicationServlet> registeredServlets = Collections
			.synchronizedMap(new HashMap<String, RestletApplicationServlet>());
	private RestletTopologyManager restletTopologyManager;
	private boolean active = false;

	public RestletComponent() {
		restletTopologyManager = Activator.getDefault()
				.getRestletTopologyManager();
	}

	void activate(BundleContext context) {
		for (ApplicationProvider ap : applicationProviders)
			httpService.registerApplicationProvider(ap);
		active = true;
	}

	void deactivate(BundleContext context) {
		for (ApplicationProvider ap : applicationProviders)
			httpService.unregisterApplicationProvider(ap);
		active = false;
	}

	void bindLogService(LogService logService) {
		this.logService = logService;
	}

	void unbindLogService(LogService logService) {
		this.logService = null;
	}

	void bindApplicationProvider(IApplicationProvider applicationProvider,
			@SuppressWarnings("rawtypes") Map properties) {
		ApplicationProvider ap = new ApplicationProvider(applicationProvider,
				properties);
		applicationProviders.add(ap);
		if (active)
			httpService.registerApplicationProvider(ap);
	}

	void unbindApplicationProvider(IApplicationProvider applicationProvider) {
		synchronized (applicationProviders) {
			ApplicationProvider removed = null;
			for (Iterator<ApplicationProvider> i = applicationProviders
					.iterator(); i.hasNext();) {
				ApplicationProvider ap = i.next();
				if (ap.getApplicationProvider() == applicationProvider) {
					i.remove();
					removed = ap;
					break;
				}
			}
			if (removed != null)
				httpService.unregisterApplicationProvider(removed);
		}
	}

	void bindResourceProvider(IResourceProvider resourceProvider,
			Map<?, ?> rpProperties) {
		if (rpProperties.get(RESTLET_SERVICE_EXPORTED_INTERFACES) != null) {
			String[] paths = resourceProvider.getPaths();
			if (paths != null)
				for (int i = 0; i < paths.length; i++)
					restletTopologyManager.addResourceProviderProperties(
							paths[i], rpProperties);
		}
	}

	void unbindResourceProvider(IResourceProvider resourceProvider) {
		String[] paths = resourceProvider.getPaths();
		if (paths != null)
			for (int i = 0; i < paths.length; i++)
				restletTopologyManager
						.removeResourceProviderProperties(paths[i]);
	}

	void bindHttpService(org.osgi.service.http.HttpService httpService,
			@SuppressWarnings("rawtypes") Map httpServiceProperties) {
		this.httpService = new HttpService(httpService, httpServiceProperties);
	}

	void unbindHttpService(org.osgi.service.http.HttpService httpService) {
		this.httpService = null;
	}

	private String getHost(@SuppressWarnings("rawtypes") Map properties) {
		String h = (String) properties.get(RESTLET_URLPREFIX_ + ".host");
		if (h == null)
			h = RESTLET_URLPREFIX_HOST;
		if (h.equals("<hostname>")) {
			try {
				h = InetAddress.getLocalHost().getCanonicalHostName();
			} catch (UnknownHostException e) {
			}
		}
		return h;
	}

	private int getPort(@SuppressWarnings("rawtypes") Map properties) {
		String p = (String) properties.get(RESTLET_URLPREFIX_ + ".port");
		if (p == null)
			p = RESTLET_URLPREFIX_PORT;
		int result = -1;
		try {
			result = new Integer(p).intValue();
		} catch (NumberFormatException e) {
		}
		return result;
	}

	private String getUrlPrefix(@SuppressWarnings("rawtypes") Map properties) {
		if (properties == null)
			return null;
		String urlPrefix = (String) properties.get(RESTLET_URLPREFIX_
				+ ".urlprefix");
		if (urlPrefix != null)
			return urlPrefix;
		StringBuffer sb = new StringBuffer();
		// protocol
		String protocol = (String) properties.get(RESTLET_URLPREFIX_
				+ ".protocol");
		sb.append((protocol != null) ? protocol : RESTLET_URLPREFIX_PROTOCOL)
				.append("://");
		// host
		String host = getHost(properties);
		sb.append((host != null) ? host : RESTLET_URLPREFIX_HOST);
		// port
		int port = getPort(properties);
		if (port > 0)
			sb.append(":").append(port);
		return sb.toString();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Map dictionaryToMap(Dictionary<String, Object> dict) {
		if (dict == null)
			return null;
		Map map = new HashMap();
		for (Enumeration<String> e = dict.keys(); e.hasMoreElements();) {
			String key = e.nextElement();
			Object val = dict.get(key);
			map.put(key, val);
		}
		return map;
	}

	private String getRemoteUrlPrefix(ApplicationProvider applicationProvider) {
		String result = null;
		// First check http service
		@SuppressWarnings("rawtypes")
		Map httpServiceProperties = this.httpService.getProperties();
		String httpUrlPrefix = getUrlPrefix(httpServiceProperties);
		if (httpUrlPrefix != null)
			result = httpUrlPrefix;
		// Then check applicationProvider properties
		@SuppressWarnings("rawtypes")
		Map apProperties = applicationProvider.getProperties();
		String apUrlPrefix = getUrlPrefix(apProperties);
		if (apUrlPrefix != null)
			result = apUrlPrefix;
		// Then check applicationProvider initParams
		@SuppressWarnings("rawtypes")
		Map apInitParams = dictionaryToMap(applicationProvider
				.getApplicationProvider().getInitParms());
		String apInitUrlPrefix = getUrlPrefix(apInitParams);
		if (apInitUrlPrefix != null)
			result = apInitUrlPrefix;
		return result;
	}

}
