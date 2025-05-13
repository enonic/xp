package com.enonic.xp.core.impl.app;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

import com.google.common.io.ByteSource;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.data.Value;
import com.enonic.xp.node.Node;
import com.enonic.xp.resource.ResourceBase;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.schema.SchemaNodePropertyNames;

@PublicApi
public final class NodeValueResource
    extends ResourceBase
{
    private final Instant timestamp;

    private final ByteSource value;

    public NodeValueResource( final ResourceKey key, final Node node )
    {
        super( key );

        this.timestamp = node.getTimestamp();

        final Value resource = node.data().getValue( SchemaNodePropertyNames.RESOURCE );
        this.value = resource != null ? ByteSource.wrap( resource.asString().getBytes( StandardCharsets.UTF_8 ) ) : ByteSource.empty();
    }

    public NodeValueResource( final ResourceKey key, final ByteSource resource, final Instant timestamp )
    {
        super( key );

        this.timestamp = timestamp;
        this.value = resource;

    }

    @Override
    public boolean exists()
    {
        return true;
    }

    @Override
    public long getSize()
    {
        try
        {
            return value.size();
        }
        catch ( IOException e )
        {
            throw new UncheckedIOException( e );
        }
    }

    @Override
    public long getTimestamp()
    {
        return timestamp.toEpochMilli();
    }

    @Override
    public ByteSource getBytes()
    {
        return value;
    }

    @Override
    public String getResolverName()
    {
        return "node";
    }
}
