package com.enonic.xp.lib.node;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

public class NodeKeys
    implements Iterable<NodeKey>
{
    private final List<NodeKey> keys;

    private NodeKeys( final Collection<NodeKey> keys )
    {
        this.keys = List.copyOf( keys );
    }

    public final Stream<NodeKey> stream()
    {
        return this.keys.stream();
    }

    public static NodeKeys empty()
    {
        return new NodeKeys( List.of() );
    }

    public int size()
    {
        return this.keys.size();
    }

    public boolean singleValue()
    {
        return this.keys.size() == 1;
    }

    public NodeKey first()
    {
        return keys.get( 0 );
    }

    @Override
    public Iterator<NodeKey> iterator()
    {
        return keys.iterator();
    }

    public static NodeKeys from( final Collection<NodeKey> keys )
    {
        return new NodeKeys( keys );
    }

    public static NodeKeys from( final NodeKey nodeKey )
    {
        return new NodeKeys( List.of( nodeKey ) );
    }

    public static NodeKeys from( final String[] keys )
    {
        if ( keys == null )
        {
            return NodeKeys.empty();
        }

        return new NodeKeys( Arrays.stream( keys ).map( NodeKey::from ).toList() );
    }
}
