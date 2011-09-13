/*******************************************************************************
 * Copyright (c) 2011 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.internal.provider.restlet.identity;

import java.net.URI;
import java.net.URL;

import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDCreateException;
import org.eclipse.ecf.core.identity.Namespace;
import org.eclipse.ecf.remoteservice.rest.identity.RestNamespace;

public class RestletNamespace extends RestNamespace {

	private static final long serialVersionUID = -6735892377999173650L;

	public static final String SCHEME = "restlet";

	public static final String NAME = "ecf.namespace.restlet";

	public RestletNamespace() {
	}

	public RestletNamespace(String name, String desc) {
		super(name, desc);
	}

	private String getInitFromExternalForm(Object[] args) {
		if (args == null || args.length < 1 || args[0] == null)
			return null;
		if (args[0] instanceof String) {
			final String arg = (String) args[0];
			if (arg.startsWith(getScheme() + Namespace.SCHEME_SEPARATOR)) {
				final int index = arg.indexOf(Namespace.SCHEME_SEPARATOR);
				if (index >= arg.length())
					return null;
				return arg.substring(index + 1);
			}
		}
		return null;
	}

	public ID createInstance(Object[] parameters) throws IDCreateException {
		URI uri = null;
		try {
			final String init = getInitFromExternalForm(parameters);
			if (init != null) {
				uri = URI.create(init);
				return new RestletID(this, uri);
			}
			if (parameters != null) {
				if (parameters[0] instanceof URI)
					return new RestletID(this, (URI) parameters[0]);
				else if (parameters[0] instanceof String)
					return new RestletID(this,
							URI.create((String) parameters[0]));
				else if (parameters[0] instanceof URL)
					return new RestletID(this, URI.create(((URL) parameters[0])
							.toExternalForm()));
				else if (parameters[0] instanceof RestletID)
					return (ID) parameters[0];
			}
			throw new IllegalArgumentException(
					"Invalid parameters to RestletID creation"); //$NON-NLS-1$
		} catch (Exception e) {
			throw new IDCreateException("Could not create restlet ID", e); //$NON-NLS-1$
		}
	}

	public String getScheme() {
		return SCHEME;
	}
}
