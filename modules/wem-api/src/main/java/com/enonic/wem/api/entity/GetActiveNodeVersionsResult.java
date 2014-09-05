package com.enonic.wem.api.entity;

import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

public class GetActiveNodeVersionsResult
{
    private ImmutableMap<Workspace, NodeVersion> nodeVersions;

    private GetActiveNodeVersionsResult( Builder builder )
    {
        nodeVersions = ImmutableMap.copyOf( builder.nodeVersions );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public ImmutableMap<Workspace, NodeVersion> getNodeVersions()
    {
        return nodeVersions;
    }


    public static final class Builder
    {
        private Map<Workspace, NodeVersion> nodeVersions = Maps.newHashMap();

        private Builder()
        {
        }

        public Builder add( final Workspace workspace, final NodeVersion nodeVersion )
        {
            this.nodeVersions.put( workspace, nodeVersion );
            return this;
        }

        public GetActiveNodeVersionsResult build()
        {
            return new GetActiveNodeVersionsResult( this );
        }
    }
}
