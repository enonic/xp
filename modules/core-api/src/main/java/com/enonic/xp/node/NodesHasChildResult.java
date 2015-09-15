package com.enonic.xp.node;

import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

public class NodesHasChildResult
{
    private final ImmutableMap<NodeId, Boolean> valueMap;

    public NodesHasChildResult( final Builder builder )
    {
        this.valueMap = ImmutableMap.copyOf( builder.valueMap );
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

        public NodesHasChildResult build()
        {
            return new NodesHasChildResult( this );
        }
    }


}