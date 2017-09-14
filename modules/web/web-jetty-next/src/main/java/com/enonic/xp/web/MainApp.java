package com.enonic.xp.web;

import com.enonic.xp.web.impl.WebServerFactoryImpl;
import com.enonic.xp.web.server.WebServer;
import com.enonic.xp.web.server.WebServerConfig;
import com.enonic.xp.web.server.WebServerFactory;

public final class MainApp
{
    public static void main( final String... args )
        throws Exception
    {
        final WebServerConfig config = WebServerConfig.newBuilder().
            port( 8080 ).
            build();

        final WebServerFactory factory = new WebServerFactoryImpl();
        final WebServer server = factory.create( config );
        server.start();
    }
}
