package com.enonic.wem.jaxrs.internal;

import com.enonic.wem.guice.GuiceActivator;

public final class Activator
    extends GuiceActivator
{
    @Override
    protected void configure()
    {
        service( JaxRsServlet.class ).attribute( "alias", "/*" ).export();
        bind( ResourceListener.class ).asEagerSingleton();
    }
}
