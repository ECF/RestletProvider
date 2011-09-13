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

import java.util.Dictionary;

import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDFactory;
import org.eclipse.ecf.core.identity.Namespace;
import org.eclipse.ecf.internal.provider.restlet.identity.RestletID;
import org.eclipse.ecf.internal.provider.restlet.identity.RestletNamespace;
import org.eclipse.ecf.remoteservice.Constants;
import org.eclipse.ecf.remoteservice.IRemoteCall;
import org.eclipse.ecf.remoteservice.IRemoteService;
import org.eclipse.ecf.remoteservice.IRemoteServiceCallPolicy;
import org.eclipse.ecf.remoteservice.IRemoteServiceID;
import org.eclipse.ecf.remoteservice.IRemoteServiceReference;
import org.eclipse.ecf.remoteservice.IRemoteServiceRegistration;
import org.eclipse.ecf.remoteservice.RemoteServiceID;
import org.eclipse.ecf.remoteservice.client.AbstractClientContainer;
import org.eclipse.ecf.remoteservice.client.IRemoteCallable;
import org.eclipse.ecf.remoteservice.client.RemoteServiceClientRegistration;

public class RestletHostContainer extends AbstractClientContainer {

	public RestletHostContainer(RestletID containerID) {
		super(containerID);
	}

	public boolean setRemoteServiceCallPolicy(IRemoteServiceCallPolicy policy) {
		return false;
	}

	class RestletHostRemoteServiceRegistration implements
			IRemoteServiceRegistration {

		private IRemoteServiceID rsID;

		public RestletHostRemoteServiceRegistration() {
			rsID = new RemoteServiceID(getConnectNamespace(),
					RestletHostContainer.this.getID(), 0);
		}

		public IRemoteServiceID getID() {
			return rsID;
		}

		public ID getContainerID() {
			return rsID.getContainerID();
		}

		public IRemoteServiceReference getReference() {
			return null;
		}

		@SuppressWarnings("rawtypes")
		public void setProperties(Dictionary properties) {
		}

		public Object getProperty(String key) {
			if (Constants.SERVICE_ID.equals(key))
				return new Long(0);
			return null;
		}

		public String[] getPropertyKeys() {
			return new String[0];
		}

		public void unregister() {
		}

	}

	@SuppressWarnings("rawtypes")
	public IRemoteServiceRegistration registerRemoteService(String[] clazzes,
			Object service, Dictionary properties) {
		return new RestletHostRemoteServiceRegistration();
	}

	@Override
	protected IRemoteService createRemoteService(
			RemoteServiceClientRegistration registration) {
		return null;
	}

	@Override
	protected String prepareEndpointAddress(IRemoteCall call,
			IRemoteCallable callable) {
		return null;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Object getAdapter(Class adapter) {
		if (adapter == null)
			return null;
		if (adapter.isAssignableFrom(this.getClass()))
			return this;
		return null;
	}

	public Namespace getConnectNamespace() {
		return IDFactory.getDefault().getNamespaceByName(RestletNamespace.NAME);
	}

}
