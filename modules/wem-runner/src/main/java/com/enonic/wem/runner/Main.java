package com.enonic.wem.runner;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.resource.ResourceCollection;
import org.eclipse.jetty.webapp.WebAppContext;

public final class Main
{
    public static void main( final String... args )
        throws Exception
    {
        final Server server = new Server( 8080 );
        server.setStopAtShutdown( true );

        final ServletContextHandler servletHandler = new ServletContextHandler( ServletContextHandler.SESSIONS );
        servletHandler.setContextPath( "/" );

        final Resource dir1 = Resource.newResource( "./modules/wem-webapp/src/main/webapp" );
        final Resource dir2 = Resource.newResource( "./modules/wem-webapp/target/generated-sources/webapp" );

        final ResourceCollection resourceDirs = new ResourceCollection();
        resourceDirs.setResources( new Resource[]{dir1, dir2} );

        final WebAppContext webApp = new WebAppContext();
        webApp.setBaseResource( resourceDirs );
        webApp.setWelcomeFiles( new String[]{"index.html", "index.jsp"} );
        webApp.setContextPath( "/" );

        server.setHandler( webApp );

        server.start();
        server.join();
    }
}
