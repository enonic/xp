package com.enonic.wem.core.entity;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

import com.enonic.wem.api.entity.Entity;
import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.core.command.CommandBinder;
import com.enonic.wem.core.entity.dao.EntityDao;
import com.enonic.wem.core.entity.dao.EntityHazelcastDao;
import com.enonic.wem.core.entity.dao.EntityIdStreamSerializer;
import com.enonic.wem.core.entity.dao.EntityStreamSerializer;
import com.enonic.wem.core.hazelcast.HazelcastBinder;

public final class EntityModule
    extends AbstractModule
{
    @Override
    protected void configure()
    {
        bind( EntityDao.class ).to( EntityHazelcastDao.class ).in( Scopes.SINGLETON );

        final CommandBinder commands = CommandBinder.from( binder() );
        commands.add( CreateNodeHandler.class );
        commands.add( UpdateNodeHandler.class );
        commands.add( DeleteNodeByPathHandler.class );
        commands.add( DeleteNodeByIdHandler.class );
        commands.add( GetNodeByIdHandler.class );
        commands.add( GetNodeByPathHandler.class );
        commands.add( GetNodesByIdsHandler.class );
        commands.add( GetNodesByPathsHandler.class );
        commands.add( GetNodesByParentHandler.class );

        final HazelcastBinder hazelcast = HazelcastBinder.from( binder() );
        hazelcast.addSerializer( Entity.class, EntityStreamSerializer.class );
        hazelcast.addSerializer( EntityId.class, EntityIdStreamSerializer.class );
    }
}
