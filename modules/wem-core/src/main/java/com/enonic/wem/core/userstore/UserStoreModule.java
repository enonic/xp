package com.enonic.wem.core.userstore;

import com.google.inject.AbstractModule;

import com.enonic.wem.core.command.CommandBinder;

public final class UserStoreModule
    extends AbstractModule
{
    @Override
    protected void configure()
    {
        final CommandBinder commands = CommandBinder.from( binder() );
        commands.add( CreateUserStoreHandler.class );
        commands.add( DeleteUserStoreHandler.class );
        commands.add( FindAllUserStoresHandler.class );
        commands.add( GetUserStoreConnectorsHandler.class );
        commands.add( GetUserStoresHandler.class );
        commands.add( UpdateUserStoreHandler.class );
    }
}
