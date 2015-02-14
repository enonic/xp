package com.enonic.xp.node;

import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import com.enonic.xp.branch.Branch;

public class GetActiveNodeVersionsResult
{
    private final ImmutableMap<Branch, NodeVersion> nodeVersions;

    private GetActiveNodeVersionsResult( Builder builder )
    {
        nodeVersions = ImmutableMap.copyOf( builder.nodeVersions );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public ImmutableMap<Branch, NodeVersion> getNodeVersions()
    {
        return nodeVersions;
    }


    public static final class Builder
    {
        private final Map<Branch, NodeVersion> nodeVersions = Maps.newHashMap();

        private Builder()
        {
        }

        public Builder add( final Branch branch, final NodeVersion nodeVersion )
        {
            this.nodeVersions.put( branch, nodeVersion );
            return this;
        }

        public GetActiveNodeVersionsResult build()
        {
            return new GetActiveNodeVersionsResult( this );
        }
    }
}
