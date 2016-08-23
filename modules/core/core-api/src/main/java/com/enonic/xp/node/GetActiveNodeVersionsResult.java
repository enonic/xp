package com.enonic.xp.node;

import java.util.Map;

import com.google.common.annotations.Beta;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import com.enonic.xp.branch.BranchId;

@Beta
public class GetActiveNodeVersionsResult
{
    private final ImmutableMap<BranchId, NodeVersionMetadata> nodeVersions;

    private GetActiveNodeVersionsResult( Builder builder )
    {
        nodeVersions = ImmutableMap.copyOf( builder.nodeVersions );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public ImmutableMap<BranchId, NodeVersionMetadata> getNodeVersions()
    {
        return nodeVersions;
    }


    public static final class Builder
    {
        private final Map<BranchId, NodeVersionMetadata> nodeVersions = Maps.newHashMap();

        private Builder()
        {
        }

        public Builder add( final BranchId branchId, final NodeVersionMetadata nodeVersionMetadata )
        {
            this.nodeVersions.put( branchId, nodeVersionMetadata );
            return this;
        }

        public GetActiveNodeVersionsResult build()
        {
            return new GetActiveNodeVersionsResult( this );
        }
    }
}
