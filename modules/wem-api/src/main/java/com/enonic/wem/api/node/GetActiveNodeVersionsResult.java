package com.enonic.wem.api.node;

import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import com.enonic.wem.api.workspace.Workspace;

public class GetActiveNodeVersionsResult
{
    private final ImmutableMap<Workspace, NodeVersion> nodeVersions;

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
        private final Map<Workspace, NodeVersion> nodeVersions = Maps.newHashMap();

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
