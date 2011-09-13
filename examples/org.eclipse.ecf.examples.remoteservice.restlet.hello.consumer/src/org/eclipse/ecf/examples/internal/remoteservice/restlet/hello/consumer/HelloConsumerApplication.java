package org.eclipse.ecf.examples.internal.remoteservice.restlet.hello.consumer;

import org.eclipse.ecf.examples.remoteservice.restlet.hello.IHelloResource;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

@SuppressWarnings("rawtypes")
public class HelloConsumerApplication implements IApplication, ServiceTrackerCustomizer<IHelloResource,IHelloResource> {

	private final Object appLock = new Object();
	private boolean done = false;

	private BundleContext bundleContext;
	private ServiceTracker helloResourceTracker;
	
	public IHelloResource addingService(
			ServiceReference<IHelloResource> reference) {
		IHelloResource helloResource = bundleContext.getService(reference);
		// Call methods
		String helloResult = helloResource.sayHello();
		System.out.println("sayHello returned: "+helloResult);
		
		return helloResource;
	}

	public void modifiedService(ServiceReference<IHelloResource> reference,
			IHelloResource service) {}

	public void removedService(ServiceReference<IHelloResource> reference,
			IHelloResource service) {}

	@SuppressWarnings("unchecked")
	public Object start(IApplicationContext context) throws Exception {
		bundleContext = Activator.getContext();
		
		helloResourceTracker = new ServiceTracker(bundleContext,IHelloResource.class.getName(),this);
		helloResourceTracker.open();

		waitForDone();
		
		return IApplication.EXIT_OK;
	}

	public void stop() {
		if (helloResourceTracker != null) {
			helloResourceTracker.close();
			helloResourceTracker = null;
		}
		bundleContext = null;
		synchronized (appLock) {
			done = true;
			appLock.notifyAll();
		}
	}

	private void waitForDone() {
		// then just wait here
		synchronized (appLock) {
			while (!done) {
				try {
					appLock.wait();
				} catch (InterruptedException e) {
					// do nothing
				}
			}
		}
	}

}
