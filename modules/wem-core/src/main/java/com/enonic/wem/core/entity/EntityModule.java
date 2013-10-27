package com.enonic.wem.core.entity;

import com.google.inject.AbstractModule;

import com.enonic.wem.core.command.CommandBinder;

public final class EntityModule
    extends AbstractModule
{
    @Override
    protected void configure()
    {
        final CommandBinder commands = CommandBinder.from( binder() );
        commands.add( CreateNodeHandler.class );
        commands.add( UpdateNodeHandler.class );
        commands.add( GetNodeByIdHandler.class );
        commands.add( GetNodeByPathHandler.class );
        commands.add( GetNodesByIdsHandler.class );
        commands.add( GetNodesByPathsHandler.class );
        commands.add( GetNodesByParentHandler.class );
    }
}
