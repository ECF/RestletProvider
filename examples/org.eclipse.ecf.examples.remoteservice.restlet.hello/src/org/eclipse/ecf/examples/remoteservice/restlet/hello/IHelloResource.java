package org.eclipse.ecf.examples.remoteservice.restlet.hello;

import org.restlet.resource.Get;

public interface IHelloResource {
	@Get("txt")
	public String sayHello();

	@Get("html")
	public String getDocument();

}
