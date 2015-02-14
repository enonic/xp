package com.enonic.wem.repo.internal.entity;

import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import com.enonic.xp.node.NodeId;
import com.enonic.xp.util.Reference;

class NodeReferenceUpdatesHolder
{
    private ImmutableMap<NodeId, NodeId> references;

    private NodeReferenceUpdatesHolder( final Builder builder )
    {
        this.references = ImmutableMap.copyOf( builder.references );
    }

    boolean mustUpdate( final Reference reference )
    {
        return this.references.containsKey( reference.getNodeId() );
    }

    Reference getNewReference( final Reference oldReference )
    {
        return Reference.from( this.references.get( oldReference.getNodeId() ).toString() );
    }

    static Builder create()
    {
        return new Builder();
    }

    static class Builder
    {
        private Map<NodeId, NodeId> references = Maps.newHashMap();

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

