package com.enonic.wem.core.resource;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

import com.enonic.wem.core.command.CommandBinder;
import com.enonic.wem.core.resource.dao.ResourceDao;
import com.enonic.wem.core.resource.dao.ResourceDaoImpl;


public final class ResourceModule
    extends AbstractModule
{
    @Override
    protected void configure()
    {
        bind( ResourceDao.class ).to( ResourceDaoImpl.class ).in( Scopes.SINGLETON );

        final CommandBinder commands = CommandBinder.from( binder() );
        commands.add( GetResourceHandler.class );
    }
}
