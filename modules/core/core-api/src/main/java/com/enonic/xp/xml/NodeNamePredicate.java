package com.enonic.xp.xml;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

import org.w3c.dom.Node;

final class NodeNamePredicate
    implements Predicate<Node>
{
    private final Set<String> set;

    NodeNamePredicate( final String... names )
    {
        this.set = new HashSet<>( Arrays.asList( names ) );
    }

    @Override
    public boolean test( final Node node )
    {
        return ( this.set.contains( node.getNodeName() ) || this.set.contains( node.getLocalName() ) );
    }
}
