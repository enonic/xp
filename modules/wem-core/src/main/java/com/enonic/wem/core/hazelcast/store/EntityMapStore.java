package com.enonic.wem.core.hazelcast.store;

import com.enonic.wem.api.entity.Entity;
import com.enonic.wem.api.entity.EntityId;

public final class EntityMapStore
    extends JdbcMapStore<EntityId, Entity>
{
    public EntityMapStore()
    {
        super( "entity" );
    }

    @Override
    protected String keyToString( final EntityId key )
    {
        return null;
    }

    @Override
    protected String valueToString( final Entity value )
    {
        return null;
    }

    @Override
    protected EntityId stringToKey( final String str )
    {
        return null;
    }

    @Override
    protected Entity stringToValue( final String str )
    {
        return null;
    }
}
