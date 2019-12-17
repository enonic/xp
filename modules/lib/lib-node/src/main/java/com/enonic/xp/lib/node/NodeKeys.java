package com.enonic.xp.lib.node;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.collect.Lists;

public class NodeKeys
    implements Iterable<NodeKey>
{
    private final List<NodeKey> keys;

    private NodeKeys( final Collection<NodeKey> keys )
    {
        this.keys = Lists.newArrayList( keys );
    }

    public final Stream<NodeKey> stream()
    {
        return this.keys.stream();
    }

    public static NodeKeys empty()
    {
        return new NodeKeys( new ArrayList<>() );
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
        if ( keys == null )
        {
            return NodeKeys.empty();
        }

        return new NodeKeys( keys );
    }

    public static NodeKeys from( final NodeKey nodeKey )
    {
        return new NodeKeys( Lists.newArrayList( nodeKey ) );
    }

    public static NodeKeys from( final String[] keys )
    {
        if ( keys == null )
        {
            return NodeKeys.empty();
        }

        return new NodeKeys( Arrays.stream( keys ).map( NodeKey::from ).collect( Collectors.toList() ) );
    }
}
