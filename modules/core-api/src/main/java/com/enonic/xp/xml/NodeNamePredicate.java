package com.enonic.xp.xml;

import java.util.Set;
import java.util.function.Predicate;

import org.w3c.dom.Node;

import com.google.common.collect.Sets;

final class NodeNamePredicate
    implements Predicate<Node>
{
    private final Set<String> set;

    public NodeNamePredicate( final String... names )
    {
        this.set = Sets.newHashSet( names );
    }

    @Override
    public boolean test( final Node node )
    {
        return ( this.set.contains( node.getNodeName() ) || this.set.contains( node.getLocalName() ) );
    }
}
