package com.enonic.xp.node;

import java.util.Map;

import com.google.common.collect.ImmutableMap;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.branch.Branch;

@PublicApi
public final class GetActiveNodeVersionsResult
{
    private final ImmutableMap<Branch, NodeVersion> nodeVersions;

    private GetActiveNodeVersionsResult( Builder builder )
    {
        nodeVersions = builder.nodeVersions.build();
    }

    public static Builder create()
    {
        return new Builder();
    }

    public Map<Branch, NodeVersion> getNodeVersions()
    {
        return nodeVersions;
    }


    public static final class Builder
    {
        private final ImmutableMap.Builder<Branch, NodeVersion> nodeVersions = ImmutableMap.builder();

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
