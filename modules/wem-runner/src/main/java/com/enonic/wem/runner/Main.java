package com.enonic.wem.runner;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;

public final class Main
{
    public static void main( final String... args )
        throws Exception
    {
        final Server server = new Server( 8080 );

        ServletContextHandler handler = new ServletContextHandler( ServletContextHandler.SESSIONS );
        handler.setContextPath( "/" );
        server.setHandler( handler );

        /*
        final BootServletListener listener = new BootServletListener();
        handler.addEventListener( listener );
*/

        server.start();

        // server.stop();
    }
}
