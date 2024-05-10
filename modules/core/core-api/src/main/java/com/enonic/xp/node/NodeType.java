package com.enonic.xp.node;

import java.util.Objects;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public final class NodeType
{
    public static final NodeType DEFAULT_NODE_COLLECTION = new NodeType( "default" );

    private final String name;

    private NodeType( final String name )
    {
        this.name = Objects.requireNonNull( name );
    }

    public static NodeType from( final String name )
    {
        return DEFAULT_NODE_COLLECTION.name.equals( name ) ? DEFAULT_NODE_COLLECTION : new NodeType( name );
    }

    public String getName()
    {
        return name;
    }

    @Override
    public boolean equals( final Object o )
    {
        return this == o || o instanceof NodeType && name.equals( ( (NodeType) o ).name );
    }

    @Override
    public int hashCode()
    {
        return name.hashCode();
    }

    @Override
    public String toString()
    {
        return this.name;
    }
}
