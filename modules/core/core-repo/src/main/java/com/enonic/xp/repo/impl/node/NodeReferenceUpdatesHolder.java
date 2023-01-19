package com.enonic.xp.repo.impl.node;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.google.common.collect.ImmutableMap;

import com.enonic.xp.node.NodeId;
import com.enonic.xp.util.Reference;

final class NodeReferenceUpdatesHolder
{
    private final ImmutableMap<NodeId, NodeId> references;

    private NodeReferenceUpdatesHolder( final Builder builder )
    {
        this.references = ImmutableMap.copyOf( builder.references );
    }

    boolean mustUpdate( final Reference reference, final NodeId duplicatedNodeId )
    {
        return this.references.containsKey( reference.getNodeId() ) &&
            !Objects.equals( this.references.get( reference.getNodeId() ), duplicatedNodeId );
    }

    Reference getNewReference( final Reference oldReference )
    {
        return new Reference( this.references.get( oldReference.getNodeId() ) );
    }

    static Builder create()
    {
        return new Builder();
    }

    static class Builder
    {
        private final Map<NodeId, NodeId> references = new HashMap<>();

        Builder add( final NodeId from, final NodeId to )
        {
            this.references.put( from, to );
            return this;
        }

        NodeReferenceUpdatesHolder build()
        {
            return new NodeReferenceUpdatesHolder( this );
        }
    }
}

