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

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.remoteserviceadmin.RemoteServiceAdminListener;

public class Activator implements BundleActivator {

	private static Activator instance;
	private RestletTopologyManager restletTopologyManager;
	private ServiceRegistration<RemoteServiceAdminListener> rsaEventListenerRegistration;
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext
	 * )
	 */
	public void start(BundleContext bundleContext) throws Exception {
		instance = this;
		this.restletTopologyManager = new RestletTopologyManager(bundleContext);
		this.rsaEventListenerRegistration = bundleContext.registerService(RemoteServiceAdminListener.class, restletTopologyManager, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		if (rsaEventListenerRegistration != null) {
			rsaEventListenerRegistration.unregister();
			rsaEventListenerRegistration = null;
		}
		if (restletTopologyManager != null) {
			restletTopologyManager.close();
			restletTopologyManager = null;
		}
		instance = null;
	}

	public static Activator getDefault() {
		return instance;
	}

	public RestletTopologyManager getRestletTopologyManager() {
		return restletTopologyManager;
	}

}
