package com.enonic.wem.jaxrs.internal;

import org.jboss.resteasy.plugins.server.servlet.HttpServletDispatcher;

import com.enonic.wem.jaxrs.JaxRsResourceFactory;

public final class JaxRsServlet
    extends HttpServletDispatcher
    implements JaxRsContainer
{
    @Override
    public void restart()
    {
        System.out.println( "Restart" );
    }

    @Override
    public void registerFactory( final JaxRsResourceFactory factory )
    {
        System.out.println( "registerFactory " + factory );
    }

    @Override
    public void registerProvider( final Object instance )
    {
        System.out.println( "registerProvider " + instance );
    }

    @Override
    public void registerResource( final Object instance )
    {
        System.out.println( "registerResource " + instance );
    }
}
