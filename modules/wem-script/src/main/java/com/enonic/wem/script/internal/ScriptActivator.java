package com.enonic.wem.script.internal;

import javax.inject.Inject;

import com.enonic.wem.guice.GuiceActivator;
import com.enonic.wem.script.ScriptService;

public final class ScriptActivator
    extends GuiceActivator
{
    @Inject
    protected ScriptLibraryTracker libraryTracker;

    @Inject
    protected CommandHandlerTracker commandHandlerTracker;

    @Override
    protected void configure()
    {
        bind( ScriptService.class ).to( ScriptServiceImpl.class );
        bind( CommandHandlerTracker.class ).asEagerSingleton();

        service( ScriptService.class ).export();
    }

    @Override
    protected void doStart()
        throws Exception
    {
        this.libraryTracker.open();
        this.commandHandlerTracker.open();
    }

    @Override
    protected void doStop()
        throws Exception
    {
        this.commandHandlerTracker.close();
        this.libraryTracker.close();
    }
}
