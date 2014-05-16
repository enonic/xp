package com.enonic.wem.admin;

import com.enonic.wem.guice.GuiceActivator;

public final class Activator
    extends GuiceActivator
{
    @Override
    protected void configure()
    {
        install( new AdminModule() );
    }
}
