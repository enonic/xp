package com.enonic.wem.script.internal;

import com.enonic.wem.guice.GuiceActivator;
import com.enonic.wem.script.ScriptService;

public final class ScriptActivator
    extends GuiceActivator
{
    @Override
    protected void configure()
    {
        bind( ScriptService.class ).to( ScriptServiceImpl.class );
        bind( ScriptLibraryTracker.class ).asEagerSingleton();
        bind( CommandHandlerTracker.class ).asEagerSingleton();

        service( ScriptService.class ).export();
    }
}
