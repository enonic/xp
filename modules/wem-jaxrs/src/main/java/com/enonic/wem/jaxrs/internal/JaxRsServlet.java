package com.enonic.wem.jaxrs.internal;

import org.jboss.resteasy.plugins.server.servlet.HttpServletDispatcher;

import com.enonic.wem.jaxrs.JaxRsContributor;

public final class JaxRsServlet
    extends HttpServletDispatcher
    implements JaxRsListener
{
    @Override
    public void add( final JaxRsContributor instance )
    {
        System.out.println( "Added " + instance );
    }

    @Override
    public void remove( final JaxRsContributor instance )
    {
        System.out.println( "Removed " + instance );
    }
}
