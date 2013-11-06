package com.enonic.wem.core.entity.dao;

import java.io.IOException;

import org.joda.time.DateTime;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.StreamSerializer;

import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.data.serializer.RootDataSetJsonSerializer;
import com.enonic.wem.api.entity.Entity;
import com.enonic.wem.api.entity.EntityId;

public final class EntityStreamSerializer
    implements StreamSerializer<Entity>
{

    private ObjectMapper mapper = new ObjectMapper();

    private RootDataSetJsonSerializer rootDataSetSerializer = new RootDataSetJsonSerializer();

    @Override
    public int getTypeId()
    {
        return 101;
    }

    @Override
    public void write( final ObjectDataOutput out, final Entity entity )
        throws IOException
    {
        out.writeUTF( entity.id().toString() );
        out.writeLong( entity.getCreatedTime().getMillis() );
        if ( entity.getModifiedTime() != null )
        {
            out.writeLong( entity.getModifiedTime().getMillis() );
        }
        else
        {
            out.writeLong( -1 );
        }

        final JsonNode dataAsJson = rootDataSetSerializer.serialize( entity.data() );
        final String dataAsString = mapper.writeValueAsString( dataAsJson );
        out.writeUTF( dataAsString );
    }

    @Override
    public Entity read( final ObjectDataInput in )
        throws IOException
    {
        final Entity.Builder builder = new Entity.Builder<>();
        builder.id( EntityId.from( in.readUTF() ) );

        final long createdTimeAsLong = in.readLong();
        builder.createdTime( new DateTime( createdTimeAsLong ) );
        final long modifiedTimeAsLong = in.readLong();
        builder.modifiedTime( modifiedTimeAsLong != -1 ? new DateTime( modifiedTimeAsLong ) : null );

        final String dataAsString = in.readUTF();
        final JsonNode dataAsJson = mapper.readTree( dataAsString );
        final RootDataSet data = rootDataSetSerializer.parse( dataAsJson );
        builder.rootDataSet( data );

        return builder.build();
    }

    @Override
    public void destroy()
    {
        // Do nothing
    }
}
