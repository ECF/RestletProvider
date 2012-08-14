This plugin is an example of how to discover a remote service using the
OSGi Standard 'Endpoint Descripton Extender Format' (EDEF).  For documentation
on EDEF, and references to the specification see [1].  Also see [2] for documentation about OSGi Remote Services,
OSGi Remote Service Admin (RSA) and other topics.

The use of this plugin assumes:

1) That a Restlet-provider-based server is currently running on localhost:8080.
2) That this server is exposing the IHello as a remote service with the
Restlet provider.  Running this launch config [2] satisfies both requirements.

Start the Client

To start this client, run this launch config [3] (present in this project)

Discovering the Remote Service

One running, to discover the remote service, in the OSGi console type:

osgi>start org.eclipse.ecf.examples.remoteservice.restlet.hello.consumer.edef

When this command is given, the remote service should be discovered, the IHello service
introduced into the local process (triggering the ServiceTracker.addingService method declared

here:  org.eclipse.ecf.examples.internal.remoteservice.restlet.hello.consumer.HelloConsumerApplication.addingService:20

This should also result in some console output, as the service is actually invoked by the code
in the addingService method, on the console of both the Restlet server, and this client.


[1] http://wiki.eclipse.org/File-based_Discovery_with_the_Endpoint_Description_Extender_Format
[2] /org.eclipse.ecf.examples.remoteservice.restlet.hello.host/Restlet RSA Server (print edef).launch
[3] /org.eclipse.ecf.examples.remoteservice.restlet.hello.consumer.edef/Restlet Hello Consumer (edef).launch