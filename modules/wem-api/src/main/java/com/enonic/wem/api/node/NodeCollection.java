package com.enonic.wem.api.node;

public class NodeCollection
{
    public final static NodeCollection DEFAULT_NODE_COLLECTION = NodeCollection.from( "default" );

    private final String name;

    private NodeCollection( final String name )
    {
        this.name = name;
    }

    public static NodeCollection from( final String name )
    {
        return new NodeCollection( name );
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

        final NodeCollection that = (NodeCollection) o;

        if ( name != null ? !name.equals( that.name ) : that.name != null )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        return name != null ? name.hashCode() : 0;
    }
}
