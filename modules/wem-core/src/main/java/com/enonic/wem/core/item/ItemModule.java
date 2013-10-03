package com.enonic.wem.core.item;

import com.google.inject.AbstractModule;

import com.enonic.wem.core.command.CommandBinder;

public final class ItemModule
    extends AbstractModule
{
    @Override
    protected void configure()
    {
        final CommandBinder commands = CommandBinder.from( binder() );
        commands.add( CreateItemHandler.class );
        commands.add( UpdateItemHandler.class );
    }
}
