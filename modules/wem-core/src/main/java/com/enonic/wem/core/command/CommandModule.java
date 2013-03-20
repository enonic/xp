package com.enonic.wem.core.command;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

public final class CommandModule
    extends AbstractModule
{
    @Override
    protected void configure()
    {
        bind( CommandContextFactory.class ).to( CommandContextFactoryImpl.class ).in( Scopes.SINGLETON );
        bind( CommandInvoker.class ).to( CommandInvokerImpl.class ).in( Scopes.SINGLETON );
    }
}
