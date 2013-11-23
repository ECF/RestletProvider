/*******************************************************************************
 * Copyright (c) 2011 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Composent, Inc. - initial API and implementation
 *   Markus Alexander Kuppe - Additions
 ******************************************************************************/
package org.eclipse.ecf.internal.provider.restlet.container;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;
import org.restlet.Application;

public class Activator implements BundleActivator {

	private static BundleContext context;
	private static ServiceTracker<Application, Application> serviceTracker;

	static BundleContext getContext() {
		return context;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext
	 * )
	 */
	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;
		
		serviceTracker = new ServiceTracker<Application, Application>(context,
				Application.class.getName(), null);
		serviceTracker.open();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		Activator.context = null;
		
		if (serviceTracker != null) {
			serviceTracker.close();
			serviceTracker = null;
		}
	}

	public static Application getOSGiApplication() {
		return serviceTracker.getService();
	}
}
