package com.enonic.wem.mustache.internal;

import com.enonic.wem.guice.GuiceActivator;
import com.enonic.wem.mustache.MustacheProcessorFactory;

public final class MustacheActivator
    extends GuiceActivator
{
    @Override
    protected void configure()
    {
        bind( MustacheProcessorFactory.class ).to( MustacheProcessorFactoryImpl.class );

        service( MustacheProcessorFactory.class ).export();
        service( MustacheScriptLibrary.class ).export();
    }
}
