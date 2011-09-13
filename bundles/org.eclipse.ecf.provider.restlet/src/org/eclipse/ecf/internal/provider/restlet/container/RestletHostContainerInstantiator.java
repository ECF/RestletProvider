/*******************************************************************************
 * Copyright (c) 2011 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.internal.provider.restlet.container;

import java.net.URI;
import java.net.URL;

import org.eclipse.ecf.core.ContainerCreateException;
import org.eclipse.ecf.core.ContainerTypeDescription;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.identity.IDFactory;
import org.eclipse.ecf.core.identity.Namespace;
import org.eclipse.ecf.core.provider.BaseRemoteServiceContainerInstantiator;
import org.eclipse.ecf.internal.provider.restlet.identity.RestletID;
import org.eclipse.ecf.internal.provider.restlet.identity.RestletNamespace;

public class RestletHostContainerInstantiator extends
		BaseRemoteServiceContainerInstantiator {

	public static final String NAME = "ecf.container.restlet.host";

	public static final String[] INTENTS = new String[] { "restlet",
			"passByValue", "exactlyOnce", "ordered" };

	public Class<?>[][] getSupportedParameterTypes(
			ContainerTypeDescription description) {
		return new Class[][] { { RestletID.class }, { String.class },
				{ URI.class }, { URL.class } };
	}

	public String[] getSupportedAdapterTypes(
			ContainerTypeDescription description) {
		return getInterfacesAndAdaptersForClass(RestletHostContainer.class);
	}

	public IContainer createInstance(ContainerTypeDescription description,
			Object[] parameters) throws ContainerCreateException {
		try {
			if (parameters == null || parameters.length < 1)
				throw new NullPointerException(
						"restlet host containers require one parameter (RestletID|String|URI|URL)");
			Object o = parameters[0];
			if (o == null)
				throw new NullPointerException(
						"restlet host container id cannot be null");
			RestletID hostID = null;
			Namespace ns = IDFactory.getDefault().getNamespaceByName(
					RestletNamespace.NAME);
			if (o instanceof RestletID) {
				hostID = (RestletID) o;
			} else if (o instanceof URI) {
				hostID = (RestletID) IDFactory.getDefault().createID(ns,
						new Object[] { (URI) o });
			} else if (o instanceof String) {
				hostID = (RestletID) IDFactory.getDefault().createID(ns,
						(String) o);
			} else
				throw new NullPointerException(
						"first parameter must be Class[]|Class|String[]|String");
			return new RestletHostContainer(hostID);
		} catch (Exception e) {
			throw new ContainerCreateException(
					"Could not create restlet host container", e);
		}
	}

	@Override
	public String[] getSupportedIntents(ContainerTypeDescription description) {
		return INTENTS;
	}

	@Override
	public String[] getImportedConfigs(ContainerTypeDescription description,
			String[] exporterSupportedConfigs) {
		return null;
	}
}
