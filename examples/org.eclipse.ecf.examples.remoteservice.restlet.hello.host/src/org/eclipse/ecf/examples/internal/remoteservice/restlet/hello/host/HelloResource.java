/*******************************************************************************
 * Copyright (c) 2010 Bryan Hunt.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Bryan Hunt - initial API and implementation
 *    Scott Lewis - Additions for remote service export (IHelloResource interface)
 *******************************************************************************/

package org.eclipse.ecf.examples.internal.remoteservice.restlet.hello.host;

import org.eclipse.ecf.examples.remoteservice.restlet.hello.IHelloResource;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

public class HelloResource extends ServerResource implements IHelloResource {
	@Get("txt")
	public String sayHello() {
		return "Hello RESTful World";
	}

	@Get("html")
	public String getDocument() {
		StringBuilder html = new StringBuilder();
		html.append("<html>\n");
		html.append("  <body>\n");
		html.append("    <h2>Hello RESTful World</h2>\n");
		html.append("   </body>\n");
		html.append("</html>\n");
		return html.toString();
	}
}
