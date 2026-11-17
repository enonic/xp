package com.enonic.xp.mcp;

import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.support.AbstractImmutableEntityList;

/**
 * An immutable, ordered collection of MCP descriptors.
 */
public final class McpDescriptors
    extends AbstractImmutableEntityList<McpDescriptor>
{
    private static final McpDescriptors EMPTY = new McpDescriptors( ImmutableList.of() );

    private McpDescriptors( final ImmutableList<McpDescriptor> list )
    {
        super( list );
    }

    /**
     * Gets the empty collection.
     *
     * @return an empty collection
     */
    public static McpDescriptors empty()
    {
        return EMPTY;
    }

    /**
     * Copies descriptors in array order.
     *
     * @param descriptors descriptors to copy
     * @return an immutable collection
     */
    public static McpDescriptors from( final McpDescriptor... descriptors )
    {
        return fromInternal( ImmutableList.copyOf( descriptors ) );
    }

    /**
     * Copies descriptors in iteration order.
     *
     * @param descriptors descriptors to copy
     * @return an immutable collection
     */
    public static McpDescriptors from( final Iterable<McpDescriptor> descriptors )
    {
        return descriptors instanceof McpDescriptors d ? d : fromInternal( ImmutableList.copyOf( descriptors ) );
    }

    /**
     * Collects descriptors in encounter order.
     *
     * @return a collector producing an immutable collection
     */
    public static Collector<McpDescriptor, ?, McpDescriptors> collector()
    {
        return Collectors.collectingAndThen( ImmutableList.toImmutableList(), McpDescriptors::fromInternal );
    }

    private static McpDescriptors fromInternal( final ImmutableList<McpDescriptor> list )
    {
        return list.isEmpty() ? EMPTY : new McpDescriptors( list );
    }
}
