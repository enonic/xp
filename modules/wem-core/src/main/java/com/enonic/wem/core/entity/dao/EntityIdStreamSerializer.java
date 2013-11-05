package com.enonic.wem.core.entity.dao;

import java.io.IOException;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.StreamSerializer;

import com.enonic.wem.api.entity.EntityId;

public final class EntityIdStreamSerializer
implements StreamSerializer<EntityId>
{
    @Override
    public int getTypeId()
    {
        return 100;
    }

    @Override
    public void write( final ObjectDataOutput out, final EntityId object )
        throws IOException
    {
        out.writeUTF( object.toString() );
    }

    @Override
    public EntityId read( final ObjectDataInput in )
        throws IOException
    {
        return EntityId.from( in.readUTF() );
    }

    @Override
    public void destroy()
    {
        // Do nothing
    }
}
