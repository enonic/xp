package com.enonic.wem.runner;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
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

        final WebAppContext webApp = new WebAppContext();
        webApp.setResourceBase( "./modules/wem-webapp/src/main/webapp" );
        webApp.setWelcomeFiles( new String[]{"index.html", "index.jsp"} );
        webApp.setContextPath( "/" );

        server.setHandler( webApp );

        server.start();
        server.join();
    }
}
