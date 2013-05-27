package com.enonic.wem.core.relationship;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

import com.enonic.wem.core.command.CommandBinder;
import com.enonic.wem.core.relationship.dao.RelationshipDao;
import com.enonic.wem.core.relationship.dao.RelationshipDaoImpl;

public final class RelationshipModule
    extends AbstractModule
{
    @Override
    protected void configure()
    {
        bind( RelationshipDao.class ).to( RelationshipDaoImpl.class ).in( Scopes.SINGLETON );

        bind( RelationshipService.class ).to( RelationshipServiceImpl.class ).in( Scopes.SINGLETON );

        final CommandBinder commands = CommandBinder.from( binder() );
        commands.add( CreateRelationshipHandler.class );
        commands.add( DeleteRelationshipHandler.class );
        commands.add( GetRelationshipsHandler.class );
        commands.add( UpdateRelationshipHandler.class );
    }
}
