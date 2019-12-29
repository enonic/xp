package com.enonic.xp.node;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public final class NodeType
{
    public final static NodeType DEFAULT_NODE_COLLECTION = NodeType.from( "default" );

    private final String name;

    private NodeType( final String name )
    {
        this.name = name;
    }

    public static NodeType from( final String name )
    {
        return new NodeType( name );
    }

    public String getName()
    {
        return name;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }

        final NodeType that = (NodeType) o;

        return name != null ? name.equals( that.name ) : that.name == null;
    }

    @Override
    public int hashCode()
    {
        return name != null ? name.hashCode() : 0;
    }

    @Override
    public String toString()
    {
        return this.name;
    }
}
