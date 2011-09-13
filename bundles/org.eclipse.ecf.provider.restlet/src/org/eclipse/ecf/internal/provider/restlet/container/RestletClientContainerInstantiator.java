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

import java.util.Arrays;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.eclipse.ecf.core.ContainerCreateException;
import org.eclipse.ecf.core.ContainerTypeDescription;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.provider.BaseRemoteServiceContainerInstantiator;
import org.osgi.framework.Bundle;

public class RestletClientContainerInstantiator extends
		BaseRemoteServiceContainerInstantiator {

	public static final String NAME = "ecf.container.restlet.client";

	private static final String CLAZZES_PROP = "clazzes";

	public static final String[] INTENTS = new String[] { "restlet",
			"passByValue", "exactlyOnce", "ordered" };

	@Override
	public Class<?>[][] getSupportedParameterTypes(
			ContainerTypeDescription description) {
		return new Class[][] { { Class[].class }, { Class.class },
				{ String[].class }, { String.class } };
	}

	public String[] getSupportedAdapterTypes(
			ContainerTypeDescription description) {
		return getInterfacesAndAdaptersForClass(RestletClientContainer.class);
	}

	public IContainer createInstance(ContainerTypeDescription description,
			Object[] parameters) throws ContainerCreateException {
		try {
			if (parameters == null || parameters.length < 1)
				throw new NullPointerException(
						"restlet client containers need at least one parameter (Class[]|Class|String[]|String)");
			Object o = parameters[0];
			if (o == null)
				throw new NullPointerException(
						"restletRemoteServiceInterface must not be null");
			Class<?>[] restletRemoteServiceInterfaces = null;
			if (o instanceof Map) {
				// Get object
				restletRemoteServiceInterfaces = (Class<?>[]) ((Map<?, ?>) o)
						.get(CLAZZES_PROP);
			} else if (o instanceof Class[]) {
				restletRemoteServiceInterfaces = (Class<?>[]) o;
			} else if (o instanceof Class) {
				restletRemoteServiceInterfaces = new Class[] { (Class<?>) o };
			} else if (o instanceof String[]) {
				restletRemoteServiceInterfaces = loadClasses((String[]) o);
			} else if (o instanceof String) {
				restletRemoteServiceInterfaces = loadClasses(new String[] { (String) o });
			} else
				throw new NullPointerException(
						"first parameter must be Class[]|Class|String[]|String");
			return new RestletClientContainer(restletRemoteServiceInterfaces);
		} catch (Exception e) {
			throw new ContainerCreateException(
					"Could not create restlet client container", e);
		}
	}

	Class<?>[] loadClasses(String[] classNames) throws ClassNotFoundException {
		Class<?>[] results = new Class[classNames.length];
		for (int i = 0; i < classNames.length; i++) {
			results[i] = loadClassWithBundle(classNames[i]);
		}
		return results;
	}

	private Class<?> loadClassWithBundle(String className)
			throws ClassNotFoundException {
		Bundle bundle = Activator.getContext().getBundle();
		return bundle.loadClass(className);
	}

	@Override
	public String[] getSupportedConfigs(ContainerTypeDescription description) {
		return null;
	}

	@Override
	public String[] getSupportedIntents(ContainerTypeDescription description) {
		return INTENTS;
	}

	@Override
	public String[] getImportedConfigs(ContainerTypeDescription description,
			String[] exporterSupportedConfigs) {
		List<String> supportedConfigs = Arrays.asList(exporterSupportedConfigs);
		if (supportedConfigs.contains(RestletHostContainerInstantiator.NAME))
			return new String[] { NAME };
		return null;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Dictionary getPropertiesForImportedConfigs(
			ContainerTypeDescription description, String[] importedConfigTypes,
			Dictionary exportedProperties) {
		Dictionary<String, Class<?>[]> result = new Hashtable<String, Class<?>[]>();
		try {
			String[] objectClass = (String[]) exportedProperties
					.get(org.osgi.framework.Constants.OBJECTCLASS);
			if (objectClass != null) {
				Class<?>[] clazzes = loadClasses(objectClass);
				result.put(CLAZZES_PROP, clazzes);
			}
			return result;
		} catch (Exception e) {
			// XXX log
			e.printStackTrace();
		}
		return null;
	}
}
