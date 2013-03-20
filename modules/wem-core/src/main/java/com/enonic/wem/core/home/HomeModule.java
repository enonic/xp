package com.enonic.wem.core.home;

import com.google.inject.AbstractModule;

public final class HomeModule
    extends AbstractModule
{
    @Override
    protected void configure()
    {
        bind( HomeDir.class ).toInstance( HomeDir.get() );
    }
}
