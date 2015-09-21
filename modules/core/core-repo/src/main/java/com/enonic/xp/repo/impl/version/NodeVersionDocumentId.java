package com.enonic.xp.repo.impl.version;

import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Iterators;

import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeVersionId;

public class NodeVersionDocumentId
{
    private static final String SEPARATOR = "_";

    private final String value;

    private final NodeId nodeId;

    private final NodeVersionId nodeVersionId;

    public NodeVersionDocumentId( final NodeId nodeId, final NodeVersionId nodeVersionId )
    {
        Preconditions.checkNotNull( nodeId );
        Preconditions.checkNotNull( nodeVersionId );

        this.value = nodeId + SEPARATOR + nodeVersionId.toString();
        this.nodeId = nodeId;
        this.nodeVersionId = nodeVersionId;
    }

    public static NodeVersionDocumentId from( final String value )
    {
        if ( !value.contains( SEPARATOR ) )
        {
            throw new IllegalArgumentException( "Invalid format of branch-key: " + value );
        }

        final Iterable<String> split = Splitter.on( SEPARATOR ).
            split( value );

        final String nodeIdAsString = Iterators.get( split.iterator(), 0 );
        final String nodeVersionId = Iterators.get( split.iterator(), 1 );

        Preconditions.checkArgument( !Strings.isNullOrEmpty( nodeIdAsString ) );
        Preconditions.checkArgument( !Strings.isNullOrEmpty( nodeVersionId ) );

        return new NodeVersionDocumentId( NodeId.from( value ), NodeVersionId.from( nodeVersionId ) );
    }

    public String getValue()
    {
        return value;
    }

    public NodeId getNodeId()
    {
        return nodeId;
    }

    public NodeVersionId getNodeVersionId()
    {
        return nodeVersionId;
    }

    @Override
    public String toString()
    {
        return value;
    }

}
