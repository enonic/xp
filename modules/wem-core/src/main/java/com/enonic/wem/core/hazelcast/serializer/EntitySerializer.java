package com.enonic.wem.core.hazelcast.serializer;

import java.io.IOException;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.StreamSerializer;

import com.enonic.wem.api.entity.Entity;

public final class EntitySerializer
    implements StreamSerializer<Entity>
{
    @Override
    public int getTypeId()
    {
        return 101;
    }

    @Override
    public void write( final ObjectDataOutput out, final Entity object )
        throws IOException
    {
        // TODO: Implement binary serialization
    }

    @Override
    public Entity read( final ObjectDataInput in )
        throws IOException
    {
        // TODO: Implement binary deserialization
        return null;
    }

    @Override
    public void destroy()
    {
        // Do nothing
    }
}
