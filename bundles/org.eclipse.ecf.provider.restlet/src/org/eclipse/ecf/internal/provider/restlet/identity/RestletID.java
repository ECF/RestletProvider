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

import org.eclipse.ecf.core.identity.Namespace;
import org.eclipse.ecf.remoteservice.rest.identity.RestID;

public class RestletID extends RestID {

	private static final long serialVersionUID = 2628937639196715956L;

	public RestletID(Namespace namespace, URI uri) {
		super(namespace, uri);
	}

}
