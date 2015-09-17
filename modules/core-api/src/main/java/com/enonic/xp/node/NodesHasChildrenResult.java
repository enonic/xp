package com.enonic.xp.node;

import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

public class NodesHasChildrenResult
{
    private final ImmutableMap<NodeId, Boolean> valueMap;

    public NodesHasChildrenResult( final Builder builder )
    {
        this.valueMap = ImmutableMap.copyOf( builder.valueMap );
    }

    private NodesHasChildrenResult()
    {
        this.valueMap = ImmutableMap.of();
    }

    public static NodesHasChildrenResult empty()
    {
        return new NodesHasChildrenResult();
    }

    public boolean hasChild( final NodeId nodeId )
    {
        return this.valueMap.get( nodeId ) != null ? this.valueMap.get( nodeId ) : false;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private final Map<NodeId, Boolean> valueMap = Maps.newHashMap();

        public Builder add( final NodeId nodeId, final boolean hasChild )
        {
            this.valueMap.put( nodeId, hasChild );
            return this;
        }

        public NodesHasChildrenResult build()
        {
            return new NodesHasChildrenResult( this );
        }
    }


}