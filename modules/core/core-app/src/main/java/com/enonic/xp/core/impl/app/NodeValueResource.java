package com.enonic.xp.core.impl.app;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

import org.jspecify.annotations.Nullable;

import com.google.common.io.ByteSource;

import com.enonic.xp.core.impl.app.resolver.RevisionedResource;
import com.enonic.xp.data.Value;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeVersionId;
import com.enonic.xp.resource.ResourceBase;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.schema.SchemaNodePropertyNames;

/**
 * A resource served from a node: the version of the node it was read from is the revision of its content, so two writes of the
 * node are told apart whatever their timestamps.
 */
public final class NodeValueResource
    extends ResourceBase
    implements RevisionedResource
{
    private final Instant timestamp;

    private final ByteSource value;

    private final @Nullable NodeVersionId version;

    public NodeValueResource( final ResourceKey key, final Node node )
    {
        super( key );

        this.timestamp = node.getTimestamp();
        this.version = node.getNodeVersionId();

        final Value resource = node.data().getValue( SchemaNodePropertyNames.RESOURCE );
        this.value = resource != null ? ByteSource.wrap( resource.asString().getBytes( StandardCharsets.UTF_8 ) ) : ByteSource.empty();
    }

    /**
     * @param version the version of the node the content comes from
     */
    public NodeValueResource( final ResourceKey key, final ByteSource resource, final Instant timestamp,
                              final @Nullable NodeVersionId version )
    {
        super( key );

        this.timestamp = timestamp;
        this.value = resource;
        this.version = version;
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

    @Override
    public @Nullable Object revision()
    {
        return version;
    }
}
