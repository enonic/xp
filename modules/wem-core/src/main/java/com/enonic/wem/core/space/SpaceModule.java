package com.enonic.wem.core.space;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

import com.enonic.wem.core.command.CommandBinder;
import com.enonic.wem.core.initializer.InitializerBinder;
import com.enonic.wem.core.space.dao.SpaceDao;
import com.enonic.wem.core.space.dao.SpaceDaoImpl;

public final class SpaceModule
    extends AbstractModule
{
    @Override
    protected void configure()
    {
        bind( SpaceDao.class ).to( SpaceDaoImpl.class ).in( Scopes.SINGLETON );
        InitializerBinder.from( binder() ).add( SpacesInitializer.class );

        final CommandBinder commands = CommandBinder.from( binder() );
        commands.add( CreateSpaceHandler.class );
        commands.add( DeleteSpaceHandler.class );
        commands.add( GetSpacesHandler.class );
        commands.add( RenameSpaceHandler.class );
        commands.add( UpdateSpaceHandler.class );
    }
}
